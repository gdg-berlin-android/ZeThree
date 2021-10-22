package berlindroid.zethree.util

import android.util.Log
import android.util.Pair
import com.fasterxml.jackson.databind.ObjectMapper
import berlindroid.zethree.BuildConfig
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

object LowQualityUpdateDownloader {
    private const val RELEASES_URL =
        "https://api.github.com/repos/gdg-berlin-android/ZeThree/releases"
    private const val TIMEOUT = 5000

    fun resolveLatestApk(): ApkResolutionFactory {
        return try {
            val content = getJSON(RELEASES_URL, TIMEOUT)
                ?: return ApkResolutionFactory.withError("Network error")
            val releases: List<Release> = ObjectMapper().readValue(
                content, object : TypeReference<List<Release>>() {}
            )
            val apkType = "application/vnd.android.package-archive"
            val isApk = Predicate { asset: Asset -> asset.contentType == apkType }
            val assetDataSorted = releases.stream()
                .filter { release: Release -> release.assets.stream().anyMatch(isApk) }
                .sorted(Comparator.comparing { o: Release -> o.publishedAt })
                .map { release: Release ->
                    val apkUrl = release.assets.stream().filter(isApk).findFirst().get().url
                    val appVersion = release.tag.replaceFirst("v".toRegex(), "")
                    Pair(apkUrl, appVersion)
                }
                .collect(Collectors.toList())

            // sorted, just take the last (latest)
            val lastReleaseData = assetDataSorted[assetDataSorted.size - 1]
            val apkUrl = lastReleaseData.first
            val appVersion = lastReleaseData.second
            if (BuildConfig.VERSION_NAME == appVersion) {
                ApkResolutionFactory.withVersion(appVersion)
            } else {
                ApkResolutionFactory.withUrl(apkUrl)
            }
        } catch (t: Throwable) {
            Log.e(LowQualityUpdateDownloader::class.java.simpleName, "Failed", t)
            ApkResolutionFactory.withError(t.message)
        }
    }

    private fun getJSON(url: String, timeout: Int): String? {
        var c: HttpURLConnection? = null
        return try {
            val u = URL(url)
            c = (u.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                useCaches = false
                allowUserInteraction = false
                connectTimeout = timeout
                readTimeout = timeout
                setRequestProperty("Accept", "application/vnd.github.v3+json")
            }

//            c.setRequestProperty(
//                "Authorization",
//                "Bearer <YOUR_TOKEN_HERE>"
//            );
            c.connect()
            val successCodes = arrayOf(200, 201)
            when (val status = c.responseCode) {
                in successCodes -> {
                    val sr = InputStreamReader(c.inputStream)
                    val br = BufferedReader(sr)
                    val sb = StringBuilder()
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        sb.append(line)
                        sb.append("\n")
                    }
                    br.close()
                    sb.toString()
                }
                else -> {
                    val t: Throwable =
                        IllegalStateException("HTTP/" + status + ": " + c.responseMessage)
                    Log.e(LowQualityUpdateDownloader::class.java.simpleName, "Failed", t)
                    null
                }
            }
        } catch (t: Throwable) {
            Log.e(LowQualityUpdateDownloader::class.java.simpleName, "Failed", t)
            null
        } finally {
            if (c != null) {
                try {
                    c.disconnect()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class Asset(
        @JsonProperty("browser_download_url")
        val url: String,

        @JsonProperty("content_type")
        val contentType: String
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class Release(
        @JsonProperty("published_at")
        val publishedAt: Date,

        @JsonProperty("assets")
        val assets: List<Asset>,

        @JsonProperty("tag_name")
        val tag: String
    )

    class ApkResolutionFactory private constructor() {
        var status = Status.FAILED
        var url: String? = null
        var appVersion: String? = null
        var errorMessage: String? = null

        companion object {
            fun withUrl(url: String?): ApkResolutionFactory {
                val r = ApkResolutionFactory()
                r.status = Status.APK_AVAILABLE
                r.url = url
                return r
            }

            fun withVersion(appVersion: String?): ApkResolutionFactory {
                val r = ApkResolutionFactory()
                r.status = Status.ALREADY_LATEST
                r.appVersion = appVersion
                return r
            }

            fun withError(error: String?): ApkResolutionFactory {
                val r = ApkResolutionFactory()
                r.status = Status.FAILED
                r.errorMessage = error
                return r
            }
        }
    }

    enum class Status {
        FAILED, APK_AVAILABLE, ALREADY_LATEST
    }
}

package berlindroid.zethree.util;

import android.util.Log;
import android.util.Pair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import berlindroid.zethree.BuildConfig;

public class LowQualityUpdateDownloader {

    private static final String RELEASES_URL =
        "https://api.github.com/repos/gdg-berlin-android/ZeThree/releases";

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Asset {
        @JsonProperty("browser_download_url") public String url;
        @JsonProperty("content_type") public String contentType;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Release {
        @JsonProperty("published_at") public Date publishedAt;
        @JsonProperty("assets") public List<Asset> assets;
        @JsonProperty("tag_name") public String tag;
    }

    public static class ApkResolution {

        @NonNull
        public static ApkResolution withUrl(String url) {
            ApkResolution r = new ApkResolution();
            r.status = Status.APK_AVAILABLE;
            r.url = url;
            return r;
        }

        @NonNull
        public static ApkResolution withVersion(String appVersion) {
            ApkResolution r = new ApkResolution();
            r.status = Status.ALREADY_LATEST;
            r.appVersion = appVersion;
            return r;
        }

        @NonNull
        public static ApkResolution withError(String error) {
            ApkResolution r = new ApkResolution();
            r.status = Status.FAILED;
            r.errorMessage = error;
            return r;
        }

        @NonNull
        public Status status = Status.FAILED;

        @Nullable
        public String url;

        @Nullable
        public String appVersion;

        @Nullable
        public String errorMessage;

        private ApkResolution() { super(); }
    }

    public enum Status {
        FAILED, APK_AVAILABLE, ALREADY_LATEST
    }

    @SuppressWarnings({"SameParameterValue", "OptionalGetWithoutIsPresent"})
    @NonNull
    public static ApkResolution resolveLatestApk() {
        try {
            String content = getJSON(RELEASES_URL, 5000);
            if (content == null) return ApkResolution.withError("Network error");

            List<Release> releases = new ObjectMapper().readValue(
                content, new TypeReference<List<Release>>() { }
            );

            String apkType = "application/vnd.android.package-archive";
            Predicate<Asset> isApk = asset -> asset.contentType.equals((apkType));
            List<Pair<String, String>> assetDataSorted = releases.stream()
                .filter(release -> release.assets.stream().anyMatch(isApk))
                .sorted(Comparator.comparing(o -> o.publishedAt))
                .map(release -> {
                    String apkUrl = release.assets.stream().filter(isApk).findFirst().get().url;
                    String appVersion = release.tag.replaceFirst("v", "");
                    return new Pair<>(apkUrl, appVersion);
                })
                .collect(Collectors.toList());

            // sorted, just take the last (latest)
            Pair<String, String> lastReleaseData = assetDataSorted.get(assetDataSorted.size() - 1);

            String apkUrl = lastReleaseData.first;
            String appVersion = lastReleaseData.second;
            if (BuildConfig.VERSION_NAME.equals(appVersion)) {
                return ApkResolution.withVersion(appVersion);
            } else {
                return ApkResolution.withUrl(apkUrl);
            }
        } catch (Throwable t) {
            Log.e(LowQualityUpdateDownloader.class.getSimpleName(), "Failed", t);
            return ApkResolution.withError(t.getMessage());
        }
    }

    @SuppressWarnings("SameParameterValue")
    @Nullable
    private static String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.setRequestProperty("Accept", "application/vnd.github.v3+json");

//            c.setRequestProperty(
//                "Authorization",
//                "Bearer <YOUR_TOKEN_HERE>"
//            );
            c.connect();

            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    InputStreamReader sr = new InputStreamReader(c.getInputStream());
                    BufferedReader br = new BufferedReader(sr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    br.close();
                    return sb.toString();
                default:
                    Throwable t =
                        new IllegalStateException("HTTP/" + status + ": " + c.getResponseMessage());
                    Log.e(LowQualityUpdateDownloader.class.getSimpleName(), "Failed", t);
                    return null;
            }
        } catch (Throwable t) {
            Log.e(LowQualityUpdateDownloader.class.getSimpleName(), "Failed", t);
            return null;
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

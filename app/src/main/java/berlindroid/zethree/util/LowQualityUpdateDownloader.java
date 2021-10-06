package berlindroid.zethree.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

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

public class LowQualityUpdateDownloader {

    private final Activity activity;

    public LowQualityUpdateDownloader(Activity activity) {
        super();
        this.activity = activity;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Asset {
        @JsonProperty("browser_download_url") public String url;
        @JsonProperty("content_type") public String contentType;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Release {
        @JsonProperty("published_at") public Date publishedAt;
        @JsonProperty("assets") public List<Asset> assets;
    }

    @SuppressWarnings({"SameParameterValue", "OptionalGetWithoutIsPresent"})
    public String getLatestApkUrl(String url) {
        try {
            String content = getJSON(url, 5000);
            if (content == null) return null;
            ObjectMapper mapper = new ObjectMapper();
            List<Release> releases = mapper.readValue(
                content,
                new TypeReference<List<Release>>() { }
            );

            String apkType = "application/vnd.android.package-archive";
            Predicate<Asset> isApk = asset -> asset.contentType.equals((apkType));
            List<String> apkUrls = releases.stream()
                .filter(release -> release.assets.stream().anyMatch(isApk))
                .sorted(Comparator.comparing(o -> o.publishedAt))
                .map(release -> release.assets.stream().filter(isApk).findFirst().get().url)
                .collect(Collectors.toList());
            return apkUrls.get(apkUrls.size() - 1);
        } catch (Throwable t) {
            failAndClose(t);
            return null;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private String getJSON(String url, int timeout) {
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
                    failAndClose(
                        new IllegalStateException("HTTP/" + status + ": " + c.getResponseMessage())
                    );
                    return null;
            }
        } catch (Throwable t) {
            failAndClose(t);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void failAndClose(Throwable t) {
        new Handler(Looper.getMainLooper()).post(
            () -> {
                Toast.makeText(
                    activity,
                    "Failed, pfff...\n" + t.getMessage(),
                    Toast.LENGTH_LONG
                ).show();
                activity.finish();
            }
        );
    }

}

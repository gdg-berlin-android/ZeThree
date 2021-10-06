package berlindroid.zethree;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import berlindroid.zethree.util.LowQualityUpdateDownloader;

public class UpdateActivity extends Activity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity);
    }

    // risky only if you want it to be risky !!!!!!111one
    @SuppressLint("StaticFieldLeak")
    @Override protected void onStart() {
        super.onStart();

        // AsyncTask is the new Coroutine Job!

        //noinspection deprecation
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... voids) {
                // cause some fake delay to prevent rate-limiter from kicking in
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                new Handler(Looper.getMainLooper()).post(
                    () -> findViewById(R.id.updateWaitText).setVisibility(View.VISIBLE)
                );
                return null;
            }
        }.execute();

        //noinspection deprecation
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... voids) {
                LowQualityUpdateDownloader downloader =
                    new LowQualityUpdateDownloader(UpdateActivity.this);
                String latestApkUrl = downloader.getLatestApkUrl(
                    "https://api.github.com/repos/gdg-berlin-android/ZeThree/releases"
                );
                if (latestApkUrl == null) return null;

                new Handler(Looper.getMainLooper()).post(
                    () -> {
                        Toast.makeText(
                            UpdateActivity.this,
                            "Install the downloaded file",
                            Toast.LENGTH_LONG
                        ).show();

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(latestApkUrl));
                        startActivity(i);

                        UpdateActivity.this.finish();
                    }
                );
                return null;
            }
        }.execute();
    }

}

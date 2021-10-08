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
import berlindroid.zethree.util.LowQualityUpdateDownloader.ApkResolution;

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
                // cause some fake delay to prevent GitHub's rate-limiter from kicking in
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                new Handler(Looper.getMainLooper()).post(
                    () -> findViewById(R.id.updateWaitText).setVisibility(View.VISIBLE)
                );
                return null;
            }
        }.execute();

        //noinspection deprecation
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... voids) {
                ApkResolution result = LowQualityUpdateDownloader.resolveLatestApk();

                new Handler(Looper.getMainLooper()).post(
                    () -> {
                        switch (result.status) {
                            case FAILED:
                                Toast.makeText(
                                    UpdateActivity.this,
                                    "Can't fetch the update: " + result.errorMessage,
                                    Toast.LENGTH_LONG
                                ).show();
                                break;
                            case APK_AVAILABLE:
                                Toast.makeText(
                                    UpdateActivity.this,
                                    "Next: Install the downloaded file",
                                    Toast.LENGTH_LONG
                                ).show();

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(result.url));
                                startActivity(i);
                                break;
                            case ALREADY_LATEST:
                                Toast.makeText(
                                    UpdateActivity.this,
                                    "Already at the latest version: v" + result.appVersion,
                                    Toast.LENGTH_LONG
                                ).show();
                                break;
                        }
                        UpdateActivity.this.finish();
                    }
                );
                return null;
            }
        }.execute();
    }

}

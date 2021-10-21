package berlindroid.zethree

import android.app.Activity
import android.os.Bundle
import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Looper
import berlindroid.zethree.util.LowQualityUpdateDownloader
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.coroutineContext

class UpdateActivity : Activity() {

    //Use this scope to run the coroutine and remove the AsyncTasks :*
    private val coroutineScope = Dispatchers.IO + Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_activity)
    }

    // risky only if you want it to be risky !!!!!!111one
    @SuppressLint("StaticFieldLeak")
    override fun onStart() {
        super.onStart()


                // AsyncTask is the new Coroutine Job!
        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                // cause some fake delay to prevent GitHub's rate-limiter from kicking in
                try {
                    Thread.sleep(1000)
                } catch (ignored: InterruptedException) {
                }
                Handler(Looper.getMainLooper()).post {
                    findViewById<View>(R.id.updateWaitText).visibility = View.VISIBLE
                }
                return null
            }
        }.execute()
        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                val result = LowQualityUpdateDownloader.resolveLatestApk()
                Handler(Looper.getMainLooper()).post {
                    when (result.status) {
                        LowQualityUpdateDownloader.Status.FAILED -> Toast.makeText(
                            this@UpdateActivity,
                            "Can't fetch the update: " + result.errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                        LowQualityUpdateDownloader.Status.APK_AVAILABLE -> {
                            Toast.makeText(
                                this@UpdateActivity,
                                "Next: Install the downloaded file",
                                Toast.LENGTH_LONG
                            ).show()
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(result.url)
                            startActivity(i)
                        }
                        LowQualityUpdateDownloader.Status.ALREADY_LATEST -> Toast.makeText(
                            this@UpdateActivity,
                            "Already at the latest version: v" + result.appVersion,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    finish()
                }
                return null
            }
        }.execute()
    }
}
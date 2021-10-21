package berlindroid.zethree

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.view.View
import berlindroid.zethree.cats.CatsActivity
import berlindroid.zethree.dogs.DogsActivity
import android.view.ViewGroup
import android.widget.TextView
import android.view.animation.BounceInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.view.children
import androidx.work.PeriodicWorkRequest
import berlindroid.zethree.util.UpdateHandlerControllerManagerRefresher
import androidx.work.BackoffPolicy
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.dtx12.android_animations_actions.actions.Actions.forever
import com.dtx12.android_animations_actions.actions.Actions.play
import com.dtx12.android_animations_actions.actions.Actions.sequence
import com.dtx12.android_animations_actions.actions.Actions.scaleTo
import com.dtx12.android_animations_actions.actions.Interpolations
import java.time.Duration
import java.util.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        scheduleUpdates()

        findViewById<ViewGroup>(R.id.main_root).woobleEVERYTHING()
    }

    public override fun onResume() {
        findViewById<View>(R.id.main_cat_button).setOnLongClickListener { view: View? ->
            onCatsLongClicked(
                findViewById(R.id.main_cat_button)
            )
            true
        }

        findViewById<View>(R.id.main_not_cat_button).setOnLongClickListener { view: View? ->
            onDogsLongClicked(
                findViewById(R.id.main_cat_button)
            )
            true
        }
        super.onResume()
    }



    @OptIn(ExperimentalFoundationApi::class)
    fun onCatsClicked(view: View?) {
        startActivity(Intent(this, CatsActivity::class.java))
    }

    fun onDogsClicked(view: View?) {
        startActivity(Intent(this, DogsActivity::class.java))
    }

    fun onDogsLongClicked(view: View?) {
        val dogs: MutableList<String> = ArrayList()
        dogs.add("🐶")
        dogs.add("🐕")
        dogs.add("🐩")

        val r = Random()
        val width = 1000
        val duration = 5000
        val root = findViewById<ViewGroup>(R.id.main_root)
        for (i in 0..23) {
            val targetScale = r.nextFloat() * 0.1f
            val dog = TextView(this)
            dog.text = dogs[r.nextInt(dogs.size)]
            dog.x = r.nextFloat() * width
            dog.y = 3000f
            dog.z = 5.5f + (r.nextFloat()-0.5f) * 3.0f
            dog.scaleX = 30.0f
            dog.scaleY = 30.0f

            dog.animate()
                .translationY(-40.0f)
                .setDuration(duration.toLong())
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setStartDelay(r.nextInt(duration / 2).toLong())
                .withEndAction { root.removeView(dog) }
                .start()

            root.addView(dog)
        }
    }

    fun onCatsLongClicked(view: View?) {
        val cats: MutableList<String> = ArrayList()
        cats.add("😻") // 🐬🐬🐬🐬
        cats.add("😺")
        cats.add("😸")
        cats.add("😹")
        cats.add("😼")
        cats.add("😽")
        cats.add("🙀")
        cats.add("😿")
        cats.add("😾")
        cats.add("🐱")
        cats.add("🐈")
        val r = Random()
        val width = 1000
        val height = 2000
        val duration = 5000
        val root = findViewById<ViewGroup>(R.id.main_root)
        for (i in 0..49) {
            val cat = TextView(this)
            cat.text = cats[r.nextInt(cats.size)]
            cat.x = r.nextFloat() * width
            cat.y = 0f
            cat.alpha = 0.0f
            val startScale = r.nextFloat() * 0.5f
            val targetScale = r.nextFloat() * 20.0f
            cat.scaleX = startScale
            cat.scaleY = startScale
            cat.rotation = r.nextFloat() * 360.0f
            cat.rotationY = r.nextFloat() * 180.0f
            cat.animate().translationY(height * 1.0f).setDuration(duration.toLong()).rotation(360f)
                .rotationY(360.0f)
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setStartDelay(r.nextInt(duration / 2).toLong())
                .withEndAction { root.removeView(cat) }
                .withStartAction { cat.alpha = 1.0f }.setInterpolator(BounceInterpolator()).start()
            root.addView(cat, 80, 80)
        }
    }

    fun tryMeFun(view: View?) {
        Toast.makeText(
            this,
            "Hey, it's fun and built upon me",
            Toast.LENGTH_LONG
        ).show()
    }

    fun doYourThingClicked(view: View?) {
        Toast.makeText(
            this,
            """
                Hey, do what ever you want, you got 15 minutes!
                
                No idea what to do? Look around you for some task  you!
                """.trimIndent(),
            Toast.LENGTH_LONG
        ).show()
    }

    fun checkForUpdates(view: View?) {
        val i = Intent(this, UpdateActivity::class.java)
        startActivity(i)
    }

    private fun scheduleUpdates() {
        val findNewApkRequest = PeriodicWorkRequest.Builder(
            UpdateHandlerControllerManagerRefresher::class.java,
            Duration.ofMinutes(60),
            Duration.ofMinutes(15)
        )
            .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(20))
            .build()
        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "app_updater_background",
                ExistingPeriodicWorkPolicy.KEEP,
                findNewApkRequest
            )
    }
}

fun ViewGroup.woobleEVERYTHING() = children.forEach {
    if ( it is Button) {
        it.startWobbling()
    }
}

fun View.startWobbling() {
    val minDuration = 1f
    val maxDuration = 1.5f
    val duration: Float = randomRange(minDuration, maxDuration)

    play(
        forever(
            sequence(
                scaleTo(0.5f, 1f, duration, Interpolations.SineEaseInOut),
                scaleTo(1f, 0.5f, duration, Interpolations.SineEaseInOut),
            )
        ),
        this
    )
}

private fun randomRange(start: Float, end: Float): Float = Random().nextFloat() * (end - start) + start


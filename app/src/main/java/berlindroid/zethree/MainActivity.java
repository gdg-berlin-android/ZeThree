package berlindroid.zethree;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import berlindroid.zethree.cats.CatsActivity;

public class MainActivity extends Activity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
    }

    @Override public void onResume() {
        findViewById(R.id.main_cat_button).setOnLongClickListener(
                view -> {
                    onCatsLongClicked(
                            findViewById(R.id.main_cat_button)
                    );
                    return true;
                }
        );

        super.onResume();
    }

    public void onCatsClicked(View view) {
        startActivity(new Intent(this, CatsActivity.class));
    }

    public void onCatsLongClicked(View view) {
        final List<String> cats = new ArrayList<>();
        cats.add("😻");cats.add("😺");cats.add("😸");cats.add("😹");cats.add("😼");cats.add("😽");cats.add("🙀");cats.add("😿");cats.add("😾");cats.add("🐱");cats.add("🐈");

        final Random r = new Random();
        final int width = 1_000;
        final int height = 2_000;
        final int duration = 5_000;
        final ViewGroup root = findViewById(R.id.main_root);

        for (int i = 0; i < 50; i++) {
            TextView cat = new TextView(this);
            cat.setText(cats.get(r.nextInt(cats.size())));

            cat.setX(r.nextFloat() * width);
            cat.setY(0);
            cat.setAlpha(0.0f);
            cat.setRotation(r.nextFloat() * 360.0f);
            cat.animate().translationY(height * 1.0f).setDuration(duration).rotation(360).setStartDelay(r.nextInt(duration / 2)).withEndAction(() -> root.removeView(cat)).withStartAction(() -> cat.setAlpha(1.0f)).setInterpolator(new BounceInterpolator()).start();
            root.addView(cat, 80, 80);
        }
    }

    public void doYourThingClicked(View view) {
        Toast.makeText(
                this,
                "Hey, do what ever you want, you got 15 minutes!\n\nNo idea what to do? Look behind you!",
                Toast.LENGTH_LONG
        ).show();
    }
}

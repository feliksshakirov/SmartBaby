package ru.smartbaby;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BreakActivity extends BaseActivity {
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, BreakActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        root.setBackground(Ui.verticalGradient(
                Color.rgb(255, 248, 219),
                Color.rgb(255, 222, 195),
                this
        ));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(Ui.dp(this, 24), Ui.dp(this, 24), Ui.dp(this, 24), Ui.dp(this, 24));
        root.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        TextView title = new TextView(this);
        title.setText("Пора сделать перерыв");
        title.setTextColor(Color.rgb(64, 54, 76));
        title.setTextSize(34f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        content.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView subtitle = new TextView(this);
        subtitle.setText("Немного отдохнём и продолжим позже");
        subtitle.setTextColor(Color.rgb(91, 84, 108));
        subtitle.setTextSize(20f);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.topMargin = Ui.dp(this, 12);
        content.addView(subtitle, subtitleParams);

        View spacer = new View(this);
        content.addView(spacer, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        TextView continueButton = Ui.createMenuButton(this, "Продолжить", Color.rgb(255, 145, 92));
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChildLockHelper.showParentChallenge(BreakActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        continueSession();
                    }
                });
            }
        });
        content.addView(continueButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ChildLockHelper.attachHoldToCorner(root, this, new Runnable() {
            @Override
            public void run() {
                continueSession();
            }
        });

        setContentView(root);
    }

    private void continueSession() {
        SessionTimerManager.resumeAfterBreak(this);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

package ru.smartbaby;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
    private TextView favoriteGameView;
    private boolean darkThemeApplied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        darkThemeApplied = AppSettingsManager.isDarkThemeEnabled(this);
        setContentView(createContent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean darkThemeEnabled = AppSettingsManager.isDarkThemeEnabled(this);
        if (darkThemeEnabled != darkThemeApplied) {
            darkThemeApplied = darkThemeEnabled;
            setContentView(createContent());
        }
        if (favoriteGameView != null) {
            favoriteGameView.setText(
                    "Возраст: " + AppSettingsManager.getAgeGroup(this)
                            + "  •  Любимая игра: " + StatsManager.getFavoriteGameTitle(this)
            );
        }
    }

    @Override
    protected boolean shouldRequireBreakScreen() {
        return true;
    }

    private View createContent() {
        FrameLayout root = new FrameLayout(this);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackground(Ui.verticalGradient(
                darkThemeApplied ? Color.rgb(23, 28, 48) : Color.rgb(255, 248, 232),
                darkThemeApplied ? Color.rgb(35, 47, 76) : Color.rgb(233, 245, 255),
                this
        ));
        root.addView(scrollView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(Ui.dp(this, 20), Ui.dp(this, 28), Ui.dp(this, 20), Ui.dp(this, 28));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(this);
        title.setText("Умный Малыш");
        title.setTextColor(darkThemeApplied ? Color.WHITE : Color.rgb(45, 55, 88));
        title.setTextSize(36f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        content.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.topMargin = Ui.dp(this, 18);

        content.addView(createGameButton("Волшебные линии", Color.rgb(85, 188, 255), new Intent(this, DrawingGameActivity.class)), buttonParams);
        content.addView(createGameButton("Лопни шарик", Color.rgb(255, 118, 120), new Intent(this, BalloonGameActivity.class)), copyLayoutParams(buttonParams));
        content.addView(createGameButton("Животные", Color.rgb(100, 199, 140), new Intent(this, AnimalsActivity.class)), copyLayoutParams(buttonParams));
        content.addView(createGameButton("Салют", Color.rgb(255, 172, 79), new Intent(this, FireworkGameActivity.class)), copyLayoutParams(buttonParams));
        content.addView(createGameButton("Найди животное", Color.rgb(125, 144, 255), new Intent(this, FindAnimalActivity.class)), copyLayoutParams(buttonParams));
        content.addView(createGameButton("Цвета", Color.rgb(238, 94, 154), new Intent(this, ColorsGameActivity.class)), copyLayoutParams(buttonParams));

        TextView settingsButton = Ui.createMenuButton(this, "Настройки для родителей", Color.rgb(130, 122, 255));
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChildLockHelper.showParentChallenge(MainActivity.this, new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, ParentSettingsActivity.class));
                    }
                });
            }
        });
        content.addView(settingsButton, copyLayoutParams(buttonParams));

        favoriteGameView = new TextView(this);
        favoriteGameView.setTextColor(darkThemeApplied ? Color.rgb(215, 224, 245) : Color.rgb(94, 102, 128));
        favoriteGameView.setTextSize(18f);
        favoriteGameView.setPadding(Ui.dp(this, 10), Ui.dp(this, 22), Ui.dp(this, 10), 0);
        content.addView(favoriteGameView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ChildLockHelper.attachHoldToCorner(root, this, new Runnable() {
            @Override
            public void run() {
                finishAffinity();
            }
        });

        return root;
    }

    private TextView createGameButton(String title, int color, final Intent intent) {
        TextView button = Ui.createMenuButton(this, title, color);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionTimerManager.ensureSessionStarted(MainActivity.this);
                startActivity(intent);
            }
        });
        return button;
    }

    private LinearLayout.LayoutParams copyLayoutParams(LinearLayout.LayoutParams source) {
        LinearLayout.LayoutParams copy = new LinearLayout.LayoutParams(source.width, source.height);
        copy.topMargin = source.topMargin;
        return copy;
    }
}

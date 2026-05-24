package ru.smartbaby;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ParentSettingsActivity extends BaseActivity {
    private TextView soundSwitchButton;
    private TextView themeSwitchButton;
    private TextView breakSwitchButton;
    private TextView ageOneButton;
    private TextView ageTwoButton;
    private TextView ageThreeButton;
    private TextView minutesFiveButton;
    private TextView minutesTenButton;
    private TextView minutesFifteenButton;
    private TextView statsView;
    private LinearLayout breakTimeGroup;
    private boolean compactLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compactLayout = isCompactScreen();
        rebuildContent();
    }

    @Override
    protected boolean allowSystemBack() {
        return true;
    }

    private void rebuildContent() {
        setContentView(createContent());
        refreshState();
    }

    private View createContent() {
        boolean darkTheme = AppSettingsManager.isDarkThemeEnabled(this);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setClipToPadding(false);
        scrollView.setBackground(Ui.verticalGradient(
                darkTheme ? Color.rgb(21, 27, 47) : Color.rgb(246, 248, 255),
                darkTheme ? Color.rgb(34, 45, 72) : Color.rgb(234, 243, 255),
                this
        ));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(
                Ui.dp(this, 20),
                Ui.dp(this, compactLayout ? 18 : 24),
                Ui.dp(this, 20),
                Ui.dp(this, compactLayout ? 18 : 28)
        );
        scrollView.addView(content, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(this);
        title.setText("Настройки для родителей");
        title.setTextColor(getTextColor());
        title.setTextSize(compactLayout ? 25f : 30f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        content.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Все данные хранятся только на устройстве и работают без интернета.");
        subtitle.setTextColor(getMutedColor());
        subtitle.setTextSize(compactLayout ? 16f : 18f);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.topMargin = Ui.dp(this, compactLayout ? 6 : 8);
        content.addView(subtitle, subtitleParams);

        LinearLayout switchRow = createOptionRow();
        soundSwitchButton = createSwitchButton();
        themeSwitchButton = createSwitchButton();
        switchRow.addView(createSwitchCell("Звук", soundSwitchButton), createOptionLayoutParams());
        switchRow.addView(createSwitchCell("Тёмная тема", themeSwitchButton), createOptionLayoutParams());
        content.addView(switchRow);

        content.addView(createSectionTitle("Возраст"));
        LinearLayout ageGroup = createOptionRow();
        ageOneButton = createOptionButton("1+");
        ageTwoButton = createOptionButton("2+");
        ageThreeButton = createOptionButton("3+");
        ageGroup.addView(ageOneButton, createOptionLayoutParams());
        ageGroup.addView(ageTwoButton, createOptionLayoutParams());
        ageGroup.addView(ageThreeButton, createOptionLayoutParams());
        content.addView(ageGroup);

        content.addView(createBreakHeader());
        breakTimeGroup = createOptionRow();
        minutesFiveButton = createOptionButton("5 мин");
        minutesTenButton = createOptionButton("10 мин");
        minutesFifteenButton = createOptionButton("15 мин");
        breakTimeGroup.addView(minutesFiveButton, createOptionLayoutParams());
        breakTimeGroup.addView(minutesTenButton, createOptionLayoutParams());
        breakTimeGroup.addView(minutesFifteenButton, createOptionLayoutParams());
        content.addView(breakTimeGroup);

        content.addView(createSectionTitle("Статистика"));
        statsView = new TextView(this);
        statsView.setTextColor(getTextColor());
        statsView.setTextSize(compactLayout ? 15.5f : 18f);
        statsView.setLineSpacing(0f, 1.05f);
        int statsPadding = Ui.dp(this, 15);
        statsView.setPadding(statsPadding, statsPadding, statsPadding, statsPadding);
        statsView.setBackground(Ui.rounded(getSurfaceColor(), 22f, this));
        content.addView(statsView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView resetButton = createFooterButton("Сбросить статистику", Color.rgb(255, 127, 109));
        LinearLayout.LayoutParams resetParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        resetParams.topMargin = Ui.dp(this, compactLayout ? 14 : 18);
        content.addView(resetButton, resetParams);

        TextView backButton = createFooterButton("Вернуться в меню", Color.rgb(92, 162, 255));
        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        backParams.topMargin = Ui.dp(this, compactLayout ? 10 : 12);
        content.addView(backButton, backParams);

        bindActions(resetButton, backButton);
        return scrollView;
    }

    private void bindActions(TextView resetButton, TextView backButton) {
        soundSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSettingsManager.setSoundEnabled(ParentSettingsActivity.this,
                        !AppSettingsManager.isSoundEnabled(ParentSettingsActivity.this));
                refreshState();
            }
        });

        themeSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSettingsManager.setDarkThemeEnabled(ParentSettingsActivity.this,
                        !AppSettingsManager.isDarkThemeEnabled(ParentSettingsActivity.this));
                rebuildContent();
            }
        });

        breakSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabled = !AppSettingsManager.isBreakEnabled(ParentSettingsActivity.this);
                AppSettingsManager.setBreakEnabled(ParentSettingsActivity.this, enabled);
                if (enabled) {
                    SessionTimerManager.startNewSession(ParentSettingsActivity.this);
                } else {
                    SessionTimerManager.clearSession(ParentSettingsActivity.this);
                }
                refreshState();
            }
        });

        ageOneButton.setOnClickListener(createAgeClickListener(1));
        ageTwoButton.setOnClickListener(createAgeClickListener(2));
        ageThreeButton.setOnClickListener(createAgeClickListener(3));

        minutesFiveButton.setOnClickListener(createSessionClickListener(5));
        minutesTenButton.setOnClickListener(createSessionClickListener(10));
        minutesFifteenButton.setOnClickListener(createSessionClickListener(15));

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResetConfirmation();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private View.OnClickListener createAgeClickListener(final int ageLevel) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSettingsManager.setAgeLevel(ParentSettingsActivity.this, ageLevel);
                refreshState();
            }
        };
    }

    private View.OnClickListener createSessionClickListener(final int minutes) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSettingsManager.setBreakEnabled(ParentSettingsActivity.this, true);
                AppSettingsManager.setSessionMinutes(ParentSettingsActivity.this, minutes);
                SessionTimerManager.startNewSession(ParentSettingsActivity.this);
                refreshState();
            }
        };
    }

    private void showResetConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Сбросить статистику?")
                .setMessage("Вы уверены?")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Сбросить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        StatsManager.reset(ParentSettingsActivity.this);
                        Toast.makeText(ParentSettingsActivity.this, "Статистика сброшена", Toast.LENGTH_SHORT).show();
                        refreshState();
                    }
                })
                .show();
    }

    private void refreshState() {
        boolean soundEnabled = AppSettingsManager.isSoundEnabled(this);
        boolean darkTheme = AppSettingsManager.isDarkThemeEnabled(this);
        boolean breakEnabled = AppSettingsManager.isBreakEnabled(this);
        int ageLevel = AppSettingsManager.getSelectedAgeGroup(this);
        int sessionMinutes = AppSettingsManager.getSessionMinutes(this);

        updateSwitchState(soundSwitchButton, soundEnabled, Color.rgb(110, 201, 122));
        updateSwitchState(themeSwitchButton, darkTheme, Color.rgb(147, 124, 255));
        updateSwitchState(breakSwitchButton, breakEnabled, Color.rgb(110, 201, 122));
        updateOptionState(ageOneButton, ageLevel == 1, Color.rgb(255, 194, 92));
        updateOptionState(ageTwoButton, ageLevel == 2, Color.rgb(104, 186, 255));
        updateOptionState(ageThreeButton, ageLevel == 3, Color.rgb(147, 124, 255));
        updateOptionState(minutesFiveButton, breakEnabled && sessionMinutes == 5, Color.rgb(255, 149, 109));
        updateOptionState(minutesTenButton, breakEnabled && sessionMinutes == 10, Color.rgb(104, 186, 255));
        updateOptionState(minutesFifteenButton, breakEnabled && sessionMinutes == 15, Color.rgb(110, 201, 122));

        breakTimeGroup.setVisibility(breakEnabled ? View.VISIBLE : View.GONE);

        StringBuilder builder = new StringBuilder();
        builder.append("Всего касаний: ").append(StatsManager.getTotalTouches(this)).append('\n');
        builder.append("Лопнуто шариков: ").append(StatsManager.getBalloonPops(this)).append('\n');
        builder.append("Правильных ответов «Найди животное»: ")
                .append(StatsManager.getFindAnimalCorrect(this)).append('\n');
        builder.append("Ошибочных нажатий «Найди животное»: ")
                .append(StatsManager.getFindAnimalWrong(this)).append('\n');
        builder.append("Правильных ответов «Цвета»: ")
                .append(StatsManager.getColorsCorrect(this)).append('\n');
        builder.append("Ошибочных нажатий «Цвета»: ")
                .append(StatsManager.getColorsWrong(this)).append('\n');
        builder.append("Любимая игра: ").append(StatsManager.getFavoriteGameTitle(this)).append('\n');
        builder.append("Время в игре: ").append(StatsManager.getTotalPlayMinutes(this)).append(" минут").append('\n');
        builder.append("Запуски игр:").append('\n');
        for (GameType gameType : GameType.values()) {
            builder.append("• ")
                    .append(gameType.getTitle())
                    .append(": ")
                    .append(StatsManager.getGameLaunchCount(this, gameType))
                    .append('\n');
        }
        statsView.setText(builder.toString().trim());
    }

    private void updateSwitchState(TextView view, boolean enabled, int selectedColor) {
        view.setText(enabled ? "Включен" : "Выключен");
        updateOptionState(view, enabled, selectedColor);
    }

    private void updateOptionState(TextView view, boolean selected, int selectedColor) {
        int background = selected ? selectedColor : getSurfaceColor();
        int textColor = selected ? Color.WHITE : getTextColor();
        view.setBackground(Ui.roundedWithStroke(background, Color.argb(40, 31, 46, 74), 18f, this));
        view.setTextColor(textColor);
    }

    private LinearLayout createBreakHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = Ui.dp(this, compactLayout ? 18 : 24);
        header.setLayoutParams(params);

        TextView title = new TextView(this);
        title.setText("Перерыв");
        title.setTextSize(compactLayout ? 18f : 22f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(getTextColor());
        header.addView(title, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));

        breakSwitchButton = createSwitchButton();
        header.addView(breakSwitchButton, new LinearLayout.LayoutParams(
                Ui.dp(this, compactLayout ? 142 : 158),
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return header;
    }

    private TextView createSectionTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextSize(compactLayout ? 18f : 22f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(getTextColor());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = Ui.dp(this, compactLayout ? 18 : 24);
        title.setLayoutParams(params);
        return title;
    }

    private LinearLayout createSwitchCell(String label, TextView switchButton) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextColor(getTextColor());
        labelView.setTextSize(compactLayout ? 16f : 18f);
        labelView.setTypeface(Typeface.DEFAULT_BOLD);
        labelView.setGravity(Gravity.CENTER);
        cell.addView(labelView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout.LayoutParams switchParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        switchParams.topMargin = Ui.dp(this, 8);
        cell.addView(switchButton, switchParams);
        return cell;
    }

    private LinearLayout createOptionRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = Ui.dp(this, compactLayout ? 10 : 12);
        row.setLayoutParams(params);
        return row;
    }

    private LinearLayout.LayoutParams createOptionLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        params.leftMargin = Ui.dp(this, 4);
        params.rightMargin = Ui.dp(this, 4);
        return params;
    }

    private TextView createSwitchButton() {
        TextView button = createOptionButton("");
        button.setMinHeight(Ui.dp(this, compactLayout ? 58 : 64));
        button.setTextSize(compactLayout ? 16f : 18f);
        return button;
    }

    private TextView createOptionButton(String text) {
        TextView button = new TextView(this);
        button.setText(text);
        button.setGravity(Gravity.CENTER);
        button.setTextSize(compactLayout ? 17f : 20f);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setMinHeight(Ui.dp(this, compactLayout ? 64 : 72));
        button.setPadding(Ui.dp(this, 8), Ui.dp(this, compactLayout ? 12 : 16), Ui.dp(this, 8), Ui.dp(this, compactLayout ? 12 : 16));
        return button;
    }

    private TextView createFooterButton(String text, int color) {
        TextView button = Ui.createMenuButton(this, text, color);
        button.setTextSize(compactLayout ? 20f : 23f);
        button.setMinHeight(Ui.dp(this, compactLayout ? 74 : 86));
        button.setPadding(Ui.dp(this, 14), Ui.dp(this, compactLayout ? 14 : 16), Ui.dp(this, 14), Ui.dp(this, compactLayout ? 14 : 16));
        return button;
    }

    private int getTextColor() {
        return AppSettingsManager.isDarkThemeEnabled(this) ? Color.rgb(242, 246, 255) : Color.rgb(42, 56, 88);
    }

    private int getMutedColor() {
        return AppSettingsManager.isDarkThemeEnabled(this) ? Color.rgb(190, 202, 224) : Color.rgb(90, 104, 132);
    }

    private int getSurfaceColor() {
        return AppSettingsManager.isDarkThemeEnabled(this) ? Color.rgb(48, 58, 86) : Color.WHITE;
    }

    private boolean isCompactScreen() {
        float density = getResources().getDisplayMetrics().density;
        float heightDp = getResources().getDisplayMetrics().heightPixels / density;
        return heightDp < 780f;
    }
}

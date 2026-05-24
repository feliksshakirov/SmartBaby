package ru.smartbaby.games;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ru.smartbaby.AppSettingsManager;
import ru.smartbaby.SoundManager;
import ru.smartbaby.StatsManager;
import ru.smartbaby.Ui;

public class ColorsGameView extends LinearLayout {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final TextView taskView;
    private final TextView feedbackView;
    private final LinearLayout choicesLayout;
    private final List<ColorOption> allColors = new ArrayList<>();
    private final List<ColorCard> currentCards = new ArrayList<>();
    private ColorOption targetColor;
    private boolean waitingForNextRound;

    private final Runnable nextRoundRunnable = new Runnable() {
        @Override
        public void run() {
            startNewRound();
        }
    };

    public ColorsGameView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setPadding(Ui.dp(context, 18), Ui.dp(context, 104), Ui.dp(context, 18), Ui.dp(context, 20));
        setBackground(Ui.verticalGradient(Color.rgb(247, 250, 255), Color.rgb(235, 255, 239), context));

        fillColorList();

        taskView = createTaskView(context);
        feedbackView = createFeedbackView(context);
        choicesLayout = new LinearLayout(context);
        choicesLayout.setOrientation(VERTICAL);
        choicesLayout.setGravity(Gravity.CENTER);

        addView(taskView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout.LayoutParams feedbackParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        feedbackParams.topMargin = Ui.dp(context, 8);
        addView(feedbackView, feedbackParams);

        LinearLayout.LayoutParams choicesParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        choicesParams.topMargin = Ui.dp(context, 16);
        addView(choicesLayout, choicesParams);

        startNewRound();
    }

    private void fillColorList() {
        allColors.add(new ColorOption("красный", Color.rgb(238, 72, 88)));
        allColors.add(new ColorOption("синий", Color.rgb(58, 132, 238)));
        allColors.add(new ColorOption("жёлтый", Color.rgb(255, 205, 66)));
        allColors.add(new ColorOption("зелёный", Color.rgb(74, 186, 95)));
        allColors.add(new ColorOption("оранжевый", Color.rgb(255, 145, 61)));
        allColors.add(new ColorOption("фиолетовый", Color.rgb(143, 91, 224)));
    }

    private TextView createTaskView(Context context) {
        TextView view = new TextView(context);
        view.setTextColor(Color.rgb(45, 55, 88));
        view.setTextSize(getAgeTextSize(34f, 32f, 30f));
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    private TextView createFeedbackView(Context context) {
        TextView view = new TextView(context);
        view.setTextColor(Color.rgb(82, 93, 122));
        view.setTextSize(getAgeTextSize(25f, 23f, 22f));
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setGravity(Gravity.CENTER);
        view.setMinHeight(Ui.dp(context, 42));
        return view;
    }

    private void startNewRound() {
        waitingForNextRound = false;
        feedbackView.setText("");
        feedbackView.setTextColor(Color.rgb(82, 93, 122));
        choicesLayout.removeAllViews();
        currentCards.clear();

        targetColor = allColors.get(random.nextInt(allColors.size()));
        taskView.setText("Нажми на " + targetColor.name);

        List<ColorOption> choices = buildChoices();
        addChoiceRows(choices);
    }

    private List<ColorOption> buildChoices() {
        int count = 4;
        List<ColorOption> pool = new ArrayList<>(allColors);
        pool.remove(targetColor);
        Collections.shuffle(pool, random);

        List<ColorOption> choices = new ArrayList<>();
        choices.add(targetColor);
        for (int i = 0; i < count - 1; i++) {
            choices.add(pool.get(i));
        }
        Collections.shuffle(choices, random);
        return choices;
    }

    private void addChoiceRows(List<ColorOption> choices) {
        int index = 0;
        int rowCount = choices.size() <= 2 ? 1 : 2;
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(HORIZONTAL);
            row.setGravity(Gravity.CENTER);
            int itemsInRow = choices.size() == 3 && rowIndex == 1 ? 1 : Math.min(2, choices.size() - index);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
            );
            rowParams.topMargin = rowIndex == 0 ? 0 : Ui.dp(getContext(), 14);
            choicesLayout.addView(row, rowParams);

            for (int i = 0; i < itemsInRow; i++) {
                ColorOption option = choices.get(index++);
                ColorCard colorCard = createColorCard(option);
                currentCards.add(colorCard);
                row.addView(colorCard.view, createCardLayoutParams());
            }
        }
    }

    private LinearLayout.LayoutParams createCardLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
        );
        params.leftMargin = Ui.dp(getContext(), 8);
        params.rightMargin = Ui.dp(getContext(), 8);
        return params;
    }

    private ColorCard createColorCard(final ColorOption option) {
        final TextView card = new TextView(getContext());
        card.setGravity(Gravity.CENTER);
        card.setText("");
        card.setMinHeight(Ui.dp(getContext(), getAgeCardHeightDp()));
        card.setBackground(Ui.roundedWithStroke(option.color, Color.argb(45, 40, 58, 90), 34f, getContext()));
        card.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAnswer(option, card);
            }
        });
        return new ColorCard(option, card);
    }

    private void handleAnswer(ColorOption selected, TextView card) {
        if (waitingForNextRound) {
            return;
        }

        StatsManager.recordTouch(getContext());
        if (selected == targetColor) {
            waitingForNextRound = true;
            StatsManager.recordColorsCorrect(getContext());
            SoundManager.playCorrect(getContext());
            feedbackView.setText("★ Молодец! ★");
            feedbackView.setTextColor(Color.rgb(52, 158, 88));
            card.setBackground(Ui.roundedWithStroke(selected.color, Color.rgb(255, 255, 255), 34f, getContext()));
            card.animate()
                    .alpha(0.9f)
                    .setDuration(getAgeAnimationDuration() / 2L)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            card.animate().alpha(1f).setDuration(getAgeAnimationDuration() / 2L).start();
                            handler.postDelayed(nextRoundRunnable, 700L);
                        }
                    })
                    .start();
        } else {
            StatsManager.recordColorsWrong(getContext());
            SoundManager.playTryAgain(getContext());
            feedbackView.setText("Попробуй ещё раз");
            feedbackView.setTextColor(Color.rgb(238, 125, 70));
            shakeCard(card);
            highlightCorrectColor();
        }
    }

    private void highlightCorrectColor() {
        for (int i = 0; i < currentCards.size(); i++) {
            final ColorCard card = currentCards.get(i);
            if (card.option == targetColor) {
                card.view.setBackground(Ui.roundedWithStroke(card.option.color, Color.WHITE, 34f, getContext()));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!waitingForNextRound) {
                            card.view.setBackground(Ui.roundedWithStroke(card.option.color, Color.argb(45, 40, 58, 90), 34f, getContext()));
                        }
                    }
                }, 500L);
                return;
            }
        }
    }

    private void shakeCard(final View card) {
        float distance = Ui.dp(getContext(), 10);
        card.animate().translationX(distance).setDuration(80L).withEndAction(new Runnable() {
            @Override
            public void run() {
                card.animate().translationX(0f).setDuration(110L).start();
            }
        }).start();
    }

    private float getAgeTextSize(float onePlus, float twoPlus, float threePlus) {
        int age = AppSettingsManager.getSelectedAgeGroup(getContext());
        if (age == 1) {
            return onePlus;
        }
        if (age == 2) {
            return twoPlus;
        }
        return threePlus;
    }

    private int getAgeCardHeightDp() {
        int age = AppSettingsManager.getSelectedAgeGroup(getContext());
        if (age == 1) {
            return 150;
        }
        if (age == 2) {
            return 132;
        }
        return 118;
    }

    private long getAgeAnimationDuration() {
        float multiplier = AppSettingsManager.getAnimationSpeedMultiplier(getContext());
        return (long) (260L / Math.max(0.5f, multiplier));
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    private static class ColorOption {
        final String name;
        final int color;

        ColorOption(String name, int color) {
            this.name = name;
            this.color = color;
        }
    }

    private static class ColorCard {
        final ColorOption option;
        final TextView view;

        ColorCard(ColorOption option, TextView view) {
            this.option = option;
            this.view = view;
        }
    }
}

package ru.smartbaby.games;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
import ru.smartbaby.StatsManager;
import ru.smartbaby.Ui;

public class FindAnimalGameView extends LinearLayout {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final TextView taskView;
    private final TextView feedbackView;
    private final LinearLayout choicesLayout;
    private final List<AnimalOption> allAnimals = new ArrayList<>();
    private AnimalOption targetAnimal;
    private boolean waitingForNextRound;

    private final Runnable nextRoundRunnable = new Runnable() {
        @Override
        public void run() {
            startNewRound();
        }
    };

    public FindAnimalGameView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setPadding(Ui.dp(context, 18), Ui.dp(context, 104), Ui.dp(context, 18), Ui.dp(context, 20));
        setBackground(Ui.verticalGradient(Color.rgb(255, 249, 229), Color.rgb(225, 247, 255), context));

        fillAnimalList();

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
        choicesParams.topMargin = Ui.dp(context, 14);
        addView(choicesLayout, choicesParams);

        startNewRound();
    }

    private void fillAnimalList() {
        allAnimals.add(new AnimalOption(AnimalArt.Kind.CAT));
        allAnimals.add(new AnimalOption(AnimalArt.Kind.DOG));
        allAnimals.add(new AnimalOption(AnimalArt.Kind.COW));
        allAnimals.add(new AnimalOption(AnimalArt.Kind.DUCK));
        allAnimals.add(new AnimalOption(AnimalArt.Kind.LION));
        allAnimals.add(new AnimalOption(AnimalArt.Kind.SHEEP));
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

        targetAnimal = allAnimals.get(random.nextInt(allAnimals.size()));
        taskView.setText("Найди " + targetAnimal.findText);

        List<AnimalOption> choices = buildChoices();
        addChoiceRows(choices);
    }

    private List<AnimalOption> buildChoices() {
        int count = 2;
        List<AnimalOption> pool = new ArrayList<>(allAnimals);
        pool.remove(targetAnimal);
        Collections.shuffle(pool, random);

        List<AnimalOption> choices = new ArrayList<>();
        choices.add(targetAnimal);
        for (int i = 0; i < count - 1; i++) {
            choices.add(pool.get(i));
        }
        Collections.shuffle(choices, random);
        return choices;
    }

    private void addChoiceRows(List<AnimalOption> choices) {
        for (int i = 0; i < choices.size(); i++) {
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
            );
            rowParams.topMargin = i == 0 ? 0 : Ui.dp(getContext(), 12);
            rowParams.leftMargin = Ui.dp(getContext(), 26);
            rowParams.rightMargin = Ui.dp(getContext(), 26);
            choicesLayout.addView(createAnimalCard(choices.get(i)), rowParams);
        }
    }

    private AnimalCardView createAnimalCard(final AnimalOption option) {
        final AnimalCardView card = new AnimalCardView(getContext(), option);
        card.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAnswer(option, card);
            }
        });
        return card;
    }

    private void handleAnswer(AnimalOption selected, AnimalCardView card) {
        if (waitingForNextRound) {
            return;
        }

        StatsManager.recordTouch(getContext());
        AnimalArt.playSound(getContext(), selected.kind);
        if (selected == targetAnimal) {
            waitingForNextRound = true;
            StatsManager.recordFindAnimalCorrect(getContext());
            feedbackView.setText("Молодец!");
            feedbackView.setTextColor(Color.rgb(52, 158, 88));
            card.setBorderColor(Color.rgb(70, 190, 105));
            card.animate()
                    .scaleX(1.04f)
                    .scaleY(1f)
                    .setDuration(getAgeAnimationDuration())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            handler.postDelayed(nextRoundRunnable, 700L);
                        }
                    })
                    .start();
        } else {
            StatsManager.recordFindAnimalWrong(getContext());
            feedbackView.setText("Попробуй еще раз");
            feedbackView.setTextColor(Color.rgb(238, 125, 70));
            card.setBorderColor(Color.rgb(255, 161, 121));
            shakeCard(card);
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

    private long getAgeAnimationDuration() {
        float multiplier = AppSettingsManager.getAnimationSpeedMultiplier(getContext());
        return (long) (260L / Math.max(0.5f, multiplier));
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(nextRoundRunnable);
        super.onDetachedFromWindow();
    }

    private static class AnimalOption {
        final AnimalArt.Kind kind;
        final String name;
        final String findText;
        final int cardColor;

        AnimalOption(AnimalArt.Kind kind) {
            this.kind = kind;
            this.name = AnimalArt.getName(kind);
            this.findText = AnimalArt.getFindText(kind);
            this.cardColor = AnimalArt.getCardColor(kind);
        }
    }

    private static class AnimalCardView extends View {
        private final AnimalOption option;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private final RectF rect = new RectF();
        private int borderColor = Color.TRANSPARENT;

        AnimalCardView(Context context, AnimalOption option) {
            super(context);
            this.option = option;
            setClickable(true);
            setPadding(0, 0, 0, 0);
        }

        void setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float inset = Ui.dp(getContext(), 4);
            rect.set(inset, inset, getWidth() - inset, getHeight() - inset);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(option.cardColor);
            canvas.drawRoundRect(rect, Ui.dp(getContext(), 26), Ui.dp(getContext(), 26), paint);

            if (Color.alpha(borderColor) > 0) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(Ui.dp(getContext(), 4));
                paint.setColor(borderColor);
                canvas.drawRoundRect(rect, Ui.dp(getContext(), 26), Ui.dp(getContext(), 26), paint);
            }

            paint.setStyle(Paint.Style.FILL);
            float size = Math.min(getWidth(), getHeight()) * 0.44f;
            AnimalArt.draw(canvas, paint, path, option.kind, getWidth() / 2f, getHeight() / 2f, size);
        }
    }
}

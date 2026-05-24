package ru.smartbaby.games;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import ru.smartbaby.StatsManager;
import ru.smartbaby.Ui;

public class AnimalsView extends View {
    private static final long BUBBLE_LIFE_MS = 950L;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bubbleTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final RectF bubbleRect = new RectF();
    private final Animal[] animals = {
            new Animal(AnimalArt.Kind.CAT),
            new Animal(AnimalArt.Kind.DOG),
            new Animal(AnimalArt.Kind.COW),
            new Animal(AnimalArt.Kind.DUCK),
            new Animal(AnimalArt.Kind.LION),
            new Animal(AnimalArt.Kind.SHEEP)
    };

    private String bubbleText = "";
    private int bubbleColor = Color.WHITE;
    private long bubbleStartedAt;

    public AnimalsView(Context context) {
        super(context);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        bubbleTextPaint.setTextAlign(Paint.Align.CENTER);
        bubbleTextPaint.setFakeBoldText(true);
        bubbleTextPaint.setTextSize(Ui.dp(context, 28));
        setFocusable(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutAnimals(w, h);
    }

    private void layoutAnimals(int w, int h) {
        float sidePadding = Ui.dp(getContext(), 18);
        float topPadding = Ui.dp(getContext(), 92);
        float bottomPadding = Ui.dp(getContext(), 18);
        float gap = Ui.dp(getContext(), 12);
        float cardW = (w - sidePadding * 2f - gap) / 2f;
        float cardH = (h - topPadding - bottomPadding - gap * 2f) / 3f;

        for (int i = 0; i < animals.length; i++) {
            int col = i % 2;
            int row = i / 2;
            float left = sidePadding + col * (cardW + gap);
            float top = topPadding + row * (cardH + gap);
            animals[i].bounds.set(left, top, left + cardW, top + cardH);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(242, 255, 238));

        boolean needsMoreFrames = false;
        long now = SystemClock.uptimeMillis();
        for (Animal animal : animals) {
            float pulse = Math.max(0f, 1f - (now - animal.pulseStartedAt) / 260f);
            if (pulse > 0f) {
                needsMoreFrames = true;
            }
            drawCard(canvas, animal, pulse);
        }

        float bubbleProgress = Math.max(0f, 1f - (now - bubbleStartedAt) / (float) BUBBLE_LIFE_MS);
        if (bubbleProgress > 0f && bubbleText.length() > 0) {
            drawBubble(canvas, bubbleProgress);
            needsMoreFrames = true;
        }

        if (needsMoreFrames) {
            postInvalidateOnAnimation();
        }
    }

    private void drawBubble(Canvas canvas, float progress) {
        bubbleRect.set(
                Ui.dp(getContext(), 24),
                Ui.dp(getContext(), 18),
                getWidth() - Ui.dp(getContext(), 24),
                Ui.dp(getContext(), 78)
        );
        paint.setColor(Color.argb((int) (230f * progress), Color.red(bubbleColor), Color.green(bubbleColor), Color.blue(bubbleColor)));
        canvas.drawRoundRect(bubbleRect, Ui.dp(getContext(), 18), Ui.dp(getContext(), 18), paint);

        bubbleTextPaint.setColor(Color.argb((int) (255f * progress), 58, 66, 90));
        float textY = bubbleRect.centerY() - (bubbleTextPaint.descent() + bubbleTextPaint.ascent()) / 2f;
        canvas.drawText(bubbleText, bubbleRect.centerX(), textY, bubbleTextPaint);
    }

    private void drawCard(Canvas canvas, Animal animal, float pulse) {
        RectF b = animal.bounds;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(animal.cardColor);
        canvas.drawRoundRect(b, Ui.dp(getContext(), 22), Ui.dp(getContext(), 22), paint);

        float cx = b.centerX();
        float cy = b.top + b.height() * 0.43f;
        float size = Math.min(b.width(), b.height()) * (0.4f + pulse * 0.05f);

        canvas.save();
        canvas.scale(1f + pulse * 0.08f, 1f + pulse * 0.08f, cx, cy);
        AnimalArt.draw(canvas, paint, path, animal.kind, cx, cy, size);
        canvas.restore();

        textPaint.setColor(Color.rgb(50, 60, 88));
        textPaint.setTextSize(Math.min(Ui.dp(getContext(), 23), b.height() * 0.13f));
        float textY = b.bottom - Ui.dp(getContext(), 20);
        canvas.drawText(animal.name, cx, textY, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        float x = event.getX();
        float y = event.getY();
        StatsManager.recordTouch(getContext());
        for (Animal animal : animals) {
            if (animal.bounds.contains(x, y)) {
                animal.pulseStartedAt = SystemClock.uptimeMillis();
                bubbleText = animal.soundText;
                bubbleColor = animal.cardColor;
                bubbleStartedAt = SystemClock.uptimeMillis();
                AnimalArt.playSound(getContext(), animal.kind);
                invalidate();
                return true;
            }
        }
        return true;
    }

    private static class Animal {
        final AnimalArt.Kind kind;
        final String name;
        final String soundText;
        final int cardColor;
        final RectF bounds = new RectF();
        long pulseStartedAt;

        Animal(AnimalArt.Kind kind) {
            this.kind = kind;
            this.name = AnimalArt.getName(kind);
            this.soundText = AnimalArt.getSoundText(kind);
            this.cardColor = AnimalArt.getCardColor(kind);
        }
    }
}

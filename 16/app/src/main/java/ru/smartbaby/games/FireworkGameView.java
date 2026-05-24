package ru.smartbaby.games;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ru.smartbaby.SoundManager;
import ru.smartbaby.StatsManager;
import ru.smartbaby.Ui;

public class FireworkGameView extends View {
    private static final long FIREWORK_LIFE_MS = 1380L;

    private final List<Firework> fireworks = new ArrayList<>();
    private final Random random = new Random();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] colors = {
            Color.rgb(255, 88, 122),
            Color.rgb(255, 214, 92),
            Color.rgb(91, 211, 255),
            Color.rgb(116, 236, 154),
            Color.rgb(255, 170, 74)
    };
    private float[] starX = new float[0];
    private float[] starY = new float[0];
    private float[] starRadius = new float[0];
    private long lastAddedAt;
    private long lastSoundAt;

    public FireworkGameView(Context context) {
        super(context);
        setFocusable(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int count = 70;
        starX = new float[count];
        starY = new float[count];
        starRadius = new float[count];
        for (int i = 0; i < count; i++) {
            starX[i] = random.nextFloat() * Math.max(1, w);
            starY[i] = random.nextFloat() * Math.max(1, h);
            starRadius[i] = Ui.dp(getContext(), 1 + random.nextInt(3));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawNight(canvas);

        long now = SystemClock.uptimeMillis();
        Iterator<Firework> iterator = fireworks.iterator();
        while (iterator.hasNext()) {
            Firework firework = iterator.next();
            float age = (now - firework.createdAt) / (float) FIREWORK_LIFE_MS;
            if (age >= 1f) {
                iterator.remove();
                continue;
            }
            drawFirework(canvas, firework, age);
        }

        if (!fireworks.isEmpty()) {
            postInvalidateOnAnimation();
        }
    }

    private void drawNight(Canvas canvas) {
        canvas.drawColor(Color.rgb(21, 26, 56));
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < starX.length; i++) {
            paint.setColor(Color.argb(155, 255, 255, 230));
            canvas.drawCircle(starX[i], starY[i], starRadius[i], paint);
        }
    }

    private void drawFirework(Canvas canvas, Firework firework, float age) {
        float eased = 1f - (1f - age) * (1f - age);
        int alpha = (int) (255f * (1f - age));
        paint.setStrokeWidth(Ui.dp(getContext(), 4));
        paint.setStrokeCap(Paint.Cap.ROUND);

        for (int i = 0; i < firework.angles.length; i++) {
            float distance = firework.speeds[i] * eased;
            float x = firework.x + (float) Math.cos(firework.angles[i]) * distance;
            float y = firework.y + (float) Math.sin(firework.angles[i]) * distance + Ui.dp(getContext(), 80) * age * age;
            float previousDistance = firework.speeds[i] * Math.max(0f, eased - 0.18f);
            float previousX = firework.x + (float) Math.cos(firework.angles[i]) * previousDistance;
            float previousY = firework.y + (float) Math.sin(firework.angles[i]) * previousDistance + Ui.dp(getContext(), 80) * age * age;
            int color = firework.colors[i];

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.argb(alpha / 2, Color.red(color), Color.green(color), Color.blue(color)));
            canvas.drawLine(previousX, previousY, x, y, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
            canvas.drawCircle(x, y, firework.sizes[i] * (1f - age * 0.35f), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            int index = event.getActionIndex();
            StatsManager.recordTouch(getContext());
            addFirework(event.getX(index), event.getY(index), true);
            return true;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            long now = SystemClock.uptimeMillis();
            if (now - lastAddedAt > 140L && event.getPointerCount() > 0) {
                for (int i = 0; i < event.getPointerCount(); i++) {
                    addFirework(event.getX(i), event.getY(i), false);
                }
            }
            return true;
        }
        return true;
    }

    private void addFirework(float x, float y, boolean withSound) {
        fireworks.add(new Firework(x, y, random, colors, getContext()));
        lastAddedAt = SystemClock.uptimeMillis();
        if (withSound) {
            long now = SystemClock.uptimeMillis();
            if (now - lastSoundAt > 140L) {
                lastSoundAt = now;
                SoundManager.playFirework(getContext());
            }
        }
        invalidate();
    }

    private static class Firework {
        final float x;
        final float y;
        final long createdAt;
        final float[] angles = new float[56];
        final float[] speeds = new float[56];
        final float[] sizes = new float[56];
        final int[] colors = new int[56];

        Firework(float x, float y, Random random, int[] palette, Context context) {
            this.x = x;
            this.y = y;
            this.createdAt = SystemClock.uptimeMillis();
            for (int i = 0; i < angles.length; i++) {
                angles[i] = (float) (random.nextFloat() * Math.PI * 2f);
                speeds[i] = Ui.dp(context, 95 + random.nextInt(170));
                sizes[i] = Ui.dp(context, 4 + random.nextInt(5));
                colors[i] = palette[random.nextInt(palette.length)];
            }
        }
    }
}

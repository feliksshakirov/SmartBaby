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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ru.smartbaby.AppSettingsManager;
import ru.smartbaby.SoundManager;
import ru.smartbaby.StatsManager;
import ru.smartbaby.Ui;

public class BalloonGameView extends View {
    private static final long BURST_LIFE_MS = 520L;
    private static final long LABEL_LIFE_MS = 620L;

    private final List<Balloon> balloons = new ArrayList<>();
    private final List<Burst> bursts = new ArrayList<>();
    private final List<PopLabel> labels = new ArrayList<>();
    private final Random random = new Random();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();
    private final Path trianglePath = new Path();
    private final int[] colors = {
            Color.rgb(255, 92, 114),
            Color.rgb(255, 193, 64),
            Color.rgb(72, 196, 246),
            Color.rgb(97, 213, 127),
            Color.rgb(149, 105, 255)
    };

    private long lastFrameAt;
    private long lastSpawnAt;
    private int successfulPops;

    public BalloonGameView(Context context) {
        super(context);
        stringPaint.setColor(Color.argb(140, 54, 72, 96));
        stringPaint.setStrokeWidth(Ui.dp(context, 2));
        stringPaint.setStyle(Paint.Style.STROKE);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setFakeBoldText(true);
        labelPaint.setTextSize(Ui.dp(context, 26));
        setFocusable(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        balloons.clear();
        for (int i = 0; i < getTargetBalloonCount(); i++) {
            spawnBalloon(true);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);

        long now = SystemClock.uptimeMillis();
        if (lastFrameAt == 0L) {
            lastFrameAt = now;
        }
        float dt = Math.min(0.04f, (now - lastFrameAt) / 1000f);
        lastFrameAt = now;

        if (balloons.size() < getTargetBalloonCount() && now - lastSpawnAt > 420L) {
            spawnBalloon(false);
            lastSpawnAt = now;
        }

        for (Balloon balloon : balloons) {
            balloon.y -= balloon.speed * dt;
            balloon.swing += dt * 2.3f;
            balloon.x += Math.sin(balloon.swing) * balloon.drift * dt;
            if (balloon.y + balloon.radius < -Ui.dp(getContext(), 40)) {
                resetBalloon(balloon, false);
            }
            drawBalloon(canvas, balloon);
        }

        drawBursts(canvas, now);
        drawLabels(canvas, now);
        postInvalidateOnAnimation();
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.rgb(225, 246, 255));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255, 255, 255));
        canvas.drawCircle(getWidth() * 0.16f, getHeight() * 0.14f, Ui.dp(getContext(), 52), paint);
        canvas.drawCircle(getWidth() * 0.52f, getHeight() * 0.08f, Ui.dp(getContext(), 38), paint);
        canvas.drawCircle(getWidth() * 0.86f, getHeight() * 0.2f, Ui.dp(getContext(), 58), paint);
    }

    private void drawBalloon(Canvas canvas, Balloon balloon) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(balloon.color);
        oval.set(
                balloon.x - balloon.radius * 0.8f,
                balloon.y - balloon.radius,
                balloon.x + balloon.radius * 0.8f,
                balloon.y + balloon.radius
        );
        canvas.drawOval(oval, paint);

        paint.setColor(Ui.darker(balloon.color));
        trianglePath.reset();
        trianglePath.moveTo(balloon.x - balloon.radius * 0.16f, balloon.y + balloon.radius * 0.9f);
        trianglePath.lineTo(balloon.x + balloon.radius * 0.16f, balloon.y + balloon.radius * 0.9f);
        trianglePath.lineTo(balloon.x, balloon.y + balloon.radius * 1.12f);
        trianglePath.close();
        canvas.drawPath(trianglePath, paint);

        paint.setColor(Color.argb(120, 255, 255, 255));
        canvas.drawOval(
                balloon.x - balloon.radius * 0.44f,
                balloon.y - balloon.radius * 0.65f,
                balloon.x - balloon.radius * 0.12f,
                balloon.y - balloon.radius * 0.24f,
                paint
        );

        canvas.drawLine(
                balloon.x,
                balloon.y + balloon.radius * 1.02f,
                balloon.x + (float) Math.sin(balloon.swing) * balloon.radius * 0.35f,
                balloon.y + balloon.radius * 1.9f,
                stringPaint
        );
    }

    private void drawBursts(Canvas canvas, long now) {
        Iterator<Burst> iterator = bursts.iterator();
        while (iterator.hasNext()) {
            Burst burst = iterator.next();
            float age = (now - burst.createdAt) / (float) BURST_LIFE_MS;
            if (age >= 1f) {
                iterator.remove();
                continue;
            }
            int alpha = (int) (255f * (1f - age));
            paint.setStyle(Paint.Style.FILL);
            for (int i = 0; i < burst.angles.length; i++) {
                float distance = burst.speeds[i] * age;
                float x = burst.x + (float) Math.cos(burst.angles[i]) * distance;
                float y = burst.y + (float) Math.sin(burst.angles[i]) * distance;
                paint.setColor(Color.argb(alpha, Color.red(burst.color), Color.green(burst.color), Color.blue(burst.color)));
                canvas.drawCircle(x, y, Ui.dp(getContext(), 4) * (1f - age * 0.45f), paint);
            }
        }
    }

    private void drawLabels(Canvas canvas, long now) {
        Iterator<PopLabel> iterator = labels.iterator();
        while (iterator.hasNext()) {
            PopLabel label = iterator.next();
            float age = (now - label.createdAt) / (float) LABEL_LIFE_MS;
            if (age >= 1f) {
                iterator.remove();
                continue;
            }
            labelPaint.setColor(Color.argb((int) (255f * (1f - age)), 61, 72, 98));
            canvas.drawText("Бум!", label.x, label.y - Ui.dp(getContext(), 22) * age, labelPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            int index = event.getActionIndex();
            StatsManager.recordTouch(getContext());
            handleTap(event.getX(index), event.getY(index));
        }
        return true;
    }

    private void handleTap(float x, float y) {
        for (int i = balloons.size() - 1; i >= 0; i--) {
            Balloon balloon = balloons.get(i);
            if (balloon.contains(x, y)) {
                balloons.remove(i);
                bursts.add(new Burst(balloon.x, balloon.y, balloon.color, random));
                labels.add(new PopLabel(balloon.x, balloon.y, SystemClock.uptimeMillis()));
                successfulPops++;
                StatsManager.recordBalloonPop(getContext());
                SoundManager.playPop(getContext());
                spawnBalloon(false);
                invalidate();
                return;
            }
        }
    }

    private int getTargetBalloonCount() {
        int age = AppSettingsManager.getAgeLevel(getContext());
        if (age == 1) {
            return 4;
        }
        if (age == 2) {
            return 5;
        }
        return 6;
    }

    private void spawnBalloon(boolean anywhere) {
        Balloon balloon = new Balloon();
        resetBalloon(balloon, anywhere);
        balloons.add(balloon);
    }

    private void resetBalloon(Balloon balloon, boolean anywhere) {
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        int age = AppSettingsManager.getAgeLevel(getContext());
        int baseDp = age == 1 ? 76 : age == 2 ? 68 : 60;
        int minDp = age == 1 ? 60 : age == 2 ? 52 : 46;
        int decrease = Math.min(successfulPops / 4, baseDp - minDp);
        int radiusDp = Math.max(minDp, baseDp - decrease + random.nextInt(6));

        balloon.radius = Ui.dp(getContext(), radiusDp);
        balloon.x = balloon.radius + random.nextInt(Math.max(1, (int) (w - balloon.radius * 2)));
        balloon.y = anywhere
                ? Ui.dp(getContext(), 110) + random.nextInt(Math.max(1, h - Ui.dp(getContext(), 140)))
                : h + balloon.radius + random.nextInt(Ui.dp(getContext(), 120));
        balloon.speed = Ui.dp(getContext(), 38 + random.nextInt(40)) * 1.5f
                * AppSettingsManager.getAnimationSpeedMultiplier(getContext());
        balloon.drift = Ui.dp(getContext(), 16 + random.nextInt(20));
        balloon.swing = random.nextFloat() * 6.28f;
        balloon.color = colors[random.nextInt(colors.length)];
    }

    private static class Balloon {
        float x;
        float y;
        float radius;
        float speed;
        float drift;
        float swing;
        int color;

        boolean contains(float touchX, float touchY) {
            float dx = (touchX - x) / (radius * 0.85f);
            float dy = (touchY - y) / radius;
            return dx * dx + dy * dy <= 1f;
        }
    }

    private static class Burst {
        final float x;
        final float y;
        final int color;
        final long createdAt;
        final float[] angles = new float[16];
        final float[] speeds = new float[16];

        Burst(float x, float y, int color, Random random) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.createdAt = SystemClock.uptimeMillis();
            for (int i = 0; i < angles.length; i++) {
                angles[i] = (float) (random.nextFloat() * Math.PI * 2f);
                speeds[i] = 80f + random.nextFloat() * 170f;
            }
        }
    }

    private static class PopLabel {
        final float x;
        final float y;
        final long createdAt;

        PopLabel(float x, float y, long createdAt) {
            this.x = x;
            this.y = y;
            this.createdAt = createdAt;
        }
    }
}

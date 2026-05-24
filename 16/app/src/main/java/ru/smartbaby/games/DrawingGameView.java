package ru.smartbaby.games;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ru.smartbaby.SoundManager;
import ru.smartbaby.StatsManager;
import ru.smartbaby.Ui;

public class DrawingGameView extends View {
    private static final long STROKE_LIFE_MS = 2200L;
    private static final long SPARKLE_LIFE_MS = 650L;

    private final SparseArray<Stroke> activeStrokes = new SparseArray<>();
    private final List<Stroke> strokes = new ArrayList<>();
    private final List<Sparkle> sparkles = new ArrayList<>();
    private final Random random = new Random();
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sparklePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] colors = {
            Color.rgb(255, 96, 128),
            Color.rgb(90, 190, 255),
            Color.rgb(255, 196, 70),
            Color.rgb(103, 214, 140),
            Color.rgb(149, 122, 255)
    };
    private long lastSoundAt;

    public DrawingGameView(Context context) {
        super(context);
        backgroundPaint.setStyle(Paint.Style.FILL);
        sparklePaint.setStyle(Paint.Style.FILL);
        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);

        long now = SystemClock.uptimeMillis();
        boolean needsMoreFrames = false;

        Iterator<Stroke> strokeIterator = strokes.iterator();
        while (strokeIterator.hasNext()) {
            Stroke stroke = strokeIterator.next();
            if (!stroke.active) {
                long age = now - stroke.finishedAt;
                if (age >= STROKE_LIFE_MS) {
                    strokeIterator.remove();
                    continue;
                }
                stroke.paint.setAlpha((int) (255f * (1f - age / (float) STROKE_LIFE_MS)));
                needsMoreFrames = true;
            } else {
                stroke.paint.setAlpha(255);
                needsMoreFrames = true;
            }
            canvas.drawPath(stroke.path, stroke.paint);
        }

        Iterator<Sparkle> sparkleIterator = sparkles.iterator();
        while (sparkleIterator.hasNext()) {
            Sparkle sparkle = sparkleIterator.next();
            long age = now - sparkle.createdAt;
            if (age >= SPARKLE_LIFE_MS) {
                sparkleIterator.remove();
                continue;
            }
            float progress = age / (float) SPARKLE_LIFE_MS;
            sparklePaint.setColor(Color.argb(
                    (int) (255f * (1f - progress)),
                    Color.red(sparkle.color),
                    Color.green(sparkle.color),
                    Color.blue(sparkle.color)
            ));
            float radius = sparkle.radius * (1f + progress * 0.35f);
            canvas.drawCircle(sparkle.x, sparkle.y, radius, sparklePaint);
            needsMoreFrames = true;
        }

        if (needsMoreFrames) {
            postInvalidateOnAnimation();
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.rgb(255, 250, 236));
        backgroundPaint.setColor(Color.rgb(255, 232, 190));
        canvas.drawCircle(getWidth() * 0.08f, getHeight() * 0.18f, Ui.dp(getContext(), 90), backgroundPaint);
        backgroundPaint.setColor(Color.rgb(214, 241, 255));
        canvas.drawCircle(getWidth() * 0.9f, getHeight() * 0.28f, Ui.dp(getContext(), 105), backgroundPaint);
        backgroundPaint.setColor(Color.rgb(227, 245, 207));
        canvas.drawCircle(getWidth() * 0.2f, getHeight() * 0.92f, Ui.dp(getContext(), 125), backgroundPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                handlePointerDown(event.getPointerId(actionIndex), event.getX(actionIndex), event.getY(actionIndex));
                return true;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    handlePointerMove(event.getPointerId(i), event.getX(i), event.getY(i));
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                finishStroke(event.getPointerId(actionIndex));
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                clearActiveStrokes();
                invalidate();
                return true;
            default:
                return true;
        }
    }

    private void handlePointerDown(int pointerId, float x, float y) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(Ui.dp(getContext(), 18));
        paint.setColor(colors[random.nextInt(colors.length)]);

        Path path = new Path();
        path.moveTo(x, y);

        // Every finger owns its own stroke, so the child can draw with multiple touches at once.
        Stroke stroke = new Stroke(path, paint, x, y);
        activeStrokes.put(pointerId, stroke);
        strokes.add(stroke);
        sparkles.add(new Sparkle(x, y, paint.getColor(), Ui.dp(getContext(), 7), SystemClock.uptimeMillis()));
        StatsManager.recordTouch(getContext());
        playSparkleSound();
        invalidate();
    }

    private void handlePointerMove(int pointerId, float x, float y) {
        Stroke stroke = activeStrokes.get(pointerId);
        if (stroke == null) {
            return;
        }

        float middleX = (stroke.lastX + x) * 0.5f;
        float middleY = (stroke.lastY + y) * 0.5f;
        stroke.path.quadTo(stroke.lastX, stroke.lastY, middleX, middleY);
        stroke.lastX = x;
        stroke.lastY = y;

        long now = SystemClock.uptimeMillis();
        if (now - stroke.lastSparkleAt > 70L) {
            stroke.lastSparkleAt = now;
            sparkles.add(new Sparkle(x, y, stroke.paint.getColor(), Ui.dp(getContext(), 5), now));
        }
        invalidate();
    }

    private void finishStroke(int pointerId) {
        Stroke stroke = activeStrokes.get(pointerId);
        if (stroke != null) {
            stroke.active = false;
            stroke.finishedAt = SystemClock.uptimeMillis();
            activeStrokes.remove(pointerId);
        }
    }

    private void clearActiveStrokes() {
        long now = SystemClock.uptimeMillis();
        for (int i = 0; i < activeStrokes.size(); i++) {
            Stroke stroke = activeStrokes.valueAt(i);
            stroke.active = false;
            stroke.finishedAt = now;
        }
        activeStrokes.clear();
    }

    private void playSparkleSound() {
        long now = SystemClock.uptimeMillis();
        if (now - lastSoundAt > 220L) {
            lastSoundAt = now;
            SoundManager.playSparkle(getContext());
        }
    }

    private static class Stroke {
        final Path path;
        final Paint paint;
        boolean active = true;
        float lastX;
        float lastY;
        long lastSparkleAt;
        long finishedAt;

        Stroke(Path path, Paint paint, float lastX, float lastY) {
            this.path = path;
            this.paint = paint;
            this.lastX = lastX;
            this.lastY = lastY;
            this.lastSparkleAt = SystemClock.uptimeMillis();
        }
    }

    private static class Sparkle {
        final float x;
        final float y;
        final int color;
        final float radius;
        final long createdAt;

        Sparkle(float x, float y, int color, float radius, long createdAt) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.radius = radius;
            this.createdAt = createdAt;
        }
    }
}

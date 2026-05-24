package ru.smartbaby.games;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

import ru.smartbaby.SoundManager;

public final class AnimalArt {
    public enum Kind {
        CAT,
        DOG,
        COW,
        DUCK,
        LION,
        SHEEP
    }

    private AnimalArt() {
    }

    public static String getName(Kind kind) {
        switch (kind) {
            case CAT:
                return "Кошка";
            case DOG:
                return "Собака";
            case COW:
                return "Корова";
            case DUCK:
                return "Утка";
            case LION:
                return "Лев";
            case SHEEP:
            default:
                return "Овца";
        }
    }

    public static String getFindText(Kind kind) {
        switch (kind) {
            case CAT:
                return "кошку";
            case DOG:
                return "собаку";
            case COW:
                return "корову";
            case DUCK:
                return "утку";
            case LION:
                return "льва";
            case SHEEP:
            default:
                return "овцу";
        }
    }

    public static String getSoundText(Kind kind) {
        switch (kind) {
            case CAT:
                return "Мяу";
            case DOG:
                return "Гав";
            case COW:
                return "Му";
            case DUCK:
                return "Кря";
            case LION:
                return "Р-р-р";
            case SHEEP:
            default:
                return "Бе-е";
        }
    }

    public static String getEmoji(Kind kind) {
        switch (kind) {
            case CAT:
                return "🐱";
            case DOG:
                return "🐶";
            case COW:
                return "🐮";
            case DUCK:
                return "🦆";
            case LION:
                return "🦁";
            case SHEEP:
            default:
                return "🐑";
        }
    }

    public static int getCardColor(Kind kind) {
        switch (kind) {
            case CAT:
                return Color.rgb(255, 238, 202);
            case DOG:
                return Color.rgb(255, 226, 216);
            case COW:
                return Color.rgb(231, 241, 255);
            case DUCK:
                return Color.rgb(255, 248, 202);
            case LION:
                return Color.rgb(255, 229, 175);
            case SHEEP:
            default:
                return Color.rgb(236, 244, 255);
        }
    }

    public static void playSound(Context context, Kind kind) {
        switch (kind) {
            case CAT:
                SoundManager.playCat(context);
                break;
            case DOG:
                SoundManager.playDog(context);
                break;
            case COW:
                SoundManager.playCow(context);
                break;
            case DUCK:
                SoundManager.playDuck(context);
                break;
            case LION:
                SoundManager.playLion(context);
                break;
            case SHEEP:
                SoundManager.playSheep(context);
                break;
        }
    }

    public static void draw(Canvas canvas, Paint paint, Path path, Kind kind, float cx, float cy, float size) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(size * 1.85f);
        paint.setColor(Color.rgb(45, 55, 88));

        Paint.FontMetrics metrics = paint.getFontMetrics();
        float baseline = cy - (metrics.ascent + metrics.descent) / 2f;
        canvas.drawText(getEmoji(kind), cx, baseline, paint);
    }

    private static void drawCat(Canvas canvas, Paint paint, Path path, float cx, float cy, float s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.rgb(229, 122, 52));
        canvas.drawOval(cx - s * 0.64f, cy + s * 0.18f, cx - s * 0.42f, cy + s * 0.72f, paint);

        paint.setColor(Color.rgb(255, 168, 78));
        drawTriangle(canvas, paint, path, cx - s * 0.55f, cy - s * 0.2f, cx - s * 0.35f, cy - s * 0.78f, cx - s * 0.12f, cy - s * 0.26f);
        drawTriangle(canvas, paint, path, cx + s * 0.55f, cy - s * 0.2f, cx + s * 0.35f, cy - s * 0.78f, cx + s * 0.12f, cy - s * 0.26f);
        paint.setColor(Color.rgb(255, 203, 143));
        drawTriangle(canvas, paint, path, cx - s * 0.38f, cy - s * 0.3f, cx - s * 0.31f, cy - s * 0.58f, cx - s * 0.18f, cy - s * 0.28f);
        drawTriangle(canvas, paint, path, cx + s * 0.38f, cy - s * 0.3f, cx + s * 0.31f, cy - s * 0.58f, cx + s * 0.18f, cy - s * 0.28f);
        paint.setColor(Color.rgb(255, 168, 78));
        canvas.drawCircle(cx, cy, s * 0.58f, paint);

        paint.setColor(Color.rgb(238, 132, 62));
        canvas.drawOval(cx - s * 0.5f, cy - s * 0.3f, cx - s * 0.18f, cy - s * 0.12f, paint);
        canvas.drawOval(cx + s * 0.18f, cy - s * 0.3f, cx + s * 0.5f, cy - s * 0.12f, paint);
        paint.setColor(Color.rgb(255, 202, 136));
        canvas.drawOval(cx - s * 0.34f, cy + s * 0.12f, cx + s * 0.34f, cy + s * 0.44f, paint);

        drawEyes(canvas, paint, cx, cy, s);
        paint.setColor(Color.rgb(255, 112, 140));
        canvas.drawCircle(cx, cy + s * 0.08f, s * 0.06f, paint);
        paint.setColor(Color.rgb(42, 48, 66));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.028f);
        canvas.drawLine(cx - s * 0.05f, cy + s * 0.17f, cx - s * 0.16f, cy + s * 0.27f, paint);
        canvas.drawLine(cx + s * 0.05f, cy + s * 0.17f, cx + s * 0.16f, cy + s * 0.27f, paint);
        canvas.drawLine(cx - s * 0.22f, cy + s * 0.09f, cx - s * 0.58f, cy + s * 0.02f, paint);
        canvas.drawLine(cx - s * 0.22f, cy + s * 0.17f, cx - s * 0.6f, cy + s * 0.18f, paint);
        canvas.drawLine(cx + s * 0.22f, cy + s * 0.09f, cx + s * 0.58f, cy + s * 0.02f, paint);
        canvas.drawLine(cx + s * 0.22f, cy + s * 0.17f, cx + s * 0.6f, cy + s * 0.18f, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private static void drawDog(Canvas canvas, Paint paint, float cx, float cy, float s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(118, 78, 46));
        canvas.drawOval(cx - s * 0.78f, cy - s * 0.62f, cx - s * 0.34f, cy + s * 0.42f, paint);
        canvas.drawOval(cx + s * 0.34f, cy - s * 0.62f, cx + s * 0.78f, cy + s * 0.42f, paint);
        paint.setColor(Color.rgb(93, 60, 39));
        canvas.drawOval(cx - s * 0.66f, cy - s * 0.44f, cx - s * 0.46f, cy + s * 0.22f, paint);
        canvas.drawOval(cx + s * 0.46f, cy - s * 0.44f, cx + s * 0.66f, cy + s * 0.22f, paint);
        paint.setColor(Color.rgb(190, 127, 73));
        canvas.drawCircle(cx, cy, s * 0.58f, paint);
        paint.setColor(Color.rgb(214, 153, 92));
        canvas.drawCircle(cx - s * 0.25f, cy - s * 0.18f, s * 0.2f, paint);
        paint.setColor(Color.rgb(255, 225, 185));
        canvas.drawOval(cx - s * 0.35f, cy + s * 0.02f, cx + s * 0.35f, cy + s * 0.44f, paint);
        drawEyes(canvas, paint, cx, cy, s);
        paint.setColor(Color.rgb(42, 48, 66));
        canvas.drawOval(cx - s * 0.1f, cy + s * 0.11f, cx + s * 0.1f, cy + s * 0.24f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.03f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(cx, cy + s * 0.23f, cx, cy + s * 0.34f, paint);
        canvas.drawLine(cx, cy + s * 0.34f, cx - s * 0.12f, cy + s * 0.39f, paint);
        canvas.drawLine(cx, cy + s * 0.34f, cx + s * 0.12f, cy + s * 0.39f, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255, 114, 136));
        canvas.drawOval(cx + s * 0.1f, cy + s * 0.31f, cx + s * 0.26f, cy + s * 0.52f, paint);
    }

    private static void drawCow(Canvas canvas, Paint paint, Path path, float cx, float cy, float s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(235, 210, 150));
        drawTriangle(canvas, paint, path, cx - s * 0.38f, cy - s * 0.42f, cx - s * 0.24f, cy - s * 0.82f, cx - s * 0.08f, cy - s * 0.4f);
        drawTriangle(canvas, paint, path, cx + s * 0.38f, cy - s * 0.42f, cx + s * 0.24f, cy - s * 0.82f, cx + s * 0.08f, cy - s * 0.4f);
        paint.setColor(Color.rgb(255, 213, 224));
        canvas.drawOval(cx - s * 0.72f, cy - s * 0.3f, cx - s * 0.4f, cy + s * 0.02f, paint);
        canvas.drawOval(cx + s * 0.4f, cy - s * 0.3f, cx + s * 0.72f, cy + s * 0.02f, paint);
        paint.setColor(Color.WHITE);
        canvas.drawOval(cx - s * 0.58f, cy - s * 0.54f, cx + s * 0.58f, cy + s * 0.5f, paint);
        paint.setColor(Color.rgb(54, 60, 78));
        canvas.drawCircle(cx - s * 0.25f, cy - s * 0.16f, s * 0.15f, paint);
        canvas.drawCircle(cx + s * 0.3f, cy - s * 0.03f, s * 0.18f, paint);
        canvas.drawOval(cx - s * 0.5f, cy + s * 0.08f, cx - s * 0.26f, cy + s * 0.34f, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx - s * 0.18f, cy - s * 0.13f, s * 0.045f, paint);
        canvas.drawCircle(cx + s * 0.37f, cy - s * 0.01f, s * 0.045f, paint);
        paint.setColor(Color.rgb(255, 179, 190));
        canvas.drawOval(cx - s * 0.38f, cy + s * 0.16f, cx + s * 0.38f, cy + s * 0.52f, paint);
        paint.setColor(Color.rgb(50, 55, 75));
        canvas.drawCircle(cx - s * 0.14f, cy + s * 0.31f, s * 0.035f, paint);
        canvas.drawCircle(cx + s * 0.14f, cy + s * 0.31f, s * 0.035f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.028f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(cx - s * 0.16f, cy + s * 0.43f, cx - s * 0.25f, cy + s * 0.49f, paint);
        canvas.drawLine(cx + s * 0.16f, cy + s * 0.43f, cx + s * 0.25f, cy + s * 0.49f, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private static void drawDuck(Canvas canvas, Paint paint, Path path, float cx, float cy, float s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255, 214, 67));
        canvas.drawOval(cx - s * 0.62f, cy - s * 0.18f, cx + s * 0.62f, cy + s * 0.48f, paint);
        canvas.drawCircle(cx + s * 0.18f, cy - s * 0.32f, s * 0.36f, paint);
        paint.setColor(Color.rgb(255, 232, 105));
        canvas.drawOval(cx - s * 0.36f, cy - s * 0.06f, cx + s * 0.24f, cy + s * 0.3f, paint);
        paint.setColor(Color.rgb(236, 184, 45));
        canvas.drawOval(cx - s * 0.44f, cy - s * 0.02f, cx + s * 0.08f, cy + s * 0.28f, paint);
        paint.setColor(Color.rgb(255, 126, 54));
        drawTriangle(canvas, paint, path, cx + s * 0.47f, cy - s * 0.32f, cx + s * 0.84f, cy - s * 0.22f, cx + s * 0.47f, cy - s * 0.1f);
        paint.setColor(Color.rgb(229, 89, 42));
        drawTriangle(canvas, paint, path, cx + s * 0.5f, cy - s * 0.18f, cx + s * 0.78f, cy - s * 0.1f, cx + s * 0.5f, cy - s * 0.02f);
        paint.setColor(Color.rgb(42, 48, 66));
        canvas.drawCircle(cx + s * 0.27f, cy - s * 0.43f, s * 0.045f, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx + s * 0.285f, cy - s * 0.455f, s * 0.018f, paint);
        paint.setColor(Color.rgb(255, 126, 54));
        canvas.drawOval(cx - s * 0.24f, cy + s * 0.44f, cx - s * 0.06f, cy + s * 0.56f, paint);
        canvas.drawOval(cx + s * 0.18f, cy + s * 0.44f, cx + s * 0.36f, cy + s * 0.56f, paint);
    }

    private static void drawLion(Canvas canvas, Paint paint, float cx, float cy, float s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(232, 142, 62));
        canvas.drawCircle(cx, cy, s * 0.72f, paint);
        paint.setColor(Color.rgb(190, 104, 47));
        for (int i = 0; i < 12; i++) {
            double angle = Math.PI * 2 * i / 12.0;
            float px = cx + (float) Math.cos(angle) * s * 0.68f;
            float py = cy + (float) Math.sin(angle) * s * 0.68f;
            canvas.drawCircle(px, py, s * 0.2f, paint);
        }
        paint.setColor(Color.rgb(232, 142, 62));
        canvas.drawCircle(cx, cy, s * 0.62f, paint);
        paint.setColor(Color.rgb(255, 202, 104));
        canvas.drawCircle(cx, cy, s * 0.48f, paint);
        paint.setColor(Color.rgb(255, 223, 142));
        canvas.drawOval(cx - s * 0.32f, cy + s * 0.08f, cx + s * 0.32f, cy + s * 0.42f, paint);
        drawEyes(canvas, paint, cx, cy, s);
        paint.setColor(Color.rgb(141, 84, 36));
        canvas.drawOval(cx - s * 0.12f, cy + s * 0.1f, cx + s * 0.12f, cy + s * 0.26f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.03f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(cx, cy + s * 0.24f, cx, cy + s * 0.34f, paint);
        canvas.drawLine(cx, cy + s * 0.34f, cx - s * 0.12f, cy + s * 0.4f, paint);
        canvas.drawLine(cx, cy + s * 0.34f, cx + s * 0.12f, cy + s * 0.4f, paint);
        canvas.drawLine(cx - s * 0.22f, cy + s * 0.17f, cx - s * 0.46f, cy + s * 0.12f, paint);
        canvas.drawLine(cx + s * 0.22f, cy + s * 0.17f, cx + s * 0.46f, cy + s * 0.12f, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private static void drawSheep(Canvas canvas, Paint paint, float cx, float cy, float s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(120, 126, 154));
        canvas.drawOval(cx - s * 0.72f, cy - s * 0.06f, cx - s * 0.46f, cy + s * 0.28f, paint);
        canvas.drawOval(cx + s * 0.46f, cy - s * 0.06f, cx + s * 0.72f, cy + s * 0.28f, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx - s * 0.45f, cy + s * 0.02f, s * 0.25f, paint);
        canvas.drawCircle(cx - s * 0.32f, cy - s * 0.06f, s * 0.28f, paint);
        canvas.drawCircle(cx, cy - s * 0.16f, s * 0.31f, paint);
        canvas.drawCircle(cx + s * 0.32f, cy - s * 0.04f, s * 0.28f, paint);
        canvas.drawCircle(cx + s * 0.48f, cy + s * 0.06f, s * 0.23f, paint);
        canvas.drawCircle(cx - s * 0.14f, cy + s * 0.18f, s * 0.3f, paint);
        canvas.drawCircle(cx + s * 0.18f, cy + s * 0.18f, s * 0.3f, paint);
        paint.setColor(Color.rgb(98, 104, 136));
        canvas.drawOval(cx - s * 0.26f, cy - s * 0.02f, cx + s * 0.26f, cy + s * 0.38f, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx - s * 0.1f, cy + s * 0.14f, s * 0.04f, paint);
        canvas.drawCircle(cx + s * 0.1f, cy + s * 0.14f, s * 0.04f, paint);
        paint.setColor(Color.rgb(48, 54, 78));
        canvas.drawCircle(cx - s * 0.1f, cy + s * 0.15f, s * 0.02f, paint);
        canvas.drawCircle(cx + s * 0.1f, cy + s * 0.15f, s * 0.02f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(s * 0.028f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(cx - s * 0.08f, cy + s * 0.3f, cx + s * 0.08f, cy + s * 0.3f, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private static void drawEyes(Canvas canvas, Paint paint, float cx, float cy, float s) {
        paint.setColor(Color.rgb(42, 48, 66));
        canvas.drawCircle(cx - s * 0.2f, cy - s * 0.08f, s * 0.06f, paint);
        canvas.drawCircle(cx + s * 0.2f, cy - s * 0.08f, s * 0.06f, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx - s * 0.18f, cy - s * 0.1f, s * 0.022f, paint);
        canvas.drawCircle(cx + s * 0.22f, cy - s * 0.1f, s * 0.022f, paint);
    }

    private static void drawTriangle(Canvas canvas, Paint paint, Path path, float x1, float y1, float x2, float y2, float x3, float y3) {
        path.reset();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();
        canvas.drawPath(path, paint);
    }
}

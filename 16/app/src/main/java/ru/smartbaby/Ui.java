package ru.smartbaby;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.TextView;

public final class Ui {
    private Ui() {
    }

    public static int dp(Context context, float value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    public static GradientDrawable rounded(int color, float radiusDp, Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(context, radiusDp));
        return drawable;
    }

    public static GradientDrawable roundedWithStroke(int color, int strokeColor, float radiusDp, Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(context, radiusDp));
        drawable.setStroke(dp(context, 2), strokeColor);
        return drawable;
    }

    public static GradientDrawable roundedWithAlpha(int color, float alpha, float radiusDp, Context context) {
        int transparentColor = Color.argb(
                Math.min(255, Math.max(0, (int) (255f * alpha))),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
        return rounded(transparentColor, radiusDp, context);
    }

    public static GradientDrawable verticalGradient(int topColor, int bottomColor, Context context) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{topColor, bottomColor}
        );
        drawable.setCornerRadius(0f);
        return drawable;
    }

    public static TextView createMenuButton(Context context, String text, int color) {
        TextView button = new TextView(context);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(25f);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dp(context, 96));
        button.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 16));
        button.setBackground(rounded(color, 24f, context));
        return button;
    }

    public static int darker(int color) {
        return Color.rgb(
                Math.max(0, (int) (Color.red(color) * 0.82f)),
                Math.max(0, (int) (Color.green(color) * 0.82f)),
                Math.max(0, (int) (Color.blue(color) * 0.82f))
        );
    }
}

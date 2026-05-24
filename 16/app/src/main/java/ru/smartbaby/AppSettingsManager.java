package ru.smartbaby;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppSettingsManager {
    public static final String PREFS_NAME = "smart_baby_prefs";

    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_DARK_THEME_ENABLED = "dark_theme_enabled";
    private static final String KEY_BREAK_ENABLED = "break_enabled";
    private static final String KEY_AGE_LEVEL = "age_level";
    private static final String KEY_SESSION_MINUTES = "session_minutes";

    private AppSettingsManager() {
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isSoundEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_SOUND_ENABLED, true);
    }

    public static void setSoundEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }

    public static boolean isDarkThemeEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_DARK_THEME_ENABLED, false);
    }

    public static void setDarkThemeEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_DARK_THEME_ENABLED, enabled).apply();
    }

    public static boolean isBreakEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_BREAK_ENABLED, true);
    }

    public static void setBreakEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_BREAK_ENABLED, enabled).apply();
    }

    public static int getAgeLevel(Context context) {
        int value = getPreferences(context).getInt(KEY_AGE_LEVEL, 2);
        if (value < 1 || value > 3) {
            return 2;
        }
        return value;
    }

    public static int getSelectedAgeGroup(Context context) {
        return getAgeLevel(context);
    }

    public static String getAgeGroup(Context context) {
        return getAgeLevel(context) + "+";
    }

    public static int getChoiceCountForAge(Context context) {
        int ageLevel = getAgeLevel(context);
        if (ageLevel == 1) {
            return 2;
        }
        if (ageLevel == 2) {
            return 3;
        }
        return 4;
    }

    public static float getAnimationSpeedMultiplier(Context context) {
        int ageLevel = getAgeLevel(context);
        if (ageLevel == 1) {
            return 0.72f;
        }
        if (ageLevel == 2) {
            return 0.9f;
        }
        return 1.08f;
    }

    public static void setAgeLevel(Context context, int ageLevel) {
        if (ageLevel < 1 || ageLevel > 3) {
            return;
        }
        getPreferences(context).edit().putInt(KEY_AGE_LEVEL, ageLevel).apply();
    }

    public static int getSessionMinutes(Context context) {
        int value = getPreferences(context).getInt(KEY_SESSION_MINUTES, 10);
        if (value != 5 && value != 10 && value != 15) {
            return 10;
        }
        return value;
    }

    public static void setSessionMinutes(Context context, int minutes) {
        if (minutes != 5 && minutes != 10 && minutes != 15) {
            return;
        }
        getPreferences(context).edit().putInt(KEY_SESSION_MINUTES, minutes).apply();
    }
}

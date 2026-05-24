package ru.smartbaby;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionTimerManager {
    private static final String KEY_SESSION_START_TIME = "session_start_time";
    private static final String KEY_SESSION_END_TIME = "session_end_time";
    private static final String KEY_BREAK_REQUIRED = "break_required";

    private SessionTimerManager() {
    }

    public static void ensureSessionStarted(Context context) {
        if (!AppSettingsManager.isBreakEnabled(context)) {
            clearSession(context);
            return;
        }
        SharedPreferences preferences = AppSettingsManager.getPreferences(context);
        boolean breakRequired = preferences.getBoolean(KEY_BREAK_REQUIRED, false);
        long endTime = preferences.getLong(KEY_SESSION_END_TIME, 0L);
        if (!breakRequired && endTime <= 0L) {
            startNewSession(context);
        }
    }

    public static boolean consumeBreakRequired(Context context) {
        if (!AppSettingsManager.isBreakEnabled(context)) {
            clearSession(context);
            return false;
        }
        SharedPreferences preferences = AppSettingsManager.getPreferences(context);
        if (preferences.getBoolean(KEY_BREAK_REQUIRED, false)) {
            return true;
        }

        long endTime = preferences.getLong(KEY_SESSION_END_TIME, 0L);
        if (endTime > 0L && System.currentTimeMillis() >= endTime) {
            markBreakRequired(context);
            return true;
        }
        return false;
    }

    public static long getMillisUntilBreak(Context context) {
        if (!AppSettingsManager.isBreakEnabled(context)) {
            return Long.MAX_VALUE;
        }
        SharedPreferences preferences = AppSettingsManager.getPreferences(context);
        if (preferences.getBoolean(KEY_BREAK_REQUIRED, false)) {
            return 0L;
        }

        long endTime = preferences.getLong(KEY_SESSION_END_TIME, 0L);
        if (endTime <= 0L) {
            return Long.MAX_VALUE;
        }
        return Math.max(0L, endTime - System.currentTimeMillis());
    }

    public static void startNewSession(Context context) {
        if (!AppSettingsManager.isBreakEnabled(context)) {
            clearSession(context);
            return;
        }
        long now = System.currentTimeMillis();
        int minutes = AppSettingsManager.getSessionMinutes(context);
        long endTime = now + minutes * 60_000L;
        AppSettingsManager.getPreferences(context)
                .edit()
                .putLong(KEY_SESSION_START_TIME, now)
                .putLong(KEY_SESSION_END_TIME, endTime)
                .putBoolean(KEY_BREAK_REQUIRED, false)
                .apply();
    }

    public static void resumeAfterBreak(Context context) {
        startNewSession(context);
    }

    public static void clearSession(Context context) {
        AppSettingsManager.getPreferences(context)
                .edit()
                .remove(KEY_SESSION_START_TIME)
                .remove(KEY_SESSION_END_TIME)
                .putBoolean(KEY_BREAK_REQUIRED, false)
                .apply();
    }

    private static void markBreakRequired(Context context) {
        SharedPreferences preferences = AppSettingsManager.getPreferences(context);
        preferences.edit()
                .putBoolean(KEY_BREAK_REQUIRED, true)
                .apply();
    }
}

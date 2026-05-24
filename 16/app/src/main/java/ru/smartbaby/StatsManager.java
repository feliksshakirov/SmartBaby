package ru.smartbaby;

import android.content.Context;
import android.content.SharedPreferences;

public final class StatsManager {
    private static final String KEY_TOTAL_TOUCHES = "stats_total_touches";
    private static final String KEY_BALLOON_POPS = "stats_balloon_pops";
    private static final String KEY_FIND_ANIMAL_CORRECT = "stats_find_animal_correct";
    private static final String KEY_FIND_ANIMAL_WRONG = "stats_find_animal_wrong";
    private static final String KEY_COLORS_CORRECT = "stats_colors_correct";
    private static final String KEY_COLORS_WRONG = "stats_colors_wrong";
    private static final String KEY_TOTAL_PLAY_MILLIS = "stats_total_play_millis";
    private static final String KEY_LAUNCH_PREFIX = "stats_launch_";

    private StatsManager() {
    }

    public static void recordTouch(Context context) {
        recordTouches(context, 1);
    }

    public static void recordTouches(Context context, int count) {
        if (count <= 0) {
            return;
        }
        addInt(context, KEY_TOTAL_TOUCHES, count);
    }

    public static void recordBalloonPop(Context context) {
        addInt(context, KEY_BALLOON_POPS, 1);
    }

    public static void recordGameLaunch(Context context, GameType gameType) {
        addInt(context, launchKey(gameType), 1);
    }

    public static void recordFindAnimalCorrect(Context context) {
        addInt(context, KEY_FIND_ANIMAL_CORRECT, 1);
    }

    public static void recordFindAnimalWrong(Context context) {
        addInt(context, KEY_FIND_ANIMAL_WRONG, 1);
    }

    public static void recordColorsCorrect(Context context) {
        addInt(context, KEY_COLORS_CORRECT, 1);
    }

    public static void recordColorsWrong(Context context) {
        addInt(context, KEY_COLORS_WRONG, 1);
    }

    public static void recordPlayTimeMillis(Context context, long millis) {
        if (millis <= 0L) {
            return;
        }
        SharedPreferences preferences = preferences(context);
        long current = preferences.getLong(KEY_TOTAL_PLAY_MILLIS, 0L);
        preferences.edit().putLong(KEY_TOTAL_PLAY_MILLIS, current + millis).apply();
    }

    public static int getTotalTouches(Context context) {
        return preferences(context).getInt(KEY_TOTAL_TOUCHES, 0);
    }

    public static int getBalloonPops(Context context) {
        return preferences(context).getInt(KEY_BALLOON_POPS, 0);
    }

    public static int getFindAnimalCorrect(Context context) {
        return preferences(context).getInt(KEY_FIND_ANIMAL_CORRECT, 0);
    }

    public static int getFindAnimalWrong(Context context) {
        return preferences(context).getInt(KEY_FIND_ANIMAL_WRONG, 0);
    }

    public static int getColorsCorrect(Context context) {
        return preferences(context).getInt(KEY_COLORS_CORRECT, 0);
    }

    public static int getColorsWrong(Context context) {
        return preferences(context).getInt(KEY_COLORS_WRONG, 0);
    }

    public static int getTotalPlayMinutes(Context context) {
        long millis = preferences(context).getLong(KEY_TOTAL_PLAY_MILLIS, 0L);
        if (millis <= 0L) {
            return 0;
        }
        return (int) ((millis + 59_999L) / 60_000L);
    }

    public static int getGameLaunchCount(Context context, GameType gameType) {
        return preferences(context).getInt(launchKey(gameType), 0);
    }

    public static String getFavoriteGameTitle(Context context) {
        GameType favoriteGame = null;
        int bestCount = 0;
        for (GameType gameType : GameType.values()) {
            int count = getGameLaunchCount(context, gameType);
            if (count > bestCount) {
                bestCount = count;
                favoriteGame = gameType;
            }
        }
        return favoriteGame == null ? "Пока нет" : favoriteGame.getTitle();
    }

    public static void reset(Context context) {
        SharedPreferences.Editor editor = preferences(context).edit();
        editor.remove(KEY_TOTAL_TOUCHES);
        editor.remove(KEY_BALLOON_POPS);
        editor.remove(KEY_FIND_ANIMAL_CORRECT);
        editor.remove(KEY_FIND_ANIMAL_WRONG);
        editor.remove(KEY_COLORS_CORRECT);
        editor.remove(KEY_COLORS_WRONG);
        editor.remove(KEY_TOTAL_PLAY_MILLIS);
        for (GameType gameType : GameType.values()) {
            editor.remove(launchKey(gameType));
        }
        editor.apply();
    }

    private static void addInt(Context context, String key, int amount) {
        SharedPreferences preferences = preferences(context);
        int current = preferences.getInt(key, 0);
        preferences.edit().putInt(key, current + amount).apply();
    }

    private static SharedPreferences preferences(Context context) {
        return AppSettingsManager.getPreferences(context);
    }

    private static String launchKey(GameType gameType) {
        return KEY_LAUNCH_PREFIX + gameType.getId();
    }
}

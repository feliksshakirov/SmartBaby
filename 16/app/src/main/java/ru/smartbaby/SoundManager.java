package ru.smartbaby;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;

public final class SoundManager {
    private static final int ANIMAL_SOUND_MAX_MS = 2200;
    private static final int EFFECT_SOUND_MAX_MS = 900;

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static ToneGenerator toneGenerator;
    private static MediaPlayer animalPlayer;
    private static MediaPlayer effectPlayer;

    private SoundManager() {
    }

    public static void playSparkle(Context context) {
        playTone(context, ToneGenerator.TONE_PROP_BEEP2, 110);
    }

    public static void playFirework(Context context) {
        playEffect(context, R.raw.firework_boom, ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 260, EFFECT_SOUND_MAX_MS);
    }

    public static void playCorrect(Context context) {
        playTone(context, ToneGenerator.TONE_PROP_ACK, 130);
    }

    public static void playTryAgain(Context context) {
        playTone(context, ToneGenerator.TONE_PROP_NACK, 100);
    }

    public static void playPop(Context context) {
        playEffect(context, R.raw.balloon_pop, ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 120, 260);
    }

    public static void playCat(Context context) {
        playAnimal(context, R.raw.animal_cat, ToneGenerator.TONE_SUP_RADIO_ACK, 180);
    }

    public static void playDog(Context context) {
        playAnimal(context, R.raw.animal_dog, ToneGenerator.TONE_PROP_BEEP, 170);
    }

    public static void playCow(Context context) {
        playAnimal(context, R.raw.animal_cow, ToneGenerator.TONE_CDMA_NETWORK_BUSY, 220);
    }

    public static void playDuck(Context context) {
        playAnimal(context, R.raw.animal_duck, ToneGenerator.TONE_CDMA_ANSWER, 140);
    }

    public static void playLion(Context context) {
        playAnimal(context, R.raw.animal_lion, ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 240);
    }

    public static void playSheep(Context context) {
        playAnimal(context, R.raw.animal_sheep, ToneGenerator.TONE_CDMA_ABBR_ALERT, 180);
    }

    private static void playAnimal(final Context context, int rawResId, int fallbackTone, int fallbackDurationMs) {
        if (!AppSettingsManager.isSoundEnabled(context)) {
            return;
        }

        synchronized (SoundManager.class) {
            releaseAnimalPlayerLocked();
            try {
                final MediaPlayer player = MediaPlayer.create(context.getApplicationContext(), rawResId);
                if (player == null) {
                    playTone(context, fallbackTone, fallbackDurationMs);
                    return;
                }

                animalPlayer = player;
                player.setVolume(1f, 1f);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer completedPlayer) {
                        synchronized (SoundManager.class) {
                            if (animalPlayer == completedPlayer) {
                                animalPlayer = null;
                            }
                        }
                        completedPlayer.release();
                    }
                });
                player.start();

                // Some real animal recordings are longer than a tap response should be.
                HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopAnimalPlayer(player);
                    }
                }, ANIMAL_SOUND_MAX_MS);
            } catch (RuntimeException ignored) {
                releaseAnimalPlayerLocked();
                playTone(context, fallbackTone, fallbackDurationMs);
            }
        }
    }

    private static void playEffect(final Context context, int rawResId, int fallbackTone, int fallbackDurationMs, long maxMs) {
        if (!AppSettingsManager.isSoundEnabled(context)) {
            return;
        }

        synchronized (SoundManager.class) {
            releaseEffectPlayerLocked();
            try {
                final MediaPlayer player = MediaPlayer.create(context.getApplicationContext(), rawResId);
                if (player == null) {
                    playTone(context, fallbackTone, fallbackDurationMs);
                    return;
                }

                effectPlayer = player;
                player.setVolume(0.9f, 0.9f);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer completedPlayer) {
                        synchronized (SoundManager.class) {
                            if (effectPlayer == completedPlayer) {
                                effectPlayer = null;
                            }
                        }
                        completedPlayer.release();
                    }
                });
                player.start();

                HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopEffectPlayer(player);
                    }
                }, maxMs);
            } catch (RuntimeException ignored) {
                releaseEffectPlayerLocked();
                playTone(context, fallbackTone, fallbackDurationMs);
            }
        }
    }

    private static void stopEffectPlayer(MediaPlayer player) {
        synchronized (SoundManager.class) {
            if (effectPlayer != player) {
                return;
            }
            effectPlayer = null;
        }

        try {
            if (player.isPlaying()) {
                player.stop();
            }
        } catch (RuntimeException ignored) {
        }
        player.release();
    }

    private static void stopAnimalPlayer(MediaPlayer player) {
        synchronized (SoundManager.class) {
            if (animalPlayer != player) {
                return;
            }
            animalPlayer = null;
        }

        try {
            if (player.isPlaying()) {
                player.stop();
            }
        } catch (RuntimeException ignored) {
        }
        player.release();
    }

    private static void releaseAnimalPlayerLocked() {
        if (animalPlayer == null) {
            return;
        }
        try {
            if (animalPlayer.isPlaying()) {
                animalPlayer.stop();
            }
        } catch (RuntimeException ignored) {
        }
        animalPlayer.release();
        animalPlayer = null;
    }

    private static void releaseEffectPlayerLocked() {
        if (effectPlayer == null) {
            return;
        }
        try {
            if (effectPlayer.isPlaying()) {
                effectPlayer.stop();
            }
        } catch (RuntimeException ignored) {
        }
        effectPlayer.release();
        effectPlayer = null;
    }

    private static void playTone(Context context, int toneType, int durationMs) {
        if (!AppSettingsManager.isSoundEnabled(context)) {
            return;
        }

        synchronized (SoundManager.class) {
            try {
                if (toneGenerator == null) {
                    toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 70);
                }
                toneGenerator.startTone(toneType, durationMs);
            } catch (RuntimeException ignored) {
            }
        }
    }
}

package ru.smartbaby;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressWarnings("deprecation")
public abstract class BaseActivity extends Activity {
    private final Handler breakHandler = new Handler(Looper.getMainLooper());
    private final Runnable breakTicker = new Runnable() {
        @Override
        public void run() {
            checkBreakIfNeeded();
            scheduleBreakCheck();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enterKidMode();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                checkBreakIfNeeded();
                scheduleBreakCheck();
            }
        });
    }

    @Override
    protected void onPause() {
        breakHandler.removeCallbacks(breakTicker);
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enterKidMode();
        }
    }

    protected void enterKidMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    protected boolean shouldRequireBreakScreen() {
        return false;
    }

    protected boolean allowSystemBack() {
        return false;
    }

    private void checkBreakIfNeeded() {
        if (!shouldRequireBreakScreen() || isFinishing()) {
            return;
        }
        // Protected screens redirect to the break page as soon as the timer expires.
        if (SessionTimerManager.consumeBreakRequired(this)) {
            startActivity(new Intent(this, BreakActivity.class));
            finish();
        }
    }

    private void scheduleBreakCheck() {
        breakHandler.removeCallbacks(breakTicker);
        if (!shouldRequireBreakScreen() || isFinishing()) {
            return;
        }

        long millisUntilBreak = SessionTimerManager.getMillisUntilBreak(this);
        if (millisUntilBreak == Long.MAX_VALUE) {
            return;
        }

        long delay = Math.max(250L, millisUntilBreak + 250L);
        breakHandler.postDelayed(breakTicker, delay);
    }

    @Override
    public void onBackPressed() {
        if (allowSystemBack()) {
            super.onBackPressed();
        } else {
            enterKidMode();
        }
    }
}

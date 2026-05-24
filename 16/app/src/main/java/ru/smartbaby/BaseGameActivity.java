package ru.smartbaby;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public abstract class BaseGameActivity extends BaseActivity {
    private long resumedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatsManager.recordGameLaunch(this, getGameType());
        SessionTimerManager.ensureSessionStarted(this);

        FrameLayout root = new FrameLayout(this);
        root.addView(createGameView(), new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        ChildLockHelper.attachHoldToCorner(root, this, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumedAt = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        if (resumedAt > 0L) {
            StatsManager.recordPlayTimeMillis(this, System.currentTimeMillis() - resumedAt);
            resumedAt = 0L;
        }
        super.onPause();
    }

    @Override
    protected boolean shouldRequireBreakScreen() {
        return true;
    }

    protected abstract GameType getGameType();

    protected abstract View createGameView();
}

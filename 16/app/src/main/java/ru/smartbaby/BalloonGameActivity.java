package ru.smartbaby;

import android.view.View;

import ru.smartbaby.games.BalloonGameView;

public class BalloonGameActivity extends BaseGameActivity {
    @Override
    protected GameType getGameType() {
        return GameType.BALLOON;
    }

    @Override
    protected View createGameView() {
        return new BalloonGameView(this);
    }
}

package ru.smartbaby;

import android.view.View;

import ru.smartbaby.games.DrawingGameView;

public class DrawingGameActivity extends BaseGameActivity {
    @Override
    protected GameType getGameType() {
        return GameType.DRAWING;
    }

    @Override
    protected View createGameView() {
        return new DrawingGameView(this);
    }
}

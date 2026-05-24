package ru.smartbaby;

import android.view.View;

import ru.smartbaby.games.ColorsGameView;

public class ColorsGameActivity extends BaseGameActivity {
    @Override
    protected GameType getGameType() {
        return GameType.COLORS;
    }

    @Override
    protected View createGameView() {
        return new ColorsGameView(this);
    }
}

package ru.smartbaby;

import android.view.View;

import ru.smartbaby.games.FireworkGameView;

public class FireworkGameActivity extends BaseGameActivity {
    @Override
    protected GameType getGameType() {
        return GameType.FIREWORK;
    }

    @Override
    protected View createGameView() {
        return new FireworkGameView(this);
    }
}

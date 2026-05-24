package ru.smartbaby;

import android.view.View;

import ru.smartbaby.games.AnimalsView;

public class AnimalsActivity extends BaseGameActivity {
    @Override
    protected GameType getGameType() {
        return GameType.ANIMALS;
    }

    @Override
    protected View createGameView() {
        return new AnimalsView(this);
    }
}

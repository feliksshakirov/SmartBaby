package ru.smartbaby;

import android.view.View;

import ru.smartbaby.games.FindAnimalGameView;

public class FindAnimalActivity extends BaseGameActivity {
    @Override
    protected GameType getGameType() {
        return GameType.FIND_ANIMAL;
    }

    @Override
    protected View createGameView() {
        return new FindAnimalGameView(this);
    }
}

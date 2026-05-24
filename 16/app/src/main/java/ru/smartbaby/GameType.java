package ru.smartbaby;

public enum GameType {
    DRAWING("drawing", "Волшебные линии"),
    BALLOON("balloon", "Лопни шарик"),
    ANIMALS("animals", "Животные"),
    FIREWORK("firework", "Салют"),
    FIND_ANIMAL("find_animal", "Найди животное"),
    COLORS("colors", "Цвета");

    private final String id;
    private final String title;

    GameType(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}

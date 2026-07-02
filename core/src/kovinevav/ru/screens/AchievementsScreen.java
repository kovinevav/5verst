package kovinevav.ru.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

import kovinevav.ru.MyGdxGame;
import kovinevav.ru.game.services.SaveService;

public class AchievementsScreen implements Screen {
    private final MyGdxGame game;

    private static final String[][] ACHIEVEMENTS = {
            {SaveService.ACH_FIRST_PYATYUNYA, "Первая пятюня"},
            {SaveService.ACH_SCORE_10, "10 очков за забег"},
            {SaveService.ACH_DISTANCE_500, "5 км за забег"},
            {SaveService.ACH_LEVEL_3, "4-й город пройден"},
            {SaveService.ACH_PYATYUNY_20, "20 пятюнь всего"},
            {SaveService.ACH_COMPLETE_NSK, "Новосибирск пройден"},
            {SaveService.ACH_COMPLETE_PERM, "Пермь пройдена"},
            {SaveService.ACH_COMPLETE_TOMSK, "Томск пройден"},
            {SaveService.ACH_COMPLETE_BARNAUL, "Барнаул пройден"},
            {SaveService.ACH_COMPLETE_KURGAN, "Курган пройден"},
            {SaveService.ACH_COMPLETE_EKB, "Екатеринбург пройден"},
            {SaveService.ACH_COMPLETE_KAZAN, "Казань пройдена"},
            {SaveService.ACH_COMPLETE_MOSCOW, "Москва пройдена"},
            {SaveService.ACH_COMPLETE_NOVGOROD, "Великий Новгород пройден"},
            {SaveService.ACH_COMPLETE_SPB, "Санкт-Петербург пройден"},
    };

    public AchievementsScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        float worldH = game.getWorldHeight();
        game.applyViewport();

        game.getBatch().begin();
        game.getUiFont().draw(game.getBatch(), "Достижения", 24, worldH - 24);

        float y = worldH - 90;
        for (String[] ach : ACHIEVEMENTS) {
            boolean unlocked = game.getSaveService().isAchievementUnlocked(ach[0]);
            String status = unlocked ? "[v] " : "[ ] ";
            game.getUiFont().draw(game.getBatch(), status + ach[1], 24, y);
            y -= 50;
        }

        game.getUiFont().draw(game.getBatch(), "Тап — в меню", 24, 80);
        game.getBatch().end();

        if (Gdx.input.justTouched()) {
            game.showMenu();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.resizeViewport(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}

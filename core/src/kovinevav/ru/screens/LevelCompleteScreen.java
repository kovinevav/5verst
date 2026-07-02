package kovinevav.ru.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import kovinevav.ru.MyGdxGame;
import kovinevav.ru.game.CityFishkiDisplay;
import kovinevav.ru.game.FontAssets;
import kovinevav.ru.game.RunRules;
import kovinevav.ru.game.StatsPanel;
import kovinevav.ru.game.cities.City;
import kovinevav.ru.game.cities.CityRegistry;
import kovinevav.ru.game.components.MarinaDanceAnimator;

public class LevelCompleteScreen implements Screen {
    private final MyGdxGame game;
    private MarinaDanceAnimator marinaDanceAnimator;
    private CityFishkiDisplay cityFishkiDisplay;
    private int completedCityIndex = -1;
    private static final float DANCE_MAX_WIDTH = 340f;
    private static final float DANCE_MAX_HEIGHT = 380f;
    private static final float DANCE_MARGIN_RIGHT = 24f;
    private static final float DANCE_MARGIN_BOTTOM = 16f;

    private final Rectangle nextLevelBounds = new Rectangle();
    private final Rectangle menuBounds = new Rectangle();
    private final Vector3 touch = new Vector3();

    public LevelCompleteScreen(MyGdxGame game) {
        this.game = game;
    }

    public void prepare(int completedCityIndex) {
        this.completedCityIndex = completedCityIndex;
    }

    @Override
    public void show() {
        if (marinaDanceAnimator == null) {
            marinaDanceAnimator = new MarinaDanceAnimator();
        }
        if (cityFishkiDisplay == null) {
            cityFishkiDisplay = new CityFishkiDisplay();
        }
        boolean gameCompleted = CityRegistry.getNext(completedCityIndex) == null;
        game.getAudioService().playVictoryMusic(gameCompleted);
    }

    @Override
    public void render(float delta) {
        float worldW = game.getWorldWidth();
        float worldH = game.getWorldHeight();

        ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1);
        game.applyViewport();

        marinaDanceAnimator.update();

        City completed = CityRegistry.get(completedCityIndex);
        City next = CityRegistry.getNext(completedCityIndex);

        float timeSec = game.getSaveService().getLastRunTimeSeconds();
        int pyatyuny = game.getSaveService().getLastRunPyatyunyCount();
        float avgPace = game.getSaveService().getLastRunAveragePaceMinPerKm();
        String pace = RunRules.formatPaceMinPerKm(avgPace);
        String timeStr = RunRules.formatTime(timeSec);

        game.getBatch().begin();
        cityFishkiDisplay.drawForLevelComplete(game.getBatch(), game.getSaveService(), worldW, worldH);

        FontAssets.drawHud(game.getBatch(), game.getUiFont(),
                "5 км уложились в " + (int) completed.getTimeLimitMinutes() + " минут!", 340,
                worldH - 30);

        marinaDanceAnimator.drawBottomRight(game.getBatch(), worldW, DANCE_MAX_WIDTH, DANCE_MAX_HEIGHT,
                DANCE_MARGIN_RIGHT, DANCE_MARGIN_BOTTOM);

        FontAssets.drawHud(game.getBatch(), game.getUiFont(), "Статистика забега", 24,
                worldH - 80);
        FontAssets.drawHud(game.getBatch(), game.getUiFont(), "Город: " + completed.getDisplayName(), 24,
                worldH - 120);
        FontAssets.drawHud(game.getBatch(), game.getUiFont(), "Пятюнь: " + pyatyuny, 24,
                worldH - 160);
        FontAssets.drawHud(game.getBatch(), game.getUiFont(), "Средний темп: " + pace, 24,
                worldH - 200);
        FontAssets.drawHud(game.getBatch(), game.getUiFont(), "Время: " + timeStr, 24,
                worldH - 240);

        if (next != null && game.getSaveService().isCityUnlocked(next.getIndex())) {
            FontAssets.drawHud(game.getBatch(), game.getUiFont(),
                    "Следующий уровень: " + next.getDisplayName(), 380, 150);
            StatsPanel.drawButtonCentered(game.getBatch(), game.getUiFont(), "Новая локация",
                    worldW / 2f, 56f, 280f, true, nextLevelBounds);
        } else {
            FontAssets.drawHud(game.getBatch(), game.getUiFont(), "Все города пройдены!", 460, 120);
            nextLevelBounds.set(0, 0, 0, 0);
        }

        StatsPanel.drawButton(game.getBatch(), game.getUiFont(), "Меню",
                32f, 40f, 140f, false, menuBounds);
        game.getBatch().end();

        handleInput(next);
    }

    private void handleInput(City next) {
        if (!Gdx.input.justTouched()) {
            return;
        }
        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.unproject(touch);

        if (menuBounds.contains(touch.x, touch.y)) {
            game.showMenu();
            return;
        }

        if (next != null && game.getSaveService().isCityUnlocked(next.getIndex())
                && nextLevelBounds.width > 0f
                && nextLevelBounds.contains(touch.x, touch.y)) {
            game.getSaveService().setCurrentCityIndex(next.getIndex());
            game.showGame();
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
        game.getAudioService().stopMusic();
    }

    @Override
    public void dispose() {
        if (cityFishkiDisplay != null) {
            cityFishkiDisplay.dispose();
            cityFishkiDisplay = null;
        }
        if (marinaDanceAnimator != null) {
            marinaDanceAnimator.dispose();
            marinaDanceAnimator = null;
        }
    }
}

package kovinevav.ru.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

import kovinevav.ru.MyGdxGame;
import kovinevav.ru.game.CitySplashBackground;
import kovinevav.ru.game.RunRules;
import kovinevav.ru.game.StatsPanel;
import kovinevav.ru.game.cities.CityRegistry;

public class StatsScreen implements Screen {
    private static final float PANEL_X = 28f;
    private static final float PANEL_Y = 110f;
    private static final float PANEL_W = 520f;
    private static final float PANEL_H = 520f;

    private final MyGdxGame game;
    private final CitySplashBackground splashBackground = new CitySplashBackground();

    public StatsScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        splashBackground.ensureForCity(game.getSaveService().getLastRunCityIndex());
    }

    @Override
    public void render(float delta) {
        int cityIndex = game.getSaveService().getLastRunCityIndex();
        splashBackground.ensureForCity(cityIndex);

        ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1);
        float worldW = game.getWorldWidth();
        float worldH = game.getWorldHeight();
        game.applyViewport();

        String cityName = CityRegistry.get(cityIndex).getDisplayName();
        int pyatyuny = game.getSaveService().getLastRunPyatyunyCount();
        float timeSec = game.getSaveService().getLastRunTimeSeconds();
        String pace = RunRules.formatPaceMinPerKm(game.getSaveService().getLastRunAveragePaceMinPerKm());
        String timeStr = RunRules.formatTime(timeSec);
        boolean won = game.getSaveService().wasLastRunWon();

        game.getBatch().begin();
        splashBackground.draw(game.getBatch(), worldW, worldH);
        StatsPanel.drawBackdropDim(game.getBatch(), worldW, worldH);

        StatsPanel.drawCard(game.getBatch(), PANEL_X, PANEL_Y, PANEL_W, PANEL_H);
        float y = PANEL_Y + PANEL_H - StatsPanel.PAD - 8f;
        y = StatsPanel.drawSectionTitle(game.getBatch(), game.getUiFont(), "Статистика", PANEL_X, PANEL_W, y);
        y = StatsPanel.drawStatusChip(game.getBatch(), game.getUiFont(),
                won ? "Последний забег — успех" : "Последний забег — не финишировали",
                won, PANEL_X, y);
        y = StatsPanel.drawRow(game.getBatch(), game.getUiFont(), "Город", cityName, PANEL_X, PANEL_W, y);
        y = StatsPanel.drawRow(game.getBatch(), game.getUiFont(), "Пятюнь", String.valueOf(pyatyuny),
                PANEL_X, PANEL_W, y);
        y = StatsPanel.drawRow(game.getBatch(), game.getUiFont(), "Средний темп", pace, PANEL_X, PANEL_W, y);
        y = StatsPanel.drawRow(game.getBatch(), game.getUiFont(), "Время", timeStr, PANEL_X, PANEL_W, y);

        y -= 8f;
        y = StatsPanel.drawRow(game.getBatch(), game.getUiFont(), "Всего пятюнь",
                String.valueOf(game.getSaveService().getPyatyunyCollected()), PANEL_X, PANEL_W, y);
        StatsPanel.drawRow(game.getBatch(), game.getUiFont(), "Лучший счёт",
                String.valueOf(game.getSaveService().getHighScore()), PANEL_X, PANEL_W, y);

        StatsPanel.drawHintPill(game.getBatch(), game.getUiFont(), "Тап в любом месте — в меню",
                worldW / 2f, 36f);
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
        splashBackground.dispose();
    }
}

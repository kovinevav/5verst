package kovinevav.ru;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import kovinevav.ru.game.FontAssets;
import kovinevav.ru.game.StatsPanel;
import kovinevav.ru.game.services.AudioService;
import kovinevav.ru.game.services.SaveService;
import kovinevav.ru.screens.AchievementsScreen;
import kovinevav.ru.screens.GameScreen;
import kovinevav.ru.screens.LevelCompleteScreen;
import kovinevav.ru.screens.MenuScreen;
import kovinevav.ru.screens.SettingsScreen;
import kovinevav.ru.screens.StatsScreen;

public class MyGdxGame extends Game {
    public static final int SCR_WIDTH = 1280;
    public static final int SCR_HEIGHT = 720;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SaveService saveService;
    private AudioService audioService;
    private BitmapFont uiFont;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private StatsScreen statsScreen;
    private AchievementsScreen achievementsScreen;
    private SettingsScreen settingsScreen;
    private LevelCompleteScreen levelCompleteScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
        viewport = new ExtendViewport(SCR_WIDTH, SCR_HEIGHT, camera);

        saveService = new SaveService();
        audioService = new AudioService(saveService);
        uiFont = FontAssets.getUi();

        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        statsScreen = new StatsScreen(this);
        achievementsScreen = new AchievementsScreen(this);
        settingsScreen = new SettingsScreen(this);
        levelCompleteScreen = new LevelCompleteScreen(this);
        showMenu();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public SaveService getSaveService() {
        return saveService;
    }

    public AudioService getAudioService() {
        return audioService;
    }

    public BitmapFont getUiFont() {
        return uiFont;
    }

    public void unproject(Vector3 touch) {
        viewport.unproject(touch);
    }

    public void showMenu() {
        setScreen(menuScreen);
    }

    public void showGame() {
        setScreen(gameScreen);
    }

    public void showStats() {
        setScreen(statsScreen);
    }

    public void showAchievements() {
        setScreen(achievementsScreen);
    }

    public void showSettings() {
        setScreen(settingsScreen);
    }

    public void showLevelComplete(int completedCityIndex) {
        levelCompleteScreen.prepare(completedCityIndex);
        setScreen(levelCompleteScreen);
    }

    public void resizeViewport(int width, int height) {
        viewport.update(width, height, true);
    }

    public void applyViewport() {
        viewport.apply(true);
        batch.setProjectionMatrix(camera.combined);
    }

    public float getWorldWidth() {
        return viewport.getWorldWidth();
    }

    public float getWorldHeight() {
        return viewport.getWorldHeight();
    }

    @Override
    public void resize(int width, int height) {
        resizeViewport(width, height);
        if (getScreen() != null) {
            getScreen().resize(width, height);
        }
    }

    @Override
    public void dispose() {
        FontAssets.dispose();
        StatsPanel.dispose();
        if (audioService != null) {
            audioService.dispose();
        }
        if (menuScreen != null) {
            menuScreen.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        if (statsScreen != null) {
            statsScreen.dispose();
        }
        if (achievementsScreen != null) {
            achievementsScreen.dispose();
        }
        if (settingsScreen != null) {
            settingsScreen.dispose();
        }
        if (levelCompleteScreen != null) {
            levelCompleteScreen.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}

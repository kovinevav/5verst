package kovinevav.ru.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import kovinevav.ru.MyGdxGame;
import kovinevav.ru.game.FontAssets;
import kovinevav.ru.game.components.Button;

public class SettingsScreen implements Screen {
    private static final float CONFIRM_RESET_SECONDS = 5f;

    private final MyGdxGame game;
    private final Button restartBtn;
    private final Rectangle musicToggleBounds = new Rectangle(360, 500, 560, 56);
    private final Rectangle sfxToggleBounds = new Rectangle(360, 430, 560, 56);
    private final Rectangle musicMinusBounds = new Rectangle(200, 340, 120, 56);
    private final Rectangle musicPlusBounds = new Rectangle(960, 340, 120, 56);
    private final Rectangle sfxMinusBounds = new Rectangle(200, 270, 120, 56);
    private final Rectangle sfxPlusBounds = new Rectangle(960, 270, 120, 56);
    private final Rectangle backBounds = new Rectangle(0, 0, 300, 120);
    private final Vector3 touch = new Vector3();

    private boolean confirmResetPending;
    private float confirmResetTimer;

    public SettingsScreen(MyGdxGame game) {
        this.game = game;
        restartBtn = new Button(480, 180, "buttons/restart.png");
    }

    @Override
    public void show() {
        confirmResetPending = false;
        confirmResetTimer = 0f;
    }

    @Override
    public void render(float delta) {
        if (confirmResetPending) {
            confirmResetTimer -= delta;
            if (confirmResetTimer <= 0f) {
                confirmResetPending = false;
            }
        }

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        float worldH = game.getWorldHeight();
        game.applyViewport();

        boolean musicOn = game.getSaveService().isMusicEnabled();
        boolean sfxOn = game.getSaveService().isSfxEnabled();
        float musicVol = game.getSaveService().getMusicVolume();
        float sfxVol = game.getSaveService().getSfxVolume();

        game.getBatch().begin();
        drawLine("Настройки", 24, worldH - 24);
        drawLine("Музыка: " + (musicOn ? "вкл" : "выкл") + "  (тап)", 380, 540);
        drawLine("Звуки: " + (sfxOn ? "вкл" : "выкл") + "  (тап)", 380, 470);
        drawLine("Громкость музыки: " + percent(musicVol), 360, 380);
        drawLine("-", 240, 380);
        drawLine("+", 1000, 380);
        drawLine("Громкость звуков: " + percent(sfxVol), 360, 310);
        drawLine("-", 240, 310);
        drawLine("+", 1000, 310);

        drawLine("Сброс прогресса", 24, 290);
        restartBtn.draw(game.getBatch());
        if (confirmResetPending) {
            FontAssets.draw(game.getBatch(), game.getUiFont(),
                    "Тап ещё раз по кнопке для подтверждения",
                    430, 160, Color.SCARLET);
        } else {
            drawLine("Сброс городов, достижений и статистики", 430, 160);
        }

        drawLine("в меню", 24, 80);
        game.getBatch().end();

        handleInput();
    }

    private void drawLine(String text, float x, float y) {
        FontAssets.draw(game.getBatch(), game.getUiFont(), text, x, y, FontAssets.HUD_LIGHT);
    }

    private static String percent(float v) {
        return Math.round(v * 100) + "%";
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) {
            return;
        }
        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        game.unproject(touch);
        int tx = (int) touch.x;
        int ty = (int) touch.y;

        if (backBounds.contains(touch.x, touch.y)) {
            game.showMenu();
            return;
        }
        if (restartBtn.isHit(tx, ty)) {
            if (!confirmResetPending) {
                confirmResetPending = true;
                confirmResetTimer = CONFIRM_RESET_SECONDS;
            } else {
                game.getSaveService().resetAllProgress();
                confirmResetPending = false;
                game.showMenu();
            }
            return;
        }
        if (musicToggleBounds.contains(touch.x, touch.y)) {
            game.getSaveService().setMusicEnabled(!game.getSaveService().isMusicEnabled());
            game.getAudioService().applyMusicSettings();
            return;
        }
        if (sfxToggleBounds.contains(touch.x, touch.y)) {
            game.getSaveService().setSfxEnabled(!game.getSaveService().isSfxEnabled());
            return;
        }
        if (musicMinusBounds.contains(touch.x, touch.y)) {
            game.getSaveService().setMusicVolume(game.getSaveService().getMusicVolume() - 0.1f);
            game.getAudioService().applyMusicSettings();
            return;
        }
        if (musicPlusBounds.contains(touch.x, touch.y)) {
            game.getSaveService().setMusicVolume(game.getSaveService().getMusicVolume() + 0.1f);
            game.getAudioService().applyMusicSettings();
            return;
        }
        if (sfxMinusBounds.contains(touch.x, touch.y)) {
            game.getSaveService().setSfxVolume(game.getSaveService().getSfxVolume() - 0.1f);
            return;
        }
        if (sfxPlusBounds.contains(touch.x, touch.y)) {
            game.getSaveService().setSfxVolume(game.getSaveService().getSfxVolume() + 0.1f);
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
        restartBtn.dispose();
    }
}

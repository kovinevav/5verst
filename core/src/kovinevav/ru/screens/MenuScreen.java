package kovinevav.ru.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import kovinevav.ru.MyGdxGame;
import kovinevav.ru.game.MovingBackground;
import kovinevav.ru.game.components.Button;

public class MenuScreen implements Screen {
    private final MyGdxGame myGdxGame;
    private MovingBackground background;
    private final Button playBtn;
    private final Button settingsBtn;
    private final Button achievementsBtn;
    private final Button statistBtn;
    private final Button exitBtn;
    private final Vector3 touch = new Vector3();

    public MenuScreen(MyGdxGame game) {
        this.myGdxGame = game;
        background = new MovingBackground("menu/screen.png");
        playBtn = new Button(10, 500, "buttons/play.png");
        settingsBtn = new Button(10, 200, "buttons/settings.png");
        achievementsBtn = new Button(10, 300, "buttons/achievements.png");
        statistBtn = new Button(10, 400, "buttons/statist.png");
        exitBtn = new Button(10, 100, "buttons/exit.png");
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        float worldW = myGdxGame.getWorldWidth();
        float worldH = myGdxGame.getWorldHeight();
        background.setWorldSize(worldW, worldH);
        myGdxGame.applyViewport();

        myGdxGame.getBatch().begin();
        background.drawCover(myGdxGame.getBatch());
        playBtn.draw(myGdxGame.getBatch());
        settingsBtn.draw(myGdxGame.getBatch());
        statistBtn.draw(myGdxGame.getBatch());
        achievementsBtn.draw(myGdxGame.getBatch());
        exitBtn.draw(myGdxGame.getBatch());
        myGdxGame.getBatch().end();

        handleInput();
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) {
            return;
        }
        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        myGdxGame.unproject(touch);
        int tx = (int) touch.x;
        int ty = (int) touch.y;

        if (playBtn.isHit(tx, ty)) {
            myGdxGame.showGame();
        } else if (statistBtn.isHit(tx, ty)) {
            myGdxGame.showStats();
        } else if (achievementsBtn.isHit(tx, ty)) {
            myGdxGame.showAchievements();
        } else if (settingsBtn.isHit(tx, ty)) {
            myGdxGame.showSettings();
        } else if (exitBtn.isHit(tx, ty)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        myGdxGame.resizeViewport(width, height);
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
        if (background != null) {
            background.dispose();
            background = null;
        }
        playBtn.dispose();
        settingsBtn.dispose();
        statistBtn.dispose();
        achievementsBtn.dispose();
        exitBtn.dispose();
    }
}

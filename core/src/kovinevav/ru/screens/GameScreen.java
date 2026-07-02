package kovinevav.ru.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import kovinevav.ru.MyGdxGame;
import kovinevav.ru.game.FontAssets;
import kovinevav.ru.game.GameState;
import kovinevav.ru.game.MovingBackground;
import kovinevav.ru.game.PaceTracker;
import kovinevav.ru.game.RunRules;
import kovinevav.ru.game.StatsPanel;
import kovinevav.ru.game.cities.City;
import kovinevav.ru.game.cities.CityRegistry;
import kovinevav.ru.game.entities.PassingRunner;
import kovinevav.ru.game.entities.PlayRuner;
import kovinevav.ru.game.entities.Puddle;
import kovinevav.ru.game.components.PyatyunyaAnimator;
import kovinevav.ru.game.entities.Pyatyunya;

public class GameScreen implements Screen {
    private static final float JUMP_SCROLL_BONUS = 5f;

    private static final int PYATYUNYA_Y = 360;
    private static final float PUDDLE_SPAWN_OFFSET = 220f;

    private static final int[] PYATYUNYA_START_X = {600, 950, 1300};
    private static final int[] PYATYUNYA_RESPAWN_OFFSET = {0, 250, 500};

    private final MyGdxGame myGdxGame;
    private final PlayRuner runer;
    private final PassingRunner passingRunner;
    private MovingBackground background;
    private final Puddle puddle;
    private final Array<Pyatyunya> pyatyunyas = new Array<>();
    private final PyatyunyaAnimator pyatyunyaAnimator;

    private final Rectangle pauseButtonBounds = new Rectangle();
    private final Rectangle menuButtonBounds = new Rectangle();
    private final Rectangle continueBounds = new Rectangle();
    private final Rectangle menuTapBounds = new Rectangle();
    private final Vector3 touch = new Vector3();

    private GameState state = GameState.PLAYING;
    private City currentCity;
    private final PaceTracker paceTracker = new PaceTracker();
    private float speed;
    private float distanceMeters;
    private int score;
    private int pyatyunyCollectedThisRun;
    private String loseReason = "";
    private boolean wasInPuddle;
    private boolean jumpedOverPuddle;
    private float prevPuddleX;
    private boolean introHintVisible;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        pyatyunyaAnimator = new PyatyunyaAnimator();

        for (int i = 0; i < PYATYUNYA_START_X.length; i++) {
            pyatyunyas.add(new Pyatyunya(
                    PYATYUNYA_START_X[i],
                    PYATYUNYA_Y,
                    2,
                    pyatyunyaAnimator,
                    PYATYUNYA_RESPAWN_OFFSET[i],
                    i * 0.11f
            ));
        }

        runer = new PlayRuner(50, 50, 200, 300);
        puddle = new Puddle(MyGdxGame.SCR_WIDTH + PUDDLE_SPAWN_OFFSET, 50, 2);

        Texture[] passingFrames = {
                new Texture("girlRun/move_00.png"),
                new Texture("girlRun/move_01.png"),
                new Texture("girlRun/move_02.png"),
                new Texture("girlRun/move_03.png"),
                new Texture("girlRun/move_04.png"),
                new Texture("girlRun/move_05.png"),
                new Texture("girlRun/move_06.png"),
                new Texture("girlRun/move_07.png")
        };
        passingRunner = new PassingRunner(400, 0, 200, 300, passingFrames);

        pauseButtonBounds.set(MyGdxGame.SCR_WIDTH - 120, MyGdxGame.SCR_HEIGHT - 80, 100, 60);
        menuButtonBounds.set(MyGdxGame.SCR_WIDTH - 120, MyGdxGame.SCR_HEIGHT - 150, 100, 60);
        continueBounds.set(0, 0, MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT);
        menuTapBounds.set(0, 0, 200, 120);
        updateViewportLayout();
    }

    private void updateViewportLayout() {
        float w = myGdxGame.getWorldWidth();
        float h = myGdxGame.getWorldHeight();
        pauseButtonBounds.set(w - 120, h - 80, 100, 60);
        menuButtonBounds.set(w - 120, h - 150, 100, 60);
        continueBounds.set(0, 0, w, h);
        float puddleSpawn = w + PUDDLE_SPAWN_OFFSET;
        puddle.setSpawnX(puddleSpawn);
        for (Pyatyunya pyatyunya : pyatyunyas) {
            pyatyunya.setWorldWidth(w);
        }
        if (background != null) {
            background.setWorldSize(w, h);
        }
    }

    @Override
    public void show() {
        myGdxGame.getAudioService().playGameMusic();
        reset();
    }

    private void reset() {
        state = GameState.PLAYING;
        score = 0;
        distanceMeters = 0f;
        loseReason = "";
        pyatyunyCollectedThisRun = 0;
        wasInPuddle = false;
        jumpedOverPuddle = false;
        prevPuddleX = myGdxGame.getWorldWidth() + PUDDLE_SPAWN_OFFSET;
        paceTracker.reset();
        runer.reset();
        puddle.reset();

        currentCity = CityRegistry.get(myGdxGame.getSaveService().getCurrentCityIndex());
        paceTracker.setBasePaceMinPerKm(currentCity.getTargetPaceMinPerKm());

        if (background != null) {
            background.dispose();
        }
        float initialSpeed = 2f + currentCity.getIndex();
        background = new MovingBackground(
                currentCity.getBackgroundPath(),
                currentCity.getFinishBackgroundPath(),
                initialSpeed);
        speed = initialSpeed;

        for (int i = 0; i < pyatyunyas.size; i++) {
            pyatyunyas.get(i).setPosition(PYATYUNYA_START_X[i], PYATYUNYA_Y);
        }

        introHintVisible = !myGdxGame.getSaveService().hasSeenGameplayHint();
        updateViewportLayout();
    }

    @Override
    public void render(float delta) {
        handleInput();

        if (state == GameState.PLAYING && !introHintVisible) {
            updateGame(delta);
        }

        if (state == GameState.VICTORY) {
            return;
        }

        drawWorld();

        if (introHintVisible) {
            drawIntroHintOverlay();
        } else if (state == GameState.PAUSED) {
            drawPauseOverlay();
        } else if (state == GameState.GAME_OVER) {
            drawGameOverOverlay();
        }
    }

    private void handleInput() {
        if (introHintVisible) {
            if (Gdx.input.justTouched()) {
                introHintVisible = false;
                myGdxGame.getSaveService().markGameplayHintSeen();
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)
                || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
            return;
        }

        if (!Gdx.input.justTouched()) {
            return;
        }

        unprojectTouch();

        switch (state) {
            case PLAYING:
                if (pauseButtonBounds.contains(touch.x, touch.y)) {
                    state = GameState.PAUSED;
                    myGdxGame.getAudioService().pauseMusic();
                    myGdxGame.getAudioService().playPause();
                } else if (menuButtonBounds.contains(touch.x, touch.y)) {
                    saveRunAndGoToMenu();
                } else if (runer.tryJump()) {
                    myGdxGame.getAudioService().playJump();
                }
                break;

            case PAUSED:
                if (menuTapBounds.contains(touch.x, touch.y)) {
                    saveRunAndGoToMenu();
                } else if (continueBounds.contains(touch.x, touch.y)) {
                    state = GameState.PLAYING;
                    myGdxGame.getAudioService().resumeMusic();
                    myGdxGame.getAudioService().playPause();
                }
                break;

            case GAME_OVER:
                if (menuTapBounds.contains(touch.x, touch.y)) {
                    goToMenu();
                } else if (continueBounds.contains(touch.x, touch.y)) {
                    reset();
                }
                break;

            default:
                break;
        }
    }

    private void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            myGdxGame.getAudioService().pauseMusic();
            myGdxGame.getAudioService().playPause();
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            myGdxGame.getAudioService().resumeMusic();
            myGdxGame.getAudioService().playPause();
        }
    }

    private void unprojectTouch() {
        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        myGdxGame.unproject(touch);
    }

    private void updateGame(float delta) {
        runer.update(delta);
        updateSpeed();
        pyatyunyaAnimator.update(delta);
        for (Pyatyunya pyatyunya : pyatyunyas) {
            pyatyunya.update(delta);
        }
        checkPyatyunyCollisions();

        distanceMeters += speed * delta * RunRules.METERS_PER_SPEED_UNIT_PER_SECOND;
        paceTracker.update(delta, speed);

        if (currentCity.hasFinishBackground()
                && distanceMeters >= RunRules.TARGET_DISTANCE_METERS - RunRules.FINISH_BACKDROP_LEAD_METERS) {
            background.beginFinishPhase();
        }

        float raceClock = paceTracker.getRaceClockSeconds();

        if (RunRules.hasCompletedRaceDistance(distanceMeters)) {
            if (RunRules.isRaceFinishedInTime(distanceMeters, raceClock, currentCity.getIndex())) {
                onRaceWin();
            } else {
                onRaceLose("5 км не уложились в "
                        + (int) currentCity.getTimeLimitMinutes() + " минут");
            }
        }
    }

    private void checkPyatyunyCollisions() {
        for (Pyatyunya pyatyunya : pyatyunyas) {
            if (pyatyunya.hitsRunner(runer)) {
                score++;
                pyatyunyCollectedThisRun++;
                pyatyunya.respawn();
                paceTracker.onPyatyunyaCollected();
                myGdxGame.getAudioService().playCollect();
            }
        }
    }

    private void onRaceWin() {
        state = GameState.VICTORY;
        saveRunProgress(true);
        myGdxGame.getSaveService().completeCity(currentCity.getIndex());
        myGdxGame.getSaveService().checkAchievements(score, (int) distanceMeters, pyatyunyCollectedThisRun);
        myGdxGame.showLevelComplete(currentCity.getIndex());
    }

    private void onRaceLose(String reason) {
        state = GameState.GAME_OVER;
        loseReason = reason;
        myGdxGame.getAudioService().playGameOver();
        myGdxGame.getAudioService().pauseMusic();
        saveRunProgress(false);
    }

    private void saveRunProgress(boolean won) {
        myGdxGame.getSaveService().updateHighScore(score);
        myGdxGame.getSaveService().addPyatyunyCollected(pyatyunyCollectedThisRun);
        myGdxGame.getSaveService().checkAchievements(score, (int) distanceMeters, pyatyunyCollectedThisRun);
        float avgPace = RunRules.hasCompletedRaceDistance(distanceMeters)
                ? paceTracker.getAveragePaceMinPerKm(RunRules.TARGET_DISTANCE_METERS)
                : currentCity.getTargetPaceMinPerKm();
        myGdxGame.getSaveService().saveLastRun(
                currentCity.getIndex(),
                pyatyunyCollectedThisRun,
                paceTracker.getRaceClockSeconds(),
                avgPace,
                won
        );
    }

    private void saveRunAndGoToMenu() {
        saveRunProgress(state == GameState.VICTORY);
        goToMenu();
    }

    private void updateSpeed() {
        int usualSpeed = 2 + currentCity.getIndex();
        speed = usualSpeed;

        float puddleX = puddle.getX();
        if (puddleX > prevPuddleX + 80f) {
            jumpedOverPuddle = false;
        }
        prevPuddleX = puddleX;

        boolean overlaps = puddle.overlapsRunnerX(runer);
        if (overlaps && !runer.isOnGround()) {
            jumpedOverPuddle = true;
        }

        boolean inPuddle = puddle.hitsRunner(runer) && runer.isOnGround() && !jumpedOverPuddle;
        if (inPuddle) {
            if (!wasInPuddle) {
                paceTracker.onPuddleHit();
            }
            speed = Math.max(1f, speed - 1f);
        }
        wasInPuddle = inPuddle;

        if (puddle.isPastRunner(runer)) {
            jumpedOverPuddle = false;
        }

        float scrollSpeed = speed;
        if (!runer.isOnGround()) {
            scrollSpeed += JUMP_SCROLL_BONUS;
        }
        applyScrollSpeed(scrollSpeed);
    }

    private void applyScrollSpeed(float scrollSpeed) {
        background.setSpeed(scrollSpeed);
        puddle.setScrollSpeed(scrollSpeed);
        for (Pyatyunya pyatyunya : pyatyunyas) {
            pyatyunya.setScrollSpeed(scrollSpeed);
        }
    }

    private void drawWorld() {
        boolean moving = state == GameState.PLAYING && !introHintVisible;

        ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1);
        updateViewportLayout();
        myGdxGame.applyViewport();

        myGdxGame.getBatch().begin();
        if (moving) {
            background.draw(myGdxGame.getBatch());
        } else {
            background.drawFrozen(myGdxGame.getBatch());
        }

        if (moving) {
            puddle.draw(myGdxGame.getBatch());
            runer.draw(myGdxGame.getBatch());
            passingRunner.draw(myGdxGame.getBatch());
            for (Pyatyunya pyatyunya : pyatyunyas) {
                pyatyunya.draw(myGdxGame.getBatch());
            }
            background.move();
        } else {
            puddle.drawStatic(myGdxGame.getBatch());
            runer.drawStatic(myGdxGame.getBatch());
            passingRunner.drawStatic(myGdxGame.getBatch());
            for (Pyatyunya pyatyunya : pyatyunyas) {
                pyatyunya.draw(myGdxGame.getBatch());
            }
        }

        if (state == GameState.PLAYING) {
            drawHud();
            FontAssets.drawHud(myGdxGame.getBatch(), myGdxGame.getUiFont(), "||",
                    pauseButtonBounds.x + 30, pauseButtonBounds.y + 45);
            FontAssets.drawHud(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Меню",
                    menuButtonBounds.x + 5, menuButtonBounds.y + 45);
        } else if (state == GameState.PAUSED) {
            drawHud();
        }

        myGdxGame.getBatch().end();
    }

    private void drawHud() {
        float hudH = myGdxGame.getWorldHeight();
        float raceClock = paceTracker.getRaceClockSeconds();
        float displayPace = paceTracker.getDisplayPaceMinPerKm();
        String paceStr = RunRules.formatPaceMinPerKm(displayPace);
        int pyatyunyaBuffs = paceTracker.getActivePyatyunyaBonusCount();
        if (pyatyunyaBuffs > 0) {
            paceStr += " (пят.×" + pyatyunyaBuffs + ")";
        }
        int best = myGdxGame.getSaveService().getHighScore();
        float targetPace = currentCity.getTargetPaceMinPerKm();
        String timeLimitStr = RunRules.formatTime(currentCity.getTimeLimitSeconds());
        FontAssets.drawHud(myGdxGame.getBatch(), myGdxGame.getUiFont(),
                currentCity.getDisplayName(), 24, hudH - 24);
        FontAssets.drawHud(myGdxGame.getBatch(), myGdxGame.getUiFont(),
                "Км: " + RunRules.formatKm(distanceMeters) + " / 5,00   Время: "
                        + RunRules.formatTime(raceClock) + " / " + timeLimitStr,
                24, hudH - 60);
        FontAssets.drawHud(myGdxGame.getBatch(), myGdxGame.getUiFont(),
                "Очки: " + score + "  Рекорд: " + best + "  Темп: " + paceStr
                        + (displayPace < targetPace - 0.05f
                        ? "  цель " + RunRules.formatPaceMinPerKm(targetPace) : ""),
                24, hudH - 96);
    }

    private void drawIntroHintOverlay() {
        float worldW = myGdxGame.getWorldWidth();
        float worldH = myGdxGame.getWorldHeight();
        myGdxGame.applyViewport();
        myGdxGame.getBatch().begin();
        StatsPanel.drawBackdropDim(myGdxGame.getBatch(), worldW, worldH);

        float panelX = 240f;
        float panelY = 160f;
        float panelW = 800f;
        float panelH = 400f;
        StatsPanel.drawCard(myGdxGame.getBatch(), panelX, panelY, panelW, panelH);

        float y = panelY + panelH - StatsPanel.PAD;
        y = StatsPanel.drawSectionTitle(myGdxGame.getBatch(), myGdxGame.getUiFont(),
                "Как играть", panelX, panelW, y);
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Лужи",
                "перепрыгивай (тап), иначе замедляют", panelX, panelW, y);
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Пятюни",
                "собирай — ускоряют", panelX, panelW, y);
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Цель",
                "5 км за " + (int) currentCity.getTimeLimitMinutes() + " мин", panelX, panelW, y);
        y -= 8f;
        FontAssets.draw(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Лёгких ног!",
                panelX + 300f, y, FontAssets.HUD_DARK);
        StatsPanel.drawHintPill(myGdxGame.getBatch(), myGdxGame.getUiFont(),
                "Тап — начать забег", worldW / 2f, panelY - 24f);
        myGdxGame.getBatch().end();
    }

    private void drawPauseOverlay() {
        float worldW = myGdxGame.getWorldWidth();
        float worldH = myGdxGame.getWorldHeight();
        myGdxGame.applyViewport();
        myGdxGame.getBatch().begin();
        StatsPanel.drawBackdropDim(myGdxGame.getBatch(), worldW, worldH);
        StatsPanel.drawCard(myGdxGame.getBatch(), 340f, 220f, 600f, 320f);
        float y = 220f + 320f - StatsPanel.PAD;
        y = StatsPanel.drawSectionTitle(myGdxGame.getBatch(), myGdxGame.getUiFont(), "ПАУЗА", 340f, 600f, y);
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Продолжить",
                "тап по центру", 340f, 600f, y);
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Меню",
                "слева внизу", 340f, 600f, y);
        StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Клавиша",
                "P / Esc", 340f, 600f, y);
        myGdxGame.getBatch().end();
    }

    private void drawGameOverOverlay() {
        float worldW = myGdxGame.getWorldWidth();
        float worldH = myGdxGame.getWorldHeight();
        int best = myGdxGame.getSaveService().getHighScore();
        String timeStr = RunRules.formatTime(paceTracker.getRaceClockSeconds());
        String avgPace = RunRules.formatPaceMinPerKm(
                paceTracker.getAveragePaceMinPerKm(distanceMeters));
        String nowPace = RunRules.formatPaceMinPerKm(
                paceTracker.getDisplayPaceMinPerKm());

        myGdxGame.applyViewport();
        myGdxGame.getBatch().begin();
        StatsPanel.drawBackdropDim(myGdxGame.getBatch(), worldW, worldH);

        float panelX = 220f;
        float panelY = 90f;
        float panelW = 840f;
        float panelH = 540f;
        StatsPanel.drawCard(myGdxGame.getBatch(), panelX, panelY, panelW, panelH);

        float y = panelY + panelH - StatsPanel.PAD;
        y = StatsPanel.drawSectionTitle(myGdxGame.getBatch(), myGdxGame.getUiFont(),
                "НЕ УСПЕЛИ", panelX, panelW, y);
        if (!loseReason.isEmpty()) {
            y = StatsPanel.drawStatusChip(myGdxGame.getBatch(), myGdxGame.getUiFont(), loseReason,
                    false, panelX, y);
        }
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Очки",
                String.valueOf(score), panelX, panelW, y);
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Дистанция",
                RunRules.formatKm(distanceMeters) + " / 5,00 км", panelX, panelW, y);
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Время", timeStr,
                panelX, panelW, y);
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Средний темп", avgPace,
                panelX, panelW, y);
        y = StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Сейчас", nowPace,
                panelX, panelW, y);
        StatsPanel.drawRow(myGdxGame.getBatch(), myGdxGame.getUiFont(), "Рекорд",
                String.valueOf(best), panelX, panelW, y);

        StatsPanel.drawHintPill(myGdxGame.getBatch(), myGdxGame.getUiFont(),
                "Тап по центру — заново", worldW / 2f, 56f);
        StatsPanel.drawHintPill(myGdxGame.getBatch(), myGdxGame.getUiFont(),
                "Слева внизу — в меню", 160f, 56f);
        myGdxGame.getBatch().end();
    }

    private void goToMenu() {
        myGdxGame.showMenu();
    }

    @Override
    public void resize(int width, int height) {
        myGdxGame.resizeViewport(width, height);
        updateViewportLayout();
    }

    @Override
    public void pause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            myGdxGame.getAudioService().pauseMusic();
        }
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        myGdxGame.getAudioService().stopMusic();
    }

    @Override
    public void dispose() {
        runer.dispose();
        passingRunner.dispose();
        if (background != null) {
            background.dispose();
        }
        puddle.dispose();

        for (Pyatyunya pyatyunya : pyatyunyas) {
            pyatyunya.dispose();
        }
        pyatyunyas.clear();
        pyatyunyaAnimator.dispose();
    }
}

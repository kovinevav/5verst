package kovinevav.ru.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import kovinevav.ru.MyGdxGame;
import kovinevav.ru.game.components.PyatyunyaAnimator;

public class Pyatyunya {
    private static final float DRAW_SCALE = 0.5f;

    private final PyatyunyaAnimator animator;
    private final float animPhase;
    private final int respawnOffset;
    private float worldWidth = MyGdxGame.SCR_WIDTH;
    private int x;
    private int y;
    private float scrollSpeed;

    public Pyatyunya(int x, int y, float scrollSpeed, PyatyunyaAnimator animator,
                     int respawnOffset, float animPhase) {
        this.x = x;
        this.y = y;
        this.scrollSpeed = scrollSpeed;
        this.animator = animator;
        this.respawnOffset = respawnOffset;
        this.animPhase = animPhase;
    }

    public void setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void setWorldWidth(float worldWidth) {
        this.worldWidth = worldWidth;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void respawn() {
        x = (int) (worldWidth + respawnOffset);
    }

    public void update(float delta) {
        x -= (int) scrollSpeed;
        if (x + getWidth() < 0) {
            respawn();
        }
    }

    public void draw(Batch batch) {
        drawStatic(batch);
    }

    public void drawStatic(Batch batch) {
        Texture texture = animator.getFrame(animPhase);
        float w = texture.getWidth() * DRAW_SCALE;
        float h = texture.getHeight() * DRAW_SCALE;
        batch.draw(texture, x, y, w, h);
    }

    private int getWidth() {
        return Math.round(animator.getFrame(animPhase).getWidth() * DRAW_SCALE);
    }

    private int getHeight() {
        return Math.round(animator.getFrame(animPhase).getHeight() * DRAW_SCALE);
    }

    public boolean hitsRunner(PlayRuner runner) {
        int width = getWidth();
        int height = getHeight();
        float runnerRight = runner.getX() + runner.getWidth();
        if (runner.getX() >= x + width || runnerRight <= x) {
            return false;
        }
        return runner.getY() + runner.getHeight() > y && runner.getY() < y + height;
    }

    public void dispose() {
    }
}

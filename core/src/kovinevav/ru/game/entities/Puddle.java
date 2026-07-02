package kovinevav.ru.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Puddle {
    private final Texture texture;
    private final int groundY;
    private final int width;
    private final int height;
    private float scrollSpeed;
    private float x;
    private float spawnX;

    public void setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public Puddle(float x, int groundY, int scrollSpeed) {
        this.texture = new Texture("obstacles/puddle.png");
        this.x = x;
        this.spawnX = x;
        this.groundY = groundY;
        this.scrollSpeed = scrollSpeed;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    public void setSpawnX(float spawnX) {
        this.spawnX = spawnX;
    }

    public float getSpawnX() {
        return spawnX;
    }

    public void update() {
        x -= scrollSpeed;
        if (x + width < 0) {
            x = spawnX;
        }
    }
    public void draw(Batch batch) {
        update();
        drawStatic(batch);
    }

    public void drawStatic(Batch batch) {
        batch.draw(texture, x, groundY, width, height);
    }
    public float getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public boolean isPastRunner(PlayRuner runner) {
        return x + width < runner.getX();
    }

    public boolean overlapsRunnerX(PlayRuner runner) {
        float marginX = width * 0.22f;
        float hitLeft = x + marginX;
        float hitRight = x + width - marginX;
        float runnerRight = runner.getX() + runner.getWidth();
        return runner.getX() < hitRight && runnerRight > hitLeft;
    }

    public boolean hitsRunner(PlayRuner runner) {
        if (!overlapsRunnerX(runner)) {
            return false;
        }
        float wetLineY = groundY + height * 0.22f;
        return runner.getPreciseY() < wetLineY;
    }
    public void reset() {
        x = spawnX;
    }

    public void dispose() {
        texture.dispose();
    }
}

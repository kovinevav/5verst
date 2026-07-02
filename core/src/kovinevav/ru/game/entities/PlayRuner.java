package kovinevav.ru.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class PlayRuner extends AbstractRuner {
    private final int groundY;
    private float preciseY;
    private float verticalSpeed;
    private static final float GRAVITY = -1100f;
    private static final float JUMP_SPEED = 620f;
    private static final float GROUND_EPSILON = 2f;
    private static final float COYOTE_TIME = 0.12f;
    private float coyoteTimer;

    public PlayRuner(int x, int y, int width, int height) {
        this.x = x;
        this.groundY = y;
        this.y = y;
        this.preciseY = y;
        this.width = width;
        this.height = height;
        this.verticalSpeed = 0f;
        this.frameCounter = 0;

        framesArray = new Texture[]{
                new Texture("marina_run/move_001.png"),
                new Texture("marina_run/move_002.png"),
                new Texture("marina_run/move_003.png"),
                new Texture("marina_run/move_004.png"),
                new Texture("marina_run/move_005.png"),
                new Texture("marina_run/move_006.png"),
                new Texture("marina_run/move_007.png"),
                new Texture("marina_run/move_008.png"),
                new Texture("marina_run/move_009.png"),
                new Texture("marina_run/move_010.png"),
                new Texture("marina_run/move_011.png"),
                new Texture("marina_run/move_012.png"),
                new Texture("marina_run/move_013.png"),
                new Texture("marina_run/move_014.png"),
                new Texture("marina_run/move_015.png"),
        };
    }

    public boolean isOnGround() {
        return preciseY <= groundY + GROUND_EPSILON && verticalSpeed <= 0f;
    }

    public float getPreciseY() {
        return preciseY;
    }

    public boolean tryJump() {
        if (!isOnGround() && coyoteTimer <= 0f) {
            return false;
        }
        verticalSpeed = JUMP_SPEED;
        coyoteTimer = 0f;
        return true;
    }

    public void update(float delta) {
        preciseY += verticalSpeed * delta + 0.5f * GRAVITY * delta * delta;
        verticalSpeed += GRAVITY * delta;
        if (preciseY <= groundY) {
            preciseY = groundY;
            verticalSpeed = 0f;
            coyoteTimer = COYOTE_TIME;
        } else {
            coyoteTimer = Math.max(0f, coyoteTimer - delta);
        }
        y = Math.round(preciseY);
    }

    @Override
    public void draw(Batch batch) {
        draw(batch, true);
    }

    public void drawStatic(Batch batch) {
        draw(batch, false);
    }

    private void draw(Batch batch, boolean animate) {
        int frameMultiplier = 8;
        int frameIndex = frameCounter / frameMultiplier;
        batch.draw(framesArray[frameIndex], x, y, width, height);
        if (animate && isOnGround()) {
            frameCounter++;
            if (frameCounter >= framesArray.length * frameMultiplier) {
                frameCounter = 0;
            }
        }
    }

    public void reset() {
        preciseY = groundY;
        y = groundY;
        verticalSpeed = 0f;
        coyoteTimer = 0f;
        frameCounter = 0;
    }
}

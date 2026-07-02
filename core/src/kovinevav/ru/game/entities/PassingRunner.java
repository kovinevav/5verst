package kovinevav.ru.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class PassingRunner extends AbstractRuner {
    private boolean runFaster = true;

    public PassingRunner(int x, int y, int width, int height, Texture[] frameArray) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.framesArray = frameArray;
        frameCounter = 0;
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
        if (animate) {
            updatePosition();
        }
        batch.draw(framesArray[frameCounter / frameMultiplier], x, y, width, height);
        if (animate && frameCounter++ == framesArray.length * frameMultiplier - 1) {
            frameCounter = 0;
        }
    }

    private void updatePosition() {
        if (x < -500) {
            runFaster = true;
        }
        if (x > 150) {
            runFaster = false;
        }
        if (runFaster) {
            x++;
        } else {
            x--;
        }
    }
}

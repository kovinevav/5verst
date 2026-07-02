package kovinevav.ru.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class AbstractRuner {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Texture[] framesArray;
    protected int frameCounter;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void draw(Batch batch);

    public void dispose() {
        if (framesArray == null) {
            return;
        }
        for (Texture frame : framesArray) {
            frame.dispose();
        }
    }
}

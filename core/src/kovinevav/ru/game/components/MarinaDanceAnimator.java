package kovinevav.ru.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class MarinaDanceAnimator {
    private static final int FRAME_COUNT = 20;
    private static final int FRAME_MULTIPLIER = 6;

    private final Texture[] frames;
    private int frameCounter;

    public MarinaDanceAnimator() {
        frames = new Texture[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = new Texture("marina_dance/dance-" + i + ".png");
        }
    }

    public void update() {
        frameCounter++;
        int max = FRAME_COUNT * FRAME_MULTIPLIER;
        if (frameCounter >= max) {
            frameCounter = 0;
        }
    }

    public void draw(Batch batch, float x, float y, float maxWidth, float maxHeight) {
        int frameIndex = (frameCounter / FRAME_MULTIPLIER) % FRAME_COUNT;
        Texture frame = frames[frameIndex];
        float texW = frame.getWidth();
        float texH = frame.getHeight();
        float scale = Math.min(maxWidth / texW, maxHeight / texH);
        float drawW = texW * scale;
        float drawH = texH * scale;
        batch.draw(frame, x, y, drawW, drawH);
    }

    public void drawBottomRight(Batch batch, float worldWidth, float maxWidth, float maxHeight,
                                float marginRight, float marginBottom) {
        int frameIndex = (frameCounter / FRAME_MULTIPLIER) % FRAME_COUNT;
        Texture frame = frames[frameIndex];
        float texW = frame.getWidth();
        float texH = frame.getHeight();
        float scale = Math.min(maxWidth / texW, maxHeight / texH);
        float drawW = texW * scale;
        float drawH = texH * scale;
        float x = worldWidth - marginRight - drawW;
        float y = marginBottom;
        batch.draw(frame, x, y, drawW, drawH);
    }

    public void dispose() {
        for (Texture frame : frames) {
            frame.dispose();
        }
    }
}

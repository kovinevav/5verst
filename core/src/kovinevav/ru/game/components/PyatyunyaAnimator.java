package kovinevav.ru.game.components;

import com.badlogic.gdx.graphics.Texture;
public class PyatyunyaAnimator {
    private static final int FRAME_COUNT = 4;
    private static final float FRAME_DURATION = 0.15f;

    private final Texture[] frames;
    private float animTime;

    public PyatyunyaAnimator() {
        frames = new Texture[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = new Texture("pyatyunia/" + (i + 1) + ".png");
        }
    }

    public void update(float delta) {
        animTime += delta;
    }

    public Texture getFrame(float phaseOffset) {
        int index = (int) ((animTime + phaseOffset) / FRAME_DURATION) % FRAME_COUNT;
        if (index < 0) {
            index = (index % FRAME_COUNT + FRAME_COUNT) % FRAME_COUNT;
        }
        return frames[index];
    }

    public void dispose() {
        for (Texture frame : frames) {
            frame.dispose();
        }
    }
}

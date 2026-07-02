package kovinevav.ru.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Button {
    String path;
    Texture texture;
    int x, y;
    int buttonWidth, buttonHeight;

    public Button(int x, int y, String path) {
        this.path = path;
        this.x = x;
        this.y = y;
        texture = new Texture(path);
        buttonWidth = texture.getWidth();
        buttonHeight = texture.getHeight();
    }

    public void draw(Batch batch) {
        batch.draw(texture, x, y, buttonWidth, buttonHeight);
    }

    public boolean isHit(int tx, int ty) {
        return tx >= x && tx <= x + buttonWidth && ty >= y && ty <= y + buttonHeight;
    }

    public void dispose() {
        texture.dispose();
    }
}

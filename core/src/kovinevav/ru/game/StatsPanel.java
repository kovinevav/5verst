package kovinevav.ru.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;

public final class StatsPanel {
    public static final float PAD = 24f;
    public static final float ROW_STEP = 44f;

    private static Texture pixel;

    private StatsPanel() {
    }

    private static Texture getPixel() {
        if (pixel == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            pixel = new Texture(pixmap);
            pixmap.dispose();
        }
        return pixel;
    }

    public static void drawBackdropDim(Batch batch, float worldW, float worldH) {
        drawRect(batch, 0, 0, worldW, worldH,
                new Color(0.05f, 0.08f, 0.14f, 0.45f));
    }

    public static void drawCard(Batch batch, float x, float y, float w, float h) {
        drawRect(batch, x, y, w, h, new Color(0.97f, 0.98f, 0.99f, 0.92f));
        drawRect(batch, x, y + h - 6f, w, 6f, new Color(0.2f, 0.55f, 0.85f, 0.9f));
    }

    public static float drawSectionTitle(Batch batch, BitmapFont font, String title,
                                         float panelX, float panelW, float y) {
        FontAssets.draw(batch, font, title, panelX + PAD, y, FontAssets.HUD_DARK);
        return y - ROW_STEP;
    }

    public static float drawStatusChip(Batch batch, BitmapFont font, String text, boolean success,
                                       float panelX, float y) {
        Color bg = success
                ? new Color(0.2f, 0.65f, 0.35f, 0.25f)
                : new Color(0.85f, 0.35f, 0.2f, 0.25f);
        drawRect(batch, panelX + PAD, y - 30f, 460f, 36f, bg);
        FontAssets.draw(batch, font, text, panelX + PAD + 12f, y - 4f, FontAssets.HUD_DARK);
        return y - ROW_STEP - 8f;
    }

    public static float drawRow(Batch batch, BitmapFont font, String label, String value,
                                float panelX, float panelW, float y) {
        FontAssets.draw(batch, font, label, panelX + PAD, y, new Color(0.35f, 0.4f, 0.48f, 1f));
        FontAssets.draw(batch, font, value, panelX + panelW - PAD - measure(font, value), y,
                FontAssets.HUD_DARK);
        return y - ROW_STEP;
    }

    public static void drawHintPill(Batch batch, BitmapFont font, String text, float centerX, float y) {
        float textW = measure(font, text);
        float pillW = textW + 40f;
        float pillX = centerX - pillW / 2f;
        drawRect(batch, pillX, y - 8f, pillW, 36f, new Color(0.08f, 0.1f, 0.16f, 0.55f));
        FontAssets.draw(batch, font, text, centerX - textW / 2f, y + 18f, FontAssets.HUD_LIGHT);
    }

    public static void drawButton(Batch batch, BitmapFont font, String text,
                                  float x, float y, float minWidth, boolean primary,
                                  Rectangle bounds) {
        float textW = measure(font, text);
        float buttonW = Math.max(minWidth, textW + 48f);
        float buttonH = 52f;
        Color bg = primary
                ? new Color(0.18f, 0.58f, 0.88f, 0.96f)
                : new Color(0.12f, 0.16f, 0.24f, 0.88f);
        drawRect(batch, x, y, buttonW, buttonH, bg);
        if (!primary) {
            drawRect(batch, x, y + buttonH - 4f, buttonW, 4f,
                    new Color(0.2f, 0.55f, 0.85f, 0.75f));
        }
        FontAssets.draw(batch, font, text, x + (buttonW - textW) / 2f, y + 34f, FontAssets.HUD_LIGHT);
        bounds.set(x, y, buttonW, buttonH);
    }

    public static void drawButtonCentered(Batch batch, BitmapFont font, String text,
                                          float centerX, float y, float minWidth, boolean primary,
                                          Rectangle bounds) {
        float textW = measure(font, text);
        float buttonW = Math.max(minWidth, textW + 48f);
        drawButton(batch, font, text, centerX - buttonW / 2f, y, minWidth, primary, bounds);
    }

    private static float measure(BitmapFont font, String text) {
        GlyphLayout layout = new GlyphLayout(font, text);
        return layout.width;
    }

    private static void drawRect(Batch batch, float x, float y, float w, float h, Color color) {
        batch.setColor(color);
        batch.draw(getPixel(), x, y, w, h);
        batch.setColor(Color.WHITE);
    }

    public static void dispose() {
        if (pixel != null) {
            pixel.dispose();
            pixel = null;
        }
    }
}

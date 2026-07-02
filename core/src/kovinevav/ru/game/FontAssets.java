package kovinevav.ru.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public final class FontAssets {
    public static final Color HUD_DARK = new Color(0.08f, 0.1f, 0.16f, 1f);
    public static final Color HUD_LIGHT = new Color(0.92f, 0.94f, 0.96f, 1f);

    private static BitmapFont uiFont;

    private FontAssets() {
    }

    public static BitmapFont getUi() {
        if (uiFont == null) {
            uiFont = createUiFont(32);
        }
        return uiFont;
    }

    private static BitmapFont createUiFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/LiberationSans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
        param.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
                + "—–«»…№";
        BitmapFont font = generator.generateFont(param);
        generator.dispose();
        font.getData().setScale(1f);
        return font;
    }

    public static void draw(Batch batch, BitmapFont font, CharSequence text, float x, float y, Color color) {
        font.setColor(color);
        font.draw(batch, text, x, y);
        font.setColor(Color.WHITE);
    }

    public static void drawHud(Batch batch, BitmapFont font, CharSequence text, float x, float y) {
        draw(batch, font, text, x, y, HUD_DARK);
    }

    public static void dispose() {
        if (uiFont != null) {
            uiFont.dispose();
            uiFont = null;
        }
    }
}

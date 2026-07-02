package kovinevav.ru.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import kovinevav.ru.game.cities.CityRegistry;
public class CitySplashBackground {
    private Texture texture;
    private int loadedCityIndex = -1;

    public void ensureForCity(int cityIndex) {
        int index = CityRegistry.clampIndex(cityIndex);
        if (texture != null && loadedCityIndex == index) {
            return;
        }
        dispose();
        texture = new Texture(CityRegistry.getSplashPath(index));
        loadedCityIndex = index;
    }

    public void draw(Batch batch, float worldWidth, float worldHeight) {
        if (texture != null) {
            batch.draw(texture, 0, 0, worldWidth, worldHeight);
        }
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
        loadedCityIndex = -1;
    }
}

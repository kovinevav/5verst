package kovinevav.ru.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import kovinevav.ru.game.cities.CityRegistry;
import kovinevav.ru.game.services.SaveService;
public class CityFishkiDisplay {
    private final Texture[] fishki = new Texture[CityRegistry.CITY_COUNT];

    public CityFishkiDisplay() {
        for (int i = 0; i < CityRegistry.CITY_COUNT; i++) {
            fishki[i] = new Texture(CityRegistry.getFishkaPath(i));
        }
    }

    public void drawForLevelComplete(Batch batch, SaveService saveService,
                                     float worldWidth, float worldHeight) {
        drawVictoryGrid(batch, saveService, worldWidth, worldHeight);
    }

    private static final float VICTORY_CHIP_ALPHA = 0.38f;
    private static final float VICTORY_MARGIN_X = 28f;
    private static final float VICTORY_MARGIN_Y = 20f;
    private static final float VICTORY_GAP = 10f;

    private void drawVictoryGrid(Batch batch, SaveService saveService,
                                 float worldWidth, float worldHeight) {
        int slotCount = CityRegistry.CITY_COUNT;
        int completedCount = 0;
        for (int i = 0; i < slotCount; i++) {
            if (saveService.isCityCompleted(i)) {
                completedCount++;
            }
        }
        if (completedCount == 0) {
            return;
        }

        float availW = worldWidth - 2f * VICTORY_MARGIN_X;
        float availH = worldHeight - 2f * VICTORY_MARGIN_Y;

        int bestCols = 1;
        float bestChipSize = 0f;
        for (int cols = 1; cols <= slotCount; cols++) {
            int rows = (slotCount + cols - 1) / cols;
            float maxW = (availW - (cols - 1) * VICTORY_GAP) / cols;
            float maxH = (availH - (rows - 1) * VICTORY_GAP) / rows;
            float size = Math.min(maxW, maxH);
            if (size > bestChipSize) {
                bestChipSize = size;
                bestCols = cols;
            }
        }

        int cols = bestCols;
        int rows = (slotCount + cols - 1) / cols;
        float gridW = cols * bestChipSize + (cols - 1) * VICTORY_GAP;
        float gridH = rows * bestChipSize + (rows - 1) * VICTORY_GAP;
        float originX = VICTORY_MARGIN_X + (availW - gridW) / 2f;
        float originY = VICTORY_MARGIN_Y + (availH - gridH) / 2f;

        int drawn = 0;
        for (int i = 0; i < slotCount; i++) {
            if (!saveService.isCityCompleted(i)) {
                continue;
            }
            int col = drawn % cols;
            int row = drawn / cols;
            float x = originX + col * (bestChipSize + VICTORY_GAP);
            float y = originY + (rows - 1 - row) * (bestChipSize + VICTORY_GAP);
            batch.setColor(1f, 1f, 1f, VICTORY_CHIP_ALPHA);
            batch.draw(fishki[i], x, y, bestChipSize, bestChipSize);
            drawn++;
        }
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        for (Texture texture : fishki) {
            texture.dispose();
        }
    }
}

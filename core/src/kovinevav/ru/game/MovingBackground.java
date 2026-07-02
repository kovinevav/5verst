package kovinevav.ru.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import kovinevav.ru.MyGdxGame;

public class MovingBackground {
    private static final float SEAM_OVERLAP = 2f;

    private static class Segment {
        float x;
        Texture texture;
        float width;
    }

    private final Texture tileTexture;
    private final Texture finishTexture;
    private final Array<Segment> segments = new Array<>();
    private float speed = 2f;
    private boolean finishPhase;
    private boolean finishQueued;
    private float worldWidth = MyGdxGame.SCR_WIDTH;
    private float worldHeight = MyGdxGame.SCR_HEIGHT;
    private int activeSegmentCount;

    public MovingBackground(String tilePath) {
        this(tilePath, null, 0);
    }

    public MovingBackground(String tilePath, String finishPath, float speed) {
        tileTexture = new Texture(tilePath);
        tileTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        if (finishPath != null) {
            finishTexture = new Texture(finishPath);
            finishTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            finishTexture = null;
        }
        this.speed = speed;
        resetScroll();
    }

    public void setWorldSize(float worldWidth, float worldHeight) {
        float prevWidth = this.worldWidth;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        int needed = requiredSegmentCount();
        if (needed > activeSegmentCount) {
            appendSegments(needed - activeSegmentCount, rightEdge());
        } else if (worldWidth > prevWidth && activeSegmentCount > 0) {
            float edge = rightEdge();
            while (activeSegmentCount < needed) {
                appendSegments(1, edge);
                edge = rightEdge();
            }
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void resetScroll() {
        finishPhase = false;
        finishQueued = false;
        activeSegmentCount = requiredSegmentCount();
        float tileW = tileTexture.getWidth();
        while (segments.size < activeSegmentCount) {
            segments.add(new Segment());
        }
        for (int i = 0; i < activeSegmentCount; i++) {
            Segment segment = segments.get(i);
            segment.texture = tileTexture;
            segment.width = tileW;
            segment.x = i * tileW;
        }
        for (int i = activeSegmentCount; i < segments.size; i++) {
            segments.get(i).texture = null;
        }
    }

    public void beginFinishPhase() {
        if (finishTexture != null) {
            finishPhase = true;
        }
    }

    public void draw(Batch batch) {
        for (int i = 0; i < activeSegmentCount; i++) {
            drawSegment(batch, segments.get(i));
        }
    }

    public void drawCover(Batch batch) {
        batch.draw(tileTexture, 0, 0, worldWidth, worldHeight);
    }

    public void drawFrozen(Batch batch) {
        float tileW = tileTexture.getWidth();
        float x = 0f;
        while (x < worldWidth) {
            float drawW = Math.min(tileW + SEAM_OVERLAP, worldWidth - x);
            batch.draw(tileTexture, x, 0, drawW, worldHeight);
            x += tileW;
        }
    }

    private void drawSegment(Batch batch, Segment segment) {
        if (segment.texture == null) {
            return;
        }
        batch.draw(segment.texture, segment.x, 0, segment.width + SEAM_OVERLAP, worldHeight);
    }

    public void move() {
        float delta = speed;
        for (int i = 0; i < activeSegmentCount; i++) {
            segments.get(i).x -= delta;
        }
        if (finishQueued) {
            for (int i = 0; i < activeSegmentCount; i++) {
                clampFinishScroll(segments.get(i));
            }
        }
        for (int i = 0; i < activeSegmentCount; i++) {
            recycleIfNeeded(i);
        }
    }

    private int requiredSegmentCount() {
        int tileW = tileTexture.getWidth();
        return Math.max(2, (int) Math.ceil(worldWidth / tileW) + 1);
    }

    private float rightEdge() {
        float max = 0f;
        boolean any = false;
        for (int i = 0; i < activeSegmentCount; i++) {
            Segment segment = segments.get(i);
            if (segment.texture != null) {
                max = Math.max(max, segment.x + segment.width);
                any = true;
            }
        }
        return any ? max : worldWidth;
    }

    private float rightEdgeExcluding(int excludeIndex) {
        float max = 0f;
        boolean any = false;
        for (int i = 0; i < activeSegmentCount; i++) {
            if (i == excludeIndex) {
                continue;
            }
            Segment segment = segments.get(i);
            if (segment.texture != null) {
                max = Math.max(max, segment.x + segment.width);
                any = true;
            }
        }
        return any ? max : worldWidth;
    }

    private void appendSegments(int count, float startX) {
        float tileW = tileTexture.getWidth();
        float x = startX;
        while (segments.size < activeSegmentCount + count) {
            segments.add(new Segment());
        }
        for (int i = 0; i < count; i++) {
            Segment segment = segments.get(activeSegmentCount);
            segment.texture = tileTexture;
            segment.width = tileW;
            segment.x = x;
            x += tileW;
            activeSegmentCount++;
        }
    }

    private void clampFinishScroll(Segment segment) {
        if (segment.texture != finishTexture) {
            return;
        }
        float minX = worldWidth - segment.width;
        if (segment.x < minX) {
            segment.x = minX;
        }
    }

    private void recycleIfNeeded(int index) {
        Segment segment = segments.get(index);
        if (segment.texture == null || segment.x + segment.width > 0f) {
            return;
        }

        if (finishPhase && !finishQueued) {
            segment.texture = finishTexture;
            segment.width = finishTexture.getWidth();
            finishQueued = true;
        } else if (finishQueued && segment.texture == finishTexture) {
            return;
        } else if (finishQueued) {
            segment.texture = null;
            segment.width = 0f;
            return;
        } else {
            segment.texture = tileTexture;
            segment.width = tileTexture.getWidth();
        }

        segment.x = rightEdgeExcluding(index);
    }

    public void dispose() {
        tileTexture.dispose();
        if (finishTexture != null) {
            finishTexture.dispose();
        }
    }
}

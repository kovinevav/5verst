package kovinevav.ru.game.cities;

import kovinevav.ru.game.RunRules;

public class City {
    private final int index;
    private final String displayName;
    private final String backgroundPath;
    private final String finishBackgroundPath;
    private final float timeLimitSeconds;
    private final float targetPaceMinPerKm;

    public City(int index, String displayName, String backgroundPath,
                String finishBackgroundPath, float timeLimitSeconds, float targetPaceMinPerKm) {
        this.index = index;
        this.displayName = displayName;
        this.backgroundPath = backgroundPath;
        this.finishBackgroundPath = finishBackgroundPath;
        this.timeLimitSeconds = timeLimitSeconds;
        this.targetPaceMinPerKm = targetPaceMinPerKm;
    }

    public int getIndex() {
        return index;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public String getFinishBackgroundPath() {
        return finishBackgroundPath;
    }

    public boolean hasFinishBackground() {
        return finishBackgroundPath != null;
    }

    public float getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public float getTimeLimitMinutes() {
        return timeLimitSeconds / 60f;
    }

    public float getTargetPaceMinPerKm() {
        return targetPaceMinPerKm;
    }
}

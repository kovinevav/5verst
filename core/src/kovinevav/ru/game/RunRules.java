package kovinevav.ru.game;

import kovinevav.ru.game.cities.CityRegistry;

public final class RunRules {
    public static final float TARGET_DISTANCE_METERS = 5000f;
    public static final float TIME_LIMIT_SECONDS = 30f * 60f;
    public static final float TIME_LIMIT_DECREASE_PER_CITY_SECONDS = 60f;
    public static final float TARGET_PACE_MIN_PER_KM = 6f + 10f / 60f;
    public static final float METERS_PER_SPEED_UNIT_PER_SECOND = 16f;
    public static final float FINISH_BACKDROP_LEAD_METERS = 900f;
    private static final float DISTANCE_COMPLETION_TOLERANCE_METERS = 1f;

    private RunRules() {
    }

    public static float getTimeLimitSecondsForCity(int cityIndex) {
        int index = cityIndex;
        if (index < 0) {
            index = 0;
        } else if (index >= CityRegistry.CITY_COUNT) {
            index = CityRegistry.CITY_COUNT - 1;
        }
        return TIME_LIMIT_SECONDS - index * TIME_LIMIT_DECREASE_PER_CITY_SECONDS;
    }

    public static boolean hasCompletedRaceDistance(float distanceMeters) {
        return distanceMeters >= TARGET_DISTANCE_METERS - DISTANCE_COMPLETION_TOLERANCE_METERS;
    }

    public static boolean isRaceFinishedInTime(float distanceMeters, float raceClockSeconds,
                                               int cityIndex) {
        return isRaceFinishedInTime(distanceMeters, raceClockSeconds,
                CityRegistry.get(cityIndex).getTimeLimitSeconds());
    }

    public static boolean isRaceFinishedInTime(float distanceMeters, float raceClockSeconds,
                                               float timeLimitSeconds) {
        return hasCompletedRaceDistance(distanceMeters)
                && raceClockSeconds <= timeLimitSeconds + 0.5f;
    }

    public static String formatTime(float seconds) {
        int total = Math.max(0, (int) seconds);
        int min = total / 60;
        int sec = total % 60;
        return String.format("%02d:%02d", min, sec);
    }

    public static String formatKm(float meters) {
        return String.format("%.2f", meters / 1000f);
    }

    public static String formatPaceMinPerKm(float minPerKm) {
        if (minPerKm <= 0f || Float.isNaN(minPerKm) || Float.isInfinite(minPerKm)) {
            return "—";
        }
        int totalSec = Math.round(minPerKm * 60f);
        int min = totalSec / 60;
        int sec = totalSec % 60;
        return min + ":" + String.format("%02d", sec) + " мин/км";
    }
}

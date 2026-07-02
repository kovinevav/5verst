package kovinevav.ru.game.cities;

import kovinevav.ru.game.RunRules;

public final class CityRegistry {
    public static final int CITY_COUNT = 10;

    private static final City[] CITIES = {
            city(0, "Новосибирск",
                    "backgrounds/nsk_tile.png", "backgrounds/nsk_finish.png"),
            city(1, "Пермь",
                    "backgrounds/perm_tile.png", "backgrounds/perm_finish.png"),
            city(2, "Томск",
                    "backgrounds/tomsk_tile.png", "backgrounds/tomsk_finish.png"),
            city(3, "Барнаул",
                    "backgrounds/barnaul_tile.png", "backgrounds/barnaul_finish.png"),
            city(4, "Курган",
                    "backgrounds/kurgan_tile.png", "backgrounds/kurgan_finish.png"),
            city(5, "Екатеринбург",
                    "backgrounds/ekb_tile.png", "backgrounds/ekb_finish.png"),
            city(6, "Казань",
                    "backgrounds/kazan_tile.png", "backgrounds/kazan_finish.png"),
            city(7, "Москва",
                    "backgrounds/moscow_tile.png", "backgrounds/moscow_finish.png"),
            city(8, "Великий Новгород",
                    "backgrounds/novgorod_tile.png", "backgrounds/novgorod_finish.png"),
            city(9, "Санкт-Петербург",
                    "backgrounds/spb_tile.png", "backgrounds/spb_finish.png"),
    };

    private CityRegistry() {
    }

    private static City city(int index, String displayName,
                             String tilePath, String finishPath) {
        return new City(index, displayName, tilePath, finishPath,
                RunRules.getTimeLimitSecondsForCity(index),
                RunRules.TARGET_PACE_MIN_PER_KM);
    }

    public static City get(int index) {
        return CITIES[clampIndex(index)];
    }

    public static int clampIndex(int index) {
        if (index < 0 || index >= CITIES.length) {
            return 0;
        }
        return index;
    }

    public static String getSplashPath(int index) {
        return "cities/" + clampIndex(index) + ".png";
    }

    public static String getFishkaPath(int index) {
        return "fishki/" + clampIndex(index) + ".png";
    }

    public static City getNext(int currentIndex) {
        int next = currentIndex + 1;
        if (next >= CITIES.length) {
            return null;
        }
        return CITIES[next];
    }
}

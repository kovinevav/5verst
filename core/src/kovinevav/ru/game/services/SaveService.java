package kovinevav.ru.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import kovinevav.ru.game.cities.CityRegistry;

public class SaveService {
    private static final String PREFS_NAME = "5verst";
    private static final String KEY_HIGH_SCORE = "high_score";
    private static final String KEY_PYATYUNY_COLLECTED = "pyatyuny_collected";
    private static final String KEY_MUSIC_ENABLED = "music_enabled";
    private static final String KEY_SFX_ENABLED = "sfx_enabled";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_SFX_VOLUME = "sfx_volume";

    public static final String ACH_FIRST_PYATYUNYA = "ach_first_pyatyunya";
    public static final String ACH_SCORE_10 = "ach_score_10";
    public static final String ACH_DISTANCE_500 = "ach_distance_500";
    public static final String ACH_LEVEL_3 = "ach_level_3";
    public static final String ACH_PYATYUNY_20 = "ach_pyatyuny_20";
    public static final String ACH_COMPLETE_NSK = "ach_complete_nsk";
    public static final String ACH_COMPLETE_PERM = "ach_complete_perm";
    public static final String ACH_COMPLETE_TOMSK = "ach_complete_tomsk";
    public static final String ACH_COMPLETE_BARNAUL = "ach_complete_barnaul";
    public static final String ACH_COMPLETE_KURGAN = "ach_complete_kurgan";
    public static final String ACH_COMPLETE_EKB = "ach_complete_ekb";
    public static final String ACH_COMPLETE_KAZAN = "ach_complete_kazan";
    public static final String ACH_COMPLETE_MOSCOW = "ach_complete_moscow";
    public static final String ACH_COMPLETE_NOVGOROD = "ach_complete_novgorod";
    public static final String ACH_COMPLETE_SPB = "ach_complete_spb";

    private static final String KEY_CURRENT_CITY = "current_city";
    private static final String KEY_UNLOCKED_CITY = "unlocked_city_max";
    private static final String KEY_LAST_RUN_CITY = "last_run_city";
    private static final String KEY_LAST_RUN_PYATYUNY = "last_run_pyatyuny";
    private static final String KEY_LAST_RUN_TIME = "last_run_time_sec";
    private static final String KEY_LAST_RUN_AVG_PACE = "last_run_avg_pace";
    private static final String KEY_LAST_RUN_WON = "last_run_won";
    private static final String KEY_GAMEPLAY_HINT_SEEN = "gameplay_hint_seen";

    private final Preferences prefs;

    public SaveService() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public int getHighScore() {
        return prefs.getInteger(KEY_HIGH_SCORE, 0);
    }

    public void updateHighScore(int score) {
        if (score > getHighScore()) {
            prefs.putInteger(KEY_HIGH_SCORE, score);
            prefs.flush();
        }
    }

    public int getPyatyunyCollected() {
        return prefs.getInteger(KEY_PYATYUNY_COLLECTED, 0);
    }

    public void addPyatyunyCollected(int count) {
        if (count <= 0) {
            return;
        }
        prefs.putInteger(KEY_PYATYUNY_COLLECTED, getPyatyunyCollected() + count);
        prefs.flush();
    }

    public boolean isMusicEnabled() {
        return prefs.getBoolean(KEY_MUSIC_ENABLED, true);
    }

    public void setMusicEnabled(boolean enabled) {
        prefs.putBoolean(KEY_MUSIC_ENABLED, enabled);
        prefs.flush();
    }

    public boolean isSfxEnabled() {
        return prefs.getBoolean(KEY_SFX_ENABLED, true);
    }

    public void setSfxEnabled(boolean enabled) {
        prefs.putBoolean(KEY_SFX_ENABLED, enabled);
        prefs.flush();
    }

    public float getMusicVolume() {
        return prefs.getFloat(KEY_MUSIC_VOLUME, 0.5f);
    }

    public void setMusicVolume(float volume) {
        prefs.putFloat(KEY_MUSIC_VOLUME, Math.max(0f, Math.min(1f, volume)));
        prefs.flush();
    }

    public float getSfxVolume() {
        return prefs.getFloat(KEY_SFX_VOLUME, 0.8f);
    }

    public void setSfxVolume(float volume) {
        prefs.putFloat(KEY_SFX_VOLUME, Math.max(0f, Math.min(1f, volume)));
        prefs.flush();
    }

    public boolean isAchievementUnlocked(String id) {
        return prefs.getBoolean(id, false);
    }

    public void unlockAchievement(String id) {
        if (!isAchievementUnlocked(id)) {
            prefs.putBoolean(id, true);
            prefs.flush();
        }
    }

    public int getCurrentCityIndex() {
        return prefs.getInteger(KEY_CURRENT_CITY, 0);
    }

    public void setCurrentCityIndex(int index) {
        prefs.putInteger(KEY_CURRENT_CITY, Math.max(0, index));
        prefs.flush();
    }

    public int getMaxUnlockedCityIndex() {
        return prefs.getInteger(KEY_UNLOCKED_CITY, 0);
    }

    public void unlockCity(int cityIndex) {
        if (cityIndex > getMaxUnlockedCityIndex()) {
            prefs.putInteger(KEY_UNLOCKED_CITY, cityIndex);
            prefs.flush();
        }
    }

    public boolean isCityUnlocked(int cityIndex) {
        return cityIndex <= getMaxUnlockedCityIndex();
    }

    public void completeCity(int cityIndex) {
        unlockCity(cityIndex);
        String achievementId = achievementForCity(cityIndex);
        if (achievementId != null) {
            unlockAchievement(achievementId);
        }
        int nextCity = cityIndex + 1;
        if (nextCity < CityRegistry.CITY_COUNT) {
            unlockCity(nextCity);
        }
        if (cityIndex >= 3) {
            unlockAchievement(ACH_LEVEL_3);
        }
    }

    public boolean isCityCompleted(int cityIndex) {
        String achievementId = achievementForCity(cityIndex);
        return achievementId != null && isAchievementUnlocked(achievementId);
    }

    private static String achievementForCity(int cityIndex) {
        switch (cityIndex) {
            case 0:
                return ACH_COMPLETE_NSK;
            case 1:
                return ACH_COMPLETE_PERM;
            case 2:
                return ACH_COMPLETE_TOMSK;
            case 3:
                return ACH_COMPLETE_BARNAUL;
            case 4:
                return ACH_COMPLETE_KURGAN;
            case 5:
                return ACH_COMPLETE_EKB;
            case 6:
                return ACH_COMPLETE_KAZAN;
            case 7:
                return ACH_COMPLETE_MOSCOW;
            case 8:
                return ACH_COMPLETE_NOVGOROD;
            case 9:
                return ACH_COMPLETE_SPB;
            default:
                return null;
        }
    }

    public void saveLastRun(int cityIndex, int pyatyunyCount, float raceClockSeconds,
                           float averagePaceMinPerKm, boolean won) {
        prefs.putInteger(KEY_LAST_RUN_CITY, cityIndex);
        prefs.putInteger(KEY_LAST_RUN_PYATYUNY, pyatyunyCount);
        prefs.putFloat(KEY_LAST_RUN_TIME, raceClockSeconds);
        prefs.putFloat(KEY_LAST_RUN_AVG_PACE, averagePaceMinPerKm);
        prefs.putBoolean(KEY_LAST_RUN_WON, won);
        prefs.flush();
    }

    public int getLastRunCityIndex() {
        return prefs.getInteger(KEY_LAST_RUN_CITY, 0);
    }

    public int getLastRunPyatyunyCount() {
        return prefs.getInteger(KEY_LAST_RUN_PYATYUNY, 0);
    }

    public float getLastRunTimeSeconds() {
        return prefs.getFloat(KEY_LAST_RUN_TIME, 0f);
    }

    public float getLastRunAveragePaceMinPerKm() {
        return prefs.getFloat(KEY_LAST_RUN_AVG_PACE, 0f);
    }

    public boolean wasLastRunWon() {
        return prefs.getBoolean(KEY_LAST_RUN_WON, false);
    }

    public boolean hasSeenGameplayHint() {
        return prefs.getBoolean(KEY_GAMEPLAY_HINT_SEEN, false);
    }

    public void markGameplayHintSeen() {
        if (!hasSeenGameplayHint()) {
            prefs.putBoolean(KEY_GAMEPLAY_HINT_SEEN, true);
            prefs.flush();
        }
    }

    public void checkAchievements(int score, int distanceMeters, int pyatyunyThisRun) {
        if (pyatyunyThisRun >= 1) {
            unlockAchievement(ACH_FIRST_PYATYUNYA);
        }
        if (score >= 10) {
            unlockAchievement(ACH_SCORE_10);
        }
        if (distanceMeters >= 5000) {
            unlockAchievement(ACH_DISTANCE_500);
        }
        if (getPyatyunyCollected() >= 20) {
            unlockAchievement(ACH_PYATYUNY_20);
        }
    }

    public void resetAllProgress() {
        boolean musicEnabled = isMusicEnabled();
        boolean sfxEnabled = isSfxEnabled();
        float musicVolume = getMusicVolume();
        float sfxVolume = getSfxVolume();

        prefs.clear();

        setMusicEnabled(musicEnabled);
        setSfxEnabled(sfxEnabled);
        setMusicVolume(musicVolume);
        setSfxVolume(sfxVolume);
        prefs.flush();
    }
}

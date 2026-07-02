package kovinevav.ru.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class AudioService {
    private final SaveService saveService;
    private Music gameMusic;
    private Music winMusic;
    private Music theEndMusic;
    private Sound jumpSound;
    private Sound collectSound;
    private Sound gameOverSound;
    private Sound pauseSound;
    private String activeMusic = "";

    public AudioService(SaveService saveService) {
        this.saveService = saveService;
        loadAssets();
    }

    private void loadAssets() {
        gameMusic = loadMusic("audio/game.mp3", true);
        winMusic = loadMusic("audio/win.mp3", false);
        theEndMusic = loadMusic("audio/theEnd.mp3", false);
        jumpSound = loadSound("audio/jump.mp3");
        collectSound = loadSound("audio/collect.mp3");
        gameOverSound = loadSound("audio/gameOver.mp3");
        pauseSound = loadSound("audio/pause.mp3");
    }

    private Music loadMusic(String path, boolean looping) {
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            return null;
        }
        Music music = Gdx.audio.newMusic(file);
        music.setLooping(looping);
        return music;
    }

    private Sound loadSound(String path) {
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            return null;
        }
        return Gdx.audio.newSound(file);
    }

    public void playGameMusic() {
        playMusic(gameMusic, "game");
    }

    public void playVictoryMusic(boolean gameCompleted) {
        if (!saveService.isMusicEnabled()) {
            return;
        }
        Music track = gameCompleted ? theEndMusic : winMusic;
        String id = gameCompleted ? "theEnd" : "win";
        playMusic(track, id);
    }

    private void playMusic(Music music, String id) {
        if (!saveService.isMusicEnabled() || music == null) {
            return;
        }
        if (id.equals(activeMusic) && music.isPlaying()) {
            return;
        }
        stopMusic();
        activeMusic = id;
        music.setVolume(saveService.getMusicVolume());
        music.play();
    }

    public void stopMusic() {
        stopIfPlaying(gameMusic);
        stopIfPlaying(winMusic);
        stopIfPlaying(theEndMusic);
        activeMusic = "";
    }

    private static void stopIfPlaying(Music music) {
        if (music != null && music.isPlaying()) {
            music.stop();
        }
    }

    public void pauseMusic() {
        Music current = getActiveMusicTrack();
        if (current != null && current.isPlaying()) {
            current.pause();
        }
    }

    public void resumeMusic() {
        if (!saveService.isMusicEnabled()) {
            return;
        }
        Music current = getActiveMusicTrack();
        if (current != null) {
            current.setVolume(saveService.getMusicVolume());
            current.play();
        }
    }

    private Music getActiveMusicTrack() {
        switch (activeMusic) {
            case "game":
                return gameMusic;
            case "win":
                return winMusic;
            case "theEnd":
                return theEndMusic;
            default:
                return null;
        }
    }

    public void applyMusicSettings() {
        float volume = saveService.getMusicVolume();
        if (gameMusic != null) {
            gameMusic.setVolume(volume);
        }
        if (winMusic != null) {
            winMusic.setVolume(volume);
        }
        if (theEndMusic != null) {
            theEndMusic.setVolume(volume);
        }
        if (!saveService.isMusicEnabled()) {
            stopMusic();
        }
    }

    public void playPause() {
        playSfx(pauseSound);
    }

    public void playJump() {
        playSfx(jumpSound);
    }

    public void playCollect() {
        playSfx(collectSound);
    }

    public void playGameOver() {
        playSfx(gameOverSound);
    }

    private void playSfx(Sound sound) {
        if (!saveService.isSfxEnabled() || sound == null) {
            return;
        }
        sound.play(saveService.getSfxVolume());
    }

    public void dispose() {
        stopMusic();
        disposeMusic(gameMusic);
        disposeMusic(winMusic);
        disposeMusic(theEndMusic);
        disposeSound(jumpSound);
        disposeSound(collectSound);
        disposeSound(gameOverSound);
        disposeSound(pauseSound);
    }

    private static void disposeMusic(Music music) {
        if (music != null) {
            music.dispose();
        }
    }

    private static void disposeSound(Sound sound) {
        if (sound != null) {
            sound.dispose();
        }
    }
}

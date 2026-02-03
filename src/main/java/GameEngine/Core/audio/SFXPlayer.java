package GameEngine.Core.audio;

import GameEngine.Core.gameObject.GameObject;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GameObject wrapper for playing SFX sounds in the game engine.
 * Allows loading and playing 8-bit sound effects created with SFXGenerator.
 *
 * Usage:
 *   SFXPlayer player = new SFXPlayer();
 *   player.loadSFX("jump", "assets/sfx/jump.sfx");
 *   player.loadSFX("explosion", "assets/sfx/explosion.sfx");
 *   objectManager.add(player);
 *
 *   // Later in game:
 *   player.play("jump");
 *   player.play("explosion", 0.8f); // with volume
 */
public class SFXPlayer extends GameObject {

    private Map<String, SFXEngine> loadedSounds = new HashMap<>();
    private Map<String, byte[]> cachedAudio = new HashMap<>();
    private float globalVolume = 1.0f;

    public SFXPlayer() {
        this.tag = "SFXPlayer";
    }

    /**
     * Loads an SFX file and registers it with a name.
     * @param name The name to reference this sound
     * @param path Path to the .sfx file
     * @return true if loaded successfully
     */
    public boolean loadSFX(String name, String path) {
        try {
            SFXEngine engine = new SFXEngine();
            engine.loadFromFile(path);
            loadedSounds.put(name, engine);
            // Pre-cache the audio data for faster playback
            cachedAudio.put(name, engine.synthesize());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to load SFX '" + name + "' from " + path + ": " + e.getMessage());
            return false;
        }
    }

    public boolean loadSFX(String name) {
        String path = "assets/sfx/" + name + ".sfx";
        return loadSFX(name, path);
    }
    /**
     * Registers an SFXEngine instance directly.
     * @param name The name to reference this sound
     * @param engine The SFXEngine with configured parameters
     */
    public void registerSFX(String name, SFXEngine engine) {
        loadedSounds.put(name, engine);
        cachedAudio.put(name, engine.synthesize());
    }

    /**
     * Regenerates cached audio for a sound (call after modifying engine params).
     * @param name The sound name
     */
    public void regenerateCache(String name) {
        SFXEngine engine = loadedSounds.get(name);
        if (engine != null) {
            cachedAudio.put(name, engine.synthesize());
        }
    }

    /**
     * Plays a loaded sound effect.
     * @param name The name of the sound to play
     */
    public void play(String name) {
        play(name, 1.0f);
    }

    /**
     * Plays a loaded sound effect with volume adjustment.
     * @param name The name of the sound to play
     * @param volume Volume multiplier (0.0 - 1.0)
     */
    public void play(String name, float volume) {
        byte[] audioData = cachedAudio.get(name);
        if (audioData != null) {
            // Apply volume
            float finalVolume = Math.max(0f, Math.min(1f, volume * globalVolume));
            if (finalVolume < 1.0f) {
                byte[] adjustedData = applyVolume(audioData, finalVolume);
                SFXEngine.playAudioData(adjustedData);
            } else {
                SFXEngine.playAudioData(audioData);
            }
        } else {
            System.err.println("SFX not found: " + name);
        }
    }

    /**
     * Plays a sound effect if it exists, no error if not found.
     * @param name The name of the sound to play
     */
    public void playIfExists(String name) {
        if (cachedAudio.containsKey(name)) {
            play(name);
        }
    }

    /**
     * Checks if a sound is loaded.
     * @param name The sound name
     * @return true if the sound is loaded
     */
    public boolean hasSound(String name) {
        return loadedSounds.containsKey(name);
    }

    /**
     * Gets the SFXEngine for a loaded sound (for parameter tweaking).
     * @param name The sound name
     * @return The SFXEngine or null if not found
     */
    public SFXEngine getEngine(String name) {
        return loadedSounds.get(name);
    }

    /**
     * Removes a loaded sound.
     * @param name The sound name
     */
    public void unloadSFX(String name) {
        loadedSounds.remove(name);
        cachedAudio.remove(name);
    }

    /**
     * Removes all loaded sounds.
     */
    public void unloadAll() {
        loadedSounds.clear();
        cachedAudio.clear();
    }

    /**
     * Sets the global volume multiplier for all sounds.
     * @param volume Volume (0.0 - 1.0)
     */
    public void setGlobalVolume(float volume) {
        this.globalVolume = Math.max(0f, Math.min(1f, volume));
    }

    /**
     * Gets the global volume multiplier.
     * @return The global volume
     */
    public float getGlobalVolume() {
        return globalVolume;
    }

    private byte[] applyVolume(byte[] audioData, float volume) {
        byte[] result = new byte[audioData.length];
        for (int i = 0; i < audioData.length; i += 2) {
            short sample = (short) ((audioData[i] & 0xff) | (audioData[i + 1] << 8));
            sample = (short) (sample * volume);
            result[i] = (byte) (sample & 0xff);
            result[i + 1] = (byte) ((sample >> 8) & 0xff);
        }
        return result;
    }

    @Override
    public void init() {}

    @Override
    public void update(double deltaTime) {}

    @Override
    public void draw(Graphics2D g) {}

    @Override
    public void onCollision(GameObject collider) {}
}

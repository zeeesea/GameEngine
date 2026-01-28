package GameEngine.Core.graphics;

import java.awt.Color;
import java.util.List;

public class Animation {

    private List<Color[][]> frames;
    private int currentFrameIndex = 0;

    private float frameDuration;
    private float timer = 0f;

    private boolean loop = true;
    private boolean playing = false;
    private boolean finished = false;

    public Animation(List<Color[][]> frames, int fps, boolean loop) {
        this.frames = frames;
        this.frameDuration = 1.0f / Math.max(1, fps);
        this.loop = loop;
    }

    public void update(float deltaTime) {
        if (!playing || frames == null || frames.isEmpty()) return;

        timer += deltaTime;

        while (timer >= frameDuration) {
            timer -= frameDuration;
            currentFrameIndex++;

            if (currentFrameIndex >= frames.size()) {
                if (loop) {
                    currentFrameIndex = 0;
                } else {
                    currentFrameIndex = frames.size() - 1;
                    playing = false;
                    finished = true;
                }
            }
        }
    }

    public Color[][] getCurrentFrame() {
        if (frames == null || frames.isEmpty()) return null;
        return frames.get(Math.min(currentFrameIndex, frames.size() - 1));
    }

    public void play() {
        playing = true;
        finished = false;
    }

    public void playFromStart() {
        reset();
        play();
    }

    public void stop() {
        playing = false;
    }

    public void reset() {
        currentFrameIndex = 0;
        timer = 0f;
        finished = false;
    }

    public void skipFrame() {
        if (frames == null || frames.isEmpty()) return;

        currentFrameIndex++;
        if (currentFrameIndex >= frames.size()) {
            if (loop) {
                currentFrameIndex = 0;
            } else {
                currentFrameIndex = frames.size() - 1;
                playing = false;
                finished = true;
            }
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFPS(int fps) {
        this.frameDuration = 1.0f / Math.max(1, fps);
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public int getFrameCount() {
        return frames != null ? frames.size() : 0;
    }

    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }
}
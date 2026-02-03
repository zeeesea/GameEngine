package GameEngine.Core.audio;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Random;

/**
 * 8-bit Sound Effect Synthesizer Engine.
 * Generates retro-style sound effects similar to SFXR/BFXR.
 */
public class SFXEngine {

    // === Waveform Types ===
    public enum WaveType {
        SQUARE,
        SAWTOOTH,
        SINE,
        NOISE,
        TRIANGLE,
        BREAKER
    }

    // === Sound Parameters ===
    private WaveType waveType = WaveType.SQUARE;

    // Envelope
    private float attackTime = 0f;      // 0-1 seconds
    private float sustainTime = 0.3f;   // 0-1 seconds
    private float sustainPunch = 0f;    // 0-1
    private float decayTime = 0.4f;     // 0-1 seconds

    // Frequency
    private float startFrequency = 0.3f;    // 0-1 (mapped to Hz)
    private float minFrequency = 0f;         // 0-1
    private float slide = 0f;                // -1 to 1
    private float deltaSlide = 0f;           // -1 to 1

    // Vibrato
    private float vibratoDepth = 0f;    // 0-1
    private float vibratoSpeed = 0f;    // 0-1

    // Arpeggiation
    private float arpMod = 0f;          // -1 to 1
    private float arpSpeed = 0f;        // 0-1

    // Duty Cycle (for square wave)
    private float squareDuty = 0.5f;    // 0-1
    private float dutySweep = 0f;       // -1 to 1

    // Repeat
    private float repeatSpeed = 0f;     // 0-1

    // Phaser
    private float phaserOffset = 0f;    // -1 to 1
    private float phaserSweep = 0f;     // -1 to 1

    // Low-pass Filter
    private float lpFilterCutoff = 1f;      // 0-1
    private float lpFilterCutoffSweep = 0f; // -1 to 1
    private float lpFilterResonance = 0f;   // 0-1

    // High-pass Filter
    private float hpFilterCutoff = 0f;      // 0-1
    private float hpFilterCutoffSweep = 0f; // -1 to 1

    // Master
    private float masterVolume = 0.5f;  // 0-1

    // Audio settings
    private static final int SAMPLE_RATE = 44100;
    private static final int BITS_PER_SAMPLE = 16;

    private Random random = new Random();

    // === Presets ===

    public void resetParams() {
        waveType = WaveType.SQUARE;
        attackTime = 0f;
        sustainTime = 0.3f;
        sustainPunch = 0f;
        decayTime = 0.4f;
        startFrequency = 0.3f;
        minFrequency = 0f;
        slide = 0f;
        deltaSlide = 0f;
        vibratoDepth = 0f;
        vibratoSpeed = 0f;
        arpMod = 0f;
        arpSpeed = 0f;
        squareDuty = 0.5f;
        dutySweep = 0f;
        repeatSpeed = 0f;
        phaserOffset = 0f;
        phaserSweep = 0f;
        lpFilterCutoff = 1f;
        lpFilterCutoffSweep = 0f;
        lpFilterResonance = 0f;
        hpFilterCutoff = 0f;
        hpFilterCutoffSweep = 0f;
        masterVolume = 0.5f;
    }

    public void generatePickup() {
        resetParams();
        waveType = random.nextBoolean() ? WaveType.SQUARE : WaveType.SINE;
        startFrequency = 0.4f + random.nextFloat() * 0.5f;
        sustainTime = 0.1f + random.nextFloat() * 0.1f;
        decayTime = 0.1f + random.nextFloat() * 0.2f;
        sustainPunch = 0.3f + random.nextFloat() * 0.3f;
        if (random.nextBoolean()) {
            arpSpeed = 0.5f + random.nextFloat() * 0.2f;
            arpMod = 0.2f + random.nextFloat() * 0.4f;
        }
    }

    public void generateLaser() {
        resetParams();
        waveType = WaveType.values()[random.nextInt(3)]; // Square, Saw, Sine
        if (waveType == WaveType.SINE && random.nextBoolean()) {
            waveType = WaveType.NOISE;
        }
        startFrequency = 0.5f + random.nextFloat() * 0.5f;
        minFrequency = startFrequency - 0.2f - random.nextFloat() * 0.6f;
        if (minFrequency < 0.2f) minFrequency = 0.2f;
        slide = -0.15f - random.nextFloat() * 0.2f;
        sustainTime = 0.1f + random.nextFloat() * 0.2f;
        decayTime = random.nextFloat() * 0.4f;
        if (random.nextBoolean()) {
            sustainPunch = random.nextFloat() * 0.3f;
        }
    }

    public void generateExplosion() {
        resetParams();
        waveType = WaveType.NOISE;
        startFrequency = 0.1f + random.nextFloat() * 0.3f;
        slide = -0.1f + random.nextFloat() * 0.2f;
        sustainTime = 0.1f + random.nextFloat() * 0.3f;
        decayTime = random.nextFloat() * 0.5f;
        sustainPunch = 0.2f + random.nextFloat() * 0.6f;
        if (random.nextBoolean()) {
            phaserOffset = -0.3f + random.nextFloat() * 0.9f;
            phaserSweep = -random.nextFloat() * 0.3f;
        }
        if (random.nextBoolean()) {
            vibratoDepth = random.nextFloat() * 0.7f;
            vibratoSpeed = random.nextFloat() * 0.6f;
        }
    }

    public void generatePowerup() {
        resetParams();
        waveType = random.nextBoolean() ? WaveType.SQUARE : WaveType.SAWTOOTH;
        if (random.nextBoolean()) {
            startFrequency = 0.2f + random.nextFloat() * 0.3f;
            slide = 0.1f + random.nextFloat() * 0.4f;
            repeatSpeed = 0.4f + random.nextFloat() * 0.4f;
        } else {
            startFrequency = 0.2f + random.nextFloat() * 0.3f;
            slide = 0.05f + random.nextFloat() * 0.2f;
            if (random.nextBoolean()) {
                vibratoDepth = random.nextFloat() * 0.7f;
                vibratoSpeed = random.nextFloat() * 0.6f;
            }
        }
        sustainTime = random.nextFloat() * 0.4f;
        decayTime = 0.1f + random.nextFloat() * 0.4f;
    }

    public void generateHit() {
        resetParams();
        waveType = WaveType.values()[random.nextInt(3)];
        if (waveType == WaveType.SINE) waveType = WaveType.NOISE;
        if (waveType == WaveType.SQUARE) squareDuty = random.nextFloat();
        startFrequency = 0.2f + random.nextFloat() * 0.6f;
        slide = -0.3f - random.nextFloat() * 0.4f;
        sustainTime = random.nextFloat() * 0.1f;
        decayTime = random.nextFloat() * 0.2f;
        if (random.nextBoolean()) {
            hpFilterCutoff = random.nextFloat() * 0.3f;
        }
    }

    public void generateJump() {
        resetParams();
        waveType = WaveType.SQUARE;
        squareDuty = random.nextFloat();
        startFrequency = 0.3f + random.nextFloat() * 0.3f;
        slide = 0.1f + random.nextFloat() * 0.2f;
        sustainTime = 0.1f + random.nextFloat() * 0.3f;
        decayTime = 0.1f + random.nextFloat() * 0.2f;
        if (random.nextBoolean()) {
            hpFilterCutoff = random.nextFloat() * 0.3f;
        }
        if (random.nextBoolean()) {
            lpFilterCutoff = 1f - random.nextFloat() * 0.6f;
        }
    }

    public void generateBlip() {
        resetParams();
        waveType = WaveType.values()[random.nextInt(2)]; // Square or Saw
        if (waveType == WaveType.SQUARE) squareDuty = random.nextFloat();
        startFrequency = 0.2f + random.nextFloat() * 0.4f;
        sustainTime = 0.1f + random.nextFloat() * 0.1f;
        decayTime = random.nextFloat() * 0.2f;
        hpFilterCutoff = 0.1f;
    }

    public void mutate() {
        if (random.nextBoolean()) startFrequency += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) slide += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) deltaSlide += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) squareDuty += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) dutySweep += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) vibratoDepth += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) vibratoSpeed += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) attackTime += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) sustainTime += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) decayTime += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) sustainPunch += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) lpFilterCutoff += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) lpFilterCutoffSweep += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) lpFilterResonance += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) hpFilterCutoff += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) hpFilterCutoffSweep += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) phaserOffset += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) phaserSweep += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) repeatSpeed += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) arpSpeed += random.nextFloat() * 0.1f - 0.05f;
        if (random.nextBoolean()) arpMod += random.nextFloat() * 0.1f - 0.05f;
        clampParams();
    }

    public void randomize() {
        waveType = WaveType.values()[random.nextInt(WaveType.values().length)];
        attackTime = random.nextFloat() * random.nextFloat() * 0.4f;
        sustainTime = random.nextFloat() * random.nextFloat() * 0.4f;
        sustainPunch = random.nextFloat() * random.nextFloat() * 0.8f;
        decayTime = random.nextFloat() * random.nextFloat() * 0.4f + 0.1f;
        startFrequency = random.nextFloat() * random.nextFloat();
        if (random.nextBoolean()) startFrequency = startFrequency * startFrequency * startFrequency + 0.1f;
        minFrequency = 0f;
        slide = random.nextFloat() * random.nextFloat() * 2f - 1f;
        deltaSlide = random.nextFloat() * random.nextFloat() * random.nextFloat();
        squareDuty = random.nextFloat();
        dutySweep = random.nextFloat() * random.nextFloat() * 2f - 1f;
        vibratoDepth = random.nextFloat() * random.nextFloat() * random.nextFloat();
        vibratoSpeed = random.nextFloat();
        arpMod = random.nextFloat() * random.nextFloat() * 2f - 1f;
        arpSpeed = random.nextFloat() * random.nextFloat();
        repeatSpeed = random.nextFloat() * random.nextFloat();
        phaserOffset = random.nextFloat() * random.nextFloat() * 2f - 1f;
        phaserSweep = random.nextFloat() * random.nextFloat() * 2f - 1f;
        lpFilterCutoff = 1f - random.nextFloat() * random.nextFloat();
        lpFilterCutoffSweep = random.nextFloat() * random.nextFloat() * 2f - 1f;
        lpFilterResonance = random.nextFloat() * random.nextFloat();
        hpFilterCutoff = random.nextFloat() * random.nextFloat() * random.nextFloat();
        hpFilterCutoffSweep = random.nextFloat() * random.nextFloat() * 2f - 1f;
        clampParams();
    }

    private void clampParams() {
        startFrequency = clamp(startFrequency, 0f, 1f);
        minFrequency = clamp(minFrequency, 0f, 1f);
        slide = clamp(slide, -1f, 1f);
        deltaSlide = clamp(deltaSlide, -1f, 1f);
        squareDuty = clamp(squareDuty, 0f, 1f);
        dutySweep = clamp(dutySweep, -1f, 1f);
        vibratoDepth = clamp(vibratoDepth, 0f, 1f);
        vibratoSpeed = clamp(vibratoSpeed, 0f, 1f);
        attackTime = clamp(attackTime, 0f, 1f);
        sustainTime = clamp(sustainTime, 0f, 1f);
        sustainPunch = clamp(sustainPunch, 0f, 1f);
        decayTime = clamp(decayTime, 0f, 1f);
        lpFilterCutoff = clamp(lpFilterCutoff, 0f, 1f);
        lpFilterCutoffSweep = clamp(lpFilterCutoffSweep, -1f, 1f);
        lpFilterResonance = clamp(lpFilterResonance, 0f, 1f);
        hpFilterCutoff = clamp(hpFilterCutoff, 0f, 1f);
        hpFilterCutoffSweep = clamp(hpFilterCutoffSweep, -1f, 1f);
        phaserOffset = clamp(phaserOffset, -1f, 1f);
        phaserSweep = clamp(phaserSweep, -1f, 1f);
        repeatSpeed = clamp(repeatSpeed, 0f, 1f);
        arpMod = clamp(arpMod, -1f, 1f);
        arpSpeed = clamp(arpSpeed, 0f, 1f);
        masterVolume = clamp(masterVolume, 0f, 1f);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    // === Sound Synthesis ===

    public byte[] synthesize() {
        clampParams();

        // Convert params to actual values
        int envelopeLength = (int) ((attackTime * attackTime * 100000f +
                sustainTime * sustainTime * 100000f +
                decayTime * decayTime * 100000f) * SAMPLE_RATE / 44100f);
        if (envelopeLength < 1) envelopeLength = 1;

        int attackSamples = (int) (attackTime * attackTime * 100000f * SAMPLE_RATE / 44100f);
        int sustainSamples = (int) (sustainTime * sustainTime * 100000f * SAMPLE_RATE / 44100f);
        int decaySamples = envelopeLength - attackSamples - sustainSamples;
        if (decaySamples < 0) decaySamples = 0;

        float fperiod = 100f / (startFrequency * startFrequency + 0.001f);
        float fmaxperiod = 100f / (minFrequency * minFrequency + 0.001f);
        float fslide = 1f - slide * slide * slide * 0.01f;
        float fdslide = -deltaSlide * deltaSlide * deltaSlide * 0.000001f;

        float squareDutyCurrent = 0.5f - squareDuty * 0.5f;
        float squareDutySlide = -dutySweep * 0.00005f;

        int arpTime = 0;
        int arpLimit = (int) ((1f - arpSpeed) * (1f - arpSpeed) * 20000 + 32);
        if (arpSpeed == 1f) arpLimit = 0;
        float arpModCurrent = 1f - arpMod * (arpMod < 0 ? -1 : 1) * arpMod;
        if (arpMod >= 0) arpModCurrent = 1f / arpModCurrent;

        // Vibrato
        float vibratoPhase = 0f;
        float vibratoPhaseInc = vibratoSpeed * vibratoSpeed * 0.01f;
        float vibratoAmp = vibratoDepth * 0.5f;

        // Phaser
        int phaserBufferSize = 1024;
        float[] phaserBuffer = new float[phaserBufferSize];
        int phaserPos = 0;
        float phaserOffsetCurrent = phaserOffset * phaserOffset * 1020f;
        if (phaserOffset < 0) phaserOffsetCurrent = -phaserOffsetCurrent;
        float phaserOffsetSlide = phaserSweep * phaserSweep * phaserSweep * 20f;

        // Filters
        float fltp = 0f;
        float fltdp = 0f;
        float fltw = (float) Math.pow(lpFilterCutoff, 3f) * 0.1f;
        float fltw_d = 1f + lpFilterCutoffSweep * 0.0001f;
        float fltdmp = 5f / (1f + lpFilterResonance * lpFilterResonance * 20f) * (0.01f + fltw);
        if (fltdmp > 0.8f) fltdmp = 0.8f;
        float fltphp = 0f;
        float flthp = (float) Math.pow(hpFilterCutoff, 2f) * 0.1f;
        float flthp_d = 1f + hpFilterCutoffSweep * 0.0003f;

        // Repeat
        int repeatLimit = (int) ((1f - repeatSpeed) * (1f - repeatSpeed) * 20000 + 32);
        if (repeatSpeed == 0f) repeatLimit = 0;
        int repeatTime = 0;

        // Noise buffer
        float[] noiseBuffer = new float[32];
        for (int i = 0; i < noiseBuffer.length; i++) {
            noiseBuffer[i] = random.nextFloat() * 2f - 1f;
        }

        // Generate samples
        float[] samples = new float[envelopeLength];
        float phase = 0f;
        int noiseIndex = 0;

        for (int i = 0; i < envelopeLength; i++) {
            // Repeat
            if (repeatLimit != 0 && ++repeatTime >= repeatLimit) {
                repeatTime = 0;
                fperiod = 100f / (startFrequency * startFrequency + 0.001f);
            }

            // Arpeggio
            if (arpLimit != 0 && ++arpTime >= arpLimit) {
                arpLimit = 0;
                fperiod *= arpModCurrent;
            }

            // Frequency slide
            fslide += fdslide;
            fperiod *= fslide;
            if (fperiod > fmaxperiod) {
                fperiod = fmaxperiod;
            }

            // Vibrato
            float rfperiod = fperiod;
            if (vibratoAmp > 0) {
                vibratoPhase += vibratoPhaseInc;
                rfperiod = fperiod * (1f + (float) Math.sin(vibratoPhase) * vibratoAmp);
            }

            // Period to increment
            float fpinc = (float) (SAMPLE_RATE / rfperiod);

            // Duty
            squareDutyCurrent = clamp(squareDutyCurrent + squareDutySlide, 0f, 0.5f);

            // Envelope
            float envelope;
            if (i < attackSamples) {
                envelope = (float) i / attackSamples;
            } else if (i < attackSamples + sustainSamples) {
                envelope = 1f + (1f - (float) (i - attackSamples) / sustainSamples) * 2f * sustainPunch;
            } else {
                envelope = 1f - (float) (i - attackSamples - sustainSamples) / decaySamples;
            }

            // Phaser
            phaserOffsetCurrent += phaserOffsetSlide;
            int iphaser = Math.abs((int) phaserOffsetCurrent);
            if (iphaser > phaserBufferSize - 1) iphaser = phaserBufferSize - 1;

            // HP/LP filters
            flthp = clamp(flthp * flthp_d, 0.00001f, 0.1f);
            fltw = clamp(fltw * fltw_d, 0f, 0.1f);

            // Waveform generation
            float sample;
            phase += 1f / rfperiod;
            while (phase >= 1f) phase -= 1f;

            switch (waveType) {
                case SINE:
                    sample = (float) Math.sin(phase * 2 * Math.PI);
                    break;
                case SQUARE:
                    sample = phase < squareDutyCurrent ? 0.5f : -0.5f;
                    break;
                case SAWTOOTH:
                    sample = 1f - phase * 2f;
                    break;
                case TRIANGLE:
                    sample = Math.abs(1f - phase * 2f) * 2f - 1f;
                    break;
                case NOISE:
                    if ((int) (phase * 32) != noiseIndex) {
                        noiseIndex = (int) (phase * 32);
                        noiseBuffer[noiseIndex % noiseBuffer.length] = random.nextFloat() * 2f - 1f;
                    }
                    sample = noiseBuffer[(int) (phase * 32) % noiseBuffer.length];
                    break;
                case BREAKER:
                    sample = Math.abs(1f - phase * phase * 2f) * 2f - 1f;
                    break;
                default:
                    sample = 0f;
            }

            // Low-pass filter
            float pp = fltp;
            fltdp += (sample - fltp) * fltw;
            fltdp -= fltdp * fltdmp;
            fltp += fltdp;

            // High-pass filter
            fltphp += fltp - pp;
            fltphp -= fltphp * flthp;
            sample = fltphp;

            // Phaser
            phaserBuffer[phaserPos % phaserBufferSize] = sample;
            sample += phaserBuffer[(phaserPos - iphaser + phaserBufferSize) % phaserBufferSize];
            phaserPos = (phaserPos + 1) % phaserBufferSize;

            // Final sample
            samples[i] = sample * envelope * masterVolume;
        }

        // Normalize
        float maxAmp = 0f;
        for (float s : samples) {
            if (Math.abs(s) > maxAmp) maxAmp = Math.abs(s);
        }
        if (maxAmp > 0f) {
            float normalizeAmp = 0.9f / maxAmp;
            for (int i = 0; i < samples.length; i++) {
                samples[i] *= normalizeAmp;
            }
        }

        // Convert to byte array (16-bit PCM)
        byte[] audioData = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            short s = (short) (samples[i] * 32767);
            audioData[i * 2] = (byte) (s & 0xff);
            audioData[i * 2 + 1] = (byte) ((s >> 8) & 0xff);
        }

        return audioData;
    }

    public void play() {
        byte[] audioData = synthesize();
        playAudioData(audioData);
    }

    public static void playAudioData(byte[] audioData) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            // Play in separate thread
            new Thread(() -> {
                line.write(audioData, 0, audioData.length);
                line.drain();
                line.close();
            }).start();

        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + e.getMessage());
        }
    }

    // === Save/Load ===

    public void saveToFile(String path) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(path))) {
            dos.writeUTF("SFX1"); // Magic number + version
            dos.writeInt(waveType.ordinal());
            dos.writeFloat(attackTime);
            dos.writeFloat(sustainTime);
            dos.writeFloat(sustainPunch);
            dos.writeFloat(decayTime);
            dos.writeFloat(startFrequency);
            dos.writeFloat(minFrequency);
            dos.writeFloat(slide);
            dos.writeFloat(deltaSlide);
            dos.writeFloat(vibratoDepth);
            dos.writeFloat(vibratoSpeed);
            dos.writeFloat(arpMod);
            dos.writeFloat(arpSpeed);
            dos.writeFloat(squareDuty);
            dos.writeFloat(dutySweep);
            dos.writeFloat(repeatSpeed);
            dos.writeFloat(phaserOffset);
            dos.writeFloat(phaserSweep);
            dos.writeFloat(lpFilterCutoff);
            dos.writeFloat(lpFilterCutoffSweep);
            dos.writeFloat(lpFilterResonance);
            dos.writeFloat(hpFilterCutoff);
            dos.writeFloat(hpFilterCutoffSweep);
            dos.writeFloat(masterVolume);
        }
    }

    public void loadFromFile(String path) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(path))) {
            String magic = dis.readUTF();
            if (!magic.equals("SFX1")) {
                throw new IOException("Invalid SFX file format");
            }
            waveType = WaveType.values()[dis.readInt()];
            attackTime = dis.readFloat();
            sustainTime = dis.readFloat();
            sustainPunch = dis.readFloat();
            decayTime = dis.readFloat();
            startFrequency = dis.readFloat();
            minFrequency = dis.readFloat();
            slide = dis.readFloat();
            deltaSlide = dis.readFloat();
            vibratoDepth = dis.readFloat();
            vibratoSpeed = dis.readFloat();
            arpMod = dis.readFloat();
            arpSpeed = dis.readFloat();
            squareDuty = dis.readFloat();
            dutySweep = dis.readFloat();
            repeatSpeed = dis.readFloat();
            phaserOffset = dis.readFloat();
            phaserSweep = dis.readFloat();
            lpFilterCutoff = dis.readFloat();
            lpFilterCutoffSweep = dis.readFloat();
            lpFilterResonance = dis.readFloat();
            hpFilterCutoff = dis.readFloat();
            hpFilterCutoffSweep = dis.readFloat();
            masterVolume = dis.readFloat();
        }
    }

    public void exportAsWav(String path) throws IOException {
        byte[] audioData = synthesize();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
             AudioInputStream ais = new AudioInputStream(bais, format, audioData.length / 2)) {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(path));
        }
    }

    // === Getters/Setters ===

    public WaveType getWaveType() { return waveType; }
    public void setWaveType(WaveType waveType) { this.waveType = waveType; }

    public float getAttackTime() { return attackTime; }
    public void setAttackTime(float attackTime) { this.attackTime = clamp(attackTime, 0, 1); }

    public float getSustainTime() { return sustainTime; }
    public void setSustainTime(float sustainTime) { this.sustainTime = clamp(sustainTime, 0, 1); }

    public float getSustainPunch() { return sustainPunch; }
    public void setSustainPunch(float sustainPunch) { this.sustainPunch = clamp(sustainPunch, 0, 1); }

    public float getDecayTime() { return decayTime; }
    public void setDecayTime(float decayTime) { this.decayTime = clamp(decayTime, 0, 1); }

    public float getStartFrequency() { return startFrequency; }
    public void setStartFrequency(float startFrequency) { this.startFrequency = clamp(startFrequency, 0, 1); }

    public float getMinFrequency() { return minFrequency; }
    public void setMinFrequency(float minFrequency) { this.minFrequency = clamp(minFrequency, 0, 1); }

    public float getSlide() { return slide; }
    public void setSlide(float slide) { this.slide = clamp(slide, -1, 1); }

    public float getDeltaSlide() { return deltaSlide; }
    public void setDeltaSlide(float deltaSlide) { this.deltaSlide = clamp(deltaSlide, -1, 1); }

    public float getVibratoDepth() { return vibratoDepth; }
    public void setVibratoDepth(float vibratoDepth) { this.vibratoDepth = clamp(vibratoDepth, 0, 1); }

    public float getVibratoSpeed() { return vibratoSpeed; }
    public void setVibratoSpeed(float vibratoSpeed) { this.vibratoSpeed = clamp(vibratoSpeed, 0, 1); }

    public float getArpMod() { return arpMod; }
    public void setArpMod(float arpMod) { this.arpMod = clamp(arpMod, -1, 1); }

    public float getArpSpeed() { return arpSpeed; }
    public void setArpSpeed(float arpSpeed) { this.arpSpeed = clamp(arpSpeed, 0, 1); }

    public float getSquareDuty() { return squareDuty; }
    public void setSquareDuty(float squareDuty) { this.squareDuty = clamp(squareDuty, 0, 1); }

    public float getDutySweep() { return dutySweep; }
    public void setDutySweep(float dutySweep) { this.dutySweep = clamp(dutySweep, -1, 1); }

    public float getRepeatSpeed() { return repeatSpeed; }
    public void setRepeatSpeed(float repeatSpeed) { this.repeatSpeed = clamp(repeatSpeed, 0, 1); }

    public float getPhaserOffset() { return phaserOffset; }
    public void setPhaserOffset(float phaserOffset) { this.phaserOffset = clamp(phaserOffset, -1, 1); }

    public float getPhaserSweep() { return phaserSweep; }
    public void setPhaserSweep(float phaserSweep) { this.phaserSweep = clamp(phaserSweep, -1, 1); }

    public float getLpFilterCutoff() { return lpFilterCutoff; }
    public void setLpFilterCutoff(float lpFilterCutoff) { this.lpFilterCutoff = clamp(lpFilterCutoff, 0, 1); }

    public float getLpFilterCutoffSweep() { return lpFilterCutoffSweep; }
    public void setLpFilterCutoffSweep(float lpFilterCutoffSweep) { this.lpFilterCutoffSweep = clamp(lpFilterCutoffSweep, -1, 1); }

    public float getLpFilterResonance() { return lpFilterResonance; }
    public void setLpFilterResonance(float lpFilterResonance) { this.lpFilterResonance = clamp(lpFilterResonance, 0, 1); }

    public float getHpFilterCutoff() { return hpFilterCutoff; }
    public void setHpFilterCutoff(float hpFilterCutoff) { this.hpFilterCutoff = clamp(hpFilterCutoff, 0, 1); }

    public float getHpFilterCutoffSweep() { return hpFilterCutoffSweep; }
    public void setHpFilterCutoffSweep(float hpFilterCutoffSweep) { this.hpFilterCutoffSweep = clamp(hpFilterCutoffSweep, -1, 1); }

    public float getMasterVolume() { return masterVolume; }
    public void setMasterVolume(float masterVolume) { this.masterVolume = clamp(masterVolume, 0, 1); }
}

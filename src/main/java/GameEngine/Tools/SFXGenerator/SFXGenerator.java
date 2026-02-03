package GameEngine.Tools.SFXGenerator;

import GameEngine.Core.GameEngine;
import GameEngine.Core.audio.SFXEngine;
import GameEngine.Core.audio.SFXEngine.WaveType;
import GameEngine.Core.gameObject.FuncInt.FuncInt;
import GameEngine.Core.gameObject.Obj.Button;
import GameEngine.Core.gameObject.Obj.Dropdown;
import GameEngine.Core.gameObject.Obj.Slider;
import GameEngine.Core.gameObject.Obj.Text;
import GameEngine.Core.input.Input;
import GameEngine.Core.scenes.SceneManager;
import GameEngine.Core.util.Vector2;
import GameEngine.Tools.MainMenu.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 8-bit Sound Effect Generator Tool.
 * Inspired by SFXR/BFXR for creating retro game sounds.
 */
public class SFXGenerator extends GameEngine {

    public static void main(String[] args) {
        GameEngine.launch(new SFXGenerator());
    }

    // === Colors (Modern Dark Theme) ===
    private static final Color BG_COLOR = new Color(25, 25, 30);
    private static final Color PANEL_COLOR = new Color(35, 35, 40);
    private static final Color TOOL_COLOR = new Color(50, 50, 55);
    private static final Color ACCENT_COLOR = new Color(0, 150, 255);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(180, 180, 180);
    private static final Color BORDER_COLOR = new Color(60, 60, 65);
    private static final Color GREEN_COLOR = new Color(60, 150, 80);
    private static final Color RED_COLOR = new Color(180, 60, 60);
    private static final Color ORANGE_COLOR = new Color(200, 150, 50);

    // === Layout ===
    private static final int PRESET_PANEL_X = 20;
    private static final int PRESET_PANEL_Y = 80;
    private static final int SLIDER_PANEL_X = 200;
    private static final int SLIDER_PANEL_Y = 80;
    private static final int SLIDER_WIDTH = 180;
    private static final int SLIDER_HEIGHT = 16;
    private static final int SLIDER_SPACING = 32;
    private static final int COLUMN_WIDTH = 220;

    // === SFX Engine ===
    private SFXEngine sfxEngine;
    private String currentSFXName = "untitled";
    private boolean updatingSliders = false; // Prevents callback loops during programmatic slider updates

    // === UI Elements ===
    private Text titleText;
    private Text currentNameText;
    private Text shortcutsText;

    // Preset Buttons
    private Button pickupBtn, laserBtn, explosionBtn, powerupBtn;
    private Button hitBtn, jumpBtn, blipBtn;
    private Button mutateBtn, randomizeBtn;

    // Waveform
    private Dropdown waveformDropdown;

    // Sliders organized by category
    // Envelope
    private Slider attackSlider, sustainSlider, sustainPunchSlider, decaySlider;
    // Frequency
    private Slider startFreqSlider, minFreqSlider, slideSlider, deltaSlideSlider;
    // Vibrato
    private Slider vibratoDepthSlider, vibratoSpeedSlider;
    // Arpeggiation
    private Slider arpModSlider, arpSpeedSlider;
    // Duty (Square wave)
    private Slider dutySlider, dutySweepSlider;
    // Repeat
    private Slider repeatSpeedSlider;
    // Phaser
    private Slider phaserOffsetSlider, phaserSweepSlider;
    // Filter
    private Slider lpCutoffSlider, lpSweepSlider, lpResonanceSlider;
    private Slider hpCutoffSlider, hpSweepSlider;
    // Master
    private Slider volumeSlider;

    // Control Buttons
    private Button playBtn, saveBtn, loadBtn, exportWavBtn, newBtn;
    private Button backToMenuBtn;

    // === Directories ===
    private static final String SFX_DIR = "assets/sfx/";

    @Override
    public void init() {
        sfxEngine = new SFXEngine();

        setTitle("SFX Generator");
        setBackground(BG_COLOR);

        ensureDirectories();
        setupTextElements();
        setupPresetButtons();
        setupWaveformDropdown();
        setupSliders();
        setupControlButtons();

        // Initial state
        sfxEngine.generatePickup();
        updateSlidersFromEngine();
    }

    private void ensureDirectories() {
        new File(SFX_DIR).mkdirs();
    }

    //<editor-fold desc="SETUP">
    private void setupTextElements() {
        titleText = new Text.Builder("SFX Generator")
                .position(new Vector2(SCREEN_WIDTH / 2f, 40))
                .color(TEXT_COLOR)
                .font(new Font("Arial", Font.BOLD, 32))
                .alignment(Text.TextAlignment.CENTER)
                .build();
        objectManager.add(titleText);

        currentNameText = new Text.Builder("Current: " + currentSFXName)
                .position(new Vector2(SCREEN_WIDTH - 200, 40))
                .color(ACCENT_COLOR)
                .font(new Font("Arial", Font.PLAIN, 14))
                .build();
        objectManager.add(currentNameText);

        shortcutsText = new Text.Builder("Space (Play) | Ctrl+S (Save) | Ctrl+O (Load) | M (Mutate) | R (Random)")
                .position(new Vector2(10, SCREEN_HEIGHT - 20))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 12))
                .build();
        objectManager.add(shortcutsText);

        backToMenuBtn = new Button.Builder()
                .preset(Button.ButtonPreset.BACK_BUTTON)
                .onClick(() -> SceneManager.loadScene(new MainMenu()))
                .build();
        objectManager.add(backToMenuBtn);
    }

    private void setupPresetButtons() {
        int btnWidth = 140;
        int btnHeight = 35;
        int spacing = 8;
        int x = PRESET_PANEL_X;
        int y = PRESET_PANEL_Y;

        // Section title
        Text presetTitle = new Text.Builder("Presets")
                .position(new Vector2(x, y - 5))
                .color(ACCENT_COLOR)
                .font(new Font("Arial", Font.BOLD, 16))
                .build();
        objectManager.add(presetTitle);

        y += 20;

        pickupBtn = createPresetButton(x, y, btnWidth, btnHeight, "Pickup/Coin", GREEN_COLOR, () -> {
            sfxEngine.generatePickup();
            updateSlidersFromEngine();
            sfxEngine.play();
        });
        y += btnHeight + spacing;

        laserBtn = createPresetButton(x, y, btnWidth, btnHeight, "Laser/Shoot", ACCENT_COLOR, () -> {
            sfxEngine.generateLaser();
            updateSlidersFromEngine();
            sfxEngine.play();
        });
        y += btnHeight + spacing;

        explosionBtn = createPresetButton(x, y, btnWidth, btnHeight, "Explosion", RED_COLOR, () -> {
            sfxEngine.generateExplosion();
            updateSlidersFromEngine();
            sfxEngine.play();
        });
        y += btnHeight + spacing;

        powerupBtn = createPresetButton(x, y, btnWidth, btnHeight, "Powerup", new Color(150, 80, 200), () -> {
            sfxEngine.generatePowerup();
            updateSlidersFromEngine();
            sfxEngine.play();
        });
        y += btnHeight + spacing;

        hitBtn = createPresetButton(x, y, btnWidth, btnHeight, "Hit/Hurt", ORANGE_COLOR, () -> {
            sfxEngine.generateHit();
            updateSlidersFromEngine();
            sfxEngine.play();
        });
        y += btnHeight + spacing;

        jumpBtn = createPresetButton(x, y, btnWidth, btnHeight, "Jump", new Color(80, 180, 150), () -> {
            sfxEngine.generateJump();
            updateSlidersFromEngine();
            sfxEngine.play();
        });
        y += btnHeight + spacing;

        blipBtn = createPresetButton(x, y, btnWidth, btnHeight, "Blip/Select", TOOL_COLOR, () -> {
            sfxEngine.generateBlip();
            updateSlidersFromEngine();
            sfxEngine.play();
        });
        y += btnHeight + spacing + 15;

        // Mutation buttons
        Text mutateTitle = new Text.Builder("Variation")
                .position(new Vector2(x, y))
                .color(ACCENT_COLOR)
                .font(new Font("Arial", Font.BOLD, 16))
                .build();
        objectManager.add(mutateTitle);
        y += 25;

        mutateBtn = createPresetButton(x, y, btnWidth, btnHeight, "Mutate", new Color(100, 100, 150), () -> {
            sfxEngine.mutate();
            updateSlidersFromEngine();
            sfxEngine.play();
        });
        y += btnHeight + spacing;

        randomizeBtn = createPresetButton(x, y, btnWidth, btnHeight, "Randomize", new Color(150, 100, 100), () -> {
            sfxEngine.randomize();
            updateSlidersFromEngine();
            sfxEngine.play();
        });
    }

    private Button createPresetButton(int x, int y, int w, int h, String text, Color color, FuncInt action) {
        Button btn = new Button.Builder()
                .rect(new Rectangle(x, y, w, h))
                .color(color)
                .text(text)
                .font(new Font("Arial", Font.BOLD, 13))
                .textColor(TEXT_COLOR)
                .cornerRadius(6)
                .border(BORDER_COLOR, 1)
                .onClick(action::call)
                .build();
        objectManager.add(btn);
        return btn;
    }

    private void setupWaveformDropdown() {
        int x = SLIDER_PANEL_X;
        int y = SLIDER_PANEL_Y;

        Text waveLabel = new Text.Builder("Waveform")
                .position(new Vector2(x, y - 5))
                .color(ACCENT_COLOR)
                .font(new Font("Arial", Font.BOLD, 14))
                .build();
        objectManager.add(waveLabel);

        String[] waveOptions = {"Square", "Sawtooth", "Sine", "Noise", "Triangle", "Breaker"};
        waveformDropdown = new Dropdown.Builder()
                .pos(new Vector2(x, y + 12))
                .size(new Vector2(SLIDER_WIDTH, 28))
                .options(waveOptions)
                .backgroundColor(TOOL_COLOR)
                .textColor(TEXT_COLOR)
                .borderColor(BORDER_COLOR)
                .font(new Font("Arial", Font.PLAIN, 13))
                .onIndexChanged(this::onWaveformChanged)
                .build();
        objectManager.add(waveformDropdown);
    }

    private void setupSliders() {
        int x = SLIDER_PANEL_X;
        int y = SLIDER_PANEL_Y + 60;

        // Column 1: Envelope
        y = createSliderSection(x, y, "Envelope");
        attackSlider = createSlider(x, y, "Attack", 0, 1, sfxEngine.getAttackTime(), v -> sfxEngine.setAttackTime(v));
        y += SLIDER_SPACING;
        sustainSlider = createSlider(x, y, "Sustain", 0, 1, sfxEngine.getSustainTime(), v -> sfxEngine.setSustainTime(v));
        y += SLIDER_SPACING;
        sustainPunchSlider = createSlider(x, y, "Punch", 0, 1, sfxEngine.getSustainPunch(), v -> sfxEngine.setSustainPunch(v));
        y += SLIDER_SPACING;
        decaySlider = createSlider(x, y, "Decay", 0, 1, sfxEngine.getDecayTime(), v -> sfxEngine.setDecayTime(v));
        y += SLIDER_SPACING + 10;

        // Column 1: Frequency
        y = createSliderSection(x, y, "Frequency");
        startFreqSlider = createSlider(x, y, "Start Freq", 0, 1, sfxEngine.getStartFrequency(), v -> sfxEngine.setStartFrequency(v));
        y += SLIDER_SPACING;
        minFreqSlider = createSlider(x, y, "Min Freq", 0, 1, sfxEngine.getMinFrequency(), v -> sfxEngine.setMinFrequency(v));
        y += SLIDER_SPACING;
        slideSlider = createSlider(x, y, "Slide", -1, 1, sfxEngine.getSlide(), v -> sfxEngine.setSlide(v));
        y += SLIDER_SPACING;
        deltaSlideSlider = createSlider(x, y, "Delta Slide", -1, 1, sfxEngine.getDeltaSlide(), v -> sfxEngine.setDeltaSlide(v));

        // Column 2: Vibrato & Arpeggio
        x = SLIDER_PANEL_X + COLUMN_WIDTH;
        y = SLIDER_PANEL_Y + 60;

        y = createSliderSection(x, y, "Vibrato");
        vibratoDepthSlider = createSlider(x, y, "Depth", 0, 1, sfxEngine.getVibratoDepth(), v -> sfxEngine.setVibratoDepth(v));
        y += SLIDER_SPACING;
        vibratoSpeedSlider = createSlider(x, y, "Speed", 0, 1, sfxEngine.getVibratoSpeed(), v -> sfxEngine.setVibratoSpeed(v));
        y += SLIDER_SPACING + 10;

        y = createSliderSection(x, y, "Arpeggiation");
        arpModSlider = createSlider(x, y, "Arp Mod", -1, 1, sfxEngine.getArpMod(), v -> sfxEngine.setArpMod(v));
        y += SLIDER_SPACING;
        arpSpeedSlider = createSlider(x, y, "Arp Speed", 0, 1, sfxEngine.getArpSpeed(), v -> sfxEngine.setArpSpeed(v));
        y += SLIDER_SPACING + 10;

        y = createSliderSection(x, y, "Duty Cycle (Square)");
        dutySlider = createSlider(x, y, "Duty", 0, 1, sfxEngine.getSquareDuty(), v -> sfxEngine.setSquareDuty(v));
        y += SLIDER_SPACING;
        dutySweepSlider = createSlider(x, y, "Duty Sweep", -1, 1, sfxEngine.getDutySweep(), v -> sfxEngine.setDutySweep(v));
        y += SLIDER_SPACING + 10;

        y = createSliderSection(x, y, "Repeat");
        repeatSpeedSlider = createSlider(x, y, "Repeat Speed", 0, 1, sfxEngine.getRepeatSpeed(), v -> sfxEngine.setRepeatSpeed(v));

        // Column 3: Phaser & Filters
        x = SLIDER_PANEL_X + COLUMN_WIDTH * 2;
        y = SLIDER_PANEL_Y + 60;

        y = createSliderSection(x, y, "Phaser");
        phaserOffsetSlider = createSlider(x, y, "Offset", -1, 1, sfxEngine.getPhaserOffset(), v -> sfxEngine.setPhaserOffset(v));
        y += SLIDER_SPACING;
        phaserSweepSlider = createSlider(x, y, "Sweep", -1, 1, sfxEngine.getPhaserSweep(), v -> sfxEngine.setPhaserSweep(v));
        y += SLIDER_SPACING + 10;

        y = createSliderSection(x, y, "Low-Pass Filter");
        lpCutoffSlider = createSlider(x, y, "Cutoff", 0, 1, sfxEngine.getLpFilterCutoff(), v -> sfxEngine.setLpFilterCutoff(v));
        y += SLIDER_SPACING;
        lpSweepSlider = createSlider(x, y, "Sweep", -1, 1, sfxEngine.getLpFilterCutoffSweep(), v -> sfxEngine.setLpFilterCutoffSweep(v));
        y += SLIDER_SPACING;
        lpResonanceSlider = createSlider(x, y, "Resonance", 0, 1, sfxEngine.getLpFilterResonance(), v -> sfxEngine.setLpFilterResonance(v));
        y += SLIDER_SPACING + 10;

        y = createSliderSection(x, y, "High-Pass Filter");
        hpCutoffSlider = createSlider(x, y, "Cutoff", 0, 1, sfxEngine.getHpFilterCutoff(), v -> sfxEngine.setHpFilterCutoff(v));
        y += SLIDER_SPACING;
        hpSweepSlider = createSlider(x, y, "Sweep", -1, 1, sfxEngine.getHpFilterCutoffSweep(), v -> sfxEngine.setHpFilterCutoffSweep(v));
        y += SLIDER_SPACING + 10;

        y = createSliderSection(x, y, "Master");
        volumeSlider = createSlider(x, y, "Volume", 0, 1, sfxEngine.getMasterVolume(), v -> sfxEngine.setMasterVolume(v));
    }

    private int createSliderSection(int x, int y, String title) {
        Text label = new Text.Builder(title)
                .position(new Vector2(x, y))
                .color(ACCENT_COLOR)
                .font(new Font("Arial", Font.BOLD, 13))
                .build();
        objectManager.add(label);
        return y + 18;
    }

    private Slider createSlider(int x, int y, String label, float min, float max, float value, java.util.function.Consumer<Float> onChange) {
        // Label
        Text labelText = new Text.Builder(label)
                .position(new Vector2(x, y + 4))
                .color(TEXT_SECONDARY)
                .font(new Font("Arial", Font.PLAIN, 11))
                .build();
        objectManager.add(labelText);

        // Slider
        Slider slider = new Slider.Builder()
                .pos(new Vector2(x, y + 10))
                .size(SLIDER_WIDTH, SLIDER_HEIGHT)
                .range(min, max)
                .startValue(value)
                .backgroundColor(TOOL_COLOR)
                .fillColor(ACCENT_COLOR)
                .handleColor(TEXT_COLOR)
                .borderColor(BORDER_COLOR)
                .handleShape(Slider.HandleShape.CIRCLE)
                .cornerRadius(4)
                .onValueChanged(v -> {
                    if (!updatingSliders) {
                        onChange.accept(v);
                    }
                })
                .build();
        objectManager.add(slider);
        return slider;
    }

    private void setupControlButtons() {
        int x = SLIDER_PANEL_X + COLUMN_WIDTH * 3 + 30;
        int y = SLIDER_PANEL_Y;
        int btnWidth = 140;
        int btnHeight = 45;
        int spacing = 12;

        // Play button (prominent)
        playBtn = new Button.Builder()
                .rect(new Rectangle(x, y, btnWidth, btnHeight + 10))
                .color(GREEN_COLOR)
                .text("Play")
                .font(new Font("Arial", Font.BOLD, 20))
                .textColor(TEXT_COLOR)
                .cornerRadius(8)
                .border(BORDER_COLOR, 2)
                .smoothHover(5, 150)
                .onClick(this::playSound)
                .build();
        objectManager.add(playBtn);
        y += btnHeight + 10 + spacing + 20;

        // File operations
        Text fileTitle = new Text.Builder("File Operations")
                .position(new Vector2(x, y))
                .color(ACCENT_COLOR)
                .font(new Font("Arial", Font.BOLD, 14))
                .build();
        objectManager.add(fileTitle);
        y += 25;

        newBtn = new Button.Builder()
                .rect(new Rectangle(x, y, btnWidth, btnHeight))
                .color(ORANGE_COLOR)
                .text("New")
                .font(new Font("Arial", Font.BOLD, 16))
                .textColor(TEXT_COLOR)
                .cornerRadius(6)
                .border(BORDER_COLOR, 1)
                .onClick(this::newSFX)
                .build();
        objectManager.add(newBtn);
        y += btnHeight + spacing;

        saveBtn = new Button.Builder()
                .rect(new Rectangle(x, y, btnWidth, btnHeight))
                .color(GREEN_COLOR)
                .text("Save .sfx")
                .font(new Font("Arial", Font.BOLD, 16))
                .textColor(TEXT_COLOR)
                .cornerRadius(6)
                .border(BORDER_COLOR, 1)
                .onClick(this::saveSFX)
                .build();
        objectManager.add(saveBtn);
        y += btnHeight + spacing;

        loadBtn = new Button.Builder()
                .rect(new Rectangle(x, y, btnWidth, btnHeight))
                .color(ACCENT_COLOR)
                .text("Load .sfx")
                .font(new Font("Arial", Font.BOLD, 16))
                .textColor(TEXT_COLOR)
                .cornerRadius(6)
                .border(BORDER_COLOR, 1)
                .onClick(this::loadSFX)
                .build();
        objectManager.add(loadBtn);
        y += btnHeight + spacing;

        exportWavBtn = new Button.Builder()
                .rect(new Rectangle(x, y, btnWidth, btnHeight))
                .color(new Color(100, 80, 150))
                .text("Export .wav")
                .font(new Font("Arial", Font.BOLD, 16))
                .textColor(TEXT_COLOR)
                .cornerRadius(6)
                .border(BORDER_COLOR, 1)
                .onClick(this::exportWav)
                .build();
        objectManager.add(exportWavBtn);
    }
    //</editor-fold>

    //<editor-fold desc="UPDATE">
    @Override
    protected void update() {
        objectManager.update(deltaTime);
        handleKeyboardShortcuts();
    }

    private void handleKeyboardShortcuts() {
        // Space = Play
        if (Input.getKeyDown(Input.KeyCode.SPACE)) {
            playSound();
        }
        // M = Mutate
        if (Input.getKeyDown(Input.KeyCode.M)) {
            sfxEngine.mutate();
            updateSlidersFromEngine();
            sfxEngine.play();
        }
        // R = Randomize
        if (Input.getKeyDown(Input.KeyCode.R) && !Input.getKey(Input.KeyCode.CONTROL)) {
            sfxEngine.randomize();
            updateSlidersFromEngine();
            sfxEngine.play();
        }
        // Ctrl+S = Save
        if (Input.getKey(Input.KeyCode.CONTROL) && Input.getKeyDown(Input.KeyCode.S)) {
            saveSFX();
        }
        // Ctrl+O = Load
        if (Input.getKey(Input.KeyCode.CONTROL) && Input.getKeyDown(Input.KeyCode.O)) {
            loadSFX();
        }
        // Ctrl+N = New
        if (Input.getKey(Input.KeyCode.CONTROL) && Input.getKeyDown(Input.KeyCode.N)) {
            newSFX();
        }
    }
    //</editor-fold>

    //<editor-fold desc="ACTIONS">
    private void playSound() {
        sfxEngine.play();
    }

    private void newSFX() {
        sfxEngine.resetParams();
        currentSFXName = "untitled";
        updateSlidersFromEngine();
        updateCurrentNameText();
    }

    private void saveSFX() {
        String name = JOptionPane.showInputDialog(null, "Enter SFX name:", currentSFXName);
        if (name != null && !name.trim().isEmpty()) {
            name = name.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
            String path = SFX_DIR + name + ".sfx";
            try {
                sfxEngine.saveToFile(path);
                currentSFXName = name;
                updateCurrentNameText();
                JOptionPane.showMessageDialog(null, "Saved to: " + path, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to save: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadSFX() {
        JFileChooser chooser = new JFileChooser(SFX_DIR);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SFX Files", "sfx"));
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                sfxEngine.loadFromFile(file.getAbsolutePath());
                currentSFXName = file.getName().replace(".sfx", "");
                updateSlidersFromEngine();
                updateCurrentNameText();
                sfxEngine.play();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to load: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportWav() {
        String name = JOptionPane.showInputDialog(null, "Enter WAV filename:", currentSFXName);
        if (name != null && !name.trim().isEmpty()) {
            name = name.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
            String path = SFX_DIR + name + ".wav";
            try {
                sfxEngine.exportAsWav(path);
                JOptionPane.showMessageDialog(null, "Exported to: " + path, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to export: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onWaveformChanged(int index) {
        sfxEngine.setWaveType(WaveType.values()[index]);
    }

    private void updateSlidersFromEngine() {
        updatingSliders = true;

        // Update waveform dropdown
        waveformDropdown.setSelectedIndex(sfxEngine.getWaveType().ordinal());

        // Update all sliders
        attackSlider.setValue(sfxEngine.getAttackTime());
        sustainSlider.setValue(sfxEngine.getSustainTime());
        sustainPunchSlider.setValue(sfxEngine.getSustainPunch());
        decaySlider.setValue(sfxEngine.getDecayTime());

        startFreqSlider.setValue(sfxEngine.getStartFrequency());
        minFreqSlider.setValue(sfxEngine.getMinFrequency());
        slideSlider.setValue(sfxEngine.getSlide());
        deltaSlideSlider.setValue(sfxEngine.getDeltaSlide());

        vibratoDepthSlider.setValue(sfxEngine.getVibratoDepth());
        vibratoSpeedSlider.setValue(sfxEngine.getVibratoSpeed());

        arpModSlider.setValue(sfxEngine.getArpMod());
        arpSpeedSlider.setValue(sfxEngine.getArpSpeed());

        dutySlider.setValue(sfxEngine.getSquareDuty());
        dutySweepSlider.setValue(sfxEngine.getDutySweep());

        repeatSpeedSlider.setValue(sfxEngine.getRepeatSpeed());

        phaserOffsetSlider.setValue(sfxEngine.getPhaserOffset());
        phaserSweepSlider.setValue(sfxEngine.getPhaserSweep());

        lpCutoffSlider.setValue(sfxEngine.getLpFilterCutoff());
        lpSweepSlider.setValue(sfxEngine.getLpFilterCutoffSweep());
        lpResonanceSlider.setValue(sfxEngine.getLpFilterResonance());

        hpCutoffSlider.setValue(sfxEngine.getHpFilterCutoff());
        hpSweepSlider.setValue(sfxEngine.getHpFilterCutoffSweep());

        volumeSlider.setValue(sfxEngine.getMasterVolume());

        updatingSliders = false;
    }

    private void updateCurrentNameText() {
        currentNameText.setText("Current: " + currentSFXName);
    }
    //</editor-fold>

    @Override
    protected void draw(Graphics2D g) {
        // Draw panel backgrounds
        g.setColor(PANEL_COLOR);
        // Preset panel
        g.fillRoundRect(PRESET_PANEL_X - 10, PRESET_PANEL_Y - 25, 165, 520, 10, 10);
        // Slider panels
        g.fillRoundRect(SLIDER_PANEL_X - 10, SLIDER_PANEL_Y - 10, COLUMN_WIDTH * 3 + 20, 530, 10, 10);
        // Control panel
        g.fillRoundRect(SLIDER_PANEL_X + COLUMN_WIDTH * 3 + 20, SLIDER_PANEL_Y - 10, 160, 350, 10, 10);

        objectManager.draw(g);
    }
}

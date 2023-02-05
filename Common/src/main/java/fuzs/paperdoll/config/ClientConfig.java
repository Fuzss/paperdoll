package fuzs.paperdoll.config;

import fuzs.paperdoll.client.gui.PositionPreset;
import fuzs.paperdoll.client.gui.DisplayAction;
import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.annotation.Config;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientConfig implements ConfigCore {
    @Config(description = "Scale of paper doll. Also affected by video settings gui scale.")
    @Config.IntRange(min = 1, max = 24)
    public int scale = 4;
    @Config(description = "Offset on x-axis from original doll position.")
    public int offsetX = 0;
    @Config(description = "Offset on y-axis from original doll position.")
    public int offsetY = 0;
    @Config(description = "Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Set to 0 to always display the doll.")
    @Config.IntRange(min = 0)
    public int displayTime = 20;
    @Config(description = "Define a screen corner to align the paper doll display to.")
    public PositionPreset position = PositionPreset.TOP_LEFT;
    @Config(description = "Shift paper doll downwards when it would otherwise overlap with potion icons while showing in the top screen right corner.")
    public boolean potionShift = true;
    @Config(description = "Only show paper doll when in first-person mode.")
    public boolean firstPersonOnly = true;
    @Config(description = "Set axis the player head can move on.")
    public ClientConfig.HeadMovement headMovement = HeadMovement.YAW;
    @Config(name = "display_actions", description = "Display paper doll while performing these actions.")
    @Config.AllowedValues(values = {"SPRINTING", "SWIMMING", "CRAWLING", "CROUCHING", "FLYING", "GLIDING", "RIDING", "SPIN_ATTACKING", "USING"})
    List<String> displayActionsRaw = Stream.of(DisplayAction.SPRINTING, DisplayAction.SWIMMING, DisplayAction.CRAWLING, DisplayAction.CROUCHING, DisplayAction.FLYING, DisplayAction.GLIDING).map(Enum::name).collect(Collectors.toList());

    public List<DisplayAction> displayActions;

    @Override
    public void afterConfigReload() {
        this.displayActions = this.displayActionsRaw.stream().map(name -> {
            try {
                return DisplayAction.valueOf(name);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    public enum HeadMovement {
        YAW, PITCH, BOTH
    }
}

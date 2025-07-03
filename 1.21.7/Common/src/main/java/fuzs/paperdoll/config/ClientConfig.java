package fuzs.paperdoll.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(description = "Display paper doll while performing these actions.")
    public final DisplayActionsConfig displayActions = new DisplayActionsConfig();
    @Config(description = "Scale of paper doll. Also affected by video settings gui scale.")
    @Config.IntRange(min = 1, max = 24)
    public int scale = 4;
    @Config(description = "Offset on x-axis from original doll position.")
    public int offsetX = 0;
    @Config(description = "Offset on y-axis from original doll position.")
    public int offsetY = 0;
    @Config(description = "Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Set to 0 to always display the doll.")
    @Config.IntRange(min = 0)
    public int displayTime = 15;
    @Config(description = "Define a position on the screen to align the paper doll display to.")
    public AnchorPoint anchorPoint = AnchorPoint.TOP_LEFT;
    @Config(description = "Shift paper doll downwards when it would otherwise overlap with potion icons while showing in the top screen right corner.")
    public boolean potionShift = true;
    @Config(description = "Only show paper doll when in first-person mode.")
    public boolean firstPersonOnly = true;
    @Config(description = "Set axis the player head can move on.")
    public ClientConfig.HeadMovement headMovement = HeadMovement.YAW;

    public enum HeadMovement {
        YAW,
        PITCH,
        BOTH
    }

    public static class DisplayActionsConfig implements ConfigCore {
        @Config(description = "Display paper doll while sprinting.")
        public boolean sprinting = true;
        @Config(description = "Display paper doll while swimming.")
        public boolean swimming = true;
        @Config(description = "Display paper doll while crawling in a tight space.")
        public boolean crawling = true;
        @Config(description = "Display paper doll while crouching.")
        public boolean crouching = true;
        @Config(description = "Display paper doll while flying in creative.")
        public boolean flying = true;
        @Config(description = "Display paper doll while gliding using elytra.")
        public boolean gliding = true;
        @Config(description = "Display paper doll while riding a vehicle or mount.")
        public boolean riding = false;
        @Config(description = "Display paper doll while spin attacking with a trident.")
        public boolean spinAttacking = false;
        @Config(description = "Display paper doll while actively using an item like a bow or food.")
        public boolean using = false;
    }
}

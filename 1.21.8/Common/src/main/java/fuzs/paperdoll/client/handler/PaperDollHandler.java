package fuzs.paperdoll.client.handler;

import fuzs.paperdoll.PaperDoll;
import fuzs.paperdoll.client.util.PaperDollRenderer;
import fuzs.paperdoll.config.AnchorPoint;
import fuzs.paperdoll.config.ClientConfig;
import fuzs.paperdoll.config.DisplayAction;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PaperDollHandler {
    static final float MAX_ROTATION_DEGREES = 30.0F;
    static final float DEFAULT_ROTATION_DEGREES = MAX_ROTATION_DEGREES / 2.0F;
    static final float SPIN_BACK_SPEED = 10.0F;

    private static int remainingDisplayTicks;
    private static int remainingRidingTicks;
    private static float yRotOffset;
    private static float yRotOffsetO;

    public static void onEndClientTick(Minecraft minecraft) {

        if (minecraft.player == null || minecraft.isPaused()) return;

        ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);

        // update display ticks
        if (DisplayAction.isActive(config.displayActions, minecraft.player, remainingRidingTicks)) {
            remainingDisplayTicks = config.displayTime;
        } else if (remainingDisplayTicks > 0) {
            remainingDisplayTicks--;
        }

        // reset rotation when no longer shown
        if (remainingDisplayTicks > 0 || config.displayTime == 0) {

            tickYRotOffset(minecraft.player);
        } else {

            yRotOffset = yRotOffsetO = 0;
        }

        // don't show paper doll in sneaking position after unmounting a vehicle / mount
        if (minecraft.player.isPassenger()) {

            remainingRidingTicks = Math.max(0, config.displayTime - 2);
        } else if (remainingRidingTicks > 0) {

            remainingRidingTicks--;
        }
    }

    private static void tickYRotOffset(Player player) {

        yRotOffsetO = yRotOffset;

        // apply rotation change from entity
        yRotOffset = Mth.clamp(yRotOffset + (player.yHeadRot - player.yHeadRotO) * 0.5F,
                -MAX_ROTATION_DEGREES,
                MAX_ROTATION_DEGREES);
        // rotate back to origin, never overshoot 0
        float nextYRotOffset = yRotOffset - yRotOffset / SPIN_BACK_SPEED;

        if (yRotOffset < 0.0F) {

            yRotOffset = Math.min(0, nextYRotOffset);
        } else if (yRotOffset > 0.0F) {

            yRotOffset = Math.max(0, nextYRotOffset);
        } else {

            yRotOffset = 0.0F;
        }
    }

    public static void renderPaperDoll(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {

        Profiler.get().push("paperDoll");
        Minecraft minecraft = Minecraft.getInstance();

        // TODO remove again when rendering multiple entities of the same type on screen is fixed
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike() && minecraft.screen != null) {
            return;
        }

        Player player = minecraft.player;

        if (!player.isInvisible() && !player.isSpectator()) {

            ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);
            if (minecraft.options.getCameraType().isFirstPerson() || !config.firstPersonOnly) {

                if (remainingDisplayTicks > 0 || config.displayTime == 0) {

                    float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
                    int scale = config.scale * 5;
                    int size = scale * 7 / 2;
                    AnchorPoint.Positioner positioner = config.anchorPoint.createPositioner(guiGraphics.guiWidth(),
                            guiGraphics.guiHeight(),
                            size,
                            size);
                    ScreenRectangle rectangle = positioner.getRectangle(config.offsetX, config.offsetY);
                    PaperDollRenderer.renderEntityInInventory(guiGraphics,
                            rectangle.left(),
                            rectangle.top(),
                            rectangle.right(),
                            rectangle.bottom(),
                            scale,
                            0.0F,
                            player,
                            partialTick);
                }
            }
        }

        Profiler.get().pop();
    }

    public static void applyEntityRotations(LivingEntity entity) {
        ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);
        ClientConfig.HeadMovement headMovement = config.headMovement;
        // head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
        if (headMovement == ClientConfig.HeadMovement.YAW || entity.isFallFlying()) {
            entity.setXRot(7.5F);
            entity.xRotO = 7.5F;
        }
        float defaultRotationYaw = getDefaultRotationYaw(config.anchorPoint);
        entity.yBodyRot = entity.yBodyRotO = defaultRotationYaw;
        if (headMovement == ClientConfig.HeadMovement.PITCH) {
            entity.yHeadRot = entity.yHeadRotO = defaultRotationYaw;
        } else {
            entity.yHeadRotO = defaultRotationYaw + yRotOffsetO;
            entity.yHeadRot = defaultRotationYaw + yRotOffset;
        }
    }

    static float getDefaultRotationYaw(AnchorPoint anchorPoint) {
        return 180.0F + (anchorPoint.isRight() ? DEFAULT_ROTATION_DEGREES : -DEFAULT_ROTATION_DEGREES);
    }
}

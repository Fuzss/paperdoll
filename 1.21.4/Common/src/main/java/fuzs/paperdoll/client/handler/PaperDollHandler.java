package fuzs.paperdoll.client.handler;

import fuzs.paperdoll.PaperDoll;
import fuzs.paperdoll.client.gui.PaperDollRenderer;
import fuzs.paperdoll.config.ClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public class PaperDollHandler {
    private static final float MAX_ROTATION_DEGREES = 30.0F;
    private static final float SPIN_BACK_SPEED = 10.0F;

    private static int remainingDisplayTicks;
    private static int remainingRidingTicks;
    private static float yRotOffset;
    private static float yRotOffsetO;

    public static void onEndClientTick(Minecraft minecraft) {

        if (minecraft.player == null || minecraft.isPaused()) return;

        ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);

        // update display ticks
        if (config.displayActions.stream().anyMatch(condition -> condition.isActive(minecraft.player, remainingRidingTicks))) {

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
        yRotOffset = Mth.clamp(yRotOffset + (player.yHeadRot - player.yHeadRotO) * 0.5F, -MAX_ROTATION_DEGREES, MAX_ROTATION_DEGREES);
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

    public static void onRenderGui(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {

        Profiler.get().push("paperDoll");
        Player player = gui.minecraft.player;
        if (!player.isInvisible() && !player.isSpectator()) {

            ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);
            if (gui.minecraft.options.getCameraType().isFirstPerson() || !config.firstPersonOnly) {

                if (remainingDisplayTicks > 0 || config.displayTime == 0) {

                    float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
                    int scale = config.scale * 5;
                    int posX = config.position.getX(0, guiGraphics.guiWidth(), (int) (scale * 1.5F) + config.offsetX);
                    // can't use PositionPreset#getY as the orientation point isn't in the top left corner of the image
                    int posY = config.position.isBottom() ? guiGraphics.guiHeight() - scale - config.offsetY : (int) (scale * 2.5F) + config.offsetY;
                    posY -= scale - getCurrentHeightOffset(player, partialTick) * scale;
                    if (config.potionShift) {

                        posY += config.position.getPotionShift(player.getActiveEffects());
                    }

                    PaperDollRenderer.renderEntityInInventoryUpdateRotations(guiGraphics, posX, posY, scale,
                            player, partialTick);
                }
            }
        }

        Profiler.get().pop();
    }

    private static float getCurrentHeightOffset(Player player, float tickDelta) {

        // crouching check after elytra since you can do both at the same time
        float height = player.getDimensions(Pose.STANDING).height();
        if (player.getFallFlyingTicks() > 0) {

            float ticksElytraFlying = player.getFallFlyingTicks() + tickDelta;
            float flyingAnimation = Mth.clamp(ticksElytraFlying * 0.09F, 0.0F, 1.0F);
            float flyingHeight = player.getDimensions(Pose.FALL_FLYING).height() / height;
            return Mth.lerp(flyingAnimation, 1.0F, flyingHeight);
        } else if (player.getSwimAmount(tickDelta) > 0.0) {

            float swimmingAnimation = player.isVisuallySwimming() ? 1.0F : player.getSwimAmount(tickDelta);
            float swimmingHeight = player.getDimensions(Pose.SWIMMING).height() / height;
            return Mth.lerp(swimmingAnimation, 1.0F, swimmingHeight);
        } else if (player.isAutoSpinAttack()) {

            return player.getDimensions(Pose.SPIN_ATTACK).height() / height;
        } else if (player.isCrouching()) {

            return player.getDimensions(Pose.CROUCHING).height() / height;
        } else if (player.isSleeping()) {

            return player.getDimensions(Pose.SLEEPING).height() / height;
        } else if (player.deathTime > 0) {

            float dyingAnimation = ((float) player.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
            dyingAnimation = Math.min(1.0F, Mth.sqrt(dyingAnimation));
            float dyingHeight = player.getDimensions(Pose.DYING).height() / height;
            return Mth.lerp(dyingAnimation, 1.0F, dyingHeight);
        } else {

            return 1.0F;
        }
    }

    public static void applyEntityRotations(LivingEntity entity) {

        ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);

        ClientConfig.HeadMovement headMovement = config.headMovement;
        // head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
        if (headMovement == ClientConfig.HeadMovement.YAW || entity.isFallFlying()) {

            entity.setXRot(7.5F);
            entity.xRotO = 7.5F;
        }

        final float defaultRotationYaw = 180.0F + config.position.getRotation(MAX_ROTATION_DEGREES / 2.0F);

        entity.yBodyRot = entity.yBodyRotO = defaultRotationYaw;

        if (headMovement == ClientConfig.HeadMovement.PITCH) {

            entity.yHeadRot = entity.yHeadRotO = defaultRotationYaw;
        } else {

            entity.yHeadRotO = defaultRotationYaw + yRotOffsetO;
            entity.yHeadRot = defaultRotationYaw + yRotOffset;
        }
    }
}

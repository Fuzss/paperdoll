package fuzs.paperdoll.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.paperdoll.PaperDoll;
import fuzs.paperdoll.client.gui.PaperDollRenderer;
import fuzs.paperdoll.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public class PaperDollHandler {
    private static int remainingDisplayTicks;
    private static int remainingRidingTicks;

    public static void onClientTick$End(Minecraft minecraft) {

        if (minecraft.player == null || minecraft.isPaused()) return;

        ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);

        // update display ticks
        if (config.displayActions.stream().anyMatch(condition -> condition.isActive(minecraft.player, remainingRidingTicks))) {

            remainingDisplayTicks = config.displayTime;
        } else if (remainingDisplayTicks > 0) {

            remainingDisplayTicks--;
        }

        // reset rotation when no longer shown
        if (remainingDisplayTicks <= 0 && config.displayTime != 0) {

            PaperDollRenderer.INSTANCE.reset();
        }

        // don't show paper doll in sneaking position after unmounting a vehicle / mount
        if (minecraft.player.isPassenger()) {

            remainingRidingTicks = Math.max(0, config.displayTime - 2);
        } else if (remainingRidingTicks > 0) {

            remainingRidingTicks--;
        }
    }

    public static void onRenderGui$Post(PoseStack poseStack, int screenWidth, int screenHeight, float tickDelta) {

        Minecraft minecraft = Minecraft.getInstance();

        minecraft.getProfiler().push("paperDoll");
        Player player = minecraft.player;
        if (!player.isInvisible() && !minecraft.player.isSpectator()) {

            ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);
            if (minecraft.options.getCameraType().isFirstPerson() || !config.firstPersonOnly) {

                if (remainingDisplayTicks > 0 || config.displayTime == 0) {

                    int scale = config.scale * 5;
                    int posX = config.position.getX(0, screenWidth, (int) (scale * 1.5F) + config.offsetX);
                    // can't use PositionPreset#getY as the orientation point isn't in the top left corner of the image
                    int posY = config.position.isBottom() ? screenHeight - scale - config.offsetY : (int) (scale * 2.5F) + config.offsetY;
                    posY -= scale - updateOffset(player, tickDelta) * scale;
                    if (config.potionShift) {

                        posY += config.position.getPotionShift(player.getActiveEffects());
                    }

                    PaperDollRenderer.INSTANCE.drawEntityOnScreen(posX, posY, scale, player, tickDelta);
                }
            }
        }

        minecraft.getProfiler().pop();
    }

    private static float updateOffset(Player player, float tickDelta) {

        // crouching check after elytra since you can do both at the same time
        float height = player.getDimensions(Pose.STANDING).height;
        if (player.getFallFlyingTicks() > 0) {

            float ticksElytraFlying = player.getFallFlyingTicks() + tickDelta;
            float flyingAnimation = Mth.clamp(ticksElytraFlying * 0.09F, 0.0F, 1.0F);
            float flyingHeight = player.getDimensions(Pose.FALL_FLYING).height / height;
            return Mth.lerp(flyingAnimation, 1.0F, flyingHeight);
        } else if (player.getSwimAmount(tickDelta) > 0.0) {

            float swimmingAnimation = player.isVisuallySwimming() ? 1.0F : player.getSwimAmount(tickDelta);
            float swimmingHeight = player.getDimensions(Pose.SWIMMING).height / height;
            return Mth.lerp(swimmingAnimation, 1.0F, swimmingHeight);
        } else if (player.isAutoSpinAttack()) {

            return player.getDimensions(Pose.SPIN_ATTACK).height / height;
        } else if (player.isCrouching()) {

            return player.getDimensions(Pose.CROUCHING).height / height;
        } else if (player.isSleeping()) {

            return player.getDimensions(Pose.SLEEPING).height / height;
        } else if (player.deathTime > 0) {

            float dyingAnimation = ((float) player.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
            dyingAnimation = Math.min(1.0F, Mth.sqrt(dyingAnimation));
            float dyingHeight = player.getDimensions(Pose.DYING).height / height;
            return Mth.lerp(dyingAnimation, 1.0F, dyingHeight);
        } else {

            return 1.0F;
        }
    }
}

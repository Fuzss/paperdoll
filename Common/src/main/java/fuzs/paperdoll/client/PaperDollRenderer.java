package fuzs.paperdoll.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import fuzs.paperdoll.PaperDoll;
import fuzs.paperdoll.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class PaperDollRenderer {
    private static final float MAX_DOLL_ROTATION = 30.0F;
    public static final PaperDollRenderer INSTANCE = new PaperDollRenderer();

    private float prevRotationYaw;

    public void drawEntityOnScreen(int posX, int posY, int scale, LivingEntity entity, float partialTicks) {

        // prepare
        RenderSystem.disableCull();
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate((float) posX, (float) posY, 950.0F);
        poseStack.scale(1.0F, 1.0F, -1.0F);

        // set angles and lighting
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack2 = new PoseStack();
        poseStack2.translate(0.0, 0.0, 1000.0);
        poseStack2.scale((float) scale, (float) scale, (float) scale);
        Quaternion quaternionZ = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternionX = Vector3f.XP.rotationDegrees(15.0F);
        quaternionZ.mul(quaternionX);
        poseStack2.mulPose(quaternionZ);

        // save rotation as we don't want to change the actual entity
        float xRot = entity.getXRot();
        float yBodyRot = entity.yBodyRot;
        float yHeadRot = entity.yHeadRot;
        float xRotO = entity.xRotO;
        float yBodyRotO = entity.yBodyRotO;
        float yHeadRotO = entity.yHeadRotO;
        this.prevRotationYaw = this.updateRotation(entity, partialTicks, this.prevRotationYaw, yHeadRot, yHeadRotO);

        // do render
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternionX.conj();
        dispatcher.overrideCameraOrientation(quaternionX);
        dispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, partialTicks, poseStack2, bufferSource, 15728880);
        });
        bufferSource.endBatch();
        dispatcher.setRenderShadow(true);

        // restore entity rotation
        entity.setXRot(xRot);
        entity.yBodyRot = yBodyRot;
        entity.yHeadRot = yHeadRot;
        entity.xRotO = xRotO;
        entity.yBodyRotO = yBodyRotO;
        entity.yHeadRotO = yHeadRotO;

        // finish
        poseStack.popPose();
        RenderSystem.enableCull();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    private float updateRotation(LivingEntity entity, float partialTicks, float prevRotationYaw, float yHeadRot, float yHeadRotO) {

        ClientConfig.HeadMovement headMovement = PaperDoll.CONFIG.get(ClientConfig.class).headMovement;
        // head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
        float defaultRotationYaw = 180.0F + PaperDoll.CONFIG.get(ClientConfig.class).position.getRotation(MAX_DOLL_ROTATION / 2.0F);
        if (headMovement == ClientConfig.HeadMovement.YAW || entity.isFallFlying()) {

            entity.setXRot(7.5F);
            entity.xRotO = 7.5F;
        }

        entity.yBodyRot = defaultRotationYaw;
        entity.yBodyRotO = defaultRotationYaw;
        if (headMovement == ClientConfig.HeadMovement.PITCH) {

            entity.yHeadRotO = defaultRotationYaw;
            entity.yHeadRot = defaultRotationYaw;
        } else {

            entity.yHeadRotO = defaultRotationYaw + prevRotationYaw;
            prevRotationYaw = this.rotateEntity(prevRotationYaw, yHeadRot - yHeadRotO, partialTicks);
            entity.yHeadRot = defaultRotationYaw + prevRotationYaw;
        }

        return prevRotationYaw;
    }

    /**
     * Rotate entity according to its yaw, slowly spin back to default when yaw stays constant for a while
     */
    private float rotateEntity(float rotationYaw, float yBodyRotDiff, float partialTicks) {

        if (Minecraft.getInstance().isPaused()) {

            return rotationYaw;
        }

        // apply rotation change from entity
        rotationYaw = Mth.clamp(rotationYaw + yBodyRotDiff * 0.5F, -MAX_DOLL_ROTATION, MAX_DOLL_ROTATION);
        // rotate back to origin, never overshoot 0
        partialTicks = rotationYaw - partialTicks * rotationYaw / 15.0F;
        if (rotationYaw < 0.0F) {

            rotationYaw = Math.min(0, partialTicks);
        } else if (rotationYaw > 0.0F) {

            rotationYaw = Math.max(0, partialTicks);
        }

        return rotationYaw;
    }

    public void reset() {

        this.prevRotationYaw = 0;
    }
}

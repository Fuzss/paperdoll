package fuzs.paperdoll.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import fuzs.paperdoll.client.handler.PaperDollHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;

public class PaperDollRenderer {

    public static void drawEntityOnScreen(int posX, int posY, int scale, LivingEntity entity, float partialTicks) {

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

        PaperDollHandler.applyEntityRotations(entity);

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
}

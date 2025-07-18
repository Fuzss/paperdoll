package fuzs.paperdoll.client.util;

import fuzs.paperdoll.client.handler.PaperDollHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PaperDollRenderer {

    /**
     * @see net.minecraft.client.gui.screens.inventory.InventoryScreen#renderEntityInInventoryFollowsMouse(GuiGraphics,
     *         int, int, int, int, int, float, float, float, LivingEntity)
     */
    public static void renderEntityInInventory(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int scale, float yOffset, LivingEntity livingEntity, float partialTick) {
        Quaternionf rotation = new Quaternionf().rotateZ(Mth.PI);
        Quaternionf overrideCameraAngle = new Quaternionf().rotateX(15.0F * Mth.DEG_TO_RAD);
        rotation.mul(overrideCameraAngle);
        float xRot = livingEntity.getXRot();
        float yRot = livingEntity.getYRot();
        float xRotO = livingEntity.xRotO;
        float yRotO = livingEntity.yRotO;
        float yBodyRot = livingEntity.yBodyRot;
        float yBodyRotO = livingEntity.yBodyRotO;
        float yHeadRot = livingEntity.yHeadRot;
        float yHeadRotO = livingEntity.yHeadRotO;
        PaperDollHandler.applyEntityRotations(livingEntity);
        float entityScale = livingEntity.getScale();
        Vector3f vector3f = new Vector3f(0.0F, livingEntity.getBbHeight() / 2.0F + yOffset * entityScale, 0.0F);
        float relativeScale = scale / entityScale;
        renderEntityInInventory(guiGraphics,
                x1,
                y1,
                x2,
                y2,
                relativeScale,
                vector3f,
                rotation,
                overrideCameraAngle,
                livingEntity,
                partialTick);
        livingEntity.setXRot(xRot);
        livingEntity.setYRot(yRot);
        livingEntity.xRotO = xRotO;
        livingEntity.yRotO = yRotO;
        livingEntity.yBodyRot = yBodyRot;
        livingEntity.yBodyRotO = yBodyRotO;
        livingEntity.yHeadRot = yHeadRot;
        livingEntity.yHeadRotO = yHeadRotO;
    }

    /**
     * @see net.minecraft.client.gui.screens.inventory.InventoryScreen#renderEntityInInventory(GuiGraphics, int, int,
     *         int, int, float, Vector3f, Quaternionf, Quaternionf, LivingEntity)
     */
    public static void renderEntityInInventory(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, float scale, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, LivingEntity livingEntity, float partialTick) {
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> entityRenderer = entityRenderDispatcher.getRenderer(livingEntity);
        EntityRenderState entityRenderState = entityRenderer.createRenderState(livingEntity, partialTick);
        entityRenderState.hitboxesRenderState = null;
        guiGraphics.submitEntityRenderState(entityRenderState,
                scale,
                translation,
                rotation,
                overrideCameraAngle,
                x1,
                y1,
                x2,
                y2);
    }
}

package fuzs.paperdoll.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.paperdoll.client.handler.PaperDollHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PaperDollRenderer {

    public static void renderEntityInInventoryUpdateRotations(GuiGraphics guiGraphics, int posX, int posY, int scale, LivingEntity entity, float partialTicks) {

        // save rotation as we don't want to change the actual entity
        PlayerRotations playerRotations = new PlayerRotations(entity);
        PaperDollHandler.applyEntityRotations(entity);

        // do render
        Quaternionf pose = new Quaternionf().rotateZ(180.0F * 0.017453292F);
        Quaternionf cameraOrientation = new Quaternionf().rotateX(15.0F * 0.017453292F);
        pose.mul(cameraOrientation);
        renderEntityInInventory(guiGraphics, posX, posY, scale, new Vector3f(), pose, cameraOrientation, entity,
                partialTicks
        );

        // restore entity rotation
        playerRotations.apply(entity);
    }

    /**
     * Copied from
     * {@link net.minecraft.client.gui.screens.inventory.InventoryScreen#renderEntityInInventory(GuiGraphics, float,
     * float, float, Vector3f, Quaternionf, Quaternionf, LivingEntity)}, but with additional partial tick parameter.
     */
    public static void renderEntityInInventory(GuiGraphics guiGraphics, float x, float y, int scale, Vector3f translate, Quaternionf pose, @Nullable Quaternionf cameraOrientation, LivingEntity entity, float partialTick) {

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 50.0);
        guiGraphics.pose().scale(scale, scale, -scale);
        guiGraphics.pose().translate(translate.x, translate.y, translate.z);
        guiGraphics.pose().mulPose(pose);

        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (cameraOrientation != null) {
            cameraOrientation.conjugate();
            entityRenderDispatcher.overrideCameraOrientation(cameraOrientation);
        }

        entityRenderDispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, partialTick, guiGraphics.pose(),
                    guiGraphics.bufferSource(), 0xF000F0
            );
        });
        guiGraphics.flush();
        entityRenderDispatcher.setRenderShadow(true);

        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    private record PlayerRotations(float xRot,
                                   float yBodyRot,
                                   float yHeadRot,
                                   float xRotO,
                                   float yBodyRotO,
                                   float yHeadRotO) {

        public PlayerRotations(LivingEntity entity) {
            this(entity.getXRot(), entity.yBodyRot, entity.yHeadRot, entity.xRotO, entity.yBodyRotO, entity.yHeadRotO);
        }

        public void apply(LivingEntity entity) {
            entity.setXRot(this.xRot);
            entity.yBodyRot = this.yBodyRot;
            entity.yHeadRot = this.yHeadRot;
            entity.xRotO = this.xRotO;
            entity.yBodyRotO = this.yBodyRotO;
            entity.yHeadRotO = this.yHeadRotO;
        }
    }
}

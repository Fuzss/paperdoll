package fuzs.paperdoll.client.gui;

import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;

public enum PositionPreset {
    TOP_LEFT(0, 0),
    TOP_RIGHT(1, 0),
    BOTTOM_LEFT(0, 1),
    BOTTOM_RIGHT(1, 1);

    private final int posX;
    private final int posY;

    PositionPreset(int posX, int posY) {

        this.posX = posX;
        this.posY = posY;
    }

    public boolean isMirrored() {

        return this.posX == 1;
    }

    public boolean isBottom() {

        return this.posY == 1;
    }

    public int getX(int textureWidth, int scaledWidth, int offset) {

        return Math.abs((scaledWidth - textureWidth) * this.posX - offset);
    }

    public int getY(int textureHeight, int scaledHeight, int offset) {

        return Math.abs((scaledHeight - textureHeight) * this.posY - offset);
    }

    public float getRotation(float rotation) {

        // inverts a value depending on the display side
        return -(rotation - rotation * 2 * this.posX);
    }

    public int getPotionShift(Collection<MobEffectInstance> activeEffects) {

        if (this != TOP_RIGHT) return 0;

        if (!activeEffects.isEmpty()) {

            if (activeEffects.stream().anyMatch(MobEffectInstance::isVisible)) {

                return activeEffects.stream().anyMatch(effect -> !effect.getEffect().value().isBeneficial()) ? 50 : 25;
            }
        }

        return 0;
    }
}

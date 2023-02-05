package fuzs.paperdoll.client;

import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;

public enum PositionPreset {
    TOP_LEFT(0, 0, false),
    TOP_RIGHT(1, 0, true),
    BOTTOM_LEFT(0, 1, false),
    BOTTOM_RIGHT(1, 1, false);

    private final int posX;
    private final int posY;
    private final boolean shift;

    PositionPreset(int posX, int posY, boolean potionShift) {

        this.posX = posX;
        this.posY = posY;
        this.shift = potionShift;
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

        if (!this.shift) return 0;

        if (!activeEffects.isEmpty()) {

            if (activeEffects.stream().anyMatch(MobEffectInstance::isVisible)) {

                return activeEffects.stream().anyMatch(effect -> !effect.getEffect().isBeneficial()) ? 50 : 25;
            }
        }

        return 0;
    }

}

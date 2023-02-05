package fuzs.paperdoll.client.handler;

import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public enum DisplayAction {
    SPRINTING(Player::canSpawnSprintParticle),
    SWIMMING(player -> player.getSwimAmount(1.0F) > 0 && player.isInWater()),
    CRAWLING(player -> player.getSwimAmount(1.0F) > 0 && !player.isInWater()),
    CROUCHING(Player::isCrouching),
    FLYING(player -> player.getAbilities().flying),
    GLIDING(Player::isFallFlying),
    RIDING(Player::isPassenger),
    SPIN_ATTACKING(Player::isAutoSpinAttack),
    USING(Player::isUsingItem);

    final Predicate<Player> action;

    DisplayAction(Predicate<Player> action) {

        this.action = action;
    }

    public boolean isActive(Player player, int remainingRidingTicks) {
        return (this != CROUCHING || remainingRidingTicks == 0) && this.action.test(player);
    }
}

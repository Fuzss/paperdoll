package fuzs.paperdoll.client.gui;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public enum DisplayAction {
    SPRINTING(Player::canSpawnSprintParticle),
    SWIMMING(Predicate.not(Player::isVisuallyCrawling).and(Player::isVisuallySwimming)),
    CRAWLING(Player::isVisuallyCrawling),
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

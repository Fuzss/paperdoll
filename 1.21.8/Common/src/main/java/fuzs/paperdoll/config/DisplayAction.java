package fuzs.paperdoll.config;

import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public enum DisplayAction {
    SPRINTING(Player::canSpawnSprintParticle, (ClientConfig.DisplayActionsConfig config) -> config.sprinting),
    SWIMMING(Predicate.not(Player::isVisuallyCrawling)
            .and(Player::isVisuallySwimming)
            .and((Player player) -> player.getSwimAmount(1.0F) > 0.0F),
            (ClientConfig.DisplayActionsConfig config) -> config.sprinting),
    CRAWLING(Player::isVisuallyCrawling, (ClientConfig.DisplayActionsConfig config) -> config.crawling),
    CROUCHING(Player::isCrouching, (ClientConfig.DisplayActionsConfig config) -> config.crouching),
    FLYING((Player player) -> player.getAbilities().flying,
            (ClientConfig.DisplayActionsConfig config) -> config.flying),
    GLIDING(Player::isFallFlying, (ClientConfig.DisplayActionsConfig config) -> config.gliding),
    RIDING(Player::isPassenger, (ClientConfig.DisplayActionsConfig config) -> config.riding),
    SPIN_ATTACKING(Player::isAutoSpinAttack, (ClientConfig.DisplayActionsConfig config) -> config.spinAttacking),
    USING(Player::isUsingItem, (ClientConfig.DisplayActionsConfig config) -> config.using);

    static final DisplayAction[] VALUES = values();

    final Predicate<Player> playerAction;
    final Predicate<ClientConfig.DisplayActionsConfig> isEnabled;

    DisplayAction(Predicate<Player> playerAction, Predicate<ClientConfig.DisplayActionsConfig> isEnabled) {
        this.playerAction = playerAction;
        this.isEnabled = isEnabled;
    }

    public static boolean isActive(ClientConfig.DisplayActionsConfig config, Player player, int remainingRidingTicks) {
        for (DisplayAction displayAction : VALUES) {
            if (displayAction.isEnabled.test(config) && displayAction.isActive(player, remainingRidingTicks)) {
                return true;
            }
        }

        return false;
    }

    private boolean isActive(Player player, int remainingRidingTicks) {
        return (this != CROUCHING || remainingRidingTicks == 0)
                && this.playerAction.test(player);
    }
}

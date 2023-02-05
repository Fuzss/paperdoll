package fuzs.paperdoll.client;

import com.mojang.blaze3d.platform.Window;
import fuzs.paperdoll.client.handler.PaperDollHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class PaperDollFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientTickEvents.END_CLIENT_TICK.register(PaperDollHandler::onClientTick$End);
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            Window window = Minecraft.getInstance().getWindow();
            PaperDollHandler.onRenderGui$Post(matrixStack, window.getGuiScaledWidth(), window.getGuiScaledHeight(), tickDelta);
        });
    }
}

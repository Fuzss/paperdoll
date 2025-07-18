package fuzs.paperdoll.client;

import fuzs.paperdoll.PaperDoll;
import fuzs.paperdoll.client.handler.PaperDollHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;

public class PaperDollClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.END.register(PaperDollHandler::onEndClientTick);
    }

    @Override
    public void onRegisterGuiLayers(GuiLayersContext context) {
        context.registerGuiLayer(PaperDoll.id("paper_doll"),
                GuiLayersContext.SLEEP_OVERLAY,
                PaperDollHandler::renderPaperDoll);
    }
}

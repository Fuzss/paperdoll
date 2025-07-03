package fuzs.paperdoll.client;

import fuzs.paperdoll.client.handler.PaperDollHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;

public class PaperDollClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.END.register(PaperDollHandler::onEndClientTick);
        RenderGuiEvents.AFTER.register(PaperDollHandler::onRenderGui);
    }
}

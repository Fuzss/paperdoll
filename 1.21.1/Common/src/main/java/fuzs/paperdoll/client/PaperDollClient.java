package fuzs.paperdoll.client;

import fuzs.paperdoll.client.handler.PaperDollHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiCallback;

public class PaperDollClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientTickEvents.END.register(PaperDollHandler::onEndClientTick);
        RenderGuiCallback.EVENT.register(PaperDollHandler::onRenderGui);
    }
}

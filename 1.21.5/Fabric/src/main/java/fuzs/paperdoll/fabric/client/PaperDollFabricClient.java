package fuzs.paperdoll.fabric.client;

import fuzs.paperdoll.PaperDoll;
import fuzs.paperdoll.client.PaperDollClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class PaperDollFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PaperDoll.MOD_ID, PaperDollClient::new);
    }
}

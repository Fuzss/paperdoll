package fuzs.paperdoll.neoforge.client;

import fuzs.paperdoll.PaperDoll;
import fuzs.paperdoll.client.PaperDollClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = PaperDoll.MOD_ID, dist = Dist.CLIENT)
public class PaperDollNeoForgeClient {

    public PaperDollNeoForgeClient() {
        ClientModConstructor.construct(PaperDoll.MOD_ID, PaperDollClient::new);
    }
}

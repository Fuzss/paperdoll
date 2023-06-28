package fuzs.paperdoll;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class PaperDollFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(PaperDoll.MOD_ID, PaperDoll::new);
    }
}

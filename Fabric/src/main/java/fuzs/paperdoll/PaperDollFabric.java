package fuzs.paperdoll;

import fuzs.puzzleslib.core.CommonFactories;
import net.fabricmc.api.ModInitializer;

public class PaperDollFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonFactories.INSTANCE.modConstructor(PaperDoll.MOD_ID).accept(new PaperDoll());
    }
}

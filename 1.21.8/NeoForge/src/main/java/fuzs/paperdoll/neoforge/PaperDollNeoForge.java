package fuzs.paperdoll.neoforge;

import fuzs.paperdoll.PaperDoll;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod(PaperDoll.MOD_ID)
public class PaperDollNeoForge {

    public PaperDollNeoForge() {
        ModConstructor.construct(PaperDoll.MOD_ID, PaperDoll::new);
    }
}

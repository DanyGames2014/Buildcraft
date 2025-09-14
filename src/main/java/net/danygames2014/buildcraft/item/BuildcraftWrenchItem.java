package net.danygames2014.buildcraft.item;

import net.danygames2014.uniwrench.api.WrenchMode;
import net.danygames2014.uniwrench.item.WrenchBase;
import net.modificationstation.stationapi.api.util.Identifier;

public class BuildcraftWrenchItem extends WrenchBase {
    public BuildcraftWrenchItem(Identifier identifier) {
        super(identifier);
        addWrenchMode(WrenchMode.MODE_WRENCH);
        addWrenchMode(WrenchMode.MODE_ROTATE);
    }
}

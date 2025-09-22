package net.danygames2014.buildcraft.api.transport;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateExpansionController;

public interface GateExpansion {
    String getUniqueIdentifier();

    String getDisplayName();

    GateExpansionController makeController(PipeBlockEntity pipe);

    void registerBlockOverlay(int blockOverlayTexture);

    void registerItemOverlay(int itemOverlayTexture);

    int getOverlayBlock();

    int getOverlayItem();
}

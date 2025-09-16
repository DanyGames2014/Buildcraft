package net.danygames2014.buildcraft.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class TextureListener {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    public static Atlas.Sprite energySprite;
    @EventListener
    public void registerTextures(TextureRegisterEvent event){
        energySprite = Atlases.getGuiItems().addTexture(NAMESPACE.id("item/energy_icon"));
    }
}

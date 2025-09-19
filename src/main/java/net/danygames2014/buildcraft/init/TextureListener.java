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

    // Pipes
    public static Atlas.Sprite cobblestoneItemPipe;
    public static Atlas.Sprite cobblestoneFluidPipe;
    public static Atlas.Sprite cobblestoneEnergyPipe;

    public static Atlas.Sprite woodenItemPipe;
    public static Atlas.Sprite woodenFluidPipe;
    public static Atlas.Sprite woodenEnergyPipe;
    
    public static Atlas.Sprite goldenItemPipe;


    @EventListener
    public void registerTextures(TextureRegisterEvent event){
        energySprite = Atlases.getGuiItems().addTexture(NAMESPACE.id("item/energy_icon"));

        cobblestoneItemPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/cobblestone_item_pipe"));
        cobblestoneFluidPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/cobblestone_fluid_pipe"));
        cobblestoneEnergyPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/cobblestone_energy_pipe"));

        woodenItemPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/wooden_item_pipe"));
        woodenFluidPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/wooden_fluid_pipe"));
        woodenEnergyPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/wooden_energy_pipe"));
        
        goldenItemPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/golden_item_pipe"));
    }
}

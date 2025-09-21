package net.danygames2014.buildcraft.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;

import java.util.ArrayList;
import java.util.List;

public class TextureListener {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    public static List<Identifier> dynamicBlockTextures = new ArrayList<>();

    public static Atlas.Sprite energySprite;

    // Pipes
    public static Atlas.Sprite cobblestoneItemPipe;
    public static Atlas.Sprite cobblestoneFluidPipe;
    public static Atlas.Sprite cobblestoneEnergyPipe;

    public static Atlas.Sprite woodenItemPipe;
    public static Atlas.Sprite woodenFluidPipe;
    public static Atlas.Sprite woodenEnergyPipe;
    public static Atlas.Sprite goldenItemPipe;

    public static Atlas.Sprite redPipeWire;
    public static Atlas.Sprite redPipeWireLit;
    public static Atlas.Sprite bluePipeWire;
    public static Atlas.Sprite bluePipeWireLit;
    public static Atlas.Sprite greenPipeWire;
    public static Atlas.Sprite greenPipeWireLit;
    public static Atlas.Sprite yellowPipeWire;
    public static Atlas.Sprite yellowPipeWireLit;

    public static Atlas.Sprite plug;
    public static Atlas.Sprite structurePipe;


    @EventListener
    public void registerTextures(TextureRegisterEvent event){

        for(Identifier identifier : dynamicBlockTextures){
            Atlases.getTerrain().addTexture(identifier);
        }

        energySprite = Atlases.getGuiItems().addTexture(NAMESPACE.id("item/energy_icon"));

//        cobblestoneItemPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/cobblestone_item_pipe"));
//        cobblestoneFluidPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/cobblestone_fluid_pipe"));
//        cobblestoneEnergyPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/cobblestone_energy_pipe"));
//
//        woodenItemPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/wooden_item_pipe"));
//        woodenFluidPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/wooden_fluid_pipe"));
//        woodenEnergyPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/wooden_energy_pipe"));
//        goldenItemPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/golden_item_pipe"));

        redPipeWire = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipewire/red_pipe_wire"));
        redPipeWireLit = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipewire/red_pipe_wire_lit"));

        bluePipeWire = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipewire/blue_pipe_wire"));
        bluePipeWireLit = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipewire/blue_pipe_wire_lit"));

        greenPipeWire = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipewire/green_pipe_wire"));
        greenPipeWireLit = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipewire/green_pipe_wire_lit"));

        yellowPipeWire = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipewire/yellow_pipe_wire"));
        yellowPipeWireLit = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipewire/yellow_pipe_wire_lit"));

        plug = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pluggable/plug"));
        structurePipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/structure_pipe"));
    }
}

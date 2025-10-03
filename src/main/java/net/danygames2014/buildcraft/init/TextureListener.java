package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.Buildcraft;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;

import java.util.ArrayList;
import java.util.List;

public class TextureListener {

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

    public static Identifier redPipeWire = Buildcraft.NAMESPACE.id("block/pipewire/red_pipe_wire");
    public static Identifier redPipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/red_pipe_wire_lit");
    public static Identifier bluePipeWire = Buildcraft.NAMESPACE.id("block/pipewire/blue_pipe_wire");
    public static Identifier bluePipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/blue_pipe_wire_lit");
    public static Identifier greenPipeWire = Buildcraft.NAMESPACE.id("block/pipewire/green_pipe_wire");
    public static Identifier greenPipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/green_pipe_wire_lit");
    public static Identifier yellowPipeWire = Buildcraft.NAMESPACE.id("block/pipewire/yellow_pipe_wire");
    public static Identifier yellowPipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/yellow_pipe_wire_lit");

    public static Identifier plug = Buildcraft.NAMESPACE.id("block/pluggable/plug");
    public static Identifier structurePipe = Buildcraft.NAMESPACE.id("block/pipe/structure_pipe");

    public static Identifier missingTexture = Buildcraft.NAMESPACE.id("block/missing_texture");

    public static Atlas.Sprite energyRedSprite;
    public static Atlas.Sprite energyCyanSprite;

    public static Atlas.Sprite redLaser;
    public static Atlas.Sprite blueLaser;
    public static Atlas.Sprite stripesLaser;


    @EventListener
    public void registerTextures(TextureRegisterEvent event){

        for(Identifier identifier : dynamicBlockTextures){
            Atlases.getTerrain().addTexture(identifier);
        }

        energySprite = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/energy_icon"));

//        cobblestoneItemPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/cobblestone_item_pipe"));
//        cobblestoneFluidPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/cobblestone_fluid_pipe"));
//        cobblestoneEnergyPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/cobblestone_energy_pipe"));
//
//        woodenItemPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/wooden_item_pipe"));
//        woodenFluidPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/wooden_fluid_pipe"));
//        woodenEnergyPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/wooden_energy_pipe"));
//        goldenItemPipe = Atlases.getTerrain().addTexture(NAMESPACE.id("block/pipe/golden_item_pipe"));

        Atlases.getTerrain().addTexture(redPipeWire);
        Atlases.getTerrain().addTexture(redPipeWireLit);

        Atlases.getTerrain().addTexture(bluePipeWire);
        Atlases.getTerrain().addTexture(bluePipeWireLit);

        Atlases.getTerrain().addTexture(greenPipeWire);
        Atlases.getTerrain().addTexture(greenPipeWireLit);

        Atlases.getTerrain().addTexture(yellowPipeWire);
        Atlases.getTerrain().addTexture(yellowPipeWireLit);

        Atlases.getTerrain().addTexture(plug);
        Atlases.getTerrain().addTexture(structurePipe);

        Atlases.getTerrain().addTexture(missingTexture);

        energyRedSprite = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/misc/power_red"));
        energyCyanSprite = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/misc/power_cyan"));

        redLaser = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/laser/block_red"));
        blueLaser = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/laser/block_red"));
        stripesLaser = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/laser/stripes_red"));
    }

    @EventListener
    public void registerItemModelPredicates(ItemModelPredicateProviderRegistryEvent event) {
        event.registry.register(
                Buildcraft.blueprint,
                Buildcraft.NAMESPACE.id("blueprint_used"),
                (stack, world, entity, seed) -> {
                    return stack.getStationNbt().getInt("used");
                }
        );

        event.registry.register(
                Buildcraft.template,
                Buildcraft.NAMESPACE.id("template_used"),
                (stack, world, entity, seed) -> {
                    return stack.getStationNbt().getInt("used");
                }
        );
    }
}

package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansions;
import net.danygames2014.buildcraft.api.transport.statement.StatementManager;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.item.LensItem;
import net.danygames2014.buildcraft.util.ColorUtil;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.color.item.ItemColorProvider;
import net.modificationstation.stationapi.api.client.event.color.item.ItemColorsRegisterEvent;
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TextureListener {

    public static List<Identifier> dynamicBlockTextures = new ArrayList<>();

    public static Atlas.Sprite energySprite;

    // Pipes
    public static Identifier diamondItemPipeUp = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_up");
    public static Identifier diamondItemPipeDown = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_down");
    public static Identifier diamondItemPipeNorth = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_north");
    public static Identifier diamondItemPipeSouth = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_south");
    public static Identifier diamondItemPipeEast = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_east");
    public static Identifier diamondItemPipeWest = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_west");

    public static Identifier diamondFluidPipeUp = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_up");
    public static Identifier diamondFluidPipeDown = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_down");
    public static Identifier diamondFluidPipeNorth = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_north");
    public static Identifier diamondFluidPipeSouth = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_south");
    public static Identifier diamondFluidPipeEast = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_east");
    public static Identifier diamondFluidPipeWest = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_west");

    public static Identifier pipeStainedOverlay = Buildcraft.NAMESPACE.id("block/pipe/pipe_stained_overlay");

    public static Identifier redPipeWire = Buildcraft.NAMESPACE.id("block/pipewire/red_pipe_wire");
    public static Identifier redPipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/red_pipe_wire_lit");
    public static Identifier bluePipeWire = Buildcraft.NAMESPACE.id("block/pipewire/blue_pipe_wire");
    public static Identifier bluePipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/blue_pipe_wire_lit");
    public static Identifier greenPipeWire = Buildcraft.NAMESPACE.id("block/pipewire/green_pipe_wire");
    public static Identifier greenPipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/green_pipe_wire_lit");
    public static Identifier yellowPipeWire = Buildcraft.NAMESPACE.id("block/pipewire/yellow_pipe_wire");
    public static Identifier yellowPipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/yellow_pipe_wire_lit");

    public static Identifier plug = Buildcraft.NAMESPACE.id("block/pluggable/plug");
    public static Identifier lens = Buildcraft.NAMESPACE.id("block/pluggable/lens_frame");
    public static Identifier filter = Buildcraft.NAMESPACE.id("block/pluggable/filter_frame");
    public static Identifier lensOverlay = Buildcraft.NAMESPACE.id("block/pluggable/lens_overlay");

    public static Identifier structurePipe = Buildcraft.NAMESPACE.id("block/pipe/structure_pipe");

    public static Identifier missingTexture = Buildcraft.NAMESPACE.id("block/missing_texture");

    public static Atlas.Sprite energyRedSprite;
    public static Atlas.Sprite energyCyanSprite;

    public static Atlas.Sprite redLaser;
    public static Atlas.Sprite blueLaser;
    public static Atlas.Sprite stripesLaser;


    @EventListener
    public void registerTextures(TextureRegisterEvent event) {

        for (Identifier identifier : dynamicBlockTextures) {
            Atlases.getTerrain().addTexture(identifier);
        }

        energySprite = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/energy_icon"));

        Atlases.getTerrain().addTexture(diamondItemPipeUp);
        Atlases.getTerrain().addTexture(diamondItemPipeDown);
        Atlases.getTerrain().addTexture(diamondItemPipeNorth);
        Atlases.getTerrain().addTexture(diamondItemPipeSouth);
        Atlases.getTerrain().addTexture(diamondItemPipeEast);
        Atlases.getTerrain().addTexture(diamondItemPipeWest);
        
        Atlases.getTerrain().addTexture(diamondFluidPipeUp);
        Atlases.getTerrain().addTexture(diamondFluidPipeDown);
        Atlases.getTerrain().addTexture(diamondFluidPipeNorth);
        Atlases.getTerrain().addTexture(diamondFluidPipeSouth);
        Atlases.getTerrain().addTexture(diamondFluidPipeEast);
        Atlases.getTerrain().addTexture(diamondFluidPipeWest);

        Atlases.getTerrain().addTexture(pipeStainedOverlay);

        Atlases.getTerrain().addTexture(redPipeWire);
        Atlases.getTerrain().addTexture(redPipeWireLit);

        Atlases.getTerrain().addTexture(bluePipeWire);
        Atlases.getTerrain().addTexture(bluePipeWireLit);

        Atlases.getTerrain().addTexture(greenPipeWire);
        Atlases.getTerrain().addTexture(greenPipeWireLit);

        Atlases.getTerrain().addTexture(yellowPipeWire);
        Atlases.getTerrain().addTexture(yellowPipeWireLit);

        Atlases.getTerrain().addTexture(plug);
        Atlases.getTerrain().addTexture(lens);
        Atlases.getTerrain().addTexture(filter);
        Atlases.getTerrain().addTexture(lensOverlay);
        Atlases.getTerrain().addTexture(structurePipe);

        Atlases.getTerrain().addTexture(missingTexture);

        energyRedSprite = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/misc/power_red"));
        energyCyanSprite = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/misc/power_cyan"));

        redLaser = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/laser/block_red"));
        blueLaser = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/laser/block_red"));
        stripesLaser = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/laser/stripes_red"));

        StatementManager.registerTextures();

        for (GateMaterial material : GateMaterial.VALUES) {
            material.registerTextures();
        }

        for (GateLogic logic : GateLogic.VALUES) {
            logic.registerTextures();
        }

        for (GateExpansion expansion : GateExpansions.getExpansions()) {
            expansion.registerTextures();
        }
    }

    @EventListener
    public void registerItemModelPredicates(ItemModelPredicateProviderRegistryEvent event) {
        event.registry.register(
                Buildcraft.blueprint,
                Buildcraft.NAMESPACE.id("blueprint_used"),
                (stack, world, entity, seed) -> {
                    return stack.getStationNbt().getBoolean("written") ? 1 : 0;
                }
        );

        event.registry.register(
                Buildcraft.template,
                Buildcraft.NAMESPACE.id("template_used"),
                (stack, world, entity, seed) -> {
                    return stack.getStationNbt().getBoolean("written") ? 1 : 0;
                }
        );
    }

    @EventListener
    public void registerItemColorProvider(ItemColorsRegisterEvent event) {
        ItemColorProvider lensColorProvider = new ItemColorProvider() {
            @Override
            public int getColor(ItemStack stack, int layer) {
                if(layer == 0 && stack.getItem() instanceof LensItem lensItem){
                    return ColorUtil.getColor(lensItem.color);
                }
                return 0;
            }
        };
        for(int i = 0; i < 15; i++){
            event.itemColors.register(lensColorProvider, Buildcraft.lens[i], Buildcraft.filter[i]);
        }
    }
}

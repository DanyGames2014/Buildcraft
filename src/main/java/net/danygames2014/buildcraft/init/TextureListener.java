package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.blockentity.ControlMode;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansions;
import net.danygames2014.buildcraft.api.transport.statement.StatementManager;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.item.LensItem;
import net.danygames2014.buildcraft.registry.ControlModeRegistry;
import net.danygames2014.buildcraft.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
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
    public static final List<Identifier> dynamicBlockTextures = new ArrayList<>();

    public static Atlas.Sprite energySprite;

    // Pipes
    public static final Identifier diamondItemPipeUp = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_up");
    public static final Identifier diamondItemPipeDown = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_down");
    public static final Identifier diamondItemPipeNorth = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_north");
    public static final Identifier diamondItemPipeSouth = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_south");
    public static final Identifier diamondItemPipeEast = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_east");
    public static final Identifier diamondItemPipeWest = Buildcraft.NAMESPACE.id("block/pipe/diamond_item_pipe_west");

    public static final Identifier diamondFluidPipeUp = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_up");
    public static final Identifier diamondFluidPipeDown = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_down");
    public static final Identifier diamondFluidPipeNorth = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_north");
    public static final Identifier diamondFluidPipeSouth = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_south");
    public static final Identifier diamondFluidPipeEast = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_east");
    public static final Identifier diamondFluidPipeWest = Buildcraft.NAMESPACE.id("block/pipe/diamond_fluid_pipe_west");

    public static final Identifier pipeStainedOverlay = Buildcraft.NAMESPACE.id("block/pipe/pipe_stained_overlay");

    public static final Identifier redPipeWire = Buildcraft.NAMESPACE.id("block/pipewire/red_pipe_wire");
    public static final Identifier redPipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/red_pipe_wire_lit");
    public static final Identifier bluePipeWire = Buildcraft.NAMESPACE.id("block/pipewire/blue_pipe_wire");
    public static final Identifier bluePipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/blue_pipe_wire_lit");
    public static final Identifier greenPipeWire = Buildcraft.NAMESPACE.id("block/pipewire/green_pipe_wire");
    public static final Identifier greenPipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/green_pipe_wire_lit");
    public static final Identifier yellowPipeWire = Buildcraft.NAMESPACE.id("block/pipewire/yellow_pipe_wire");
    public static final Identifier yellowPipeWireLit = Buildcraft.NAMESPACE.id("block/pipewire/yellow_pipe_wire_lit");

    public static final Identifier plug = Buildcraft.NAMESPACE.id("block/pluggable/plug");
    public static final Identifier lens = Buildcraft.NAMESPACE.id("block/pluggable/lens_frame");
    public static final Identifier filter = Buildcraft.NAMESPACE.id("block/pluggable/filter_frame");
    public static final Identifier lensOverlay = Buildcraft.NAMESPACE.id("block/pluggable/lens_overlay");

    public static final Identifier structurePipe = Buildcraft.NAMESPACE.id("block/pipe/structure_pipe");

    public static final Identifier missingTexture = Buildcraft.NAMESPACE.id("block/missing_texture");

    public static Atlas.Sprite energyRedSprite;
    public static Atlas.Sprite energyCyanSprite;

    public static Atlas.Sprite redLaser;
    public static Atlas.Sprite blueLaser;
    public static Atlas.Sprite stripesLaser;

    public static Atlas.Sprite drill;
    public static Atlas.Sprite drillHead;

    public static Atlas.Sprite itemColorBox;


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
        blueLaser = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/laser/block_blue"));
        stripesLaser = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/laser/stripes"));

        drill = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/drill"));
        drillHead = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/drill_head"));

        itemColorBox = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/item_color_box"));

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
        
        for (ControlMode controlMode : ControlModeRegistry.getRegistry().values()) {
            controlMode.sprite = Atlases.getTerrain().addTexture(controlMode.texture);
        }
    }

    @SuppressWarnings("CodeBlock2Expr")
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

    @SuppressWarnings("Convert2Lambda")
    @Environment(EnvType.CLIENT)
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

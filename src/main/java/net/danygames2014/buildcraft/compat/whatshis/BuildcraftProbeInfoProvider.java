package net.danygames2014.buildcraft.compat.whatshis;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.danygames2014.buildcraft.block.entity.*;
import net.danygames2014.whatsthis.api.*;
import net.danygames2014.whatsthis.apiimpl.styles.LayoutStyle;
import net.danygames2014.whatsthis.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;

public class BuildcraftProbeInfoProvider implements IProbeInfoProvider {
    int borderColor;
    int filledColor;
    int alternateFilledColor;

    public BuildcraftProbeInfoProvider() {
        borderColor = Config.parseColor(Config.PROBE_CONFIG.rfbarBorderColor);
        filledColor = Config.parseColor(Config.PROBE_CONFIG.rfbarFilledColor);
        alternateFilledColor = Config.parseColor(Config.PROBE_CONFIG.rfbarAlternateFilledColor);
    }

    @Override
    public String getID() {
        return Buildcraft.NAMESPACE.id("buildcraft_block").toString();
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState state, IProbeHitData data) {
        BlockPos pos = data.getPos();
        Block block = state.getBlock();
        BlockEntity blockEntity = world.getBlockEntity(pos.x, pos.y, pos.z);

        if (blockEntity != null) {
            // Assembly Table
            if (blockEntity instanceof AssemblyTableBlockEntity assemblyTable) {
                AssemblyTableBlockEntity.RecipeEntry currentRecipe = assemblyTable.currentRecipe;
                if (currentRecipe != null) {
                    IProbeInfo vertical;
                    if (mode == ProbeMode.EXTENDED) {
                        vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xFF212121).spacing(3));
                    } else {
                        vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().spacing(3));
                    }
                    
                    vertical.progress(
                            assemblyTable.getProgressScaled(100),
                            100,
                            probeInfo.defaultProgressStyle()
                                    .filledColor(0xFFFF0000)
                                    .alternateFilledColor(0xFFBB0000)
                                    .suffix("%")
                                    .prefix("Assembling ")
                    );
                    
                    if (mode == ProbeMode.EXTENDED) {
                        IProbeInfo horizontal = vertical.horizontal(new LayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                        horizontal.item(currentRecipe.icon).itemLabel(currentRecipe.icon);
                    }
                }
            }

            // Laser
            if (blockEntity instanceof LaserBlockEntity laser) {
                IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().spacing(3));

                vertical.progress(
                        (int) laser.powerHandler.getEnergyStored(),
                        (int) laser.powerHandler.getMaxEnergyStored(),
                        probeInfo.defaultProgressStyle()
                                .borderColor(borderColor)
                                .filledColor(filledColor)
                                .alternateFilledColor(alternateFilledColor)
                                .prefix("Energy ")
                                .suffix("MJ")
                );
            }

            // Mining Well
            if (blockEntity instanceof MiningWellBlockEntity miningWell) {
                IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().spacing(3));

                vertical.progress(
                        (int) miningWell.powerHandler.getEnergyStored(),
                        (int) miningWell.powerHandler.getMaxEnergyStored(),
                        probeInfo.defaultProgressStyle()
                                .borderColor(borderColor)
                                .filledColor(filledColor)
                                .alternateFilledColor(alternateFilledColor)
                                .prefix("Energy ")
                                .suffix("MJ")
                );
                
                if (miningWell.isActive()) {
                    vertical.text(TextStyleClass.OK + "Active");
                } else {
                    vertical.text(TextStyleClass.WARNING + "Inactive");
                }
            }
            
            // Engines
            if (blockEntity instanceof BaseEngineBlockEntity engine) {
                IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().spacing(3));
                
                if (mode == ProbeMode.EXTENDED) {
                    vertical.text("Generating " + (MathHelper.floor(engine.getCurrentEnergyOutput() * 100) / 100D) + " MJ/t");
                }
                
                vertical.progress(
                        (int) engine.getEnergyStored(),
                        (int) engine.getMaxEnergy(),
                        probeInfo.defaultProgressStyle()
                                .borderColor(borderColor)
                                .filledColor(filledColor)
                                .alternateFilledColor(alternateFilledColor)
                                .prefix("Energy ")
                                .suffix("MJ")
                );

                EnergyStage stage = engine.calculateEnergyStage();
                vertical.progress(
                        (int) engine.getHeat(),
                        (int) BaseEngineBlockEntity.MAX_HEAT,
                        probeInfo.defaultProgressStyle()
                                .borderColor(borderColor)
                                .filledColor(stage.primaryColor)
                                .alternateFilledColor(stage.secondaryColor)
                                .prefix("Temperature ")
                                .suffix("°C")
                );
            }

            // Mining Well
            if (blockEntity instanceof BuilderBlockEntity builder) {
                IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().spacing(3));

                vertical.progress(
                        (int) builder.powerHandler.getEnergyStored(),
                        (int) builder.powerHandler.getMaxEnergyStored(),
                        probeInfo.defaultProgressStyle()
                                .borderColor(borderColor)
                                .filledColor(filledColor)
                                .alternateFilledColor(alternateFilledColor)
                                .prefix("Energy ")
                                .suffix("MJ")
                );

                switch (builder.state) {
                    case IDLE -> {
                        vertical.text(TextStyleClass.INFO + "Idle");
                    }
                    case READY -> {
                        vertical.text(TextStyleClass.OK + "Ready");
                        if (mode == ProbeMode.EXTENDED) {
                            vertical.text(builder.remainingBlocks + " blocks remaining");
                        }
                    }
                    case BUILDING -> {
                        vertical.text(TextStyleClass.OK + "Building");
                        if (mode == ProbeMode.EXTENDED) {
                            vertical.text(builder.remainingBlocks + " blocks remaining");
                        }
                    }
                    case STOPPED -> {
                        vertical.text(TextStyleClass.WARNING + "Stopped");
                    }
                }
            }
        }
    }
}

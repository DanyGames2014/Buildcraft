package net.danygames2014.buildcraft.config;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter.HandOffResult;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigFactoryProvider;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;
import net.glasslauncher.mods.gcapi3.impl.SeptFunction;
import net.glasslauncher.mods.gcapi3.impl.factory.DefaultFactoryProvider;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.EnumConfigEntryHandler;
import net.modificationstation.stationapi.api.util.Identifier;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.Function;

public class Config implements ConfigFactoryProvider {
    @ConfigRoot(value = "pipe", visibleName = "Pipe Config", index = 1)
    public static final PipeConfig PIPE_CONFIG = new PipeConfig();

    @ConfigRoot(value = "machine", visibleName = "Machine Config", index = 2)
    public static final MachineConfig MACHINE_CONFIG = new MachineConfig();

    @ConfigRoot(value = "worldgen", visibleName = "Worldgen Config", index = 3)
    public static final WorldgenConfig WORLDGEN_CONFIG = new WorldgenConfig();
    
    @Override
    public void provideLoadFactories(ImmutableMap.Builder<Type, SeptFunction<String, ConfigEntry, Field, Object, Boolean, Object, Object, ConfigEntryHandler<?>>> immutableBuilder) {
        immutableBuilder.put(HandOffResult.class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, enumOrOrdinal, defaultEnum) -> new EnumConfigEntryHandler<HandOffResult>(id, configEntry, parentField, parentObject, isMultiplayerSynced, DefaultFactoryProvider.enumOrOrdinalToOrdinal(enumOrOrdinal), ((HandOffResult) defaultEnum).ordinal(), HandOffResult.class)));
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, Object>> immutableBuilder) {
        immutableBuilder.put(HandOffResult.class, object -> object);
    }

    // Pump Fluid Blacklist
    public static Int2ObjectOpenHashMap<ArrayList<Fluid>> fluidBlacklistMap = new Int2ObjectOpenHashMap<>();
    
    public static void initPumpBlacklist() {
        // Fluid Blacklist
        Int2ObjectOpenHashMap<ArrayList<Fluid>> blacklistMap = new Int2ObjectOpenHashMap<>();

        for (String line : MACHINE_CONFIG.pump.fluidBlacklist) {
            String[] split = line.split(",");

            int dimensionId = 0;
            Fluid fluid = null;

            if (split.length == 1) {
                fluid = FluidRegistry.get(Identifier.of(split[0]));
                dimensionId = -999;
            }

            if (split.length == 2) {
                fluid = FluidRegistry.get(Identifier.of(split[1]));
                dimensionId = Integer.parseInt(split[0]);
            }

            if (fluid != null) {
                if (!blacklistMap.containsKey(dimensionId)) {
                    blacklistMap.put(dimensionId, new ArrayList<>());
                }

                if (!blacklistMap.get(dimensionId).contains(fluid)) {
                    blacklistMap.get(dimensionId).add(fluid);
                }
            }
        }

        fluidBlacklistMap = blacklistMap;
    }
}

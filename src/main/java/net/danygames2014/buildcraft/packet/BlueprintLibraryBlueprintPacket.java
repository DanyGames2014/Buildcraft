package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.item.*;
import net.danygames2014.buildcraft.screen.handler.BlueprintLibraryScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class BlueprintLibraryBlueprintPacket extends Packet implements ManagedPacket<BlueprintLibraryBlueprintPacket> {
    public static final PacketType<BlueprintLibraryBlueprintPacket> TYPE = PacketType.builder(true, true, BlueprintLibraryBlueprintPacket::new).build();

    BlueprintData blueprintData;
    /**
     * 0 - Read from blueprint
     * 1 - Write to blueprint
     */
    int operation;

    public BlueprintLibraryBlueprintPacket() {
    }

    public BlueprintLibraryBlueprintPacket(BlueprintData blueprintData, int operation) {
        this.blueprintData = blueprintData;
        this.operation = operation;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            operation = stream.readInt();

            NbtCompound blueprintNbt = new NbtCompound();

            int length = stream.readInt();
            if (length > 0) {
                byte[] bytes = new byte[length];
                stream.readFully(bytes);
                blueprintNbt = NbtIo.readCompressed(new ByteArrayInputStream(bytes));
            }

            BlueprintData data = new BlueprintData();
            data.readNbt(blueprintNbt);
            this.blueprintData = data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(operation);

            NbtCompound blueprintNbt = new NbtCompound();
            if (blueprintData == null) {
                stream.writeInt(0);
                return;
            }

            blueprintData.writeNbt(blueprintNbt);

            if (blueprintNbt.values().isEmpty()) {
                stream.writeInt(0); // length   
            } else {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                NbtIo.writeCompressed(blueprintNbt, byteStream);
                byte[] bytes = byteStream.toByteArray();
                stream.writeInt(bytes.length); // length
                stream.write(bytes); // nbt data
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        World world = player.world;
        
        if (blueprintData == null) {
            return;
        }

        // Read from Blueprint - Only on client
        if (operation == 0 && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            if (player.currentScreenHandler instanceof BlueprintLibraryScreenHandler library) {
                ItemStack stack = library.blockEntity.getStack(0);
                if (stack == null || library.blockEntity.getStack(1) != null) {
                    return;
                }

                if (stack.getItem() instanceof BuilderBlueprintItem || stack.getItem() instanceof BuilderTemplateItem) {
                    SideUtil.run(() -> BlueprintManager.save(blueprintData), () -> {
                    });

                    if (!world.isRemote) {
                        library.blockEntity.setStack(1, stack);
                        library.blockEntity.setStack(0, null);
                    }
                }
            }
        }

        // Write to Blueprint
        if (operation == 1) {
            if (player.currentScreenHandler instanceof BlueprintLibraryScreenHandler library) {
                ItemStack stack = library.blockEntity.getStack(2);
                if (stack == null || library.blockEntity.getStack(3) != null) {
                    return;
                }

                if (stack.getItem() instanceof BuilderBlueprintItem || stack.getItem() instanceof BuilderTemplateItem) {
                    BlueprintPersistentState blueprint;
                    if (stack.getDamage() == 0) {
                        blueprint = BlueprintPersistentState.get(world);
                    } else {
                        blueprint = BlueprintPersistentState.get(world, stack.getDamage());
                    }

                    blueprint.data = blueprintData;
                    blueprint.markDirty();

                    NbtCompound nbt = stack.getStationNbt();
                    nbt.putBoolean("written", true);
                    nbt.putString("name", blueprintData.name);
                    nbt.putString("author", blueprintData.author);
                    nbt.putInt("sizeX", blueprintData.sizeX);
                    nbt.putInt("sizeY", blueprintData.sizeY);
                    nbt.putInt("sizeZ", blueprintData.sizeZ);

                    stack.setDamage(blueprint.rawId);

                    if (!world.isRemote) {
                        library.blockEntity.setStack(3, stack);
                        library.onSlotUpdate(library.blockEntity);
                        library.blockEntity.setStack(2, null);
                        library.onSlotUpdate(library.blockEntity);
                    }
                }
            }
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<BlueprintLibraryBlueprintPacket> getType() {
        return TYPE;
    }
}

package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.api.core.Debuggable;
import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.danygames2014.buildcraft.block.entity.BaseEngineBlockEntity;
import net.danygames2014.buildcraft.client.render.block.EngineRenderer;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.danygames2014.uniwrench.api.Wrenchable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.client.model.block.BlockWithInventoryRenderer;
import net.modificationstation.stationapi.api.client.model.block.BlockWithWorldRenderer;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.lwjgl.opengl.GL11;

import java.util.Random;

@SuppressWarnings("deprecation")
public abstract class BaseEngineBlock extends TemplateBlockWithEntity implements BlockWithInventoryRenderer, BlockWithWorldRenderer, Wrenchable, Debuggable {
    public static final EnumProperty<EnergyStage> ENERGY_STAGE_PROPERTY = EnumProperty.of("energy_stage", EnergyStage.class);
    public static final BooleanProperty PUMPING_PROPERTY = BooleanProperty.of("pumping");
    @Environment(EnvType.CLIENT)
    private EngineRenderer engineRenderer;

    public BaseEngineBlock(Identifier identifier) {
        super(identifier, Material.METAL);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            engineRenderer = new EngineRenderer();
        }
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ENERGY_STAGE_PROPERTY);
        builder.add(Properties.FACING);
        builder.add(PUMPING_PROPERTY);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context)
                .with(ENERGY_STAGE_PROPERTY, EnergyStage.BLUE)
                .with(Properties.FACING, context.getPlayerLookDirection().getOpposite())
                .with(PUMPING_PROPERTY, false);
    }

    // Wrenching
    @Override
    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        // Wrench + Sneaking = Disassemble
        if (wrenchMode == WrenchMode.MODE_WRENCH) {
            if (isSneaking) {
                int meta = world.getBlockMeta(x, y, z);
                world.setBlockStateWithNotify(x, y, z, States.AIR.get());
                this.dropStacks(world, x, y, z, meta);
                return true;
            }
        }
        
        // Rotate/Wrench = Rotate
        if (wrenchMode == WrenchMode.MODE_ROTATE || wrenchMode == WrenchMode.MODE_WRENCH) {
            if (world.getBlockState(x, y, z).isOf(this)) {
                if (world.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
                    world.setBlockStateWithNotify(x, y, z, world.getBlockState(x, y, z).cycle(Properties.FACING));
                    engine.checkOrienation = true;
                    return true;
                }
            }
        }

        return false;
    }

    // Block Entity
    @Override
    protected abstract BlockEntity createBlockEntity();

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        if (!world.isRemote) {
            if (world.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
                engine.checkRedstonePower();
            }
        }
    }

    @Override
    public boolean isSolidFace(BlockView blockView, int x, int y, int z, int face) {
        if (blockView.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
            return engine.getFacing().getOpposite().getId() == face;
        }

        return super.isSolidFace(blockView, x, y, z, face);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (world.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
            player.sendMessage("Energy:" + engine.energy + ", Heat: " + engine.heat + ", RS:" + engine.isRedstonePowered + ", Prog:" + engine.progress);
        }

        return super.onUse(world, x, y, z, player);
    }

    // Rendering
    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (world.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
            if (!engine.isBurning()) {
                return;
            }

            float particleX = (float) x + 0.5F;
            float particleY = (float) y + 0.0F + (random.nextFloat() * 6F) / 16F;
            float particleZ = (float) z + 0.5F;
            float particleOffset = 0.52F;
            float randomOffset = random.nextFloat() * 0.6F - 0.3F;

            world.addParticle("reddust", particleX - particleOffset, particleY, particleZ + randomOffset, 0.0D, 0.0D, 0.0D);
            world.addParticle("reddust", particleX + particleOffset, particleY, particleZ + randomOffset, 0.0D, 0.0D, 0.0D);
            world.addParticle("reddust", particleX + randomOffset, particleY, particleZ - particleOffset, 0.0D, 0.0D, 0.0D);
            world.addParticle("reddust", particleX + randomOffset, particleY, particleZ + particleOffset, 0.0D, 0.0D, 0.0D);
        }
    }

    public String getBaseTexturePath() {
        return null;
    }

    public String getTrunkTexturePath(){
        return null;
    }

    public String getChamberTexturePath(){
        return null;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void renderInventory(BlockRenderManager blockRenderManager, int i) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        engineRenderer.render(Minecraft.INSTANCE.textureManager, EnergyStage.BLUE, 0.25F, Direction.UP, this.getBaseTexturePath(), this.getChamberTexturePath(), this.getTrunkTexturePath(), -0.5D, -0.5D, -0.5D);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderWorld(BlockRenderManager blockRenderManager, BlockView blockView, int i, int i1, int i2) {
        return true;
    }

    // Debug
    @Override
    public void debug(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side) {
        if (world.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
            System.out.println(engine);
            player.sendMessage(engine.toString());
        }
    }
}

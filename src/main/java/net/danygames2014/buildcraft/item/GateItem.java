package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.PipePluggableItem;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansions;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateFactory;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.pluggable.GatePluggable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.glasslauncher.mods.alwaysmoreitems.api.SubItemProvider;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.*;

@EnvironmentInterface(itf = CustomItemRenderer.class, value = EnvType.CLIENT)
public class GateItem extends TemplateItem implements CustomTooltipProvider, PipePluggableItem, CustomItemRenderer {
    public static final String NBT_TAG_MAT = "mat";
    public static final String NBT_TAG_LOGIC = "logic";
    public static final String NBT_TAG_EX = "ex";

//    private static ArrayList<ItemStack> allGates;

    private static final Map<Identifier, GateItem> gateItems = new HashMap<>();

    public final GateMaterial gateMaterial;
    public final GateLogic gateLogic;

    public GateItem(GateMaterial gateMaterial, GateLogic gateLogic) {
        super(getIdentifier(gateMaterial, gateLogic));
        this.gateMaterial = gateMaterial;
        this.gateLogic = gateLogic;

        gateItems.put(getIdentifier(gateMaterial, gateLogic), this);

        setHasSubtypes(false);
        setMaxDamage(0);
    }

    public static GateItem getGateItem(GateMaterial gateMaterial, GateLogic gateLogic) {
        Identifier gateIdentifier = getIdentifier(gateMaterial, gateLogic);
        if (!gateItems.containsKey(gateIdentifier)) {
            return null;
        }
        return gateItems.get(gateIdentifier);
    }

    public static GateMaterial getMaterial(ItemStack stack) {
        if (stack.getItem() instanceof GateItem gateItem) {
            return gateItem.gateMaterial;
        }
        return GateMaterial.REDSTONE;
    }

    public static GateLogic getLogic(ItemStack stack) {
        if (stack.getItem() instanceof GateItem gateItem) {
            return gateItem.gateLogic;
        }
        return GateLogic.AND;
    }

    public static Identifier getIdentifier(GateMaterial gateMaterial, GateLogic gateLogic) {
        return Buildcraft.NAMESPACE.id(gateMaterial.name().toLowerCase(Locale.ENGLISH) + "_" + gateLogic.name().toLowerCase(Locale.ENGLISH) + "_gate");
    }

    public static void addGateExpansion(ItemStack stack, GateExpansion expansion) {
        NbtCompound nbt = stack.getStationNbt();

        if (nbt == null) {
            return;
        }

        NbtList expansionList = nbt.getList(NBT_TAG_EX);
        expansionList.add(new NbtString(expansion.getIdentifier().toString()));
        nbt.put(NBT_TAG_EX, expansionList);
    }

    public static boolean hasGateExpansion(ItemStack stack, GateExpansion expansion) {
        NbtCompound nbt = stack.getStationNbt();

        if (nbt == null) {
            return false;
        }

        try {
            NbtList expansionList = nbt.getList(NBT_TAG_EX);

            for (int i = 0; i < expansionList.size(); i++) {
                NbtString ex = (NbtString) expansionList.get(i);

                if (ex.value.equals(expansion.getIdentifier().toString())) {
                    return true;
                }
            }
        } catch (RuntimeException error) {
        }

        return false;
    }

    public static Set<GateExpansion> getInstalledExpansions(ItemStack stack) {
        Set<GateExpansion> expansions = new HashSet<GateExpansion>();
        NbtCompound nbt = stack.getStationNbt();

        if (nbt == null) {
            return expansions;
        }

        try {
            NbtList expansionList = nbt.getList(NBT_TAG_EX);
            for (int i = 0; i < expansionList.size(); i++) {
                NbtString exTag = (NbtString) expansionList.get(i);
                GateExpansion ex = GateExpansions.getExpansion(Identifier.tryParse(exTag.value));
                if (ex != null) {
                    expansions.add(ex);
                }
            }
        } catch (RuntimeException error) {
        }

        return expansions;
    }

    public static ItemStack makeGateItem(GateMaterial material, GateLogic logic) {
        ItemStack stack = new ItemStack(GateItem.getGateItem(material, logic));
        return stack;
    }

    public static ItemStack makeGateItem(Gate gate) {
        ItemStack stack = new ItemStack(GateItem.getGateItem(gate.material, gate.logic));

        for (GateExpansion expansion : gate.expansions.keySet()) {
            addGateExpansion(stack, expansion);
        }

        return stack;
    }

    @SubItemProvider
    public List<ItemStack> getSubItems() {
        List<ItemStack> items = new ArrayList<>();

        items.add(makeGateItem(this.gateMaterial, this.gateLogic));
        for (GateExpansion exp : GateExpansions.getExpansions()) {
            ItemStack stackExpansion = makeGateItem(this.gateMaterial, this.gateLogic);
            addGateExpansion(stackExpansion, exp);
            items.add(stackExpansion);
        }
        
        return items;
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack itemStack, String originalTooltip) {
        TranslationStorage translationStorage = TranslationStorage.getInstance();
        List<String> lines = new ArrayList<>();

        lines.add(originalTooltip);
        Set<GateExpansion> expansions = getInstalledExpansions(itemStack);

        if (!expansions.isEmpty()) {
            lines.add(translationStorage.get("tooltip.buildcraft.gate.expansions"));

            for (GateExpansion expansion : expansions) {
                lines.add(expansion.getDisplayName());
            }
        }

        return lines.toArray(String[]::new);
    }

    @Override
    public PipePluggable createPipePluggable(PipeBlockEntity pipe, Direction side, ItemStack stack) {
        return new GatePluggable(GateFactory.makeGate(pipe, stack, side));
    }

    public void renderSprite(ArsenicItemRenderer arsenicItemRenderer, int x, int y, Atlas.Sprite sprite) {
        arsenicItemRenderer.renderItemQuad(x, y, sprite.getStartU(), sprite.getStartV(), sprite.getEndU(), sprite.getEndV());
    }

    @Override
    public void renderInGui(ArsenicItemRenderer arsenic, ItemRenderer itemRenderer, TextRenderer textRenderer, TextureManager textureManager, ItemStack stack, int x, int y) {
        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();
        Atlas.Sprite logic = GateItem.getLogic(stack).getItemTexture();
        renderSprite(arsenic, x, y, logic);

        Atlas.Sprite material = GateItem.getMaterial(stack).getItemTexture();
        if (material != null) {
            renderSprite(arsenic, x, y, material);
        }

        for (GateExpansion expansion : GateItem.getInstalledExpansions(stack)) {
            Atlas.Sprite overlay = expansion.getOverlayItemSprite();
            if (overlay != null) {
                renderSprite(arsenic, x, y, overlay);
            }
        }
    }

    @Override
    public void renderInHand(SpriteAtlasTexture atlas, Sprite texture, Tessellator tessellator, LivingEntity entity, ItemStack stack) {
        GL11.glTranslatef(-1f, 0f, 0f);
        renderIn3D(tessellator, stack);
    }

    private void renderIn3D(Tessellator tessellator, ItemStack stack) {
        GL11.glPushMatrix();

        renderLayerIn3D(tessellator, GateItem.getLogic(stack).getItemTexture());
        GL11.glScalef(1, 1, 1.05f);
        renderLayerIn3D(tessellator, GateItem.getMaterial(stack).getItemTexture());

        for (GateExpansion expansion : GateItem.getInstalledExpansions(stack)) {
            renderLayerIn3D(tessellator, expansion.getOverlayItemSprite());
        }

        GL11.glPopMatrix();
    }

    private void renderLayerIn3D(Tessellator tessellator, Atlas.Sprite sprite) {
        if (sprite == null) {
            return;
        }
        GL11.glPushMatrix();

        double uv1 = sprite.getStartU();
        double uv2 = sprite.getEndU();
        double uv3 = sprite.getStartV();
        double uv4 = sprite.getEndV();

        renderHeldItem(tessellator, (float) uv2, (float) uv3, (float) uv1, (float) uv4, sprite.getWidth(), sprite.getHeight(), 0.0625F);
        GL11.glPopMatrix();
    }

    public static void renderHeldItem(Tessellator tessellator, float uv2, float uv3, float uv1, float uv4, int width, int height, float zOffset)
    {
        tessellator.startQuads();
        tessellator.normal(0.0F, 0.0F, 1.0F);
        tessellator.vertex(0.0D, 0.0D, 0.0D, uv2, uv4);
        tessellator.vertex(1.0D, 0.0D, 0.0D, uv1, uv4);
        tessellator.vertex(1.0D, 1.0D, 0.0D, uv1, uv3);
        tessellator.vertex(0.0D, 1.0D, 0.0D, uv2, uv3);
        tessellator.draw();
        tessellator.startQuads();
        tessellator.normal(0.0F, 0.0F, -1.0F);
        tessellator.vertex(0.0D, 1.0D, (0.0F - zOffset), uv2, uv3);
        tessellator.vertex(1.0D, 1.0D, (0.0F - zOffset), uv1, uv3);
        tessellator.vertex(1.0D, 0.0D, (0.0F - zOffset), uv1, uv4);
        tessellator.vertex(0.0D, 0.0D, (0.0F - zOffset), uv2, uv4);
        tessellator.draw();
        float var8 = 0.5F * (uv2 - uv1) / (float)width;
        float var9 = 0.5F * (uv4 - uv3) / (float)height;
        tessellator.startQuads();
        tessellator.normal(-1.0F, 0.0F, 0.0F);
        int var10;
        float var11;
        float var12;

        for (var10 = 0; var10 < width; ++var10)
        {
            var11 = (float)var10 / (float)width;
            var12 = uv2 + (uv1 - uv2) * var11 - var8;
            tessellator.vertex(var11, 0.0D, (0.0F - zOffset), var12, uv4);
            tessellator.vertex(var11, 0.0D, 0.0D, var12, uv4);
            tessellator.vertex(var11, 1.0D, 0.0D, var12, uv3);
            tessellator.vertex(var11, 1.0D, (0.0F - zOffset), var12, uv3);
        }

        tessellator.draw();
        tessellator.startQuads();
        tessellator.normal(1.0F, 0.0F, 0.0F);
        float var13;

        for (var10 = 0; var10 < width; ++var10)
        {
            var11 = (float)var10 / (float)width;
            var12 = uv2 + (uv1 - uv2) * var11 - var8;
            var13 = var11 + 1.0F / (float)width;
            tessellator.vertex(var13, 1.0D, (0.0F - zOffset), var12, uv3);
            tessellator.vertex(var13, 1.0D, 0.0D, var12, uv3);
            tessellator.vertex(var13, 0.0D, 0.0D, var12, uv4);
            tessellator.vertex(var13, 0.0D, (0.0F - zOffset), var12, uv4);
        }

        tessellator.draw();
        tessellator.startQuads();
        tessellator.normal(0.0F, 1.0F, 0.0F);

        for (var10 = 0; var10 < height; ++var10)
        {
            var11 = (float)var10 / (float)height;
            var12 = uv4 + (uv3 - uv4) * var11 - var9;
            var13 = var11 + 1.0F / (float)height;
            tessellator.vertex(0.0D, var13, 0.0D, uv2, var12);
            tessellator.vertex(1.0D, var13, 0.0D, uv1, var12);
            tessellator.vertex(1.0D, var13, (0.0F - zOffset), uv1, var12);
            tessellator.vertex(0.0D, var13, (0.0F - zOffset), uv2, var12);
        }

        tessellator.draw();
        tessellator.startQuads();
        tessellator.normal(0.0F, -1.0F, 0.0F);

        for (var10 = 0; var10 < height; ++var10)
        {
            var11 = (float)var10 / (float)height;
            var12 = uv4 + (uv3 - uv4) * var11 - var9;
            tessellator.vertex(1.0D, var11, 0.0D, uv1, var12);
            tessellator.vertex(0.0D, var11, 0.0D, uv2, var12);
            tessellator.vertex(0.0D, var11, (0.0F - zOffset), uv2, var12);
            tessellator.vertex(1.0D, var11, (0.0F - zOffset), uv1, var12);
        }

        tessellator.draw();
    }

    @Override
    public boolean renderOnGround(ArsenicItemRenderer arsenicItemRenderer, ItemRenderer itemRenderer, Tessellator tessellator, ItemEntity itemEntity, float x, float y, float z, float delta, ItemStack stack, float yOffset, float angle, byte renderedAmount, SpriteAtlasTexture atlas) {

        Atlas.Sprite logic = GateItem.getLogic(stack).getItemTexture();
        addQuad(tessellator, logic);

        Atlas.Sprite material = GateItem.getMaterial(stack).getItemTexture();
        if (material != null) {
            addQuad(tessellator, material);
        }

        for (GateExpansion expansion : GateItem.getInstalledExpansions(stack)) {
            Atlas.Sprite overlay = expansion.getOverlayItemSprite();
            if (overlay != null) {
                addQuad(tessellator, overlay);
            }
        }
        return true;
    }

    private void addQuad(Tessellator tessellator, Atlas.Sprite sprite) {
        tessellator.vertex((0.0F - 0.5F), (0.0F - 0.25F), 0.0F, sprite.getStartU(), sprite.getEndV());
        tessellator.vertex((1.0F - 0.5F), (0.0F - 0.25F), 0.0F, sprite.getEndU(), sprite.getEndV());
        tessellator.vertex((1.0F - 0.5F), (1.0F - 0.25F), 0.0F, sprite.getEndU(), sprite.getStartV());
        tessellator.vertex((0.0F - 0.5F), (1.0F - 0.25F), 0.0F, sprite.getStartU(), sprite.getStartV());
    }
}

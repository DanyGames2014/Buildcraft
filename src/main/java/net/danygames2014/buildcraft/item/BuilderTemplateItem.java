package net.danygames2014.buildcraft.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Formatting;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BuilderTemplateItem extends TemplateItem implements CustomTooltipProvider {
    public BuilderTemplateItem(Identifier identifier) {
        super(identifier);
        this.setMaxCount(1);
        this.setMaxDamage(0);
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        ArrayList<String> lines = new ArrayList<>();
        NbtCompound nbt = stack.getStationNbt();
        
        lines.add(originalTooltip);
        if (nbt.contains("name")) {
            lines.add(Formatting.GRAY + "#" + stack.getDamage() + " - " + nbt.getString("name"));
            lines.add(Formatting.GRAY + "by " + nbt.getString("author"));
            lines.add(Formatting.GRAY + "Size: " + nbt.getInt("sizeX") + "x" + nbt.getInt("sizeY") + "x" + nbt.getInt("sizeZ"));
        } else {
            lines.add(Formatting.GRAY + "Blank");
        }
        
        return lines.toArray(new String[0]);
    }
}

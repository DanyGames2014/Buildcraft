package net.danygames2014.buildcraft.item;

import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class BuilderTemplateItem extends TemplateItem implements CustomTooltipProvider {
    public BuilderTemplateItem(Identifier identifier) {
        super(identifier);
        this.setMaxCount(1);
        this.setMaxDamage(0);
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        return new String[] {
                originalTooltip,
                "#" + stack.getDamage()
        };
    }
}

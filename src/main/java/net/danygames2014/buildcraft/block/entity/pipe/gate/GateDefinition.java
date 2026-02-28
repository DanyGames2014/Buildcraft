package net.danygames2014.buildcraft.block.entity.pipe.gate;

import net.minecraft.client.resource.language.TranslationStorage;

public class GateDefinition {
    public static String getLocalizedName(GateMaterial material, GateLogic logic) {
        TranslationStorage translationStorage = TranslationStorage.getInstance();
        if (material == GateMaterial.REDSTONE) {
            return translationStorage.get("gate.buildcraft.basic.name");
        } else {
            return String.format(translationStorage.get("gate.buildcraft.name"), translationStorage.get("gate.buildcraft.material." + material.getTag()),
                    translationStorage.get("gate.buildcraft.logic." + logic.getTag()));
        }
    }
}

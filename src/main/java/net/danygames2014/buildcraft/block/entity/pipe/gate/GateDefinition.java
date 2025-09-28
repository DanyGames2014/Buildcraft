package net.danygames2014.buildcraft.block.entity.pipe.gate;

import net.minecraft.client.resource.language.TranslationStorage;

public class GateDefinition {
    public static String getLocalizedName(GateMaterial material, GateLogic logic) {
        TranslationStorage translationStorage = TranslationStorage.getInstance();
        if (material == GateMaterial.REDSTONE) {
            return translationStorage.get("gate.name.basic");
        } else {
            return String.format(translationStorage.get("gate.name"), translationStorage.get("gate.material." + material.getTag()),
                    translationStorage.get("gate.logic." + logic.getTag()));
        }
    }
}

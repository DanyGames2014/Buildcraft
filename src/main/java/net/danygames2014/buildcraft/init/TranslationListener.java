package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.item.GateItem;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.event.resource.language.TranslationInvalidationEvent;

import java.util.Locale;
import java.util.Properties;

public class TranslationListener {
    @EventListener
    public void registerGateTranslations(TranslationInvalidationEvent event) {
        Properties translations = TranslationStorage.getInstance().translations;

        String basicGateTranslatedName = translations.get("gate.buildcraft.basic.name").toString();
        String gateTranslatedName = translations.get("gate.buildcraft.name").toString();

        if (gateTranslatedName == null || basicGateTranslatedName == null) {
            return;
        }

        for (GateItem gateItem : GateItem.gateItems.values()) {
            translations.put(
                    gateItem.getTranslationKey() + ".name",
                    String.format(
                            gateItem.gateMaterial == GateMaterial.REDSTONE ? basicGateTranslatedName : gateTranslatedName,
                            translations.get("gate.buildcraft.material." + gateItem.gateMaterial.name().toLowerCase(Locale.ENGLISH)),
                            translations.get("gate.buildcraft.logic." + gateItem.gateLogic.name().toLowerCase(Locale.ENGLISH))
                    )
            );
        }
    }
}

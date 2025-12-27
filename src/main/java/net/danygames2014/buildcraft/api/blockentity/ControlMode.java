package net.danygames2014.buildcraft.api.blockentity;

import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.util.Identifier;

public class ControlMode {
    public static ControlMode ON;
    public static ControlMode OFF;
    
    public final Identifier identifier;
    public final Identifier texture;
    public Atlas.Sprite sprite;
    
    private final String nameTranslationKey;
    private final String descriptionTranslationKey;

    public ControlMode(Identifier identifier, Identifier texture) {
        this.identifier = identifier;
        this.texture = texture;
        this.nameTranslationKey = "controlmode." + identifier.namespace + "." + identifier.path + ".name";
        this.descriptionTranslationKey = "controlmode." + identifier.namespace + "." + identifier.path + ".description";
    }
    
    public String getTranslatedName() {
        return TranslationStorage.getInstance().get(nameTranslationKey);
    }
    
    public String getTranslatedDescription() {
        return TranslationStorage.getInstance().get(descriptionTranslationKey);
    }
}

package net.danygames2014.buildcraft.api.blockentity;

import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.util.Identifier;

/**
 * Defines a Control Mode which a gate can use on a {@link Controllable} Block Entity
 */
public class ControlMode {
    public static ControlMode ON;
    public static ControlMode OFF;

    public final Identifier identifier;
    public final Identifier texture;
    public Atlas.Sprite sprite;

    private final String nameTranslationKey;
    private final String descriptionTranslationKey;

    /**
     * @param identifier The identifier of this control mode
     * @param texture    The texture location of the sprite that will be used in the logic gate interface
     */
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

package net.danygames2014.buildcraft.api.core;

/**
 * Used for more fine-grained control of whether or not a machine connects
 * to the provider here.
 */
public interface BlockEntityAreaProvider extends AreaProvider {
    boolean isValidFromLocation(int x, int y, int z);
}

package net.danygames2014.buildcraft.api.core;

public interface SynchedBlockEntity {
    /**
     * called by the PacketHandler for each state contained in a StatePacket
     *
     * @param stateId
     * @return an object that should be refreshed from the state
     */
    Serializable getStateInstance(byte stateId);

    /**
     * Called after a state has been updated
     *
     * @param stateId
     */
    void afterStateUpdated(byte stateId);
}

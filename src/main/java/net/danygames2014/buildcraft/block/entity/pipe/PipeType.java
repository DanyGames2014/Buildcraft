package net.danygames2014.buildcraft.block.entity.pipe;

public enum PipeType {
    ITEM("item"),
    FLUID("fluid"),
    ENERGY("energy"),
    STRUCTURE("structure");

    public final String name;
    
    PipeType(String name) {
        this.name = name;
    }
}

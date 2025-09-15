package net.danygames2014.buildcraft.api.energy;

public class EngineCoolant {
    private final float degreesCooledPerMb;

    public EngineCoolant(float degreesCooledPerMb) {
        this.degreesCooledPerMb = degreesCooledPerMb;
    }

    public float getDegreesCooledPerMb() {
        return degreesCooledPerMb;
    }
}

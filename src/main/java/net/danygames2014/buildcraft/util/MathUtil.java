package net.danygames2014.buildcraft.util;

public class MathUtil {
    @SuppressWarnings("ManualMinMaxCalculation")
    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        
        if (value > max) {
            return max;
        }
        
        return value;
    }
}

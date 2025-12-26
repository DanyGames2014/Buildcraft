package net.danygames2014.buildcraft.util;

public class ColorUtil {
    public static final String[] names = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "light_gray", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
    public static final int[] colors = new int[]{1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 2651799, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};

    public static int getColor(int index){
        return colors[Math.max(0, Math.min(15, index))];
    }

    public static String getName(int index){
        return names[Math.max(0, Math.min(15, index))];
    }
}

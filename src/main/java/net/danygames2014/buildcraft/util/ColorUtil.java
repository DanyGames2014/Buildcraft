package net.danygames2014.buildcraft.util;

public class ColorUtil {
    public static final String[] names = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "light_gray", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
    public static final int[] colors = new int[]{0x1E1B1B, 0xB3312C, 0x3B511A, 0x51301A, 0x253192, 0x7B2FBE, 0x287697, 0x999999, 0x434343, 0xD88198, 0x41CD34, 0xDECF2A, 0x6689D3, 0xC354CD, 0xEB8844, 0xF0F0F0};

    public static int getColor(int index){
        return colors[Math.max(0, Math.min(15, index))];
    }

    public static String getName(int index){
        return names[Math.max(0, Math.min(15, index))];
    }
}

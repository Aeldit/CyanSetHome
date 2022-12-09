package fr.aeldit.cyansh.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class SethomeMidnightConfig extends MidnightConfig
{

    @Comment
    public static Comment allowOptions;
    @Entry
    public static boolean allowSethome = true;
    @Entry
    public static boolean allowHomeOf = true;

    @Comment
    public static Comment intOptions;
    @Entry(min = 1, max = 64)
    public static int maxHomes = 20;
    @Entry(min = 0, max = 4)
    public static int minOpLevelExeKgi = 4;

    // Booleans
    public static void setAllowSethome(boolean value)
    {
        allowSethome = value;
        write("sethome");
    }

    public static void setAllowHomeOf(boolean value)
    {
        allowHomeOf = value;
        write("sethome");
    }

    // Ints
    public static void setMaxHomes(int value)
    {
        maxHomes = value;
        write("sethome");
    }

    public static void setMinOpLevelExeKgi(int value)
    {
        minOpLevelExeKgi = value;
        write("sethome");
    }

}

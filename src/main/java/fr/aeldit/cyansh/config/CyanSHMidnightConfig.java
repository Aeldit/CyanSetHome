package fr.aeldit.cyansh.config;

import eu.midnightdust.lib.config.MidnightConfig;
import org.jetbrains.annotations.NotNull;

public class CyanSHMidnightConfig extends MidnightConfig {

    @Comment
    public static Comment allowOptions;
    @Entry
    public static boolean allowHomes = true;
    public static boolean allowHomeOf = true;
    public static boolean useTranslations = true;
    public static boolean msgToActionBar = true;
    public static boolean errorToActionBar = true;

    @Comment
    public static Comment intOptions;
    @Entry(min = 1, max = 128)
    public static int maxHomes = 2;
    @Entry(isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeHome = 0;
    @Entry(isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeHomeOf = 4;

    public static void setBoolOption(@NotNull String optionName, boolean value) {
        switch (optionName) {
            case "allowHome" -> allowHomes = value;
            case "allowHomeOf" -> allowHomeOf = value;
            case "useTranslations" -> useTranslations = value;
            case "msgToActionBar" -> msgToActionBar = value;
            case "errorToActionBar" -> errorToActionBar = value;
        }
        write("cyansh");
    }

    public static void setIntOption(@NotNull String optionName, int value) {
        switch (optionName) {
            case "maxHomes" -> maxHomes = value;
            case "minOpLevelExeHome" -> minOpLevelExeHome = value;
            case "minOpLevelExeHomeOf" -> minOpLevelExeHomeOf = value;
        }
        write("cyansh");
    }
}

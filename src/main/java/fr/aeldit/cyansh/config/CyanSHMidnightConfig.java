package fr.aeldit.cyansh.config;

import eu.midnightdust.lib.config.MidnightConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CyanSHMidnightConfig extends MidnightConfig
{
    public static final List<String> commandsList = new ArrayList<>();
    public static Map<String, Object> boolOptionsMap = new HashMap<>();
    public static Map<String, Object> integerOptionsMap = new HashMap<>();
    public static Map<String, Object> allOptionsMap = new HashMap<>();
    @Comment
    public static Comment allowOptions;
    @Entry
    public static boolean allowHomes = true;
    @Entry
    public static boolean allowHomeOf = true;
    @Entry
    public static boolean useTranslations = true;
    @Entry
    public static boolean msgToActionBar = true;
    @Entry
    public static boolean errorToActionBar = true;

    @Comment
    public static Comment intOptions;
    @Entry(min = 1, max = 128)
    public static int maxHomes = 2;
    @Entry(isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeHomes = 0;
    @Entry(isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeHomeOf = 4;
    @Entry(isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeEditConfig = 4;

    public static Map<String, Object> generateBoolOptionsMap()
    {
        boolOptionsMap.put("allowHomes", allowHomes);
        boolOptionsMap.put("allowHomeOf", allowHomeOf);

        boolOptionsMap.put("useTranslations", useTranslations);
        boolOptionsMap.put("msgToActionBar", msgToActionBar);
        boolOptionsMap.put("errorToActionBar", errorToActionBar);

        return boolOptionsMap;
    }

    public static Map<String, Object> generateIntegerOptionsMap()
    {
        integerOptionsMap.put("maxHomes", maxHomes);

        integerOptionsMap.put("minOpLevelExeHomes", minOpLevelExeHomes);
        integerOptionsMap.put("minOpLevelExeHomeOf", minOpLevelExeHomeOf);
        integerOptionsMap.put("minOpLevelExeEditConfig", minOpLevelExeEditConfig);

        return integerOptionsMap;
    }

    public static Map<String, Object> generateAllOptionsMap()
    {
        allOptionsMap.putAll(generateBoolOptionsMap());
        allOptionsMap.putAll(generateIntegerOptionsMap());
        return allOptionsMap;
    }

    // For the ArgumentSuggestions
    public static List<String> generateCommandsList()
    {
        commandsList.add("sethome");
        commandsList.add("home");
        commandsList.add("removehome");
        commandsList.add("gethomes");

        return commandsList;
    }

    public static void setBoolOption(@NotNull String optionName, boolean value)
    {
        switch (optionName)
        {
            case "allowHomes" -> allowHomes = value;
            case "allowHomeOf" -> allowHomeOf = value;
            case "useTranslations" -> useTranslations = value;
            case "msgToActionBar" -> msgToActionBar = value;
            case "errorToActionBar" -> errorToActionBar = value;
        }
        write("cyansh");
    }

    public static void setIntOption(@NotNull String optionName, int value)
    {
        switch (optionName)
        {
            case "maxHomes" -> maxHomes = value;
            case "minOpLevelExeHomes" -> minOpLevelExeHomes = value;
            case "minOpLevelExeHomeOf" -> minOpLevelExeHomeOf = value;
            case "minOpLevelExeEditConfig" -> minOpLevelExeEditConfig = value;
        }
        write("cyansh");
    }
}
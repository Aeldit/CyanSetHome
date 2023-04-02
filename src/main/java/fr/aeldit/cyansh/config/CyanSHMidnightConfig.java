/*
 * Copyright (c) 2023  -  Made by Aeldit
 *
 *              GNU LESSER GENERAL PUBLIC LICENSE
 *                  Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 *
 *
 * This version of the GNU Lesser General Public License incorporates
 * the terms and conditions of version 3 of the GNU General Public
 * License, supplemented by the additional permissions listed in the LICENSE.txt file
 * in the repo of this mod (https://github.com/Aeldit/CyanSetHome)
 */

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
    public static boolean allowHomesOf = true;
    @Entry
    public static boolean allowOPHomeOf = true;
    @Entry
    public static boolean useTranslations = true;
    @Entry
    public static boolean msgToActionBar = true;
    @Entry
    public static boolean errorToActionBar = true;

    @Comment
    public static Comment intOptions;
    @Entry(min = 1, max = 128)
    public static int maxHomes = 10;
    @Entry(isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeHomes = 0;
    @Entry(isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeHomesOf = 0;
    @Entry(isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeEditConfig = 4;
    @Entry(isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeOPHomesOf = 4;

    public static void generateBoolOptionsMap()
    {
        boolOptionsMap.put("allowHomes", allowHomes);
        boolOptionsMap.put("allowHomesOf", allowHomesOf);
        boolOptionsMap.put("allowOPHomesOf", allowOPHomeOf);

        boolOptionsMap.put("useTranslations", useTranslations);
        boolOptionsMap.put("msgToActionBar", msgToActionBar);
        boolOptionsMap.put("errorToActionBar", errorToActionBar);
    }

    public static void generateIntegerOptionsMap()
    {
        integerOptionsMap.put("maxHomes", maxHomes);

        integerOptionsMap.put("minOpLevelExeHomes", minOpLevelExeHomes);
        integerOptionsMap.put("minOpLevelExeHomesOf", minOpLevelExeHomesOf);
        integerOptionsMap.put("minOpLevelExeEditConfig", minOpLevelExeEditConfig);
        integerOptionsMap.put("minOpLevelExeOPHomesOf", minOpLevelExeOPHomesOf);
    }

    public static Map<String, Object> getAllOptionsMap()
    {
        allOptionsMap.putAll(getBoolOptionsMap());
        allOptionsMap.putAll(getIntegerOptionsMap());
        return allOptionsMap;
    }

    // For the ArgumentSuggestions
    public static void generateCommandsList()
    {
        commandsList.add("sethome");
        commandsList.add("home");
        commandsList.add("removehome");
        commandsList.add("gethomes");

        commandsList.add("homeof");
        commandsList.add("removehomeof");
        commandsList.add("gethomesof");
    }

    public static void setBoolOption(@NotNull String optionName, boolean value)
    {
        switch (optionName)
        {
            case "allowHomes" -> allowHomes = value;
            case "allowHomesOf" -> allowHomesOf = value;
            case "allowOPHomesOf" -> allowOPHomeOf = value;
            case "useTranslations" -> useTranslations = value;
            case "msgToActionBar" -> msgToActionBar = value;
            case "errorToActionBar" -> errorToActionBar = value;
        }
        write("cyansh");
        generateBoolOptionsMap();
    }

    public static void setIntOption(@NotNull String optionName, int value)
    {
        switch (optionName)
        {
            case "maxHomes" -> maxHomes = value;
            case "minOpLevelExeHomes" -> minOpLevelExeHomes = value;
            case "minOpLevelExeHomesOf" -> minOpLevelExeHomesOf = value;
            case "minOpLevelExeOPHomesOf" -> minOpLevelExeOPHomesOf = value;
            case "minOpLevelExeEditConfig" -> minOpLevelExeEditConfig = value;
        }
        write("cyansh");
        generateIntegerOptionsMap();
    }

    public static List<String> getCommandsList()
    {
        return commandsList;
    }

    public static Map<String, Object> getBoolOptionsMap()
    {
        if (boolOptionsMap.isEmpty())
        {
            generateBoolOptionsMap();
        }
        return boolOptionsMap;
    }

    public static Map<String, Object> getIntegerOptionsMap()
    {
        if (integerOptionsMap.isEmpty())
        {
            generateIntegerOptionsMap();
        }
        return integerOptionsMap;
    }
}

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

import java.util.HashMap;
import java.util.Map;

import static fr.aeldit.cyansh.util.Utils.*;

public class CyanSHMidnightConfig extends MidnightConfig
{
    public static Map<String, Object> allOptionsMap = new HashMap<>();

    @Entry(category = "allows")
    public static boolean allowHomes = true;
    @Entry(category = "allows")
    public static boolean allowHomesOf = true;
    @Entry(category = "allows")
    public static boolean allowByPass = false;
    @Entry(category = "allows")
    public static boolean useCustomTranslations = false;
    @Entry(category = "allows")
    public static boolean msgToActionBar = true;

    @Entry(category = "integers", min = 1, max = 128)
    public static int maxHomes = 10;
    @Entry(category = "integers", isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeHomes = 0;
    @Entry(category = "integers", isSlider = true, min = 0, max = 4)
    public static int minOpLevelExeEditConfig = 4;

    public static void generateAllOptionsMap()
    {
        allOptionsMap.put("allowHomes", allowHomes);
        allOptionsMap.put("allowHomesOf", allowHomesOf);
        allOptionsMap.put("allowByPass", allowByPass);

        allOptionsMap.put("useCustomTranslations", useCustomTranslations);
        allOptionsMap.put("msgToActionBar", msgToActionBar);

        allOptionsMap.put("maxHomes", maxHomes);

        allOptionsMap.put("minOpLevelExeHomes", minOpLevelExeHomes);
        allOptionsMap.put("minOpLevelExeEditConfig", minOpLevelExeEditConfig);
    }

    public static void setBoolOption(@NotNull String optionName, boolean value)
    {
        switch (optionName)
        {
            case "allowHomes" -> allowHomes = value;
            case "allowHomesOf" -> allowHomesOf = value;
            case "allowByPass" -> allowByPass = value;
            case "useCustomTranslations" -> useCustomTranslations = value;
            case "msgToActionBar" -> msgToActionBar = value;
        }

        allOptionsMap.put(optionName, value);
        write(MODID);

        if (useCustomTranslations)
        {
            CyanSHLanguageUtils.loadLanguage(getDefaultTranslations());
        }
        if (optionName.equals("msgToActionBar"))
        {
            CyanLibUtils.setMsgToActionBar(value);
        }
        else if (optionName.equals("useCustomTranslations"))
        {
            CyanLibUtils.setUseCustomTranslations(value);

            if (useCustomTranslations)
            {
                CyanSHLanguageUtils.loadLanguage(getDefaultTranslations());
            }
        }
    }

    public static void setIntOption(@NotNull String optionName, int value)
    {
        switch (optionName)
        {
            case "maxHomes" -> maxHomes = value;
            case "minOpLevelExeHomes" -> minOpLevelExeHomes = value;
            case "minOpLevelExeEditConfig" -> minOpLevelExeEditConfig = value;
        }

        allOptionsMap.put(optionName, value);
        write(MODID);
    }

    public static Map<String, Object> getAllOptionsMap()
    {
        return allOptionsMap;
    }
}

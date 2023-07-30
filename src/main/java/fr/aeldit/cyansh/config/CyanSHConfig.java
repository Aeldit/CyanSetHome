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

import fr.aeldit.cyanlib.lib.config.CyanLibOptionsStorage;
import fr.aeldit.cyanlib.lib.utils.RULES;

import static fr.aeldit.cyansh.util.Utils.CYANSH_OPTIONS_STORAGE;

public class CyanSHConfig
{
    public static final CyanLibOptionsStorage.BooleanOption ALLOW_HOMES = CYANSH_OPTIONS_STORAGE.new BooleanOption("allowHomes", true);
    public static final CyanLibOptionsStorage.BooleanOption ALLOW_BYPASS = CYANSH_OPTIONS_STORAGE.new BooleanOption("allowByPass", false);

    public static final CyanLibOptionsStorage.IntegerOption MIN_OP_LVL_HOMES = CYANSH_OPTIONS_STORAGE.new IntegerOption("minOpLvlHomes", 4, RULES.OP_LEVELS);
    public static final CyanLibOptionsStorage.IntegerOption MAX_HOMES = CYANSH_OPTIONS_STORAGE.new IntegerOption("maxHomes", 20, RULES.POSITIVE_VALUE);

    // CyanLib Required Options
    public static final CyanLibOptionsStorage.BooleanOption USE_CUSTOM_TRANSLATIONS = CYANSH_OPTIONS_STORAGE.new BooleanOption("useCustomTranslations", false, RULES.LOAD_CUSTOM_TRANSLATIONS);
}

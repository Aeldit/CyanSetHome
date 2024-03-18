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

package fr.aeldit.cyansh;

import fr.aeldit.cyanlib.lib.CyanLib;
import fr.aeldit.cyanlib.lib.CyanLibLanguageUtils;
import fr.aeldit.cyanlib.lib.config.CyanLibOptionsStorage;
import fr.aeldit.cyansh.config.CyanSHConfig;
import fr.aeldit.cyansh.homes.Homes;
import fr.aeldit.cyansh.homes.Trusts;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static fr.aeldit.cyansh.homes.Homes.HOMES_PATH;

public class CyanSHCore
{
    public static final String CYANSH_MODID = "cyansh";
    public static final Logger CYANSH_LOGGER = LoggerFactory.getLogger(CYANSH_MODID);
    public static Path MOD_PATH = FabricLoader.getInstance().getConfigDir().resolve(CYANSH_MODID);

    public static Homes HomesObj = new Homes();
    public static Trusts TrustsObj = new Trusts();

    public static CyanLibOptionsStorage CYANSH_OPTIONS_STORAGE = new CyanLibOptionsStorage(
            CYANSH_MODID,
            new CyanSHConfig()
    );
    public static CyanLibLanguageUtils CYANSH_LANGUAGE_UTILS = new CyanLibLanguageUtils(CYANSH_MODID);
    public static CyanLib CYANSH_LIB_UTILS = new CyanLib(CYANSH_MODID, CYANSH_OPTIONS_STORAGE, CYANSH_LANGUAGE_UTILS);

    public static void checkOrCreateHomesDir()
    {
        if (!Files.exists(MOD_PATH))
        {
            try
            {
                Files.createDirectory(MOD_PATH);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        if (!Files.exists(HOMES_PATH))
        {
            try
            {
                Files.createDirectory(HOMES_PATH);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void removeEmptyModDir()
    {
        if (Files.exists(HOMES_PATH))
        {
            File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();

            if (listOfFiles != null && listOfFiles.length == 0)
            {
                try
                {
                    Files.delete(HOMES_PATH);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        if (Files.exists(MOD_PATH))
        {
            File[] listOfFiles = new File(MOD_PATH.toUri()).listFiles();

            if (listOfFiles != null && listOfFiles.length == 0)
            {
                try
                {
                    Files.delete(MOD_PATH);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

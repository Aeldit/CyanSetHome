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

package fr.aeldit.cyansh.util;

import fr.aeldit.cyanlib.lib.CyanLib;
import fr.aeldit.cyanlib.lib.CyanLibLanguageUtils;
import fr.aeldit.cyanlib.lib.commands.CyanLibConfigCommands;
import fr.aeldit.cyanlib.lib.config.CyanLibOptionsStorage;
import fr.aeldit.cyansh.config.CyanSHConfig;
import fr.aeldit.cyansh.homes.Homes;
import fr.aeldit.cyansh.homes.Trusts;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static fr.aeldit.cyansh.homes.Homes.HOMES_PATH;

public class Utils
{
    public static final String CYANSH_MODID = "cyansh";
    public static final Logger CYANSH_LOGGER = LoggerFactory.getLogger(CYANSH_MODID);
    public static Path MOD_PATH = FabricLoader.getInstance().getConfigDir().resolve(CYANSH_MODID);
    private static final Map<String, String> CYANSH_DEFAULT_TRANSLATIONS = new HashMap<>();

    public static Homes HomesObj = new Homes();
    public static Trusts TrustsObj = new Trusts();

    public static CyanLibOptionsStorage CYANSH_OPTIONS_STORAGE = new CyanLibOptionsStorage(CYANSH_MODID, CyanSHConfig.class);
    public static CyanLibLanguageUtils CYANSH_LANGUAGE_UTILS = new CyanLibLanguageUtils(CYANSH_MODID, CYANSH_OPTIONS_STORAGE, getDefaultTranslations());
    public static CyanLib CYANSH_LIB_UTILS = new CyanLib(CYANSH_MODID, CYANSH_OPTIONS_STORAGE, CYANSH_LANGUAGE_UTILS);
    public static CyanLibConfigCommands CYANSH_CONFIG_COMMANDS = new CyanLibConfigCommands(CYANSH_MODID, CYANSH_LIB_UTILS);

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

            if (listOfFiles.length == 0)
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

            if (listOfFiles.length == 0)
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

    public static @NotNull Map<String, String> getDefaultTranslations()
    {
        if (CYANSH_DEFAULT_TRANSLATIONS.isEmpty())
        {
            CYANSH_DEFAULT_TRANSLATIONS.put("error.playerOnlyCmd", "This command can only be executed by a player");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.notOp", "§cYou don't have the required permission to do that");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.notOpOrTrusted", "§cThis player doesn't trust you or you don't have the required permission");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.disabled.homes", "§cThe home commands are disabled");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.homeAlreadyExists", "§cThis home already exists");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.homeNotFound", "§cThis home doesn't exist (check the spelling)");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.maxHomesReached", "§cYou reached the maximum number of homes §6(%s§6)");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.playerNotOnline", "§cThis player is not online");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.playerNotTrusted", "§cYou don't trust this player");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.playerNotTrusting", "§cThis player doesn't trust you");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.playerAlreadyTrusted", "§cYou already trust this player");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.selfTrust", "§cYou can't trust/untrust yourself");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.noHomes", "§cYou don't have any home");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.noHomesOf", "§cThis player doesn't have any home");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.useSelfHomes", "§cPlease use the normal commands to use your homes");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.optionNotFound", "§cThis option does not exist");
            CYANSH_DEFAULT_TRANSLATIONS.put("error.bypassDisabled", "§cThe ByPass option is disabled or you are not OP level 4");

            CYANSH_DEFAULT_TRANSLATIONS.put("setHome", "§3The home %s §3have been created");
            CYANSH_DEFAULT_TRANSLATIONS.put("setHomeOf", "§3The home %s §3have been created for the player %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("removeHome", "§3The home %s §3have been removed");
            CYANSH_DEFAULT_TRANSLATIONS.put("removeHomeOf", "§3The home %s §3have been removed from %s§3's homes");
            CYANSH_DEFAULT_TRANSLATIONS.put("removeAllHomes", "§3All your homes have been removed");
            CYANSH_DEFAULT_TRANSLATIONS.put("removeAllHomesOf", "§3All %s§3's homes have been removed");
            CYANSH_DEFAULT_TRANSLATIONS.put("renameHome", "§3The home %s §3have been renamed to %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("renameHomeOf", "§3The home %s §3have been renamed to %s §3for the player %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("goToHome", "§3You have been teleported to the home %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("getTrustingPlayers", "§3Players that trust you : %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("getTrustedPlayers", "§3Players that you trust : %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("noTrustingPlayer", "§3No player trusts you");
            CYANSH_DEFAULT_TRANSLATIONS.put("noTrustedPlayer", "§3You don't trust any player");
            CYANSH_DEFAULT_TRANSLATIONS.put("playerTrusted", "§3You now trust %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("playerUnTrusted", "§3You no longer trust %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("translationsReloaded", "§3Translations have been reloaded");
            CYANSH_DEFAULT_TRANSLATIONS.put("currentValue", "§7Current value : %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("setValue", "§7Set value to : %s  %s  %s  %s  %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("getHome", "%s §3(%s§3, created on the %s§3)");

            CYANSH_DEFAULT_TRANSLATIONS.put("set.allowHomes", "§3Toggled §dhome §3commands %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("set.allowByPass", "§3Toggled ByPass %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("set.minOpLvlHomes", "§3The OP level required to use the §dhome §3commands is now %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("set.minOpLvlBypass", "§3The OP level required to use the Bypass is now %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("set.maxHomes", "§3The maximum number of homes per player is now %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("set.useCustomTranslations", "§3Toggled translations %s");

            CYANSH_DEFAULT_TRANSLATIONS.put("dashSeparation", "§6------------------------------------");
            CYANSH_DEFAULT_TRANSLATIONS.put("listHomes", "§6CyanSetHome - YOUR HOMES\n");
            CYANSH_DEFAULT_TRANSLATIONS.put("listHomesOf", "§6CyanSetHome - HOMES OF %s\n");

            CYANSH_DEFAULT_TRANSLATIONS.put("desc.allowHomes", "§3The §dallowHomes §3option defines whether the home commands are enabled or not");
            CYANSH_DEFAULT_TRANSLATIONS.put("desc.allowByPass", "§3The §dallowByPass §3option defines whether admins with the correct OP level can bypass permissions like trust between players");
            CYANSH_DEFAULT_TRANSLATIONS.put("desc.minOpLvlHomes", "§3The §dminOpLevelExeHomes §3option defines the OP level required to run the home commands");
            CYANSH_DEFAULT_TRANSLATIONS.put("desc.minOpLvlBypass", "§3The §dminOpLvlBypass §3option defines the OP level required to use the bypass for the §dhomeOf §3commands");
            CYANSH_DEFAULT_TRANSLATIONS.put("desc.maxHomes", "§3The §dmaxHomes §3option defines the maximum number of homes a player can have");
            CYANSH_DEFAULT_TRANSLATIONS.put("desc.useCustomTranslations", "§3The §duseCustomTranslations §3option defines whether the translation will be used or not");

            CYANSH_DEFAULT_TRANSLATIONS.put("getCfg.header", "§6CyanSetHome - OPTIONS\n");
            CYANSH_DEFAULT_TRANSLATIONS.put("getCfg.allowHomes", "§6- §dhome §3commands : %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("getCfg.allowByPass", "§6- §3Allow bypass for OPs : %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("getCfg.minOpLvlHomes", "§6- §3Minimum OP level for §dhome §3commands : %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("getCfg.minOpLvlBypass", "§6- §3Minimum OP level to use Bypass : %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("getCfg.maxHomes", "§6- §3Max homes per player : %s");
            CYANSH_DEFAULT_TRANSLATIONS.put("getCfg.useCustomTranslations", "§6- §3Use custom translations : %s");
        }
        return CYANSH_DEFAULT_TRANSLATIONS;
    }
}

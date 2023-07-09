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
import fr.aeldit.cyanlib.lib.CyanLibConfig;
import fr.aeldit.cyanlib.lib.CyanLibLanguageUtils;
import fr.aeldit.cyanlib.lib.utils.RULES;
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
    public static final String MODID = "cyansh";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static Path MOD_PATH = FabricLoader.getInstance().getConfigDir().resolve(MODID);

    public static Homes HomesObj = new Homes();
    public static Trusts TrustsObj = new Trusts();

    public static CyanLibConfig LibConfig = new CyanLibConfig(MODID, getOptions(), getRules());
    public static CyanLibLanguageUtils LanguageUtils = new CyanLibLanguageUtils(MODID, LibConfig);
    public static CyanLib LibUtils = new CyanLib(MODID, LibConfig, LanguageUtils);

    private static Map<String, String> defaultTranslations;

    public static @NotNull Map<String, Object> getOptions()
    {
        Map<String, Object> options = new HashMap<>();

        options.put("allowHomes", true);
        options.put("allowHomesOf", true);
        options.put("allowByPass", false);
        options.put("useCustomTranslations", false);
        options.put("msgToActionBar", true);

        options.put("maxHomes", 10);
        options.put("minOpLevelExeHomes", 0);
        options.put("minOpLevelExeEditConfig", 4);

        return options;
    }

    public static @NotNull Map<String, Object> getRules()
    {
        Map<String, Object> rules = new HashMap<>();

        rules.put("maxHomes", RULES.POSITIVE_VALUE);
        rules.put("minOpLevelExeHomes", RULES.OP_LEVELS);
        rules.put("minOpLevelExeEditConfig", RULES.OP_LEVELS);

        return rules;
    }

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
        if (defaultTranslations == null)
        {
            defaultTranslations = new HashMap<>();

            defaultTranslations.put("error.playerOnlyCmd", "This command can only be executed by a player");
            defaultTranslations.put("error.notOp", "§cYou don't have the required permission to do that");
            defaultTranslations.put("error.notOpOrTrusted", "§cThis player doesn't trust you or you don't have the required permission");
            defaultTranslations.put("error.incorrectIntOp", "§cThe OP level must be in [0;4]");
            defaultTranslations.put("error.incorrectIntMaxHomes", "§cThe number must be in [1;128]");
            defaultTranslations.put("error.disabled.homes", "§cThe home commands are disabled. To enable them, enter '/cyansh config booleanOptions allowHomes true' in chat");
            defaultTranslations.put("error.homeAlreadyExists", "§cThis home already exists");
            defaultTranslations.put("error.homeNotFound", "§cThis home doesn't exist (check the spelling)");
            defaultTranslations.put("error.maxHomesReached", "§cYou reached the maximum number of homes §6(%s§6)");
            defaultTranslations.put("error.playerNotOnline", "§cThis player is not online");
            defaultTranslations.put("error.playerNotTrusted", "§cYou don't trust this player");
            defaultTranslations.put("error.playerNotTrusting", "§cThis player doesn't trust you");
            defaultTranslations.put("error.playerAlreadyTrusted", "§cYou already trust this player");
            defaultTranslations.put("error.selfTrust", "§cYou can't trust/untrust yourself");
            defaultTranslations.put("error.noHomes", "§cYou don't have any home");
            defaultTranslations.put("error.noHomesOf", "§cThis player doesn't have any home");
            defaultTranslations.put("error.useSelfHomes", "§cPlease use the normal commands to use your homes");
            defaultTranslations.put("error.optionNotFound", "§cThis option does not exist");

            defaultTranslations.put("setHome", "§3The home %s §3have been created");
            defaultTranslations.put("goToHome", "§3You have been teleported to the home %s");
            defaultTranslations.put("removeHome", "§3The home %s §3have been removed");
            defaultTranslations.put("removeAllHomes", "§3All your homes have been removed");
            defaultTranslations.put("removeHomeOf", "§3The home %s §3have been removed from %s§3's homes");
            defaultTranslations.put("getTrustingPlayers", "§3Players that trust you : %s");
            defaultTranslations.put("getTrustedPlayers", "§3Players that you trust : %s");
            defaultTranslations.put("noTrustingPlayer", "§3No player trusts you");
            defaultTranslations.put("noTrustedPlayer", "§3You don't trust any player");
            defaultTranslations.put("playerTrusted", "§3You now trust %s");
            defaultTranslations.put("playerUnTrusted", "§3You no longer trust %s");
            defaultTranslations.put("translationsReloaded", "§3Translations have been reloaded");
            defaultTranslations.put("currentValue", "§7Current value : %s");
            defaultTranslations.put("setValue", "§7Set value to : %s  %s  %s  %s  %s");
            defaultTranslations.put("getHome", "%s §3(%s§3, created on the %s§3)");

            defaultTranslations.put("set.allowHomes", "§3Toggled §dhome §3commands %s");
            defaultTranslations.put("set.allowHomesOf", "§3Toggled §dhomeOf §3commands %s");
            defaultTranslations.put("set.allowByPass", "§3Toggled ByPass %s");
            defaultTranslations.put("set.useCustomTranslations", "§3Toggled translations %s");
            defaultTranslations.put("set.msgToActionBar", "§3Toggled messages to action bar %s");
            defaultTranslations.put("set.maxHomes", "§3The maximum number of homes per player is now %s");
            defaultTranslations.put("set.minOpLevelExeHomes", "§3The OP level required to use the §dhome §3commands is now %s");
            defaultTranslations.put("set.minOpLevelExeEditConfig", "§3The minimum OP level to edit the config is now %s");

            defaultTranslations.put("dashSeparation", "§6------------------------------------");
            defaultTranslations.put("listHomes", "§6CyanSetHome - YOUR HOMES :\n");
            defaultTranslations.put("listHomesOf", "§6CyanSetHome - HOMES OF %s :\n");

            defaultTranslations.put("desc.sethome", "§3The §d/set-home §3command saves your current location");
            defaultTranslations.put("desc.home", "§3The §d/home §3command teleports you to the given home");
            defaultTranslations.put("desc.removehome", "§3The §d/remove-home §3command removes the given home");
            defaultTranslations.put("desc.removeallhomes", "§3The §d/remove-all-homes §3command removes all your homes");
            defaultTranslations.put("desc.gethomes", "§3The §d/get-homes §3command lists all your homes");
            defaultTranslations.put("desc.homeof", "§3The §d/home-of <player_name> <home_name> §3command teleports you to the home of the player");
            defaultTranslations.put("desc.removehomeof", "§3The §d/remove-home-of <player_name> <home_name> §3command removes the home of the player");
            defaultTranslations.put("desc.gethomesof", "§3The §d/get-homes-of <player_name> §3command lists all the player's homes");

            defaultTranslations.put("desc.allowHomes", "§3The §dallowHomes §3option defines whether the home commands are enabled or not");
            defaultTranslations.put("desc.allowHomesOf", "§3The §dallowHomesOf §3option defines whether the homeOf commands are enabled or not");
            defaultTranslations.put("desc.allowByPass", "§3The §dallowByPass §3option defines whether admins with the correct OP level can bypass permissions like trust between players");
            defaultTranslations.put("desc.useCustomTranslations", "§3The §duseTranslations §3option defines whether the translation will be used or not");
            defaultTranslations.put("desc.msgToActionBar", "§3The §dmsgToActionBar §3option defines whether the messages will be sent to the action bar or not");
            defaultTranslations.put("desc.maxHomes", "§3The §dmaxHomes §3option defines the maximum number of homes a player can have");
            defaultTranslations.put("desc.minOpLevelExeHomes", "§3The §dminOpLevelExeHomes §3option defines the OP level required to run the home commands");
            defaultTranslations.put("desc.minOpLevelExeEditConfig", "§3The §dminOpLevelExeEditConfig §3option defines the OP level required to edit the config");

            defaultTranslations.put("getCfg.header", "§6CyanSetHome - OPTIONS :\n");
            defaultTranslations.put("getCfg.allowHomes", "§6- §dhome §3commands : %s");
            defaultTranslations.put("getCfg.allowHomesOf", "§6- §dhomeOf §3commands : %s");
            defaultTranslations.put("getCfg.allowByPass", "§6- §3Allow bypass for OPs : %s");
            defaultTranslations.put("getCfg.useCustomTranslations", "§6- §3Use custom translations : %s");
            defaultTranslations.put("getCfg.msgToActionBar", "§6- §3Messages to action bar : %s");
            defaultTranslations.put("getCfg.maxHomes", "§6- §3Max homes per player : %s");
            defaultTranslations.put("getCfg.minOpLevelExeHomes", "§6- §3Minimum OP level for §dhome §3commands : %s");
            defaultTranslations.put("getCfg.minOpLevelExeEditConfig", "§6- §3Minimum OP level to edit config : %s");
        }
        return defaultTranslations;
    }
}

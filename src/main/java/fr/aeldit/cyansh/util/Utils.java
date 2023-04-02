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

import fr.aeldit.cyanlib.util.LanguageUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Formatting;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

public class Utils
{
    public static final String MODID = "cyansh";
    public static final Path homesPath = FabricLoader.getInstance().getConfigDir().resolve(MODID);
    public static final Path trustPath = Path.of(homesPath + "/trusted_players.properties");

    public static String on = Formatting.GREEN + "ON";
    public static String off = Formatting.RED + "OFF";
    public static Formatting gold = Formatting.GOLD;
    public static Formatting yellow = Formatting.YELLOW;

    private static final List<String> options = new ArrayList<>();

    private static void generateOptionsTraductionsList()
    {
        options.add("allowHomes");
        options.add("allowHomesOf");
        options.add("allowOPHomeOf");

        options.add("useTranslations");
        options.add("msgToActionBar");
        options.add("errorToActionBar");
    }

    public static List<String> getOptionsList()
    {
        if (options.isEmpty())
        {
            generateOptionsTraductionsList();
        }
        return options;
    }

    // Files
    public static void checkOrCreateHomesDir()
    {
        if (!Files.exists(homesPath))
        {
            try
            {
                Files.createDirectory(homesPath);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void checkOrCreateHomesFiles(Path filePath)
    {
        checkOrCreateHomesDir();
        if (!Files.exists(filePath))
        {
            try
            {
                Files.createFile(filePath);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void checkOrCreateTrustFile()
    {
        checkOrCreateHomesDir();
        if (!Files.exists(trustPath))
        {
            try
            {
                Files.createFile(trustPath);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean playerTrust(String trustingPlayer, String trustedPlayer)
    {
        if (Files.exists(trustPath))
        {
            try
            {
                Properties properties = new Properties();
                properties.load(new FileInputStream(trustPath.toFile()));

                for (String key : properties.stringPropertyNames())
                {
                    if (key.split("_")[1].equals(trustingPlayer))
                    {
                        for (String playerName : properties.get(key).toString().split(" "))
                        {
                            if (playerName.split("_")[1].equals(trustedPlayer))
                            {
                                return true;
                            }
                        }
                    }
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    // Language Utils
    public static LanguageUtils CyanSHLanguageUtils = new LanguageUtils(MODID);
    public static LinkedHashMap<String, String> defaultTranslations = new LinkedHashMap<>();

    public static void generateDefaultTranslations()
    {

        defaultTranslations.put("desc.sethome", "§3The §d/sethome §3command saves your current location");
        defaultTranslations.put("desc.home", "§3The §d/home §3command teleports you to the given home");
        defaultTranslations.put("desc.removehome", "§3The §d/removehome §3command removes the given home");
        defaultTranslations.put("desc.removeallhomes", "§3The §d/removeallhomes §3command removes all your homes");
        defaultTranslations.put("desc.gethomes", "§3The §d/gethomes §3command lists all your homes");
        defaultTranslations.put("desc.homeof", "§3The §d/homeof <player_name> <home_name> §3command teleports you to the home of the player");
        defaultTranslations.put("desc.removehomeof", "§3The §d/removehomeof <player_name> <home_name> §3command removes the home of the player");
        defaultTranslations.put("desc.removeallhomesof", "§3The §d/removeallhomesof <player_name> §3command removes all the homes of the player");
        defaultTranslations.put("desc.gethomesof", "§3The §d/gethomesof <player_name> §3command lists all the player's homes");

        defaultTranslations.put("desc.allowHomes", "§3The §dallowHomes §3option defines wether the home commands are enabled or not");
        defaultTranslations.put("desc.allowHomeOf", "§3The §dallowHomeOf §3option defines wether the homeOf commands are enabled or not");
        defaultTranslations.put("desc.allowOPHomeOf", "§3The §dallowHomeOf (OP) §3option defines wether the homeOf commands can be used by OP players, independently of the trust system");
        defaultTranslations.put("desc.useTranslations", "§3The §duseTranslations §3option defines wether the translation will be used or not");
        defaultTranslations.put("desc.msgToActionBar", "§3The §dmsgToActionBar §3option defines wether the messages will be sent to the action bar or not");
        defaultTranslations.put("desc.errorToActionBar", "§3The §derrorToActionBar §3option defines wether the error messages will be sent to the action bar or not");
        defaultTranslations.put("desc.maxHomes", "§3The §dmaxHomes §3option defines the maximum number of homes a player can have");
        defaultTranslations.put("desc.minOpLevelExeHomes", "§3The §dminOpLevelExeHomes §3option defines the OP level required to run the home commands");
        defaultTranslations.put("desc.minOpLevelExeHomeOf", "§3The §dminOpLevelExeHomeOf §3option defines the OP level required to run the homeOf commands");
        defaultTranslations.put("desc.minOpLevelExeEditConfig", "§3The §dminOpLevelExeEditConfig §3option defines the OP level required to edit the config");
        defaultTranslations.put("desc.minOpLevelExeRemoveHomeOf", "§3The §dminOpLevelExeRemoveHomeOf §3option defines the OP level required to remove the home of another player");

        defaultTranslations.put("dashSeparation", "§6------------------------------------");
        defaultTranslations.put("listHomes", "§6CyanSetHome - YOUR HOMES :\n");
        defaultTranslations.put("listHomesOf", "§6CyanSetHome - HOMES OF %s :\n");
        defaultTranslations.put("headerDescCmd", "§6CyanSetHome - DESCRIPTION (commands) :\n");
        defaultTranslations.put("headerDescOptions", "§6CyanSetHome - DESCRIPTION (options) :\n");
        defaultTranslations.put("dateCreated", "created on the ");

        defaultTranslations.put("getCfg.header", "§6CyanSetHome - OPTIONS :\n");
        defaultTranslations.put("getCfg.allowHomes", "§3home commands : %s");
        defaultTranslations.put("getCfg.allowHomeOf", "§3homeOf commands : %s");
        defaultTranslations.put("getCfg.allowOPHomeOf", "§3homeOf commands for OP : %s");
        defaultTranslations.put("getCfg.useTranslations", "§3Use translations : %s");
        defaultTranslations.put("getCfg.msgToActionBar", "§3Messages to action bar : %s");
        defaultTranslations.put("getCfg.errorToActionBar", "§3Error messages to action bar : %s");
        defaultTranslations.put("getCfg.maxHomes", "§3Max homes per player : %s");
        defaultTranslations.put("getCfg.minOpLevelExeEditConfig", "§3Minimum OP level to edit config : %s");
        defaultTranslations.put("getCfg.minOpLevelExeHomes", "§3Minimum OP level for §dhome §3commands : %s");
        defaultTranslations.put("getCfg.minOpLevelExeHomeOf", "§3Minimum OP level for §dhomeOf §3commands : %s");
        defaultTranslations.put("getCfg.minOpLevelExeRemoveHomeOf", "§3Minimum OP level for §d/removehomeof §3: %s");

        defaultTranslations.put("set.allowHomes", "§3Toogled §dhome §3commands %s");
        defaultTranslations.put("set.allowHomeOf", "§3Toogled §dhomeOf §3commands %s");
        defaultTranslations.put("set.allowOPHomeOf", "§3Toogled §dhomeOf §3commands for the OP %s");
        defaultTranslations.put("set.useTranslations", "§3Toogled translations %s");
        defaultTranslations.put("set.msgToActionBar", "§3Toogled messages to action bar %s");
        defaultTranslations.put("set.errorToActionBar", "§3Toogled error messages to action bar %s");
        defaultTranslations.put("set.maxHomes", "§3The maximum number of homes per player is now %s");
        defaultTranslations.put("set.minOpLevelExeHomes", "§3The OP level required to use the §dhome §3commands is now %s");
        defaultTranslations.put("set.minOpLevelExeHomeOf", "§3The OP level required to use the §dhomeOf §3commands is now %s");
        defaultTranslations.put("set.minOpLevelExeEditConfig", "§3The minimum OP level to edit the config is now %s");
        defaultTranslations.put("set.minOpLevelExeRemoveHomeOf", "§3The minimum OP level required to remove the home of another player is now %s");

        defaultTranslations.put("error.playerOnlyCmd", "This command can only be executed by a player");
        defaultTranslations.put("error.notOp", "§cYou don't have the required permission to do that");
        defaultTranslations.put("error.incorrectIntOp", "§cThe OP level must be in [0;4]");
        defaultTranslations.put("error.incorrectIntMaxHomes", "§cThe number must be in [1;128]");
        defaultTranslations.put("error.disabled.homes", "§cThe home commands are disabled. To enable them, enter '/cyansh config booleanOptions allowHomes true' in chat");
        defaultTranslations.put("error.homeAlreadyExists", "§cThis home alreay exists");
        defaultTranslations.put("error.homeNotFound", "§cThis home doesn't exist (check the spelling)");
        defaultTranslations.put("error.maxHomesReached", "§cYou reached the maximum number of homes §6(%s§6)");
        defaultTranslations.put("error.playerNotOnline", "§cThis player is not online");
        defaultTranslations.put("error.playerNotTrusted", "§cYou don't trust this player");
        defaultTranslations.put("error.playerNotTrusting", "§cThis player doesn't trust you");
        defaultTranslations.put("error.playerAlreadyTrusted", "§cYou already trust this player");
        defaultTranslations.put("error.selfTrust", "§cYou can't trust/untrust yourself");
        defaultTranslations.put("error.noHomes", "§cYou don't have any home");
        defaultTranslations.put("error.noHomesOf", "§cThis player doesn't have any home");

        defaultTranslations.put("setHome", "§3The home %s §3have been created");
        defaultTranslations.put("goToHome", "§3You have been teleported to the home %s");
        defaultTranslations.put("removeHome", "§3The home %s §3have been removed");
        defaultTranslations.put("removeAllHomes", "§3The home %s §3have been removed");
        defaultTranslations.put("removeHomeOf", "§3The home %s §3have been removed from %s§3's homes");
        defaultTranslations.put("getTrustingPlayers", "§3Players that trust you :%s");
        defaultTranslations.put("getTrustedPlayers", "§3Players that you trust :%s");
        defaultTranslations.put("noTrustingPlayer", "§3No player trusts you\"");
        defaultTranslations.put("noTrustedPlayer", "§3You don't trust any player");
        defaultTranslations.put("playerTrusted", "§3You now trust %s");
        defaultTranslations.put("playerUnTrusted", "§3You no longer trust %s");
        defaultTranslations.put("translationsReloaded", "§3Translations have been reloaded");
    }

    public static LinkedHashMap<String, String> getDefaultTranslations()
    {
        if (defaultTranslations.isEmpty())
        {
            generateDefaultTranslations();
        }
        return defaultTranslations;
    }
}

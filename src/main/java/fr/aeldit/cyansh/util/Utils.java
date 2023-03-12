package fr.aeldit.cyansh.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Formatting;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Utils
{
    public static final Path homesPath = FabricLoader.getInstance().getConfigDir().resolve("cyansh");
    public static final Path trustPath = Path.of(homesPath + "\\trusted_players.properties");

    public static String on = Formatting.GREEN + "ON";
    public static String off = Formatting.RED + "OFF";
    public static Formatting gold = Formatting.GOLD;
    public static Formatting yellow = Formatting.YELLOW;

    private static final Map<String, String> commandsTraductionsMap = new HashMap<>();
    private static final Map<String, String> optionsTraductionsMap = new HashMap<>();
    private static final Map<String, String> configTraductionsMap = new HashMap<>();
    private static final Map<String, String> configSetTraductionsMap = new HashMap<>();
    private static final Map<String, String> miscTraductionsMap = new HashMap<>();
    private static final Map<String, String> errorsTraductionsMap = new HashMap<>();
    private static final Map<String, String> cmdFeedbackTraductionsMap = new HashMap<>();

    // Generates

    private static void generateConfigSetTraductionsMap()
    {
        configSetTraductionsMap.put("allowHomes", "§3Toogled §dhome §3commands %s");
        configSetTraductionsMap.put("allowHomeOf", "§3Toogled §dhomeOf §3commands %s");
        configSetTraductionsMap.put("allowOPHomeOf", "§3Toogled §dhomeOf §3commands for the OP %s");
        configSetTraductionsMap.put("useTranslations", "§3Toogled translations %s");
        configSetTraductionsMap.put("msgToActionBar", "§3Toogled messages to action bar %s");
        configSetTraductionsMap.put("errorToActionBar", "§3Toogled error messages to action bar %s");

        configSetTraductionsMap.put("maxHomes", "§3The maximum number of homes per player is now %s");
        configSetTraductionsMap.put("minOpLevelExeHomes", "§3The OP level required to use the §dhome §3commands is now %s");
        configSetTraductionsMap.put("minOpLevelExeHomeOf", "§3The OP level required to use the §dhomeOf §3commands is now %s");
        configSetTraductionsMap.put("minOpLevelExeEditConfig", "§3The minimum OP level to edit the config is now %s");
        configSetTraductionsMap.put("minOpLevelExeRemoveHomeOf", "§3The minimum OP level required to remove the home of another player is now %s");
    }

    public static void generateErrorsTraductionsMap()
    {
        errorsTraductionsMap.put("playerOnlyCmd", "This command can only be executed by a player");
        errorsTraductionsMap.put("notOp", "§cYou don't have the required permission to do that");
        errorsTraductionsMap.put("incorrectIntOp", "§cThe OP level must be in [0;4]");
        errorsTraductionsMap.put("incorrectIntMaxHomes", "§cThe number must be in [1;128]");
        errorsTraductionsMap.put("disabled.homes", "§cThe home commands are disabled. To enable them, enter '/cyansh config booleanOptions allowHomes true' in chat");
        errorsTraductionsMap.put("homeAlreadyExists", "§cThis home alreay exists");
        errorsTraductionsMap.put("homeNotFound", "§cThis home doesn't exist (check the spelling)");
        errorsTraductionsMap.put("maxHomesReached", "§cYou reached the maximum number of homes §6(%s§6)");
        errorsTraductionsMap.put("playerNotOnline", "§cThis player is not online");
        errorsTraductionsMap.put("playerNotTrusted", "§cYou don't trust this player");
        errorsTraductionsMap.put("playerNotTrusting", "§cThis player doesn't trust you");
        errorsTraductionsMap.put("playerAlreadyTrusted", "§cYou already trust this player");
        errorsTraductionsMap.put("selfTrust", "§cYou can't trust/untrust yourself");
        errorsTraductionsMap.put("noHomes", "§cYou don't have any home");
        errorsTraductionsMap.put("noHomesOf", "§cThis player doesn't have any home");
    }

    private static void generateCmdFeedbackTraductionsMap()
    {
        cmdFeedbackTraductionsMap.put("setHome", "§3The home %s §3have been created");
        cmdFeedbackTraductionsMap.put("goToHome", "§3You have been teleported to the home %s");
        cmdFeedbackTraductionsMap.put("removeHome", "§3The home %s §3have been removed");
        cmdFeedbackTraductionsMap.put("removeAllHomes", "§3The home %s §3have been removed");
        cmdFeedbackTraductionsMap.put("removeHomeOf", "§3The home %s §3have been removed from %s§3's homes");
        cmdFeedbackTraductionsMap.put("listHomes", "§3Homes :");
        cmdFeedbackTraductionsMap.put("getTrustingPlayers", "§3Players that trust you :%s");
        cmdFeedbackTraductionsMap.put("getTrustedPlayers", "§3Players that you trust :%s");
        cmdFeedbackTraductionsMap.put("noTrustingPlayer", "§3No player trusts you");
        cmdFeedbackTraductionsMap.put("noTrustedPlayer", "§3You don't trust any player");
        cmdFeedbackTraductionsMap.put("playerTrusted", "§3You now trust %s");
        cmdFeedbackTraductionsMap.put("playerUnTrusted", "§3You no longer trust %s");
    }

    public static void generateAllMaps()
    {
        generateCommandsTraductionsMap();
        generateOptionsTraductionsMap();
        generateMiscTraductionsMap();
        generateConfigTraductionsMap();
        generateConfigSetTraductionsMap();
        generateErrorsTraductionsMap();
        generateCmdFeedbackTraductionsMap();
    }

    // Gets
    public static String getCommandTraduction(String command)
    {
        return commandsTraductionsMap.get(command) != null ? commandsTraductionsMap.get(command) : "null";
    }

    public static String getOptionTraduction(String option)
    {
        return optionsTraductionsMap.get(option) != null ? optionsTraductionsMap.get(option) : "null";
    }

    public static String getMiscTraduction(String option)
    {
        return miscTraductionsMap.get(option) != null ? miscTraductionsMap.get(option) : "null";
    }

    public static String getConfigTraduction(String option)
    {
        return configTraductionsMap.get(option) != null ? configTraductionsMap.get(option) : "null";
    }

    public static String getConfigSetTraduction(String option)
    {
        return configSetTraductionsMap.get(option) != null ? configSetTraductionsMap.get(option) : "null";
    }

    public static String getErrorTraduction(String option)
    {
        return errorsTraductionsMap.get(option) != null ? errorsTraductionsMap.get(option) : "null";
    }

    public static String getCmdFeedbackTraduction(String option)
    {
        return cmdFeedbackTraductionsMap.get(option) != null ? cmdFeedbackTraductionsMap.get(option) : "null";
    }

    public static Map<String, String> getOptionsTraductionsMap()
    {
        if (optionsTraductionsMap.isEmpty())
        {
            generateOptionsTraductionsMap();
        }
        return optionsTraductionsMap;
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
}

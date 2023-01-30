package fr.aeldit.cyansh.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Formatting;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Constants
{
    public static final Path locationsPath = FabricLoader.getInstance().getConfigDir().resolve("cyansh");

    public static String on = Formatting.GREEN + "ON";
    public static String off = Formatting.RED + "OFF";
    public static Formatting gold = Formatting.GOLD;
    public static Formatting yellow = Formatting.YELLOW;
    public static Formatting cyan = Formatting.DARK_AQUA;
    public static Formatting magenta = Formatting.LIGHT_PURPLE;

    private static final Map<String, String> commandsTraductionsMap = new HashMap<>();
    private static final Map<String, String> optionsTraductionsMap = new HashMap<>();
    private static final Map<String, String> configTraductionsMap = new HashMap<>();
    private static final Map<String, String> configSetTraductionsMap = new HashMap<>();
    private static final Map<String, String> miscTraductionsMap = new HashMap<>();
    private static final Map<String, String> errorsTraductionsMap = new HashMap<>();
    private static final Map<String, String> cmdFeedbackTraductionsMap = new HashMap<>();

    private static void generateCommandsTraductionsMap()
    {
        commandsTraductionsMap.put("sethome", "§3The §d/sethome §3command creates a home at your current location\n");
        commandsTraductionsMap.put("home", "§3The §d/home §3command teleports you to the given home\n");
        commandsTraductionsMap.put("removehome", "§3The §d/removehome §3command removes the given home\n");
        commandsTraductionsMap.put("gethomes", "§3The §d/gethomes §3command lists all your homes\n");
    }

    private static void generateOptionsTraductionsMap()
    {
        optionsTraductionsMap.put("allowHomes", "§3The §dallowHomes §3option defines wether the home commands are enable or not\n");
        optionsTraductionsMap.put("allowHomeOf", "§3The §dallowHomeOf §3option defines wether the homeOf commands are enable or not\n");

        optionsTraductionsMap.put("useTranslations", "§3The §duseTranslations §3option defines wether the translation will be used or not\n");
        optionsTraductionsMap.put("msgToActionBar", "§3The §dmsgToActionBar §3option defines wether the messages will be sent to the action bar or not\n");
        optionsTraductionsMap.put("errorToActionBar", "§3The §derrorToActionBar §3option defines wether the error messages will be sent to the action bar or not\n");

        optionsTraductionsMap.put("maxHomes", "§3The §dmaxHomes §3option defines the maximum number of homes a player can have\n");
        optionsTraductionsMap.put("minOpLevelExeHomes", "§3The §dminOpLevelExeHomes §3option defines the OP level required to run the home commands\n");
        optionsTraductionsMap.put("minOpLevelExeHomeOf", "§3The §dminOpLevelExeHomeOf §3option defines the OP level required to run the homeOf commands\n");
        optionsTraductionsMap.put("minOpLevelExeEditConfig", "§3The §dminOpLevelExeEditConfig §3option defines the OP level required to edit the config");
    }

    private static void generateMiscTraductionsMap()
    {
        miscTraductionsMap.put("headerTop", "§3------------------------------------");
        miscTraductionsMap.put("listHomes", "§3Homes :");
    }

    private static void generateConfigTraductionsMap()
    {
        configTraductionsMap.put("header", "§3Cyan mod's options :");
        configTraductionsMap.put("allowHomes", "- §3location commands : %s");
        configTraductionsMap.put("allowHomeOf", "- §3location commands : %s");
        configTraductionsMap.put("useTranslations", "- §3Use translations : %s");
        configTraductionsMap.put("msgToActionBar", "- §3Messages to action bar : %s");
        configTraductionsMap.put("errorToActionBar", "- §3Error messages to action bar : %s");

        configTraductionsMap.put("distanceToEntitiesKgi", "- §3kgi distance (in chunks) : %s");
        configTraductionsMap.put("minOpLevelExeModifConfig", "- §3Minimum OP level to edit config : %s");
        configTraductionsMap.put("minOpLevelExeBed", "- §3Minimum OP level for §d/bed §3: %s");
        configTraductionsMap.put("minOpLevelExeKgi", "- §3Minimum OP level for §d/kgi §3: %s");
        configTraductionsMap.put("minOpLevelExeSurface", "- §3Minimum OP level for §d/surface §3: %s");
        configTraductionsMap.put("minOpLevelExeLocation", "- §3Minimum OP level to see / teleport to locations §3: %s");
        configTraductionsMap.put("minOpLevelExeEditLocation", "- §3Minimum OP level to edit locations: %s");
    }

    private static void generateConfigSetTraductionsMap()
    {
        configSetTraductionsMap.put("allowBed", "§3Toogled §d/bed §3command %s");
        configSetTraductionsMap.put("allowKgi", "§3Toogled §d/kgi §3command %s");
        configSetTraductionsMap.put("allowSurface", "§3Toogled §d/surface §3command %s");
        configSetTraductionsMap.put("allowLocations", "§3Toogled §dlocation §3commands %s");
        configSetTraductionsMap.put("useTranslations", "§3Toogled translations %s");
        configSetTraductionsMap.put("msgToActionBar", "§3Toogled messages to action bar %s");
        configSetTraductionsMap.put("errorToActionBar", "§3Toogled error messages to action bar %s");
        configSetTraductionsMap.put("distanceToEntitiesKgi", "§3The distance for §d/kgi §3is now %s");
        configSetTraductionsMap.put("minOpLevelExeModifConfig", "§3The minimum OP level to edit the config is now %s");
        configSetTraductionsMap.put("minOpLevelExeBed", "§3The minimum OP level to execute §d/bed §3is now %s");
        configSetTraductionsMap.put("minOpLevelExeKgi", "§3The minimum OP level to execute §d/kgi §3is now %s");
        configSetTraductionsMap.put("minOpLevelExeSurface", "§3The minimum OP level to execute §d/surface §3is now %s");
        configSetTraductionsMap.put("minOpLevelExeLocation", "§3The minimum OP level to see / teleport to locations is now %s");
        configSetTraductionsMap.put("minOpLevelExeEditLocation", "§3The minimum OP level to edit locations is now %s");
    }

    public static void generateErrorsTraductionsMap()
    {
        errorsTraductionsMap.put("playerOnlyCmd", "§cThis command can only be executed by a player");
        errorsTraductionsMap.put("notOp", "§cYou don't have the required permission to do that");
        errorsTraductionsMap.put("incorrectIntOp", "§cThe OP level must be in [0;4]");
        errorsTraductionsMap.put("incorrectIntMaxHomes", "§cThe number must be in [1;128]");
        errorsTraductionsMap.put("disabled.homes", "§cThe home commands are disabled. To enable them, enter '/cyansh config booleanOptions allowHomes true' in chat");
        errorsTraductionsMap.put("homeAlreadyExists", "§cThis home alreay exists");
        errorsTraductionsMap.put("homeNotFound", "§cThis home doesn't exist (check the spelling)");
        errorsTraductionsMap.put("maxHomesReached", "§cYou reached the maximum number of homes §6(%s§6)");
    }

    private static void generateCmdFeedbackTraductionsMap()
    {
        cmdFeedbackTraductionsMap.put("setHome", "§3The home %s §3have been created");
        cmdFeedbackTraductionsMap.put("goToHome", "§3You have been teleported to the home %s");
        cmdFeedbackTraductionsMap.put("removeHome", "§3The home %s §3have been removed");
        cmdFeedbackTraductionsMap.put("listHomes", "§3Homes :");
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
}

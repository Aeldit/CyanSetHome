package fr.aeldit.cyansh.util;

import net.fabricmc.loader.api.FabricLoader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Properties;

public class LanguageUtils
{
    public static Path languagePath = FabricLoader.getInstance().getConfigDir().resolve("cyansh/translations.properties");
    public static LinkedHashMap<String, String> translations = new LinkedHashMap<>();
    public static LinkedHashMap<String, String> defaultTranslations = new LinkedHashMap<>();
    public static final String DESC = "desc.";
    public static final String GETCFG = "getCfg.";
    public static final String SET = "set.";
    public static final String ERROR = "error.";

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

        defaultTranslations.put("set.allowBed", "§3Toogled §d/bed §3command %s");
        defaultTranslations.put("set.allowKgi", "§3Toogled §d/kgi §3command %s");
        defaultTranslations.put("set.allowSurface", "§3Toogled §d/surface §3command %s");
        defaultTranslations.put("set.allowLocations", "§3Toogled §dlocation §3commands %s");
        defaultTranslations.put("set.useTranslations", "§3Toogled translations %s");
        defaultTranslations.put("set.msgToActionBar", "§3Toogled messages to action bar %s");
        defaultTranslations.put("set.errorToActionBar", "§3Toogled error messages to action bar %s");
        defaultTranslations.put("set.distanceToEntitiesKgi", "§3The distance for §d/kgi §3is now %s");
        defaultTranslations.put("set.minOpLevelExeModifConfig", "§3The minimum OP level to edit the config is now %s");
        defaultTranslations.put("set.minOpLevelExeBed", "§3The minimum OP level to execute §d/bed §3is now %s");
        defaultTranslations.put("set.minOpLevelExeKgi", "§3The minimum OP level to execute §d/kgi §3is now %s");
        defaultTranslations.put("set.minOpLevelExeSurface", "§3The minimum OP level to execute §d/surface §3is now %s");
        defaultTranslations.put("set.minOpLevelExeLocation", "§3The minimum OP level to see / teleport to locations is now %s");
        defaultTranslations.put("set.minOpLevelExeEditLocation", "§3The minimum OP level to edit locations is now %s");

        defaultTranslations.put("error.notOp", "§cYou don't have the required permission to do that");
        defaultTranslations.put("error.wrongOPLevel", "§cThe OP level must be in [0;4]");
        defaultTranslations.put("error.wrongDistanceKgi", "§cThe kgi distance must be in [1;128]");
        defaultTranslations.put("error.playerNotFound", "§cPlayer not found. The player must be online");
        defaultTranslations.put("error.incorrectIntOp", "§cThe OP level must be in [0;4]");
        defaultTranslations.put("error.incorrectIntKgi", "§cThe distance must be in [1;128]");
        defaultTranslations.put("error.bedDisabled", "§cThe /bed command is disabled. To enable it, enter '/cyan config booleanOptions allowBed true' in chat");
        defaultTranslations.put("error.kgiDisabled", "§cThe /kgi command is disabled. To enable it, enter '/cyan config booleanOptions allowKgi true' in chat");
        defaultTranslations.put("error.surfaceDisabled", "§cThe /surface command is disabled. To enable it, enter '/cyan config booleanOptions allowSurface true' in chat");
        defaultTranslations.put("error.servOnly", "§cThis command can only be used on servers");
        defaultTranslations.put("error.bed", "§cYou don't have an attributed bed or respawn anchor");
        defaultTranslations.put("error.playerOnlyCmd", "§cThis command can only be executed by a player");
        defaultTranslations.put("error.locationAlreadyExists", "§cA location with this name already exists");
        defaultTranslations.put("error.locationsDisabled", "§cThe locations commands are disabled. To enable them, enter '/cyan config booleanOptions allowLocations true' in chat");
        defaultTranslations.put("error.locationNotFound", "§cThe location %s §cdoesn't exist (check if you spelled it correctly)");
        defaultTranslations.put("error.fileNotRemoved", "§cAn error occured while trying to remove the locations file");

        defaultTranslations.put("bed", "§3You have been teleported to your bed");
        defaultTranslations.put("respawnAnchor", "§3You have been teleported to your respawn anchor");
        defaultTranslations.put("kgi", "§3Ground items have been removed");
        defaultTranslations.put("kgir", "§3Ground items have been removed in a radius of %s §3chunks");
        defaultTranslations.put("surface", "§3You have been teleported to the surface");
        defaultTranslations.put("setLocation", "§3The location %s §3have been saved");
        defaultTranslations.put("goToLocation", "§3You have been teleported to %s");
        defaultTranslations.put("removeLocation", "§3The location %s §3have been removed");
        defaultTranslations.put("removedAllLocations", "§3All the locations have been removed");
        defaultTranslations.put("translationsReloaded", "§3The translations have been reloaded");
    }

    public static void loadLanguage()
    {
        if (!Files.exists(FabricLoader.getInstance().getConfigDir().resolve("cyan")))
        {
            try
            {
                Files.createDirectory(FabricLoader.getInstance().getConfigDir().resolve("cyan"));
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (!Files.exists(languagePath))
        {
            try
            {
                Files.createFile(languagePath);
                generateDefaultTranslations();
                Properties properties = new Properties();
                properties.putAll(defaultTranslations);
                properties.store(new FileOutputStream(languagePath.toFile()), null);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream(languagePath.toFile()));
            for (String key : properties.stringPropertyNames())
            {
                translations.put(key, properties.getProperty(key));
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String getTranslation(String key)
    {
        return translations.get(key) != null ? translations.get(key) : "null";
    }
}

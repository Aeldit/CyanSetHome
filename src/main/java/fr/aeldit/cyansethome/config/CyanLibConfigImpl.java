package fr.aeldit.cyansethome.config;

import fr.aeldit.cyanlib.lib.config.BooleanOption;
import fr.aeldit.cyanlib.lib.config.ICyanLibConfig;
import fr.aeldit.cyanlib.lib.config.IntegerOption;
import fr.aeldit.cyanlib.lib.utils.RULES;

import java.util.HashMap;
import java.util.Map;

public class CyanLibConfigImpl implements ICyanLibConfig
{
    public static final BooleanOption ALLOW_HOMES = new BooleanOption("allowHomes", true);
    public static final BooleanOption ALLOW_BYPASS = new BooleanOption("allowByPass", false);

    public static final IntegerOption MIN_OP_LVL_HOMES = new IntegerOption("minOpLvlHomes", 4, RULES.OP_LEVELS);
    public static final IntegerOption MAX_HOMES = new IntegerOption("maxHomes", 20, RULES.POSITIVE_VALUE);
    public static final IntegerOption MIN_OP_LVL_BYPASS = new IntegerOption("minOpLvlBypass", 4, RULES.OP_LEVELS);

    public static final BooleanOption USE_XP_TO_TP_HOME = new BooleanOption("useXpToTpHome", true);

    public static final IntegerOption BLOCKS_PER_XP_LEVEL_HOME = new IntegerOption("blockPerXpLevelHome", 300,
            RULES.POSITIVE_VALUE
    );

    @Override
    public Map<String, String> getDefaultTranslations()
    {
        Map<String, String> translations = new HashMap<>();

        // ERRORS
        translations.put("cyansethome.error.notOpOrTrusted", "§cThis player doesn't trust you or you don't have the " +
                "required permission");
        translations.put("cyansethome.error.homesDisabled", "§cThe /home command is disabled");
        translations.put("cyansethome.error.homeAlreadyExists", "§cThis home already exists");
        translations.put("cyansethome.error.homeNotFound", "§cThis home doesn't exist (check the spelling)");
        translations.put("cyansethome.error.homeNotFoundOrExists", "The home doesn't exists or you tried to rename it" +
                " with" +
                " the name of an existing home");
        translations.put("cyansethome.error.maxHomesReached", "§cYou reached the maximum number of homes §6(%s§6)");
        translations.put("cyansethome.error.playerNotOnline", "§cThis player is not online");
        translations.put("cyansethome.error.playerNotTrusted", "§cYou don't trust this player");
        translations.put("cyansethome.error.playerNotTrusting", "§cThis player doesn't trust you");
        translations.put("cyansethome.error.playerAlreadyTrusted", "§cYou already trust this player");
        translations.put("cyansethome.error.selfTrust", "§cYou can't trust/untrust yourself");
        translations.put("cyansethome.error.noHomes", "§cYou don't have any home");
        translations.put("cyansethome.error.noHomesOf", "§cThis player doesn't have any home");
        translations.put("cyansethome.error.useSelfHomes", "§cPlease use the normal commands to use your homes");
        translations.put("cyansethome.error.bypassDisabled", "§cThe ByPass option is disabled or you are not OP level" +
                " 4");
        translations.put("cyansethome.error.notEnoughXp", "§cYou don't have enough XP (%s §clevels are required)");

        // MESSAGES
        translations.put("cyansethome.msg.setHome", "§3The home %s §3has been created");
        translations.put("cyansethome.msg.setHomeOf", "§3The home %s §3has been created for the player %s");
        translations.put("cyansethome.msg.removeHome", "§3The home %s §3has been removed");
        translations.put("cyansethome.msg.removeHomeOf", "§3The home %s §3was removed from %s§3's homes");
        translations.put("cyansethome.msg.removeAllHomes", "§3All your homes have been removed");
        translations.put("cyansethome.msg.removeAllHomesOf", "§3All %s§3's homes were removed");
        translations.put("cyansethome.msg.renameHome", "§3The home %s §3has been renamed to %s");
        translations.put("cyansethome.msg.renameHomeOf", "§3The home %s §3has been renamed to %s §3for the player %s");
        translations.put("cyansethome.msg.goToHome", "§3You have been teleported to the home %s");
        translations.put("cyansethome.msg.getTrustingPlayers", "§3Players that trust you : %s");
        translations.put("cyansethome.msg.getTrustedPlayers", "§3Players that you trust : %s");
        translations.put("cyansethome.msg.noTrustingPlayer", "§3No player trusts you");
        translations.put("cyansethome.msg.noTrustedPlayer", "§3You don't trust any player");
        translations.put("cyansethome.msg.playerTrusted", "§3You now trust %s");
        translations.put("cyansethome.msg.playerUnTrusted", "§3You no longer trust %s");
        translations.put("cyansethome.msg.translationsReloaded", "§3Translations have been reloaded");
        translations.put("cyansethome.msg.getHome", "%s §3(%s§3, created on the %s§3)");

        // SETS
        translations.put("cyansethome.msg.set.allowHomes", "§3Toggled §dallowHome §3option %s");
        translations.put("cyansethome.msg.set.allowByPass", "§3Toggled ByPass %s");
        translations.put("cyansethome.msg.set.maxHomes", "§3The maximum number of homes per player is now %s");
        translations.put("cyansethome.msg.set.minOpLvlBypass", "§3The minimum OP level required to bypass permissions" +
                " is " +
                "now %s");
        translations.put("cyansethome.msg.set.minOpLvlHomes", "§3The minimum OP level required to use the §dhome " +
                "§3commands is now %s");

        // HEADERS
        translations.put("cyansethome.msg.listHomes", "§6CyanSetHome - YOUR HOMES\n");
        translations.put("cyansethome.msg.listHomesOf", "§6CyanSetHome - HOMES OF %s\n");

        // CONFIG_DESC
        translations.put("cyansethome.msg.getDesc.allowHomes", "§3The §dallowHomes §3option defines whether the home " +
                "commands are enabled or not");
        translations.put("cyansethome.msg.getDesc.allowByPass", "§3The §dallowByPass §3option defines whether admins " +
                "with " +
                "the correct OP level can bypass permissions like trust between players");
        translations.put("cyansethome.msg.getDesc.maxHomes", "§3The §dmaxHomes §3option defines the maximum number of" +
                " " +
                "homes a player can have");
        translations.put("cyansethome.msg.getDesc.minOpLvlBypass", "§3The §dminOpLvlBypass §3option defines the OP " +
                "level " +
                "required to bypass permissions");
        translations.put("cyansethome.msg.getDesc.minOpLvlHomes", "§3The §dminOpLvlHomes §3option defines the OP " +
                "level " +
                "required to run the home commands");

        // GET_CFG
        translations.put("cyansethome.msg.getCfg.header", "§6CyanSetHome - OPTIONS\n");
        translations.put("cyansethome.msg.getCfg.allowHomes", "§6- §dhome §3commands : %s");
        translations.put("cyansethome.msg.getCfg.allowByPass", "§6- §3Allow bypass for OPs : %s");
        translations.put("cyansethome.msg.getCfg.maxHomes", "§6- §3Max homes per player : %s");
        translations.put("cyansethome.msg.getCfg.minOpLvlBypass", "§6- §3Minimum OP level to use Bypass : %s");
        translations.put("cyansethome.msg.getCfg.minOpLvlHomes", "§6- §3Minimum OP level for §dhome §3commands : %s");

        return translations;
    }
}

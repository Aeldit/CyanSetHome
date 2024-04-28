package fr.aeldit.cyansh.config;

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
        translations.put("cyansh.error.notOpOrTrusted", "§cThis player doesn't trust you or you don't have the " +
                "required permission");
        translations.put("cyansh.error.homesDisabled", "§cThe /home command is disabled");
        translations.put("cyansh.error.homeAlreadyExists", "§cThis home alreay exists");
        translations.put("cyansh.error.homeNotFound", "§cThis home doesn't exist (check the spelling)");
        translations.put("cyansh.error.homeNotFoundOrExists", "The home doesn't exists or you tried to rename it with" +
                " the name of an existing home");
        translations.put("cyansh.error.maxHomesReached", "§cYou reached the maximum number of homes §6(%s§6)");
        translations.put("cyansh.error.playerNotOnline", "§cThis player is not online");
        translations.put("cyansh.error.playerNotTrusted", "§cYou don't trust this player");
        translations.put("cyansh.error.playerNotTrusting", "§cThis player doesn't trust you");
        translations.put("cyansh.error.playerAlreadyTrusted", "§cYou already trust this player");
        translations.put("cyansh.error.selfTrust", "§cYou can't trust/untrust yourself");
        translations.put("cyansh.error.noHomes", "§cYou don't have any home");
        translations.put("cyansh.error.noHomesOf", "§cThis player doesn't have any home");
        translations.put("cyansh.error.useSelfHomes", "§cPlease use the normal commands to use your homes");
        translations.put("cyansh.error.bypassDisabled", "§cThe ByPass option is disabled or you are not OP level 4");
        translations.put("cyansh.error.notEnoughXp", "§cYou don't have enough XP (%s §clevels are required)");

        // MESSAGES
        translations.put("cyansh.msg.setHome", "§3The home %s §3have been created");
        translations.put("cyansh.msg.setHomeOf", "§3The home %s §3have been created for the player %s");
        translations.put("cyansh.msg.removeHome", "§3The home %s §3have been removed");
        translations.put("cyansh.msg.removeHomeOf", "§3The home %s §3have been removed from %s§3's homes");
        translations.put("cyansh.msg.removeAllHomes", "§3All your homes have been removed");
        translations.put("cyansh.msg.removeAllHomesOf", "§3All %s's homes have been removed");
        translations.put("cyansh.msg.renameHome", "§3The home %s §3have been renamed to %s");
        translations.put("cyansh.msg.renameHomeOf", "§3The home %s §3have been renamed to %s §3for the player %s");
        translations.put("cyansh.msg.goToHome", "§3You have been teleported to the home %s");
        translations.put("cyansh.msg.getTrustingPlayers", "§3Players that trust you : %s");
        translations.put("cyansh.msg.getTrustedPlayers", "§3Players that you trust : %s");
        translations.put("cyansh.msg.noTrustingPlayer", "§3No player trusts you");
        translations.put("cyansh.msg.noTrustedPlayer", "§3You don't trust any player");
        translations.put("cyansh.msg.playerTrusted", "§3You now trust %s");
        translations.put("cyansh.msg.playerUnTrusted", "§3You no longer trust %s");
        translations.put("cyansh.msg.translationsReloaded", "§3Translations have been reloaded");
        translations.put("cyansh.msg.getHome", "%s §3(%s§3, created on the %s§3)");

        // SETS
        translations.put("cyansh.msg.set.allowHomes", "§3Toggled §dallowHome §3option %s");
        translations.put("cyansh.msg.set.allowByPass", "§3Toggled ByPass %s");
        translations.put("cyansh.msg.set.maxHomes", "§3The maximun number of homes per player is now %s");
        translations.put("cyansh.msg.set.minOpLvlBypass", "§3The minimum OP level required to bypass permissions is " +
                "now %s");
        translations.put("cyansh.msg.set.minOpLvlHomes", "§3The minimum OP level required to use the §dhome " +
                "§3commands is now %s");

        // HEADERS
        translations.put("cyansh.msg.listHomes", "§6CyanSetHome - YOUR HOMES\n");
        translations.put("cyansh.msg.listHomesOf", "§6CyanSetHome - HOMES OF %s\n");

        // CONFIG_DESC
        translations.put("cyansh.msg.getDesc.allowHomes", "§3The §dallowHomes §3option defines wether the home " +
                "commands are enabled or not");
        translations.put("cyansh.msg.getDesc.allowByPass", "§3The §dallowByPass §3option defines wether admins with " +
                "the correct OP level can bypass permissions like trust between players");
        translations.put("cyansh.msg.getDesc.maxHomes", "§3The §dmaxHomes §3option defines the maximum number of " +
                "homes a player can have");
        translations.put("cyansh.msg.getDesc.minOpLvlBypass", "§3The §dminOpLvlBypass §3option defines the OP level " +
                "required to bypass permissions");
        translations.put("cyansh.msg.getDesc.minOpLvlHomes", "§3The §dminOpLvlHomes §3option defines the OP level " +
                "required to run the home commands");

        // GET_CFG
        translations.put("cyansh.msg.getCfg.header", "§6CyanSetHome - OPTIONS\n");
        translations.put("cyansh.msg.getCfg.allowHomes", "§6- §dhome §3commands : %s");
        translations.put("cyansh.msg.getCfg.allowByPass", "§6- §3Allow bypass for OPs : %s");
        translations.put("cyansh.msg.getCfg.maxHomes", "§6- §3Max homes per player : %s");
        translations.put("cyansh.msg.getCfg.minOpLvlBypass", "§6- §3Minimum OP level to use Bypass : %s");
        translations.put("cyansh.msg.getCfg.minOpLvlHomes", "§6- §3Minimum OP level for §dhome §3commands : %s");

        return translations;
    }
}

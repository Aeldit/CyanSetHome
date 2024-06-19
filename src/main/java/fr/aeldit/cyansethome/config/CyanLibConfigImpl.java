package fr.aeldit.cyansethome.config;

import fr.aeldit.cyanlib.lib.config.BooleanOption;
import fr.aeldit.cyanlib.lib.config.ICyanLibConfig;
import fr.aeldit.cyanlib.lib.config.IntegerOption;
import fr.aeldit.cyanlib.lib.utils.RULES;

import java.util.Map;

import static java.util.Map.entry;

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
        return Map.<String, String>ofEntries(
                // ERRORS
                entry("cyansethome.error.notOpOrTrusted",
                        "§cThis player doesn't trust you or you don't have the required permission"
                ),
                entry("cyansethome.error.homesDisabled", "§cThe /home command is disabled"),
                entry("cyansethome.error.homeAlreadyExists", "§cThis home already exists"),
                entry("cyansethome.error.homeNotFound", "§cThis home doesn't exist (check the spelling)"),
                entry("cyansethome.error.homeNotFoundOrExists",
                        "The home doesn't exists or you tried to rename it with the name of an existing home"
                ),
                entry("cyansethome.error.maxHomesReached", "§cYou reached the maximum number of homes §6(%s§6)"),
                entry("cyansethome.error.playerNotOnline", "§cThis player is not online"),
                entry("cyansethome.error.playerNotTrusted", "§cYou don't trust this player"),
                entry("cyansethome.error.playerNotTrusting", "§cThis player doesn't trust you"),
                entry("cyansethome.error.playerAlreadyTrusted", "§cYou already trust this player"),
                entry("cyansethome.error.selfTrust", "§cYou can't trust/untrust yourself"),
                entry("cyansethome.error.noHomes", "§cYou don't have any home"),
                entry("cyansethome.error.noHomesOf", "§cThis player doesn't have any home"),
                entry("cyansethome.error.useSelfHomes", "§cPlease use the normal commands to use your homes"),
                entry("cyansethome.error.bypassDisabled",
                        "§cThe ByPass option is disabled or you are not OP level 4"
                ),
                entry("cyansethome.error.notEnoughXp", "§cYou don't have enough XP (%s §clevels are required)"),

                // MESSAGES
                entry("cyansethome.msg.setHome", "§3The home %s §3has been created"),
                entry("cyansethome.msg.setHomeOf", "§3The home %s §3has been created for the player %s"),
                entry("cyansethome.msg.removeHome", "§3The home %s §3has been removed"),
                entry("cyansethome.msg.removeHomeOf", "§3The home %s §3was removed from %s§3's homes"),
                entry("cyansethome.msg.removeAllHomes", "§3All your homes have been removed"),
                entry("cyansethome.msg.removeAllHomesOf", "§3All %s§3's homes were removed"),
                entry("cyansethome.msg.renameHome", "§3The home %s §3has been renamed to %s"),
                entry("cyansethome.msg.renameHomeOf", "§3The home %s §3has been renamed to %s §3for the player %s"),
                entry("cyansethome.msg.goToHome", "§3You have been teleported to the home %s"),
                entry("cyansethome.msg.getTrustingPlayers", "§3Players that trust you : %s"),
                entry("cyansethome.msg.getTrustedPlayers", "§3Players that you trust : %s"),
                entry("cyansethome.msg.noTrustingPlayer", "§3No player trusts you"),
                entry("cyansethome.msg.noTrustedPlayer", "§3You don't trust any player"),
                entry("cyansethome.msg.playerTrusted", "§3You now trust %s"),
                entry("cyansethome.msg.playerUnTrusted", "§3You no longer trust %s"),
                entry("cyansethome.msg.translationsReloaded", "§3Translations have been reloaded"),
                entry("cyansethome.msg.getHome", "%s §3(%s§3, created on the %s§3)"),

                // SEPARATIONS
                entry("cyansethome.msg.dashSeparation", "§6------------------------------------"),

                // SETS
                entry("cyansethome.msg.set.allowHomes", "§3Toggled §dallowHome §3option %s"),
                entry("cyansethome.msg.set.allowByPass", "§3Toggled ByPass %s"),
                entry("cyansethome.msg.set.maxHomes", "§3The maximum number of homes per player is now %s"),
                entry("cyansethome.msg.set.minOpLvlBypass",
                        "§3The minimum OP level required to bypass permissions is now %s"
                ),
                entry("cyansethome.msg.set.minOpLvlHomes",
                        "§3The minimum OP level required to use the §dhome §3commands is now %s"
                ),

                // HEADERS
                entry("cyansethome.msg.listHomes", "§6CyanSetHome - YOUR HOMES\n"),
                entry("cyansethome.msg.listHomesOf", "§6CyanSetHome - HOMES OF %s\n"),

                // CONFIG_DESC
                entry("cyansethome.msg.getDesc.allowHomes",
                        "§3The §dallowHomes §3option defines whether the home commands are enabled or not"
                ),
                entry("cyansethome.msg.getDesc.allowByPass", "§3The §dallowByPass §3option defines whether admins " +
                        "with the correct OP level can bypass permissions like trust between players"),
                entry("cyansethome.msg.getDesc.maxHomes",
                        "§3The §dmaxHomes §3option defines the maximum number of homes a player can have"
                ),
                entry("cyansethome.msg.getDesc.minOpLvlBypass",
                        "§3The §dminOpLvlBypass §3option defines the OP  level required to bypass permissions"
                ),
                entry("cyansethome.msg.getDesc.minOpLvlHomes",
                        "§3The §dminOpLvlHomes §3option defines the OP level required to run the home commands"
                ),

                // GET_CFG
                entry("cyansethome.msg.getCfg.header", "§6CyanSetHome - OPTIONS\n"),
                entry("cyansethome.msg.getCfg.allowHomes", "§6- §dhome §3commands : %s"),
                entry("cyansethome.msg.getCfg.allowByPass", "§6- §3Allow bypass for OPs : %s"),
                entry("cyansethome.msg.getCfg.maxHomes", "§6- §3Max homes per player : %s"),
                entry("cyansethome.msg.getCfg.minOpLvlBypass", "§6- §3Minimum OP level to use Bypass : %s"),
                entry("cyansethome.msg.getCfg.minOpLvlHomes", "§6- §3Minimum OP level for §dhome §3commands : %s")
        );
    }
}

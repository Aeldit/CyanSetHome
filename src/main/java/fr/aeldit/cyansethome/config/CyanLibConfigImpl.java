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

    public static final IntegerOption MIN_OP_LVL_HOMES = new IntegerOption("minOpLvlHomes", 0, RULES.OP_LEVELS);
    public static final IntegerOption MAX_HOMES = new IntegerOption("maxHomes", 20, RULES.POSITIVE_VALUE);
    public static final IntegerOption MIN_OP_LVL_BYPASS = new IntegerOption("minOpLvlBypass", 4, RULES.OP_LEVELS);

    public static final BooleanOption USE_XP_TO_TP_HOME = new BooleanOption("useXpToTpHome", false);
    public static final BooleanOption XP_USE_POINTS = new BooleanOption("xpUsePoints", false);

    public static final IntegerOption BLOCKS_PER_XP_LEVEL_HOME = new IntegerOption(
            "blockPerXpLevelHome", 300,
            RULES.POSITIVE_VALUE
    );

    @Override
    public Map<String, String> getDefaultTranslations()
    {
        return Map.<String, String>ofEntries(
                // ERRORS
                entry(
                        "error.notOpOrTrusted",
                        "§cThis player doesn't trust you or you don't have the required permission"
                ),
                entry("error.homesDisabled", "§cThe /home command is disabled"),
                entry("error.homeAlreadyExists", "§cThis home already exists"),
                entry("error.homeNotFound", "§cThis home doesn't exist (check the spelling)"),
                entry(
                        "error.homeNotFoundOrExists",
                        "The home doesn't exists or you tried to rename it with the name of an existing home"
                ),
                entry("error.maxHomesReached", "§cYou reached the maximum number of homes §6(%s§6)"),
                entry("error.playerNotOnline", "§cThis player is not online"),
                entry("error.playerNotTrusted", "§cYou don't trust this player"),
                entry("error.playerNotTrusting", "§cThis player doesn't trust you"),
                entry("error.playerAlreadyTrusted", "§cYou already trust this player"),
                entry("error.selfTrust", "§cYou can't trust/untrust yourself"),
                entry("error.noHomes", "§cYou don't have any home"),
                entry("error.noHomesOf", "§cThis player doesn't have any home"),
                entry("error.useSelfHomes", "§cPlease use the normal commands to use your homes"),
                entry(
                        "error.bypassDisabled",
                        "§cThe ByPass option is disabled or you are not OP level 4"
                ),
                entry("error.notEnoughXp", "§cYou don't have enough XP (%s§c %s§c are required)"),
                entry("error.playerNotFound", "§cCouldn't find the player %s"),

                // MESSAGES
                entry("msg.setHome", "§3The home %s §3has been created"),
                entry("msg.setHomeOf", "§3The home %s §3has been created for the player %s"),
                entry("msg.removeHome", "§3The home %s §3has been removed"),
                entry("msg.removeHomeOf", "§3The home %s §3was removed from %s§3's homes"),
                entry("msg.removeAllHomes", "§3All your homes have been removed"),
                entry("msg.removeAllHomesOf", "§3All %s§3's homes were removed"),
                entry("msg.renameHome", "§3The home %s §3has been renamed to %s"),
                entry("msg.renameHomeOf", "§3The home %s §3has been renamed to %s §3for the player %s"),
                entry("msg.goToHome", "§3You have been teleported to the home %s"),
                entry("msg.getTrustingPlayers", "§3Players that trust you : %s"),
                entry("msg.getTrustedPlayers", "§3Players that you trust : %s"),
                entry("msg.noTrustingPlayer", "§3No player trusts you"),
                entry("msg.noTrustedPlayer", "§3You don't trust any player"),
                entry("msg.playerTrusted", "§3You now trust %s"),
                entry("msg.playerUnTrusted", "§3You no longer trust %s"),
                entry("msg.translationsReloaded", "§3Translations have been reloaded"),
                entry("msg.getHome", "%s §3(%s§3, created on the %s§3)"),

                // SETS
                entry("msg.set.allowHomes", "§3Toggled§d allowHome §3option %s"),
                entry("msg.set.allowByPass", "§3Toggled ByPass %s"),
                entry("msg.set.maxHomes", "§3The maximum number of homes per player is now %s"),
                entry(
                        "msg.set.minOpLvlBypass",
                        "§3The minimum OP level required to bypass permissions is now %s"
                ),
                entry(
                        "msg.set.minOpLvlHomes",
                        "§3The minimum OP level required to use the§d home §3commands is now %s"
                ),
                entry("msg.set.useXpToTeleport", "§3Toggled the use of XP to teleport %s"),
                entry("msg.set.xpUsePoints", "§3Toggled the use of XP points instead of XP levels %s"),

                // HEADERS
                entry("msg.listHomes", "§6CyanSetHome - YOUR HOMES\n"),
                entry("msg.listHomesOf", "§6CyanSetHome - HOMES OF %s\n"),

                // CONFIG_DESC
                entry(
                        "msg.getDesc.allowHomes",
                        "§3The§d allowHomes §3option defines whether the home commands are enabled or not"
                ),
                entry("msg.getDesc.allowByPass", "§3The§d allowByPass §3option defines whether admins " +
                        "with the correct OP level can bypass permissions like trust between players"),
                entry(
                        "msg.getDesc.maxHomes",
                        "§3The§d maxHomes §3option defines the maximum number of homes a player can have"
                ),
                entry(
                        "msg.getDesc.minOpLvlBypass",
                        "§3The§d minOpLvlBypass §3option defines the OP  level required to bypass permissions"
                ),
                entry(
                        "msg.getDesc.minOpLvlHomes",
                        "§3The§d minOpLvlHomes §3option defines the OP level required to run the home commands"
                ),
                entry(
                        "msg.getDesc.blockPerXpLevelHome",
                        "§3The number of blocks that will consume 1 XP level for teleportation to a home.\n" +
                                "If set to 300 (default), a player teleporting to a home in a distance <= 300 blocks" +
                                " will lose 1 XP level, 2 XP level for 600 blocks, ..."
                ),
                entry(
                        "msg.getDesc.useXpToTpHome",
                        "§3Whether or not to consume XP on teleportation to a home"
                ),
                entry(
                        "msg.getDesc.xpUsePoints",
                        "§3The§e xpUsePoints §3option defines whether the necessary XP will be in points or in " +
                                "levels"
                ),

                // GET_CFG
                entry("msg.getCfg.header", "§6CyanSetHome - OPTIONS\n"),
                entry("msg.getCfg.allowHomes", "§6-§d home §3commands : %s"),
                entry("msg.getCfg.allowByPass", "§6- §3Allow bypass for OPs : %s"),
                entry("msg.getCfg.maxHomes", "§6- §3Max homes per player : %s"),
                entry("msg.getCfg.minOpLvlBypass", "§6- §3Minimum OP level to use Bypass : %s"),
                entry("msg.getCfg.minOpLvlHomes", "§6- §3Minimum OP level for§d home §3commands : %s"),
                entry("msg.getCfg.useXpToTpHome", "§6- §3Use XP to tp to a home : %s"),
                entry("msg.getCfg.xpUsePoints", "§6- §3Use XP points instead of XP levels : %s"),
                entry("msg.getCfg.blockPerXpLevelHome", "§6- §3Block per XP point for home tp : %s")
        );
    }
}

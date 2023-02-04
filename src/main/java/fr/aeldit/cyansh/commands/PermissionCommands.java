package fr.aeldit.cyansh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.aeldit.cyansh.commands.argumentTypes.ArgumentSuggestion;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static fr.aeldit.cyanlib.util.ChatUtil.sendPlayerMessage;
import static fr.aeldit.cyansh.util.Utils.*;

public class PermissionCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("hometrust")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getOnlinePlayersName(builder4, context4.getSource()))
                        .executes(PermissionCommands::trustPlayer)
                )
        );

        dispatcher.register(CommandManager.literal("homeuntrust")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getTrustedPlayersName(builder4, context4.getSource()))
                        .executes(PermissionCommands::untrustPlayer)
                )
        );

        dispatcher.register(CommandManager.literal("gettrustingplayers")
                .executes(PermissionCommands::getTrustingPlayers)
        );
        dispatcher.register(CommandManager.literal("gettrustedplayers")
                .executes(PermissionCommands::getTrustedPlayers)
        );
    }

    public static int trustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        String playerName = StringArgumentType.getString(context, "player");

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else if (source.getServer().getPlayerManager().getPlayer(playerName) == null)
        {
            sendPlayerMessage(player,
                    getErrorTraduction("playerNotOnline"),
                    playerName,
                    "cyansh.error.playerNotOnline",
                    CyanSHMidnightConfig.errorToActionBar,
                    CyanSHMidnightConfig.useTranslations
            );
        } else
        {
            UUID playerUUID = Objects.requireNonNull(source.getServer().getPlayerManager().getPlayer(playerName)).getUuid();
            String trustingPlayer = player.getUuidAsString() + "_" + player.getName().getString();
            String trustedPlayer = playerUUID + "_" + playerName;

            checkOrCreateTrustFile();
            Properties properties = new Properties();
            try
            {
                properties.load(new FileInputStream(trustPath.toFile()));

                if (!properties.containsKey(trustingPlayer))
                {
                    properties.put(trustingPlayer, trustedPlayer);
                } else
                {
                    if (!properties.get(trustingPlayer).toString().contains(trustedPlayer))
                    {
                        properties.put(trustingPlayer, "%s %s".formatted(properties.get(trustingPlayer), trustedPlayer));

                        properties.store(new FileOutputStream(trustPath.toFile()), null);

                        sendPlayerMessage(player,
                                getCmdFeedbackTraduction("playerTrusted"),
                                null,
                                "cyansh.message.playerTrusted",
                                CyanSHMidnightConfig.msgToActionBar,
                                CyanSHMidnightConfig.useTranslations
                        );
                    } else if (trustedPlayer.equals(trustingPlayer))
                    {
                        sendPlayerMessage(player,
                                getErrorTraduction("selfTrust"),
                                playerName,
                                "cyansh.error.selfTrust",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useTranslations
                        );
                    } else
                    {
                        sendPlayerMessage(player,
                                getErrorTraduction("playerAlreadyTrusted"),
                                playerName,
                                "cyansh.error.playerAlreadyTrusted",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useTranslations
                        );
                    }
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int untrustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        String playerName = StringArgumentType.getString(context, "player");

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            String trustingPlayer = player.getUuidAsString() + "_" + player.getName().getString();
            String trustedPlayer = "";
            List<String> tmp;

            checkOrCreateTrustFile();
            Properties properties = new Properties();
            try
            {
                properties.load(new FileInputStream(trustPath.toFile()));
                String UUIDSearch = (String) properties.get(trustingPlayer);
                UUIDSearch = UUIDSearch.replace("[", "").replace("]", "").replace(",", "");

                // Used to obtain the UUID of the player, even if it is not online
                for (String s : UUIDSearch.split(" "))
                {
                    String[] tmpS = s.split("_");
                    String tmpUUID = tmpS[0];
                    String tmpName = tmpS[1];
                    if (tmpName.equals(playerName))
                    {
                        trustedPlayer = tmpUUID.concat("_").concat(playerName);
                        break;
                    }
                }

                if (properties.get(trustingPlayer).toString().contains(trustedPlayer))
                {
                    tmp = List.of(properties.get(trustingPlayer).toString().split(" "));
                    if (tmp.contains(trustedPlayer))
                    {
                        if (tmp.size() == 1 && Objects.equals(tmp.get(0), trustedPlayer))
                        {
                            properties.remove(trustingPlayer);
                            properties.store(new FileOutputStream(trustPath.toFile()), null);
                        } else
                        {
                            String replace = tmp.toString()
                                    .replace("[", "")
                                    .replace(",", "")
                                    .replace("]", "");

                            if (tmp.indexOf(trustedPlayer) == tmp.size() - 1)
                            {
                                properties.put(trustingPlayer, "%s".formatted(replace.replace(" " + trustedPlayer, "")));
                            } else
                            {
                                properties.put(trustingPlayer, "%s".formatted(replace.replace(trustedPlayer + " ", "")));
                            }
                            properties.store(new FileOutputStream(trustPath.toFile()), null);
                        }
                    } else
                    {
                        sendPlayerMessage(player,
                                getErrorTraduction("playerNotTrusted"),
                                null,
                                "cyansh.error.playerNotTrusted",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useTranslations
                        );
                    }
                } else
                {
                    sendPlayerMessage(player,
                            getErrorTraduction("playerNotTrusted"),
                            null,
                            "cyansh.error.playerNotTrusted",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int getTrustingPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            checkOrCreateTrustFile();
            Properties properties = new Properties();
            try
            {
                properties.load(new FileInputStream(trustPath.toFile()));
                String trustedPlayer = player.getUuidAsString() + "_" + player.getName().getString();
                String trustingPlayers = "";

                for (String p : properties.stringPropertyNames())
                {
                    if (properties.get(p).toString().contains(trustedPlayer))
                    {
                        trustingPlayers = trustingPlayers.concat(" ").concat(p.split("_")[1]);
                    }
                }

                if (trustingPlayers.equals(""))
                {
                    sendPlayerMessage(player,
                            getCmdFeedbackTraduction("noTrustingPlayer"),
                            null,
                            "cyansh.message.noTrustingPlayer",
                            CyanSHMidnightConfig.msgToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                } else
                {
                    sendPlayerMessage(player,
                            getCmdFeedbackTraduction("getTrustingPlayers"),
                            trustingPlayers,
                            "cyansh.message.getTrustingPlayers",
                            false,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int getTrustedPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null)
        {
            source.getServer().sendMessage(Text.of(getErrorTraduction("playerOnlyCmd")));
        } else
        {
            checkOrCreateTrustFile();
            Properties properties = new Properties();
            try
            {
                properties.load(new FileInputStream(trustPath.toFile()));
                String trustingPlayer = player.getUuidAsString() + "_" + player.getName().getString();
                String trustedPlayers = "";

                if (properties.containsKey(trustingPlayer))
                {
                    for (String p : properties.get(trustingPlayer).toString().split(" "))
                    {
                        player.sendMessage(Text.of(p));
                        trustedPlayers = trustedPlayers.concat(" ").concat(p.split("_")[1]);
                    }

                    if (trustedPlayers.equals(""))
                    {
                        sendPlayerMessage(player,
                                getCmdFeedbackTraduction("noTrustedPlayer"),
                                trustedPlayers,
                                "cyansh.message.noTrustedPlayer",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useTranslations
                        );
                    } else
                    {
                        sendPlayerMessage(player,
                                getCmdFeedbackTraduction("getTrustedPlayers"),
                                trustedPlayers,
                                "cyansh.message.getTrustedPlayers",
                                false,
                                CyanSHMidnightConfig.useTranslations
                        );
                    }
                } else
                {
                    sendPlayerMessage(player,
                            getCmdFeedbackTraduction("noTrustedPlayer"),
                            trustedPlayers,
                            "cyansh.message.noTrustedPlayer",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}

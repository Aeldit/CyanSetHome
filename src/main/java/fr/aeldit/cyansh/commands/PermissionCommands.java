package fr.aeldit.cyansh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.aeldit.cyansh.commands.argumentTypes.ArgumentSuggestion;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import fr.aeldit.cyansh.util.Utils;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static fr.aeldit.cyanlib.util.ChatUtil.sendPlayerMessage;
import static fr.aeldit.cyansh.util.Utils.*;

public class PermissionCommands
{
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(CommandManager.literal("hometrust")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests((context4, builder4) -> ArgumentSuggestion.getAllPlayersName(builder4, context4.getSource()))
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
            return 0;
        } else if (source.getServer().getPlayerManager().getPlayer(playerName) == null)
        {
            sendPlayerMessage(player,
                    getErrorTraduction("playerNotOnline"),
                    playerName,
                    "cyansh.error.playerNotOnline",
                    CyanSHMidnightConfig.errorToActionBar,
                    CyanSHMidnightConfig.useTranslations
            );
            return 0;
        } else
        {
            UUID playerUUID = Objects.requireNonNull(source.getServer().getPlayerManager().getPlayer(playerName)).getUuid();
            Path trustPath = Utils.trustPath;
            Properties properties = new Properties();
            String trustingPlayerKey = player.getUuidAsString() + "_" + player.getName().getString();
            String trustedPlayerKey = playerUUID + "_" + playerName;

            checkOrCreateTrustFile();

            try
            {
                properties.load(new FileInputStream(trustPath.toFile()));

                if (!properties.containsKey(trustedPlayerKey))
                {
                    properties.put(trustedPlayerKey, trustingPlayerKey);
                } else
                {
                    properties.put(trustedPlayerKey, "%s %s_%s".formatted(properties.get(trustedPlayerKey), trustingPlayerKey, source.getServer().getPlayerManager().getPlayer(trustedPlayerKey)));
                }

                properties.store(new FileOutputStream(trustPath.toFile()), null);
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
            return 0;
        } else
        {
            UUID playerUUID = Objects.requireNonNull(source.getServer().getPlayerManager().getPlayer(playerName)).getUuid();
            Path trustPath = Utils.trustPath;
            Properties properties = new Properties();
            String trustingPlayerKey = player.getUuidAsString() + "_" + player.getName().getString();
            String trustedPlayerKey = playerUUID + "_" + playerName;
            String tmp;

            checkOrCreateTrustFile();

            try
            {
                properties.load(new FileInputStream(trustPath.toFile()));

                if (properties.containsKey(trustedPlayerKey))
                {
                    tmp = Arrays.toString(properties.get(trustedPlayerKey).toString().split(" "));
                    if (tmp.contains(trustingPlayerKey))
                    {
                        tmp = tmp.replace(trustingPlayerKey, "");
                        properties.put(trustedPlayerKey, "%s".formatted(tmp));
                        properties.store(new FileOutputStream(trustPath.toFile()), null);
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
            return 0;
        } else
        {
            Path trustPath = Utils.trustPath;
            Properties properties = new Properties();

            checkOrCreateTrustFile();

            try
            {
                properties.load(new FileInputStream(trustPath.toFile()));
                String trustingPlayersString = "";
                String trustedPlayerKey = player.getUuidAsString() + "_" + player.getName().getString();

                if (properties.containsKey(trustedPlayerKey))
                {
                    for (String p : properties.get(trustedPlayerKey).toString().split(" "))
                    {
                        trustingPlayersString = trustingPlayersString.concat(" ").concat(p.split("_")[1]);
                    }

                    sendPlayerMessage(player,
                            getCmdFeedbackTraduction("getTrustingPlayers"),
                            trustingPlayersString,
                            "cyansh.message.getTrustingPlayers",
                            false,
                            CyanSHMidnightConfig.useTranslations
                    );
                } else
                {
                    sendPlayerMessage(player,
                            getCmdFeedbackTraduction("noTrustingPlayer"),
                            null,
                            "cyansh.message.noTrustingPlayer",
                            CyanSHMidnightConfig.msgToActionBar,
                            CyanSHMidnightConfig.useTranslations
                    );
                    return 0;
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
            return 0;
        } else
        {
            Path trustPath = Utils.trustPath;
            Properties properties = new Properties();

            checkOrCreateTrustFile();

            try
            {
                properties.load(new FileInputStream(trustPath.toFile()));
                String trustedPlayersString = "";
                String[] tmp;
                List<String> stringList;

                for (String p : properties.stringPropertyNames())
                {
                    stringList = List.of(properties.get(p).toString().split(" "));

                    for (String str : stringList)
                    {
                        tmp = str.split("_");
                        if (tmp[0].equals(player.getUuidAsString()))
                        {
                            trustedPlayersString = trustedPlayersString.concat(" ").concat(p.split("_")[1]);
                            break;
                        }
                    }
                }

                sendPlayerMessage(player,
                        getCmdFeedbackTraduction("getTrustedPlayers"),
                        trustedPlayersString,
                        "cyansh.message.getTrustedPlayers",
                        false,
                        CyanSHMidnightConfig.useTranslations
                );
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}

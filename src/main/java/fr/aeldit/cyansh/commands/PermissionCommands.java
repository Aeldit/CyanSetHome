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
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static fr.aeldit.cyanlib.util.ChatUtils.sendPlayerMessage;
import static fr.aeldit.cyanlib.util.Constants.ERROR;
import static fr.aeldit.cyansh.util.GsonUtils.*;
import static fr.aeldit.cyansh.util.HomeUtils.TRUST_PATH;
import static fr.aeldit.cyansh.util.Utils.CyanLibUtils;
import static fr.aeldit.cyansh.util.Utils.CyanSHLanguageUtils;

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

    /**
     * Called by the command {@code /hometrust <player>}
     * <p>
     * Used to define which players can use the homes of the trusting player
     */
    public static int trustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        String playerName = StringArgumentType.getString(context, "player");

        if (CyanLibUtils.isPlayer(source))
        {
            if (source.getServer().getPlayerManager().getPlayer(playerName) == null)
            {
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation(ERROR + "playerNotOnline"),
                        "cyansh.error.playerNotOnline",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useCustomTranslations,
                        playerName
                );
            }
            else
            {
                String trustingPlayer = player.getUuidAsString() + "_" + player.getName().getString();
                String trustedPlayer = source.getServer().getPlayerManager().getPlayer(playerName).getUuid() + "_" + playerName;

                if (!trustedPlayer.equals(trustingPlayer))
                {
                    try
                    {
                        if (!Files.exists(TRUST_PATH))
                        {
                            Files.createFile(TRUST_PATH);

                            Map<String, ArrayList<String>> gsonTrustingPlayers = new HashMap<>();
                            gsonTrustingPlayers.put(trustingPlayer, new ArrayList<>(Collections.singleton(trustedPlayer)));

                            writeGson(TRUST_PATH, gsonTrustingPlayers);

                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("playerTrusted"),
                                    "cyansh.message.playerTrusted",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useCustomTranslations,
                                    Formatting.AQUA + playerName
                            );
                        }
                        else if (Files.readAllLines(TRUST_PATH).isEmpty())
                        {
                            Map<String, ArrayList<String>> gsonTrustingPlayers = new HashMap<>();
                            gsonTrustingPlayers.put(trustingPlayer, new ArrayList<>(Collections.singleton(trustedPlayer)));

                            writeGson(TRUST_PATH, gsonTrustingPlayers);

                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation("playerTrusted"),
                                    "cyansh.message.playerTrusted",
                                    CyanSHMidnightConfig.msgToActionBar,
                                    CyanSHMidnightConfig.useCustomTranslations,
                                    Formatting.AQUA + playerName
                            );
                        }
                        else
                        {
                            Map<String, ArrayList<String>> gsonTrustingPlayers = readTrustFile();

                            if (!gsonTrustingPlayers.containsKey(trustingPlayer))
                            {
                                gsonTrustingPlayers.put(trustingPlayer, new ArrayList<>(Collections.singleton(trustedPlayer)));

                                writeGson(TRUST_PATH, gsonTrustingPlayers);

                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation("playerTrusted"),
                                        "cyansh.message.playerTrusted",
                                        CyanSHMidnightConfig.msgToActionBar,
                                        CyanSHMidnightConfig.useCustomTranslations,
                                        Formatting.AQUA + playerName
                                );
                            }
                            else if (!gsonTrustingPlayers.get(trustingPlayer).contains(trustedPlayer))
                            {
                                gsonTrustingPlayers.get(trustingPlayer).add(trustedPlayer);

                                writeGson(TRUST_PATH, gsonTrustingPlayers);

                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation("playerTrusted"),
                                        "cyansh.message.playerTrusted",
                                        CyanSHMidnightConfig.msgToActionBar,
                                        CyanSHMidnightConfig.useCustomTranslations,
                                        Formatting.AQUA + playerName
                                );
                            }
                            else
                            {
                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation(ERROR + "playerAlreadyTrusted"),
                                        "cyansh.error.playerAlreadyTrusted",
                                        CyanSHMidnightConfig.errorToActionBar,
                                        CyanSHMidnightConfig.useCustomTranslations
                                );
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "selfTrust"),
                            "cyansh.error.selfTrust",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
                    );
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /homeuntrust <player>}
     * <p>
     * Used to remove a player from the trust list
     */
    public static int untrustPlayer(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            String untrustedPlayerName = StringArgumentType.getString(context, "player");
            if (!player.getName().getString().equals(untrustedPlayerName))
            {
                String trustingPlayer = player.getUuidAsString() + "_" + player.getName().getString();

                if (Files.exists(TRUST_PATH))
                {
                    Map<String, ArrayList<String>> gsonTrustingPlayers = readTrustFile();

                    if (!gsonTrustingPlayers.isEmpty())
                    {
                        if (gsonTrustingPlayers.containsKey(trustingPlayer))
                        {
                            for (String playerKey : gsonTrustingPlayers.get(trustingPlayer))
                            {
                                if (playerKey.split("_")[1].equals(untrustedPlayerName))
                                {
                                    gsonTrustingPlayers.get(trustingPlayer).remove(playerKey);

                                    if (gsonTrustingPlayers.get(trustingPlayer).isEmpty())
                                    {
                                        gsonTrustingPlayers.remove(trustingPlayer);
                                    }

                                    writeGsonOrDeleteFile(TRUST_PATH, gsonTrustingPlayers);

                                    sendPlayerMessage(player,
                                            CyanSHLanguageUtils.getTranslation("playerUnTrusted"),
                                            "cyansh.message.playerUnTrusted",
                                            CyanSHMidnightConfig.msgToActionBar,
                                            CyanSHMidnightConfig.useCustomTranslations,
                                            Formatting.AQUA + untrustedPlayerName
                                    );
                                    break;
                                }
                            }
                        }
                        else
                        {
                            sendPlayerMessage(player,
                                    CyanSHLanguageUtils.getTranslation(ERROR + "playerNotTrusted"),
                                    "cyansh.error.playerNotTrusted",
                                    CyanSHMidnightConfig.errorToActionBar,
                                    CyanSHMidnightConfig.useCustomTranslations
                            );
                        }
                    }
                    else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation(ERROR + "playerNotTrusted"),
                                "cyansh.error.playerNotTrusted",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useCustomTranslations
                        );
                    }
                }
                else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation(ERROR + "playerNotTrusted"),
                            "cyansh.error.playerNotTrusted",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
                    );
                }
            }
            else
            {
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation(ERROR + "selfTrust"),
                        "cyansh.error.selfTrust",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useCustomTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /gettrustingplayers}
     * <p>
     * Send a message to the player with all the players that trust her/him
     */
    public static int getTrustingPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (Files.exists(TRUST_PATH))
            {
                Map<String, ArrayList<String>> gsonTrustingPlayers = readTrustFile();

                ArrayList<String> trustingPlayers = new ArrayList<>();

                for (Map.Entry<String, ArrayList<String>> entry : gsonTrustingPlayers.entrySet())
                {
                    if (entry.getValue().contains(player.getUuidAsString() + "_" + player.getName().getString()))
                    {
                        trustingPlayers.add(entry.getKey().split("_")[1]);
                    }
                }

                if (!trustingPlayers.isEmpty())
                {
                    String players = "";

                    for (int i = 0; i < trustingPlayers.size(); i++)
                    {
                        if (trustingPlayers.size() == 1)
                        {
                            players = players.concat("%s".formatted(trustingPlayers.get(i)));
                        }
                        else if (i == trustingPlayers.size() - 1)
                        {
                            players = players.concat(", %s".formatted(trustingPlayers.get(i)));
                        }
                        else
                        {
                            players = players.concat(", %s,".formatted(trustingPlayers.get(i)));
                        }
                    }

                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation("getTrustingPlayers"),
                            "cyansh.message.getTrustingPlayers",
                            false,
                            CyanSHMidnightConfig.useCustomTranslations,
                            Formatting.AQUA + players
                    );
                }
                else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation("noTrustingPlayer"),
                            "cyansh.message.noTrustingPlayer",
                            CyanSHMidnightConfig.msgToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
                    );
                }
            }
            else
            {
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation("noTrustingPlayer"),
                        "cyansh.message.noTrustingPlayer",
                        CyanSHMidnightConfig.msgToActionBar,
                        CyanSHMidnightConfig.useCustomTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /gettrustedplayers}
     * <p>
     * Send a message to the player with all the players that she/he trusts
     */
    public static int getTrustedPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (CyanLibUtils.isPlayer(context.getSource()))
        {
            if (Files.exists(TRUST_PATH))
            {
                Map<String, ArrayList<String>> gsonTrustingPlayers = readTrustFile();

                String trustingPlayer = player.getUuidAsString() + "_" + player.getName().getString();

                if (gsonTrustingPlayers.containsKey(trustingPlayer))
                {
                    if (gsonTrustingPlayers.get(trustingPlayer).size() != 0)
                    {
                        String players = "";

                        for (int i = 0; i < gsonTrustingPlayers.get(trustingPlayer).size(); i++)
                        {
                            if (gsonTrustingPlayers.get(trustingPlayer).size() == 1)
                            {
                                players = players.concat("%s".formatted(gsonTrustingPlayers.get(trustingPlayer).get(i).split("_")[1]));
                            }
                            else if (i == gsonTrustingPlayers.get(trustingPlayer).size() - 1)
                            {
                                players = players.concat(", %s".formatted(gsonTrustingPlayers.get(trustingPlayer).get(i).split("_")[1]));
                            }
                            else
                            {
                                players = players.concat(", %s,".formatted(gsonTrustingPlayers.get(trustingPlayer).get(i).split("_")[1]));
                            }
                        }

                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation("getTrustedPlayers"),
                                "cyansh.message.getTrustedPlayers",
                                false,
                                CyanSHMidnightConfig.useCustomTranslations,
                                Formatting.AQUA + players
                        );
                    }
                    else
                    {
                        sendPlayerMessage(player,
                                CyanSHLanguageUtils.getTranslation("noTrustedPlayer"),
                                "cyansh.message.noTrustedPlayer",
                                CyanSHMidnightConfig.errorToActionBar,
                                CyanSHMidnightConfig.useCustomTranslations
                        );
                    }
                }
                else
                {
                    sendPlayerMessage(player,
                            CyanSHLanguageUtils.getTranslation("noTrustedPlayer"),
                            "cyansh.message.noTrustedPlayer",
                            CyanSHMidnightConfig.errorToActionBar,
                            CyanSHMidnightConfig.useCustomTranslations
                    );
                }
            }
            else
            {
                sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation("noTrustedPlayer"),
                        "cyansh.message.noTrustedPlayer",
                        CyanSHMidnightConfig.errorToActionBar,
                        CyanSHMidnightConfig.useCustomTranslations
                );
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}

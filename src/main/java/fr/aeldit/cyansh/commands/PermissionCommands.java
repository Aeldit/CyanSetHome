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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

import static fr.aeldit.cyanlib.util.ChatUtils.sendPlayerMessage;
import static fr.aeldit.cyanlib.util.Constants.ERROR;
import static fr.aeldit.cyansh.util.HomeUtils.trustPath;
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
                UUID playerUUID = Objects.requireNonNull(source.getServer().getPlayerManager().getPlayer(playerName)).getUuid();
                String trustingPlayer = player.getUuidAsString() + "_" + player.getName().getString();
                String trustedPlayer = playerUUID + "_" + playerName;

                if (!trustedPlayer.equals(trustingPlayer))
                {
                    checkOrCreateFile(trustPath);
                    try
                    {
                        Gson gsonReader = new Gson();
                        Reader reader = Files.newBufferedReader(trustPath);
                        Type mapType = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
                        Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, mapType);
                        reader.close();

                        Map<String, ArrayList<String>> playersTrustObjects = new HashMap<>();

                        if (gsonTrustingPlayers == null)
                        {
                            playersTrustObjects.put(trustingPlayer, new ArrayList<>(Collections.singleton(trustedPlayer)));
                            Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                            Writer writer = Files.newBufferedWriter(trustPath);
                            gsonWriter.toJson(playersTrustObjects, writer);
                            writer.close();

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
                            playersTrustObjects.putAll(gsonTrustingPlayers);

                            if (!playersTrustObjects.containsKey(trustingPlayer))
                            {
                                playersTrustObjects.put(trustingPlayer, new ArrayList<>(Collections.singleton(trustedPlayer)));

                                Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                                Writer writer = Files.newBufferedWriter(trustPath);
                                gsonWriter.toJson(playersTrustObjects, writer);
                                writer.close();

                                sendPlayerMessage(player,
                                        CyanSHLanguageUtils.getTranslation("playerTrusted"),
                                        "cyansh.message.playerTrusted",
                                        CyanSHMidnightConfig.msgToActionBar,
                                        CyanSHMidnightConfig.useCustomTranslations,
                                        Formatting.AQUA + playerName
                                );
                            }
                            else if (!playersTrustObjects.get(trustingPlayer).contains(trustedPlayer))
                            {
                                playersTrustObjects.get(trustingPlayer).add(trustedPlayer);

                                Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();
                                Writer writer = Files.newBufferedWriter(trustPath);
                                gsonWriter.toJson(playersTrustObjects, writer);
                                writer.close();

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
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            String untrustedPlayerName = StringArgumentType.getString(context, "player");
            if (!player.getName().getString().equals(untrustedPlayerName))
            {
                try
                {
                    String trustingPlayer = player.getUuidAsString() + "_" + player.getName().getString();

                    if (Files.exists(trustPath))
                    {
                        Gson gsonReader = new Gson();
                        Reader reader = Files.newBufferedReader(trustPath);
                        Type mapType = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
                        Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, mapType);
                        reader.close();

                        if (!gsonTrustingPlayers.isEmpty())
                        {
                            Map<String, ArrayList<String>> trustedPlayers = new HashMap<>(gsonTrustingPlayers);

                            if (gsonTrustingPlayers.containsKey(trustingPlayer))
                            {
                                for (String playerKey : gsonTrustingPlayers.get(trustingPlayer))
                                {
                                    if (Objects.equals(playerKey.split("_")[1], untrustedPlayerName))
                                    {
                                        trustedPlayers.get(trustingPlayer).remove(playerKey);

                                        if (trustedPlayers.get(trustingPlayer).isEmpty())
                                        {
                                            trustedPlayers.remove(trustingPlayer);
                                        }

                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        Writer w = Files.newBufferedWriter(trustPath);
                                        gson.toJson(trustedPlayers, w);
                                        w.close();

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
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Called by the command {@code /gettrustingplayers}
     * <p>
     * Send a message to the player with all the players that trust her/him
     */
    public static int getTrustingPlayers(@NotNull CommandContext<ServerCommandSource> context)
    {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            checkOrCreateFile(trustPath);
            try
            {
                Gson gsonReader = new Gson();
                Reader reader = Files.newBufferedReader(trustPath);
                Type mapType = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
                Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, mapType);
                reader.close();

                String trustedPlayer = player.getUuidAsString() + "_" + player.getName().getString();
                ArrayList<String> trustingPlayers = new ArrayList<>();

                for (Map.Entry<String, ArrayList<String>> entry : gsonTrustingPlayers.entrySet())
                {
                    if (entry.getValue().contains(trustedPlayer))
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
                            players = players.concat(" %s".formatted(trustingPlayers.get(i)));
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
            catch (IOException e)
            {
                throw new RuntimeException(e);
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
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (CyanLibUtils.isPlayer(source))
        {
            checkOrCreateFile(trustPath);
            try
            {
                Gson gsonReader = new Gson();
                Reader reader = Files.newBufferedReader(trustPath);
                Type mapType = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
                Map<String, ArrayList<String>> gsonTrustingPlayers = gsonReader.fromJson(reader, mapType);
                reader.close();

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
                                players = players.concat(" %s".formatted(gsonTrustingPlayers.get(trustingPlayer).get(i).split("_")[1]));
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
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}

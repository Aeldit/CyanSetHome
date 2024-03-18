/*
 * Copyright (c) 2023-2024  -  Made by Aeldit
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

package fr.aeldit.cyansh.util;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;

import static fr.aeldit.cyansh.CyanSHCore.HomesObj;
import static fr.aeldit.cyansh.CyanSHCore.TrustsObj;

public class EventUtils
{
    /**
     * Called on {@code ServerPlayConnectionEvents.JOIN} event
     * Renames the trust and homes files if the players username corresponding to the UUID changed.
     * (ex: UUID_Username -> UUID_updatedUsername)
     *
     * @param handler The ServerPlayNetworkHandler
     */
    public static void renameFileIfUsernameChanged(@NotNull ServerPlayNetworkHandler handler)
    {
        String playerUUID = handler.getPlayer().getUuidAsString();
        String playerName = handler.getPlayer().getName().getString();
        String playerKey = playerUUID + " " + playerName;

        HomesObj.renameChangedUsernames(playerKey, playerUUID, playerName);
        TrustsObj.renameChangedUsernames(playerKey, playerUUID, playerName);
    }
}

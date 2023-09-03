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

package fr.aeldit.cyansh;

import fr.aeldit.cyansh.commands.HomeCommands;
import fr.aeldit.cyansh.commands.HomeOfCommands;
import fr.aeldit.cyansh.commands.PermissionCommands;
import fr.aeldit.cyansh.config.CyanSHConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static fr.aeldit.cyansh.util.EventUtils.renameFileIfUsernameChanged;
import static fr.aeldit.cyansh.util.Utils.*;

public class CyanSHClientCore implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        CYANSH_LIB_UTILS.init(CYANSH_MODID, CYANSH_OPTIONS_STORAGE, CyanSHConfig.class);

        // Join World Event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            renameFileIfUsernameChanged(handler);
            HomesObj.readClient(server.getSaveProperties().getLevelName());
            TrustsObj.readClient(server.getSaveProperties().getLevelName());
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            CYANSH_CONFIG_COMMANDS.register(dispatcher);
            HomeCommands.register(dispatcher);
            HomeOfCommands.register(dispatcher);
            PermissionCommands.register(dispatcher);
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> removeEmptyModDir());

        CYANSH_LOGGER.info("[CyanSetHome] Successfully initialized");
    }
}

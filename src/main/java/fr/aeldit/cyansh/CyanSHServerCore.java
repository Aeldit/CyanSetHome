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

import eu.midnightdust.lib.config.MidnightConfig;
import fr.aeldit.cyansh.commands.ConfigCommands;
import fr.aeldit.cyansh.commands.HomeCommands;
import fr.aeldit.cyansh.commands.HomeOfCommands;
import fr.aeldit.cyansh.commands.PermissionCommands;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import fr.aeldit.cyansh.homes.Homes;
import fr.aeldit.cyansh.homes.Trusts;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static fr.aeldit.cyansh.config.CyanSHMidnightConfig.generateAllOptionsMap;
import static fr.aeldit.cyansh.util.EventUtils.renameFileIfUsernameChanged;
import static fr.aeldit.cyansh.util.EventUtils.transferPropertiesToGson;
import static fr.aeldit.cyansh.util.Utils.*;

public class CyanSHServerCore implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        MidnightConfig.init(MODID, CyanSHMidnightConfig.class);
        LOGGER.info("[CyanSetHome] Successfully initialized config");

        HomesObj = new Homes();
        HomesObj.readServer();
        TrustsObj = new Trusts();
        TrustsObj.readServer();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            ConfigCommands.register(dispatcher);
            HomeCommands.register(dispatcher);
            HomeOfCommands.register(dispatcher);
            PermissionCommands.register(dispatcher);
        });
        LOGGER.info("[CyanSetHome] Successfully initialized commands");

        if (CyanSHMidnightConfig.useCustomTranslations)
        {
            CyanSHLanguageUtils.loadLanguage(getDefaultTranslations());
        }

        generateAllOptionsMap();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> transferPropertiesToGson());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> renameFileIfUsernameChanged(handler));

        LOGGER.info("[CyanSetHome] Successfully completed initialization");
    }
}

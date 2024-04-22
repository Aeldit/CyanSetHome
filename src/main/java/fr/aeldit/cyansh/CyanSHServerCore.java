package fr.aeldit.cyansh;

import fr.aeldit.cyanlib.lib.commands.CyanLibConfigCommands;
import fr.aeldit.cyansh.commands.HomeCommands;
import fr.aeldit.cyansh.commands.HomeOfCommands;
import fr.aeldit.cyansh.commands.PermissionCommands;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static fr.aeldit.cyansh.CyanSHCore.*;
import static fr.aeldit.cyansh.util.EventUtils.renameFileIfUsernameChanged;

public class CyanSHServerCore implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        CYANSH_LIB_UTILS.init(MODID, CYANSH_OPTS_STORAGE);

        HomesObj.readServer();
        TrustsObj.readServer();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> renameFileIfUsernameChanged(handler));

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            new CyanLibConfigCommands(MODID, CYANSH_LIB_UTILS).register(dispatcher);
            HomeCommands.register(dispatcher);
            HomeOfCommands.register(dispatcher);
            PermissionCommands.register(dispatcher);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> removeEmptyModDir());

        CYANSH_LOGGER.info("[CyanSetHome] Successfully initialized");
    }
}

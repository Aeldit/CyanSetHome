package fr.aeldit.cyansethome;

import fr.aeldit.cyanlib.lib.commands.CyanLibConfigCommands;
import fr.aeldit.cyansethome.commands.HomeCommands;
import fr.aeldit.cyansethome.commands.HomeOfCommands;
import fr.aeldit.cyansethome.commands.PermissionCommands;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static fr.aeldit.cyansethome.CyanSHCore.*;
import static fr.aeldit.cyansethome.util.EventUtils.renameFileIfUsernameChanged;

public class CyanSHServerCore implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        HOMES.readServer();
        TRUSTS.readServer();

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

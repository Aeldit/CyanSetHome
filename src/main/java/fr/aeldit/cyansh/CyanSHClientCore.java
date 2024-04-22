package fr.aeldit.cyansh;

import fr.aeldit.cyanlib.lib.commands.CyanLibConfigCommands;
import fr.aeldit.cyansh.commands.HomeCommands;
import fr.aeldit.cyansh.commands.HomeOfCommands;
import fr.aeldit.cyansh.commands.PermissionCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static fr.aeldit.cyansh.CyanSHCore.*;
import static fr.aeldit.cyansh.util.EventUtils.renameFileIfUsernameChanged;

public class CyanSHClientCore implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        CYANSH_LIB_UTILS.init(MODID, CYANSH_OPTS_STORAGE);

        // Join World Event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            renameFileIfUsernameChanged(handler);
            HomesObj.readClient(server.getSaveProperties().getLevelName());
            TrustsObj.readClient();
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            new CyanLibConfigCommands(MODID, CYANSH_LIB_UTILS).register(dispatcher);
            HomeCommands.register(dispatcher);
            HomeOfCommands.register(dispatcher);
            PermissionCommands.register(dispatcher);
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> removeEmptyModDir());

        CYANSH_LOGGER.info("[CyanSetHome] Successfully initialized");
    }
}

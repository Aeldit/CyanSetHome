package fr.aeldit.cyansethome;

import fr.aeldit.cyanlib.lib.commands.CyanLibConfigCommands;
import fr.aeldit.cyanlib.lib.events.MissingLivingEntityEvent;
import fr.aeldit.cyansethome.commands.HomeCommands;
import fr.aeldit.cyansethome.commands.HomeOfCommands;
import fr.aeldit.cyansethome.commands.PermissionCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;

import static fr.aeldit.cyansethome.CombatTracking.removePlayerOnPlayerQuit;
import static fr.aeldit.cyansethome.CyanSHCore.*;
import static fr.aeldit.cyansethome.util.EventUtils.renameFileIfUsernameChanged;

public class CyanSHClientCore implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // Join World Event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            renameFileIfUsernameChanged(handler);
            HOMES.readClient(server.getSaveProperties().getLevelName());
            TRUSTS.readClient();
        });

        //? if >1.20.6 {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (entity.isPlayer())
            {
                CombatTracking.addEntry(entity.getName().getString(), System.currentTimeMillis());
                Entity attacker = source.getAttacker();
                if (attacker != null)
                {
                    CombatTracking.addEntry(attacker.getName().getString(), System.currentTimeMillis());
                }
            }
        });
        //?} else {
        /*MissingLivingEntityEvent.AFTER_DAMAGE.register((entity, source, amount) -> {
            if (entity.isPlayer())
            {
                CombatTracking.addEntry(entity.getName().getString(), System.currentTimeMillis());
                Entity attacker = source.getAttacker();
                if (attacker != null)
                {
                    CombatTracking.addEntry(attacker.getName().getString(), System.currentTimeMillis());
                }
            }
        });
        *///?}

        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> removePlayerOnPlayerQuit(handler.player.getName().getString())
        );

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

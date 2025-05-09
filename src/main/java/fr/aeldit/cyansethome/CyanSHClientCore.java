package fr.aeldit.cyansethome;

import fr.aeldit.cyanlib.events.MissingLivingEntityEvent;
import fr.aeldit.cyanlib.lib.commands.CyanLibConfigCommands;
import fr.aeldit.cyansethome.commands.HomeCommands;
import fr.aeldit.cyansethome.commands.HomeOfCommands;
import fr.aeldit.cyansethome.commands.PermissionCommands;
import fr.aeldit.cyansethome.event.PlayerMoveEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.util.Formatting;

import static fr.aeldit.cyansethome.CombatTracking.removePlayerOnPlayerQuit;
import static fr.aeldit.cyansethome.CooldownManager.*;
import static fr.aeldit.cyansethome.CyanSHCore.*;
import static fr.aeldit.cyansethome.config.CyanLibConfigImpl.XP_USE_POINTS;
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

        PlayerMoveEvent.AFTER_MOVE.register((player) -> {
            if (HOMES.playerRequestedTp(player.getName().getString()))
            {
                cancelCooldown(player);
                HOMES.endTpRequest(player.getName().getString());
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            getCanceledCooldowns().forEach(player -> CYANSH_LANG_UTILS.sendPlayerMessage(
                    player, "error.movedWhileWaitingForTp"
            ));
            clearCanceledCooldowns();

            getPlayersCompletedCooldowns().forEach((player, t) -> {
                HOMES.endTpRequest(player.getName().getString());
                t.home().teleport(t.server(), player);
                if (XP_USE_POINTS.getValue())
                {
                    player.addExperience(-1 * t.requiredXpLevel());
                }
                else
                {
                    player.addExperienceLevels(-1 * t.requiredXpLevel());
                }
                CYANSH_LANG_UTILS.sendPlayerMessage(player, "msg.goToHome", Formatting.YELLOW + t.home().name());
            });
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

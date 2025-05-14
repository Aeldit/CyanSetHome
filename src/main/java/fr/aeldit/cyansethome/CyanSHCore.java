package fr.aeldit.cyansethome;

import fr.aeldit.cyanlib.events.MissingLivingEntityEvent;
import fr.aeldit.cyanlib.events.PlayerMovedEvent;
import fr.aeldit.cyanlib.lib.CombatTracking;
import fr.aeldit.cyanlib.lib.CyanLib;
import fr.aeldit.cyanlib.lib.CyanLibLanguageUtils;
import fr.aeldit.cyanlib.lib.commands.CyanLibConfigCommands;
import fr.aeldit.cyansethome.commands.HomeCommands;
import fr.aeldit.cyansethome.commands.HomeOfCommands;
import fr.aeldit.cyansethome.commands.PermissionCommands;
import fr.aeldit.cyansethome.config.CyanLibConfigImpl;
import fr.aeldit.cyansethome.homes.Homes;
import fr.aeldit.cyansethome.homes.Trusts;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static fr.aeldit.cyansethome.CooldownManager.*;
import static fr.aeldit.cyansethome.CooldownManager.getPlayersCompletedCooldowns;
import static fr.aeldit.cyansethome.config.CyanLibConfigImpl.XP_USE_POINTS;
import static fr.aeldit.cyansethome.homes.Homes.HOMES_PATH;
import static fr.aeldit.cyansethome.util.EventUtils.renameFileIfUsernameChanged;

public class CyanSHCore implements ModInitializer
{
    public static final String MODID = "cyansethome";
    public static final Logger CYANSH_LOGGER = LoggerFactory.getLogger(MODID);
    public static Path MOD_PATH = FabricLoader.getInstance().getConfigDir().resolve(MODID);

    public static final Homes HOMES = new Homes();
    public static final Trusts TRUSTS = new Trusts();

    public static CyanLib CYANSH_LIB_UTILS = new CyanLib(MODID, new CyanLibConfigImpl());
    public static CyanLibLanguageUtils CYANSH_LANG_UTILS = CYANSH_LIB_UTILS.getLanguageUtils();

    public static void checkOrCreateHomesDir()
    {
        if (!Files.exists(MOD_PATH))
        {
            try
            {
                Files.createDirectory(MOD_PATH);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        if (!Files.exists(HOMES_PATH))
        {
            try
            {
                Files.createDirectory(HOMES_PATH);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void removeEmptyModDir()
    {
        if (Files.exists(HOMES_PATH))
        {
            File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();

            if (listOfFiles != null && listOfFiles.length == 0)
            {
                try
                {
                    Files.delete(HOMES_PATH);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        if (Files.exists(MOD_PATH))
        {
            File[] listOfFiles = new File(MOD_PATH.toUri()).listFiles();

            if (listOfFiles != null && listOfFiles.length == 0)
            {
                try
                {
                    Files.delete(MOD_PATH);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onInitialize()
    {
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> renameFileIfUsernameChanged(handler)
        );

        PlayerMovedEvent.AFTER_MOVE.register((player) -> {
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
                (handler, server) -> CombatTracking.removePlayerOnPlayerQuit(handler.player.getName().getString())
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            new CyanLibConfigCommands(MODID, CYANSH_LIB_UTILS).register(dispatcher);
            HomeCommands.register(dispatcher);
            HomeOfCommands.register(dispatcher);
            PermissionCommands.register(dispatcher);
        });

        CYANSH_LOGGER.info("[CyanSetHome] Successfully initialized");
    }
}

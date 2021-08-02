package fr.raphoulfifou.sethome;

import fr.raphoulfifou.sethome.commands.SetCommands;
import fr.raphoulfifou.sethome.commands.SetHomeCommand;
import fr.raphoulfifou.sethome.commands.TeleportationCommands;
import fr.raphoulfifou.sethome.util.SetHomeJSONConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class HomeClientCore implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(HomeServerCore.MODID);
    public static final String MODNAME = HomeServerCore.MODNAME;
    public static SetHomeJSONConfig config = new SetHomeJSONConfig();

    @Override
    // Initialize the differents instances (here commands) when lauched on client (used when in singleplayer)
    public void onInitializeClient() {
        // Loads the homes
        config.load();

        // Register all the commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            TeleportationCommands.register(dispatcher);
            SetHomeCommand.register(dispatcher);
            SetCommands.register(dispatcher);
        });
        HomeClientCore.LOGGER.info("{} Successfully initialized commands", MODNAME);
        HomeClientCore.LOGGER.info("{} Successfully completed initialization", MODNAME);
    }
}
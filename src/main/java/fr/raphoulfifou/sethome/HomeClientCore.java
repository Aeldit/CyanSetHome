package fr.raphoulfifou.sethome;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.raphoulfifou.sethome.commands.SetCommands;
import fr.raphoulfifou.sethome.commands.SetHomeCommand;
import fr.raphoulfifou.sethome.commands.TeleportationCommands;
import fr.raphoulfifou.sethome.util.SetHomeJSONConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

/**
 * @since 0.0.1
 * @see HomeServerCore
 * @author Raphoulfifou
 */
@Environment(EnvType.CLIENT)
public class HomeClientCore implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(HomeServerCore.MODID);
    public static final String MODNAME = HomeServerCore.MODNAME;

    public static SetHomeJSONConfig config;
    public static final File jsonFile = FabricLoader.getInstance().getConfigDir().resolve("sethome.json").toFile();

    @Override
    // Initialize the differents instances (here commands) when lauched on client (used when in singleplayer)
    public void onInitializeClient() {

        // Register all the commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            TeleportationCommands.register(dispatcher);
            SetHomeCommand.register(dispatcher);
            SetCommands.register(dispatcher);
        });
        HomeClientCore.LOGGER.info("{} Successfully initialized commands", MODNAME);
        HomeClientCore.LOGGER.info("{} Successfully completed initialization", MODNAME);
    }

    public static SetHomeJSONConfig getConfig() {
		if (config == null) {
			loadConfig();
		}
		return config;
	}

	private static void loadConfig() {
		config = new SetHomeJSONConfig();
		config.load(jsonFile);
	}
}
package fr.raphoulfifou.sethome.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import fr.raphoulfifou.sethome.HomeClientCore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public class ModMenuApiImpl implements ModMenuApi {
    
    @Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
			return new ClothConfigScreenFactory(HomeClientCore.getConfig());
		}
		return (screen) -> null;
	}

}

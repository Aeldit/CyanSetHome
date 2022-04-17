package fr.raphoulfifou.cyansh.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModMenuApiImpl implements ModMenuApi
{

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> MidnightConfig.getScreen(parent, "sethome");
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories()
    {
        HashMap<String, ConfigScreenFactory<?>> map = new HashMap<>();
        MidnightConfig.configClass.forEach((modid, cClass) -> map.put(modid, parent -> MidnightConfig.getScreen(parent, modid)));
        return map;
    }

}

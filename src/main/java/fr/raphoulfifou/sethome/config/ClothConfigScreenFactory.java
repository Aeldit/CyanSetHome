package fr.raphoulfifou.sethome.config;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import fr.raphoulfifou.sethome.util.SetHomeJSONConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ClothConfigScreenFactory implements ConfigScreenFactory<Screen> {
	private SetHomeJSONConfig config;

	public ClothConfigScreenFactory(SetHomeJSONConfig config) {
		this.config = config;
	}

	@Override
	public Screen create(Screen parent) {
		SavingRunnable savingRunnable = new SavingRunnable();

		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(new TranslatableText("screen.sh.config.title"))
				.setSavingRunnable(savingRunnable);
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.sh.general"));
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("option.sh.allowHomes"), config.allowHomes)
				.setSaveConsumer((value) -> {
					if (config.allowHomes != value) {
						savingRunnable.reloadResources = true;
					}
					config.allowHomes = value;
				})
				.setTooltip(new TranslatableText("sh.mm.msg.allowHomes"))
				.setDefaultValue(config.allowHomes)
				.build());

		return builder.build();
	}

	private class SavingRunnable implements Runnable {
		public boolean reloadResources = false;

		@Override
		public void run() {
			config.save();
			if (reloadResources) {
				MinecraftClient.getInstance().reloadResources();
			}
			reloadResources = false;
		}
	}
}

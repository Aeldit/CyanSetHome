package fr.raphoulfifou.sethome.util;

import fr.raphoulfifou.sethome.HomeServerCore;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class Localization {
    
    public static String translationKey(String domain, String path) {
		return domain + "." + HomeServerCore.MODID + "." + path;
	}
	
	public static Text localized(String domain, String path, Object... args) {
		return new TranslatableText(translationKey(domain, path), args);
	}
	
	private Localization() {}

}

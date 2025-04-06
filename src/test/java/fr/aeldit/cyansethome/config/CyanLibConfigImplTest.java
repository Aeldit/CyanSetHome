package fr.aeldit.cyansethome.config;

import fr.aeldit.cyanlib.lib.config.CyanLibOptionsStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class CyanLibConfigImplTest
{
    private final CyanLibConfigImpl cfg = new CyanLibConfigImpl();
    private final Map<String, String> translations = cfg.getDefaultTranslations();
    private final CyanLibOptionsStorage opts = new CyanLibOptionsStorage("cyansethome-test", cfg);

    @Test
    void checkSet()
    {
        List<String> missingTranslations = opts.getOptionsNames().stream()
                                               .filter(s -> !translations.containsKey("msg.set.%s".formatted(s)))
                                               .toList();
        Assertions.assertTrue(
                missingTranslations.isEmpty(),
                "Missing translations are: " + missingTranslations.stream().map("msg.set.%s"::formatted).toList()
        );
    }

    @Test
    void checkGetDesc()
    {
        List<String> missingTranslations = opts.getOptionsNames().stream()
                                               .filter(s -> !translations.containsKey("msg.getDesc.%s".formatted(s)))
                                               .toList();
        Assertions.assertTrue(
                missingTranslations.isEmpty(),
                "Missing translations are: " + missingTranslations.stream().map("msg.getDesc.%s"::formatted).toList()
        );
    }

    @Test
    void checkGetCfg()
    {
        List<String> missingTranslations = opts.getOptionsNames().stream()
                                               .filter(s -> !translations.containsKey("msg.getCfg.%s".formatted(s)))
                                               .toList();
        Assertions.assertTrue(
                missingTranslations.isEmpty(),
                "Missing translations are: " + missingTranslations.stream().map("msg.getCfg.%s"::formatted).toList()
        );
    }
}
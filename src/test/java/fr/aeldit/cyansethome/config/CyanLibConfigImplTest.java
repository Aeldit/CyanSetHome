package fr.aeldit.cyansethome.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.aeldit.cyanlib.lib.config.CyanLibOptionsStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class CyanLibConfigImplTest
{
    private final CyanLibConfigImpl cfg = new CyanLibConfigImpl();
    private final Map<String, String> translations = cfg.getDefaultTranslations();
    private final CyanLibOptionsStorage opts = new CyanLibOptionsStorage("cyansethome-test", cfg);
    private final TypeToken<HashMap<String, String>> langType = new TypeToken<>()
    {
    };

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

    @Test
    void checkLang()
    {
        HashMap<String, String> langFields = new HashMap<>();
        // It is run inside versions/1....
        Path filePath = Path.of("../../src/main/resources/assets/cyansh/lang/en_us.json");
        try (Reader reader = Files.newBufferedReader(filePath))
        {
            langFields.putAll(new Gson().fromJson(reader, langType));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        List<String> missingTranslations = opts.getOptionsNames()
                                               .stream()
                                               .map("cyanlib.config.option.%s"::formatted)
                                               .filter(s -> !langFields.containsKey(s))
                                               .toList();
        Assertions.assertTrue(
                missingTranslations.isEmpty(),
                "Missing translations are: " + missingTranslations.stream()
                                                                  .map("cyanlib.config.option.%s"::formatted)
                                                                  .toList()
        );
    }
}
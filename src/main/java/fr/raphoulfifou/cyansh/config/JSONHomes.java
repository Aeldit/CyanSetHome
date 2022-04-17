package fr.raphoulfifou.cyansh.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JSONHomes
{

    public static void createUserFile(UUID playerUuid, String playerName)
    {
        String path = FabricLoader.getInstance().getConfigDir() + "\\homes\\" + playerUuid + ".json";

        File dir = new File(FabricLoader.getInstance().getConfigDir() + "\\homes\\");
        File f = new File(path);
        if (! dir.exists())
        {
            dir.mkdir();

            if (! f.exists())
            {
                try
                {
                    HashMap<Object, Object> map = new HashMap<>();
                    Homes homes = new Homes(playerName, map);

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    Writer writer = Files.newBufferedWriter(Paths.get(path));

                    gson.toJson(homes, writer);

                    writer.close();

                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Map<?, ?> addHome(UUID playerUuid, String playerName, String homeName, String dimension, List<Double> pos)
    {
        createUserFile(playerUuid, playerName);
        String path = FabricLoader.getInstance().getConfigDir() + "\\homes\\" + playerUuid + ".json";

        Map<?, ?> map;
        try
        {
            Homes.Home home = new Homes.Home(homeName, dimension, pos);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Gson gsonr = new GsonBuilder().serializeNulls().create();

            Path of = Path.of(path);
            Writer writer = Files.newBufferedWriter(of);
            Reader reader = Files.newBufferedReader(of);

            map = gsonr.fromJson(reader, Map.class);
            //map.put("homes", home);
            for (Map.Entry<?, ?> entry : map.entrySet())
            {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }

            Homes homes = new Homes(playerName, map);

            gson.toJson(homes, writer);

            writer.close();

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return map;
    }

}

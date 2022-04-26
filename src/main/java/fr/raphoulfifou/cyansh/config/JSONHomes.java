package fr.raphoulfifou.cyansh.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JSONHomes
{

    public static void createUserFile(UUID playerUuid, String playerName, ServerPlayerEntity player)
    {
        String path = FabricLoader.getInstance().getConfigDir() + "\\homes\\" + playerUuid + ".json";

        File dir = new File(FabricLoader.getInstance().getConfigDir() + "\\homes\\");
        File f = new File(path);
        if (! dir.exists())
        {
            if (! dir.mkdir())
            {
                player.sendMessage(new TranslatableText("Error while creating the folder 'homes'"), false);
            }
        }
        if (! f.exists())
        {
            try
            {
                List<Home> homesList = List.of(new Home("exampleName", "dimension", Arrays.asList(1.0, 256.2165, 50.3)));
                Map<String, Object> content = new HashMap<>();
                content.put("player_name", playerName);
                content.put("homes", homesList);

                Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

                Writer writer = Files.newBufferedWriter(Path.of(path));

                gson.toJson(content, writer);

                writer.close();

            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addHome(UUID playerUuid, String playerName, String homeName, String dimension, List<Double> pos, ServerPlayerEntity player)
    {
        createUserFile(playerUuid, playerName, player);
        String path = FabricLoader.getInstance().getConfigDir() + "\\homes\\" + playerUuid + ".json";

        Home homesRead;
        List<Home> homeRead;
        try
        {
            List<Home> homesList = List.of(new Home(homeName, dimension, pos));

            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

            Reader reader = Files.newBufferedReader(Path.of(path));

            Map<?, ?> map = gson.fromJson(reader, Map.class);
            player.sendMessage(new TranslatableText(String.valueOf(map)), false);

            List<?> homes = (List<?>) map.get("homes");
            player.sendMessage(new TranslatableText(String.valueOf(homes)), false);
            player.sendMessage(new TranslatableText("Home name : " + homes.get(0)), false);


            /*homesRead = gson.fromJson(reader, Homes.class);
            homeRead = homesRead.getHome();
            player.sendMessage(new TranslatableText(String.valueOf(homeRead)), false);

            Homes homes = new Homes(playerName, homeRead);*/
            //gson.toJson(jo, writer);

            reader.close();

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}

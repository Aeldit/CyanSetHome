/*
 * Copyright (c) 2023  -  Made by Aeldit
 *
 *              GNU LESSER GENERAL PUBLIC LICENSE
 *                  Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 *
 *
 * This version of the GNU Lesser General Public License incorporates
 * the terms and conditions of version 3 of the GNU General Public
 * License, supplemented by the additional permissions listed in the LICENSE.txt file
 * in the repo of this mod (https://github.com/Aeldit/CyanSetHome)
 */

package fr.aeldit.cyansh.util;

import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static fr.aeldit.cyansh.util.Utils.*;

public class GsonUtils
{
    public static void removePropertiesFiles(ServerPlayerEntity player)
    {
        if (Files.exists(MOD_PATH))
        {
            File[] listOfFiles = new File(MOD_PATH.toUri()).listFiles();
            boolean fileDeleted = false;

            if (listOfFiles != null)
            {
                for (File file : listOfFiles)
                {
                    if (file.isFile())
                    {
                        if (file.getName().split("\\.")[-1].equals(".properties"))
                        {
                            try
                            {
                                Files.delete(file.toPath());
                                fileDeleted = true;
                            }
                            catch (IOException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }

            if (fileDeleted)
            {
                CyanLibUtils.sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation("propertiesFilesDeleted"),
                        "cyan.message.propertiesFilesDeleted"
                );
            }
            else
            {
                CyanLibUtils.sendPlayerMessage(player,
                        CyanSHLanguageUtils.getTranslation("noPropertiesFiles"),
                        "cyan.message.noPropertiesFiles"
                );
            }
        }
    }
}

package com.cloudcore.echo.core;

import com.cloudcore.echo.core.Config;
import com.cloudcore.echo.server.Command;
import com.cloudcore.echo.util.FileUtils;
import com.cloudcore.echo.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FileSystem {

    /* Fields */

    public static String RootPath = "C:\\Users\\Public\\Documents\\CloudCoin\\";

    public static String CommandFolder = RootPath + File.separator + Config.TAG_COMMAND + File.separator;

    public static String LogsFolder = RootPath + Config.TAG_LOGS + File.separator + Config.MODULE_NAME + File.separator;


    /* Methods */

    public static boolean createDirectories() {
        try {
            Files.createDirectories(Paths.get(RootPath));

            Files.createDirectories(Paths.get(CommandFolder));
            Files.createDirectories(Paths.get(LogsFolder));
        } catch (Exception e) {
            System.out.println("FS#CD: " + e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void changeRootPath(String rootPath) {
        RootPath = rootPath;

        CommandFolder = RootPath + File.separator + Config.TAG_COMMAND + File.separator;
        LogsFolder = RootPath + Config.TAG_LOGS + File.separator + Config.MODULE_NAME + File.separator;
    }

    public static void archiveCommand(Command command) {
        try {
            Files.move(Paths.get(CommandFolder + command.filename),
                    Paths.get(LogsFolder + command.filename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Command> getCommands() {
        String[] commandFiles = FileUtils.selectFileNamesInFolder(CommandFolder);
        ArrayList<Command> commands = new ArrayList<>();

        for (int i = 0, j = commandFiles.length; i < j; i++) {
            if (!commandFiles[i].contains(Config.MODULE_NAME))
                continue;

            try {
                String json = new String(Files.readAllBytes(Paths.get(CommandFolder + commandFiles[i])));
                Command command = Utils.createGson().fromJson(json, Command.class);
                command.filename = commandFiles[i];
                commands.add(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return commands;
    }
}

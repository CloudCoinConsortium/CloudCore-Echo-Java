package com.cloudcore.echo;

/*
  Copyright (c) 2018 Ben Ward, 07/06/18

  This work is licensed under the terms of the MIT license.
  For a copy, see <https://opensource.org/licenses/MIT>.
 */

import com.cloudcore.echo.core.FileSystem;
import com.cloudcore.echo.server.Command;
import com.cloudcore.echo.server.FolderWatcher;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class CommandInterpreter {


    // Fields

    public static Scanner scanner;

    public static ExecutorService executor = Executors.newFixedThreadPool(25);

    public static RAIDA[] raidaArray = new RAIDA[25];

    public static String prompt = "Start Mode";


    // Methods

    public static void main(String[] args) {
        SimpleLogger.writeLog("ServantEchoerStarted", "");
        singleRun = isSingleRun(args);
        if (args.length != 0 && Files.exists(Paths.get(args[0]))) {
            System.out.println("New root path: " + args[0]);
            FileSystem.changeRootPath(args[0]);
        }
        System.out.println(FileSystem.RootPath);

        initialize();
        initializeRealRaida();

        ArrayList<Command> commands = FileSystem.getCommands();
        if (commands.size() > 0)
            for (Command command : commands) {
                testRaidaEcho();
                FileSystem.archiveCommand(command);
                exitIfSingleRun();
            }

        FolderWatcher watcher = new FolderWatcher(FileSystem.CommandFolder);
        System.out.println("Watching for commands at " + FileSystem.CommandFolder);
        while (true) {
            try {
                Thread.sleep(1000);

                if (watcher.newFileDetected()) {
                    System.out.println(Instant.now().toString() + ": Exporting coins...");
                    commands = FileSystem.getCommands();
                    for (Command command : commands) {
                        testRaidaEcho();
                        FileSystem.archiveCommand(command);
                        exitIfSingleRun();
                    }
                }
            } catch (Exception e) {
                System.out.println("Uncaught exception - " + e.getLocalizedMessage());
            }
        }
    }

    public static boolean singleRun = false;
    public static boolean isSingleRun(String[] args) {
        for (String arg : args)
            if (arg.equals("singleRun"))
                return true;
        return false;
    }
    public static void exitIfSingleRun() {
        if (singleRun)
            System.exit(0);
    }

    public static void commandlineversion(String[] args) {
        initialize();

        run();

        System.out.println("Thank you for using CloudCoin Foundation. Goodbye.");
    }

    /** Initializes the RAIDA array and prints the welcome message. */
    public static void initialize() {
        for (int i = 0; i < 25; i++) {
            raidaArray[i] = new RAIDA(i);
        }

        printWelcome();
        scanner = new Scanner(System.in);
    }

    /** Sets the Raida array to echo the production servers. */
    public static void initializeRealRaida() {
        for (int i = 0; i < 25; i++) {
            raidaArray[i].switchToRealHost();
        }
    }

    /** Sets the Raida array to echo the test servers. */
    public static void initializeFakeRaida() {
        for (int i = 0; i < 25; i++) {
            raidaArray[i].switchToFakeHost();
        }
    }

    /** The main method of the Command Interpreter. */
    public static void run() {
        while (true) {
            System.out.println("=======================");
            System.out.println(prompt + " Commands Available:");

            String[] commands = new String[]{"test mode", "fake test mode"};
            for (int i = 0; i < commands.length; i++) {
                System.out.println((i + 1) + ". " + commands[i]);
            }

            System.out.print(prompt + ">");
            String commandRecieved = scanner.nextLine();

            switch (commandRecieved.toLowerCase()) {
                case "test mode":
                    initializeRealRaida();
                    testRaidaEcho();
                    prompt = "Test Mode";
                    break;
                case "fake test mode":
                    initializeFakeRaida();
                    testRaidaEcho();
                    prompt = "Test Mode";
                    break;
                default:
                    System.out.println("Command failed. Try again.");
                    break;
            }
        }
    }

    /** Print out the opening message for the user. */
    public static void printWelcome() {
        System.out.println("Welcome to CloudCoin Foundation Opensource.");
        System.out.println("The Software is provided as is, with all faults, defects and errors,");
        System.out.println("and without warranty of any kind.");
    }

    /** Prepares 25 ping tests to RAIDA severs*/
    public static void initializeRaidaEcho() {
        List<Callable<Void>> taskList = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            final int index = i;

            Callable<Void> callable = () -> {
                raidaArray[index].echo();
                System.out.print("." + index);
                return null;
            };
            taskList.add(callable);
        }

        try {
            List<Future<Void>> futureList = executor.invokeAll(taskList);

            for (Future<Void> voidFuture : futureList) {
                try {
                    voidFuture.get(100, TimeUnit.MILLISECONDS);
                } catch (ExecutionException e) {
                    System.out.println("Error executing task " + e.getMessage());
                } catch (TimeoutException e) {
                    System.out.println("Timed out executing task" + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Sends an echo to each RAIDA server and logs the results. */
    public static void testRaidaEcho() {
        try {
            for(File file: new File(FileSystem.LogsFolder).listFiles())
                if (!file.isDirectory())
                    file.delete();
            Files.deleteIfExists(Paths.get(FileSystem.LogsFolder));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nEchoing RAIDA.\n");

        initializeRaidaEcho();

        StringBuilder masterFile = new StringBuilder();
        Logger.emptyFolder("Echo");

        for (int i = 0; i < 25; i++) {
            masterFile.append(raidaArray[i].lastJsonRaFromServer);
            masterFile.append("<br>");
            Logger.logFile("Echo", i + "." + raidaArray[i].status + "." + raidaArray[i].msServer + "." +
                    raidaArray[i].ms + ".log", raidaArray[i].lastJsonRaFromServer.getBytes());
            System.out.println("RAIDA" + i + ": " + raidaArray[i].status + ", ms:" + raidaArray[i].ms);
        }

        Logger.logFile("Echo", "echo_log.html", masterFile.toString().getBytes());
    }
}

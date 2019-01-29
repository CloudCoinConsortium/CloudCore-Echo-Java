package com.cloudcore.echo.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class FileUtils {

    /**
     * Returns an array containing all filenames in a directory.
     *
     * @param folderPath the directory to check for files
     * @return String[]
     */
    public static String[] selectFileNamesInFolder(String folderPath) {
        File folder = new File(folderPath);
        Collection<String> files = new ArrayList<>();
        if (folder.isDirectory()) {
            File[] filenames = folder.listFiles();

            if (null != filenames) {
                for (File file : filenames) {
                    if (file.isFile()) {
                        files.add(file.getName());
                    }
                }
            }
        }
        return files.toArray(new String[]{});
    }
}

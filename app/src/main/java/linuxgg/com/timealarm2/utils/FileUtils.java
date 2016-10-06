package linuxgg.com.timealarm2.utils;

import java.io.File;

/**
 * Created by tom on 2016/10/6.<p>
 * Version 1.0 <p>
 * Desc :    <p>
 */
public class FileUtils {

    public static void deleteFile(String path) {
        try {
            File file = new File(path);
            deleteFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteFile(File file) {
        try {
            if (file.isDirectory() && file.listFiles().length > 0) {
                deleteFilesThread(file);
            } else {
                file.deleteOnExit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void deleteFilesThread(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File[] files = file.listFiles();
                for (File child : files) {
                    deleteFile(child);
                }
            }
        }).start();

    }
}

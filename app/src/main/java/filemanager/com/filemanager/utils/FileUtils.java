package filemanager.com.filemanager.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Honza on 26.11.2017.
 */

public class FileUtils {
    private static int BUFFER_SIZE = 4096;

    public static boolean renameFile(File file, String name) {
        File parentFile = file.getParentFile();
        if (parentFile != null && parentFile.exists()) {
            return file.renameTo(new File(parentFile, name));
        }
        return false;
    }

    public static boolean deleteFile(File file) {
        if (file.isFile()) {
            return file.delete();
        }
        return false;
    }

    public static boolean deleteDirectory(File directory) {
        if (!directory.isDirectory()) {
            return false;
        }

        boolean result = true;
        File [] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for(File f : files) {
                if (f.isDirectory()) {
                    result &= deleteDirectory(f);
                } else {
                    result &= deleteFile(f);
                }
            }
        }
        result &= directory.delete();
        return result;
    }

    public static boolean moveFile(File source, File destination) {
        boolean copied = copyFile(source, destination);
        if (copied) {
            return deleteFile(source);
        }
        return false;
    }

    public static boolean copyFile(File source, File destination) {
        if (!source.exists() || source.isDirectory()) {
            return false;
        }
        try(FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(destination, false)) {
            byte [] buffer = new byte[BUFFER_SIZE];
            int readBytes = 0;
            while((readBytes = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, readBytes);
            }
            return true;
        }catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean moveFile(File source, File destination, FileOperationCallback foc) {
        if (!source.exists() || source.isDirectory()) {
            return false;
        }
        boolean copied = copyFile(source, destination, foc);
        if (copied) {
            return deleteFile(source);
        }
        return false;
    }

    public static boolean copyFile(File source, File destination, FileOperationCallback foc) {
        if (!source.exists() || source.isDirectory()) {
            return false;
        }

        try(FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(destination, false)) {
            byte [] buffer = new byte[BUFFER_SIZE];
            int copiedBytes = 0;
            long length = source.length();
            int readBytes = 0;

            while((readBytes = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, readBytes);
                copiedBytes += readBytes;
                foc.copyFileProgress(source, copiedBytes, length);
            }
            foc.copyFileComplete(destination);
            return true;
        }catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyDirectory(File source, File destination, FileOperationCallback foc) {
        if (!source.exists() || source.isFile()) {
            return false;
        }
        List<File> filesToCopy = getFiles(source);
        if (filesToCopy == null) {
            return false;
        }

        boolean result = true;
        File parentFolder = destination;
        for(File file: filesToCopy) {
            if (file.isDirectory()) {
                parentFolder = new File(parentFolder, file.getName());
                result &= parentFolder.mkdir();
            } else {
                result &= copyFile(file, new File(parentFolder, file.getName()), foc);
            }
        }
        return result;
    }

    public static boolean moveDirectory(File source, File destination, FileOperationCallback foc) {
        boolean result = copyDirectory(source, destination, foc);
        if (result) {
            result = deleteDirectory(source);
        }
        return result;
    }

    public static List<File> getFiles(File directory) {
        List<File> files = new ArrayList<>();
        getFiles(directory, files);
        for(File f : files) {
            if (!f.exists()) {
                return null;
            }
        }
        return files;
    }

    private static void getFiles(File root, List<File> files) {
        if (root.isDirectory()) {
            files.add(root);
            File[] filesList = root.listFiles();
            if (filesList != null && filesList.length > 0) {
                for(File file : filesList) {
                    if (file.isDirectory()) {
                        getFiles(file, files);
                    } else {
                        files.add(file);
                    }
                }
            }
        } else {
            files.add(root);
        }
    }

    public static String getFileExtension(String absolutePath) {
        File file = new File(absolutePath);
        if (file.isDirectory()) return null;
        int lastDotIndex = absolutePath.lastIndexOf(".");
        return absolutePath.substring(lastDotIndex + 1, absolutePath.length());
    }

    public interface FileOperationCallback {
        void copyFileProgress(File file, int current, long length);
        void copyFileComplete(File file);
    }

    public static class FileOperationAdapter implements FileOperationCallback {

        @Override
        public void copyFileProgress(File file, int current, long length) {

        }

        @Override
        public void copyFileComplete(File file) {

        }
    }
}

package filemanager.com.filemanager.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Honza on 06.12.2017.
 */

public abstract class FileComparator implements Comparator<File> {
    protected boolean sortDirectorySeparatory = false;

    public List<File> sort(List<File> items) {
        File[] files = items.toArray(new File[items.size()]);

        if (items.size() > 1) {
            if (sortDirectorySeparatory) {
                List<File> directories = new ArrayList<>();
                List<File> filesItems = new ArrayList<>();
                for(File file: files) {
                    if (file.isDirectory()) {
                        directories.add(file);
                    } else {
                        filesItems.add(file);
                    }
                }
                File[] sortedDirectories = directories.toArray(new File[directories.size()]);
                File[] sortedFiles = filesItems.toArray(new File[filesItems.size()]);
                bubbleSort(sortedDirectories);
                bubbleSort(sortedFiles);
                List<File> sortedItems = new ArrayList<>();
                sortedItems.addAll(Arrays.asList(sortedDirectories));
                sortedItems.addAll(Arrays.asList(sortedFiles));
                return sortedItems;
            } else {
                bubbleSort(files);
            }
        }
        return Arrays.asList(files);
    }

    private void bubbleSort(File[] files) {
        for(int i = 0; i < files.length - 1; i++) {
            for (int j = 0; j < files.length - i - 1; j++) {
                if (compare(files[j], files[j + 1]) > 0) {
                    swap(files, j, j + 1);
                }
            }
        }
    }

    private void swap(File [] fileArray, int left, int right) {
        File tmp = fileArray[right];
        fileArray[right] = fileArray[left];
        fileArray[left] = tmp;
    }

    public void setSortDirectorySeparatory(boolean value) {
        sortDirectorySeparatory = value;
    }
}

package filemanager.com.filemanager.utils;

import java.io.File;
import java.util.List;

/**
 * Created by Honza on 06.12.2017.
 */

public class FileSortByName extends FileComparator {

    @Override
    public int compare(File f1, File f2) {
        return f1.getName().compareTo(f2.getName());
    }
}

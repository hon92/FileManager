package filemanager.com.filemanager.utils;

import java.io.File;
import java.util.List;

/**
 * Created by Honza on 06.12.2017.
 */

public class FileSortBySize extends FileComparator {

    @Override
    public int compare(File f1, File f2) {
        if (f1.length() >= f2.length()) {
            return -1;
        }
        return 1;
    }
}

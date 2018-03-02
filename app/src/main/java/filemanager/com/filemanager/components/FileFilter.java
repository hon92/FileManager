package filemanager.com.filemanager.components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import filemanager.com.filemanager.adapters.FileAdapter;

/**
 * Created by Honza on 27.12.2017.
 */

public class FileFilter {
    private String query = "";
    private List<File> originFileList;

    public FileFilter() {
        originFileList = new ArrayList<>();
    }

    public void add(File file) {
        originFileList.add(file);
    }

    public void addAll(List<File> files) {
        originFileList.addAll(files);
    }

    public void clear() {
        originFileList.clear();
    }

    public String getQuery() {
        return query;
    }

    public boolean isFiltered() {
        return !query.equals("");
    }

    public List<File> applyFilter(String query) {
        List<File> filesList = new ArrayList<>();
        if (query.equals("")) { //no filter
            filesList.addAll(originFileList);
        } else { //filter according to query
            for (int i = 0; i < originFileList.size(); i++) {
                File file = originFileList.get(i);
                if (file.getName().startsWith(query)) {
                    filesList.add(file);
                }
            }
        }
        this.query = query;
        return filesList;
    }
}

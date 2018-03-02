package filemanager.com.filemanager.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import filemanager.com.filemanager.R;
import filemanager.com.filemanager.components.FileFilter;
import filemanager.com.filemanager.modes.SelectionActionMode;
import filemanager.com.filemanager.activities.FileManagerActivity;
import filemanager.com.filemanager.adapters.FileAdapter;
import filemanager.com.filemanager.components.FileOperations;
import filemanager.com.filemanager.components.FilePropertiesWindow;
import filemanager.com.filemanager.utils.FileComparator;
import filemanager.com.filemanager.utils.FileSortByName;

/**
 * Created by Honza on 04.12.2017.
 */

public class Page extends Fragment implements FileAdapter.OnSelectItemListener {
    private static final File DEFAULT_ROOT_DIRECTORY = new File("/");
    private File directory;
    private FileAdapter fileAdapter;
    private RecyclerView recyclerView;
    private ConstraintLayout emptyItemLayout;
    private LinearLayoutManager linearLayoutManager;
    private TextView pathTextView;
    private TextView infoTextView;
    private FileManagerActivity activity;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_layout, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        pathTextView = view.findViewById(R.id.pathTextView);
        infoTextView = view.findViewById(R.id.infoTextView);
        emptyItemLayout = view.findViewById(R.id.emptyItemLayout);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(fileAdapter);
        recyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileAdapter = new FileAdapter(getContext());
        fileAdapter.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        setDirectory(DEFAULT_ROOT_DIRECTORY);
    }

    public File getDirectory() {
        return directory;
    }

    public void reloadFiles() {
        setFilesForFileAdapter();
        if (fileAdapter.getItemCount() == 0) {
            emptyItemLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyItemLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void sortFiles() {
        FileComparator fileComparator = new FileSortByName();
        fileComparator.setSortDirectorySeparatory(sharedPreferences.getBoolean("show_folders_first", false));
        fileAdapter.sortItems(fileComparator);
    }

    public FileAdapter getFileAdapter() {
        return fileAdapter;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
        String path = directory.getAbsolutePath();
        pathTextView.setText(path);
        reloadFiles();
        sortFiles();
        FileFilter fileFilter = fileAdapter.getFileFilter();
        if (fileFilter.isFiltered()) {
            filterFiles(fileFilter.getQuery());
        } else {
            setFilesCountInfoText();
        }
    }

    @Override
    public void onClickItem(File file, int position) {
        boolean isDirectory = file.isDirectory();
        if (isDirectory) {
            setDirectory(file);
        } else {
            openFile(file);
        }
    }

    @Override
    public boolean onLongClickItem(File file, int position) {
        new SelectionActionMode(getFileManagerActivity()).showSelectionMode(this);
        fileAdapter.selectFileAt(position);
        return true;
    }

    @Override
    public void onInfoButtonClick(View view, final File file, int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view, Gravity.DISPLAY_CLIP_VERTICAL);
        popupMenu.inflate(R.menu.file_item_context_menu);
        if (file.isDirectory()) {
            popupMenu.getMenu().findItem(R.id.openAsItem).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FileOperations fileOperations = activity.getFileOperations();
                switch (item.getItemId()) {
                    case R.id.copyItem:
                        fileOperations.copyFile(file);
                        break;
                    case R.id.cutItem:
                        fileOperations.cutFile(file);
                        break;
                    case R.id.renameItem:
                        fileOperations.renameFile(file);
                        break;
                    case R.id.deleteItem:
                        fileOperations.deleteFiles(Arrays.asList(file));
                        break;
                    case R.id.propertiesItem:
                        showFilePropertiesWindow(file);
                        break;
                    case R.id.openAsTextItem:
                        openFileWithExtension(file, ".txt");
                        break;
                    case R.id.openAsVideoItem:
                        openFileWithExtension(file, ".mp4");
                        break;
                    case R.id.openAsPictureItem:
                        openFileWithExtension(file, ".jpg");
                        break;
                }
                activity.invalidateOptionsMenu();
                return false;
            }
        });
        popupMenu.show();
    }

    private void setFilesCountInfoText() {
        List<File> files = fileAdapter.getFiles();
        int foldersCount = 0;
        int filesCount = 0;

        for (File file : files) {
            if (file.isDirectory()) {
                foldersCount++;
            } else {
                filesCount++;
            }
        }
        String infoText = String.format("%d folders and %d files", foldersCount, filesCount);
        infoTextView.setText(infoText);
    }

    public boolean moveToParentDirectory() {
        File parentDirectory = getDirectory().getParentFile();
        if (parentDirectory != null && parentDirectory.exists()) {
            setDirectory(parentDirectory);
            return true;
        }
        return false;
    }

    public void openFile(File file) {
        openFileWithExtension(file, file.getName());
    }

    public FileManagerActivity getFileManagerActivity() {
        return activity;
    }

    public void setFileManagerActivity(FileManagerActivity activity) {
        this.activity = activity;
    }

    public void showFilePropertiesWindow(File file) {
        new FilePropertiesWindow(getContext(), file).show();
    }

    public void filterFiles(String query) {
        fileAdapter.filterFiles(query);
        sortFiles();
        setFilesCountInfoText();
    }

    private void openFileWithExtension(File file, String filenameWithExtension) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String contentType = URLConnection.guessContentTypeFromName(filenameWithExtension);
        if (contentType != null) {
            intent.setDataAndType(Uri.fromFile(file), contentType);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Unable to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFilesForFileAdapter() {
        boolean showHiddenFiles = sharedPreferences.getBoolean("show_hidden_files", false);
        boolean hideEmptyFolders = sharedPreferences.getBoolean("hide_empty_folders", false);
        boolean showFoldersFirst = sharedPreferences.getBoolean("show_folders_first", false);

        fileAdapter.clear();
        File directory = getDirectory();
        if (!directory.exists()) return;

        File[] files = directory.listFiles();
        if (files != null) {
            List<File> tmpDFileList = new ArrayList<>();
            List<File> tmpFFileList = new ArrayList<>();

            for (File file : files) {
                String name = file.getName();
                if (!showHiddenFiles && name.startsWith(".")) { // hidden file
                    continue;
                }
                if (hideEmptyFolders && file.isDirectory()) {
                    File[] filesList = file.listFiles();
                    if (filesList == null || filesList.length == 0) {
                        continue;
                    }
                }
                if (showFoldersFirst) {
                    if (file.isDirectory()) {
                        tmpDFileList.add(file);
                    } else {
                        tmpFFileList.add(file);
                    }
                } else {
                    fileAdapter.addFile(file);
                }
            }

            if (showFoldersFirst) {
                fileAdapter.addFiles(tmpDFileList);
                fileAdapter.addFiles(tmpFFileList);
            }
        }
    }
}

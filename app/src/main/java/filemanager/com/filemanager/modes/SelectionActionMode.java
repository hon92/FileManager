package filemanager.com.filemanager.modes;

import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import filemanager.com.filemanager.R;
import filemanager.com.filemanager.activities.FileManagerActivity;
import filemanager.com.filemanager.adapters.FileAdapter;
import filemanager.com.filemanager.adapters.SelectableAdapter;
import filemanager.com.filemanager.pages.Page;

/**
 * Created by Honza on 24.11.2017.
 */

public class SelectionActionMode implements Callback, FileAdapter.OnSelectItemListener, SelectableAdapter.SelectionListener {
    private FileManagerActivity activity;
    private FileAdapter fileAdapter;
    private ActionMode actionMode;
    private Page page;

    public SelectionActionMode(FileManagerActivity activity) {
        this.activity = activity;
    }

    public void showSelectionMode(Page page) {
        if (actionMode == null) {
            this.page = page;
            this.fileAdapter = page.getFileAdapter();
            fileAdapter.clearSelection();
            activity.getViewPager().setScrollEnabled(false);
            actionMode = activity.startActionMode(this);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.selection_items_menu, menu);
        fileAdapter.setOnClickListener(this);
        fileAdapter.setSelectionListener(this);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.selectAllItem:
                fileAdapter.selectAll();
                break;
            case R.id.copySelectedItem:
                copySelectedFiles(false);
                break;
            case R.id.cutSelectedItem:
                copySelectedFiles(true);
                break;
            case R.id.deleteSelectedItem:
                deleteSelectedFiles();
                break;
            case R.id.renameSelectedItem:
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        fileAdapter.setOnClickListener(page);
        fileAdapter.clearSelection();
        fileAdapter.setSelectionListener(null);
        activity.getViewPager().setScrollEnabled(true);
    }

    @Override
    public void onClickItem(File file, int position) {
        fileAdapter.toggleSelectionAt(position);
    }

    @Override
    public boolean onLongClickItem(File file, int position) {
        return false;
    }

    @Override
    public void onInfoButtonClick(View view, File file, int position) {

    }

    @Override
    public void onSelectionChanged(List<Integer> positions) {
        if (actionMode != null) {
            int selectedCount = positions.size();
            if (selectedCount > 0) {
                actionMode.setTitle(String.format("%d selected", selectedCount));
            } else {
                actionMode.finish();
            }
        }
    }

    public void copySelectedFiles(boolean removeOriginals) {
        List<Integer> selectedPositions = fileAdapter.getSelectedItems();
        List<File> selectedFiles = new ArrayList<>();
        for(int i = 0; i < selectedPositions.size(); i++) {
            selectedFiles.add(fileAdapter.getFile(selectedPositions.get(i)));
        }
        fileAdapter.clearSelection();
        if (removeOriginals) {
            activity.getFileOperations().cutFiles(selectedFiles);
        } else {
            activity.getFileOperations().copyFiles(selectedFiles);
        }
        activity.invalidateOptionsMenu();
    }

    public void deleteSelectedFiles() {
        List<Integer> selectedPositions = fileAdapter.getSelectedItems();
        List<File> selectedFiles = new ArrayList<>();
        for(int i = 0; i < selectedPositions.size(); i++) {
            selectedFiles.add(fileAdapter.getFile(selectedPositions.get(i)));
        }

        fileAdapter.clearSelection();
        activity.getFileOperations().deleteFiles(selectedFiles);
        activity.reloadPageFiles();
        activity.invalidateOptionsMenu();
    }
}

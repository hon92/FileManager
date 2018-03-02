package filemanager.com.filemanager.components;

import android.os.Handler;
import android.support.design.widget.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import filemanager.com.filemanager.R;
import filemanager.com.filemanager.activities.FileManagerActivity;
import filemanager.com.filemanager.fileoperations.CopyOperation;
import filemanager.com.filemanager.fileoperations.DeleteOperation;
import filemanager.com.filemanager.fileoperations.Operation;
import filemanager.com.filemanager.fileoperations.RenameOperation;

/**
 * Created by Honza on 26.11.2017.
 */

public class FileOperations {
    private FileManagerActivity activity;
    private ArrayList<File> filesInClipboard;
    private boolean removeSourcesFilesAfterCopy = false;

    public FileOperations(FileManagerActivity activity) {
        this.activity = activity;
        filesInClipboard = new ArrayList<>();
    }

    private void showSnackBar(String message, int length) {
        Snackbar.make(activity.findViewById(R.id.coordinationLayout), message, length).show();
    }

    public boolean hasEmptyClipboard() {
        return filesInClipboard.size() == 0;
    }

    public void renameFile(File file) {
        RenameOperation operation = new RenameOperation(activity, file);
        operation.setSuccessCallback(new Operation.SuccessCallback<RenameOperation.RenameOperationData>() {
            @Override
            public void onSuccess(RenameOperation.RenameOperationData data) {
                if (data.isRenamed()) {
                    String oldFileName = data.getOldFileName();
                    String newFileName = data.getNewFileName();
                    activity.reloadPageFiles();
                    String message = String.format("Rename file \"%s\" to \"%s\"", oldFileName, newFileName);
                    showSnackBar(message, Snackbar.LENGTH_LONG);
                } else {
                    showSnackBar("Can't rename this file", Snackbar.LENGTH_LONG);
                }
            }
        });
        operation.setErrorCallback(new Operation.ErrorCallback<RenameOperation.RenameOperationError>() {
            @Override
            public void onError(RenameOperation.RenameOperationError error) {
                showSnackBar("Rename canceled", Snackbar.LENGTH_SHORT);
            }
        });
        operation.perform();
    }

    public void deleteFiles(List<File> files) {
        DeleteOperation operation = new DeleteOperation(activity, files);
        operation.setSuccessCallback(new Operation.SuccessCallback<DeleteOperation.DeleteOperationData>() {
            @Override
            public void onSuccess(DeleteOperation.DeleteOperationData data) {
                int deleteCount = data.getDeleteCount();
                List<File> files = data.getFiles();

                if (deleteCount == 0) {
                    showSnackBar("Error when deleting files", Snackbar.LENGTH_LONG);
                    return;
                }

                String message = String.format("%d files was deleted.", files.size());

                if (deleteCount == 1) {
                    message = String.format("File \"%s\" was deleted.", files.get(0).getName());
                }

                activity.reloadPageFiles();
                showSnackBar(message, Snackbar.LENGTH_LONG);
            }
        });
        operation.setErrorCallback(new Operation.ErrorCallback<DeleteOperation.DeleteOperationError>() {
            @Override
            public void onError(DeleteOperation.DeleteOperationError error) {
                showSnackBar("Deleting file canceled", Snackbar.LENGTH_SHORT);
            }
        });
        operation.perform();
    }

    public void copyFile(File file) {
        filesInClipboard.clear();
        filesInClipboard.add(file);
        removeSourcesFilesAfterCopy = false;
        showSnackBar(file.getName() + " was added to clipboard", Snackbar.LENGTH_LONG);
    }

    public void cutFile(File file) {
        filesInClipboard.clear();
        filesInClipboard.add(file);
        removeSourcesFilesAfterCopy = true;
        showSnackBar(file.getName() + " was added to clipboard", Snackbar.LENGTH_LONG);
    }

    public void copyFiles(List<File> files) {
        filesInClipboard.clear();
        filesInClipboard.addAll(files);
        removeSourcesFilesAfterCopy = false;
        showSnackBar(String.format("%d files was added to clipboard", files.size()), Snackbar.LENGTH_LONG);
    }

    public void cutFiles(List<File> files) {
        filesInClipboard.clear();
        filesInClipboard.addAll(files);
        removeSourcesFilesAfterCopy = true;
        showSnackBar(String.format("%d files was added to clipboard", files.size()), Snackbar.LENGTH_LONG);
    }

    public void createFolder(final File directory) {
        if (!directory.exists()) {
            showSnackBar("Cant create folder to this path", Snackbar.LENGTH_LONG);
            return;
        }

        InputDialog inputDialog = new InputDialog(activity, "Insert new folder name", false);
        inputDialog.setCallbacks(new InputDialog.InputDialogCallback() {
            @Override
            public void onSuccess(String newName) {
                File newFolder = new File(directory, newName);
                if (!newFolder.exists()) {
                    boolean folderCreated = newFolder.mkdir();
                    if (folderCreated) {
                        activity.reloadPageFiles();
                        showSnackBar(String.format("Folder \"%s\" was created.", newName), Snackbar.LENGTH_LONG);
                    } else {
                        showSnackBar(String.format("Folder \"%s\" was not created.", newName), Snackbar.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFail() {

            }
        });
        inputDialog.show();
    }

    public void createFile(final File directory) {
        if (!directory.exists()) {
            showSnackBar("Cant create file on this path", Snackbar.LENGTH_LONG);
            return;
        }

        InputDialog inputDialog = new InputDialog(activity, "Insert new file name", false);
        inputDialog.setCallbacks(new InputDialog.InputDialogCallback() {
            @Override
            public void onSuccess(String newName) {
                File newFile = new File(directory, newName);
                if (!newFile.exists()) {
                    try {
                        boolean fileCreated = newFile.createNewFile();
                        if (fileCreated) {
                            activity.reloadPageFiles();
                            showSnackBar(String.format("File \"%s\" was created.", newName), Snackbar.LENGTH_LONG);
                        } else {
                            showSnackBar(String.format("File \"%s\" was not created.", newName), Snackbar.LENGTH_LONG);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showSnackBar(String.format("File \"%s\" was not created.", newName), Snackbar.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFail() {

            }
        });
        inputDialog.show();
    }

    public void pasteFile(File directory) {
        final Handler handler = new Handler();
        CopyOperation operation = new CopyOperation(activity, filesInClipboard, directory, removeSourcesFilesAfterCopy);
        operation.setSuccessCallback(new Operation.SuccessCallback<CopyOperation.CopyOperationData>(){

            @Override
            public void onSuccess(CopyOperation.CopyOperationData data) {
                int copiedFilesCount = data.getCopiedFilesCount();
                String message = String.format("%d / %d files was pasted into current directory",
                        copiedFilesCount,
                        data.getFiles().size());

                if (copiedFilesCount == 0) {
                    message = "No files was pasted into current directory";
                }
                filesInClipboard.clear();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        activity.invalidateOptionsMenu();
                        activity.reloadPageFiles();
                    }
                });
                showSnackBar(message, Snackbar.LENGTH_LONG);
            }
        });

        operation.setErrorCallback(new Operation.ErrorCallback<CopyOperation.CopyOperationError>() {
            @Override
            public void onError(CopyOperation.CopyOperationError error) {
                showSnackBar("Copying was canceled", Snackbar.LENGTH_LONG);
            }
        });
        operation.perform();
    }
}

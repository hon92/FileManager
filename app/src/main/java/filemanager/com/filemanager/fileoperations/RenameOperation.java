package filemanager.com.filemanager.fileoperations;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.List;

import filemanager.com.filemanager.components.InputDialog;
import filemanager.com.filemanager.utils.FileUtils;

/**
 * Created by Honza on 02.12.2017.
 */

public class RenameOperation extends Operation {
    private File file;
    private List<File> files;
    private Context context;

    public RenameOperation(Context context, File file) {
        this.file = file;
        this.context = context;
    }

    public RenameOperation(Context context, List<File> files) {
        this.files = files;
        this.context = context;
    }

    @Override
    public void perform() {
        String title = file.isDirectory() ? "Rename folder" : "Rename file";
        final InputDialog inputDialog = new InputDialog(context, title, true);
        inputDialog.setCallbacks(new InputDialog.InputDialogCallback() {
            @Override
            public void onSuccess(String newName) {
                boolean wasRenamed = FileUtils.renameFile(file, newName);
                fireSuccessCallback(new RenameOperationData(file, wasRenamed, file.getName(), newName));
            }

            @Override
            public void onFail() {
                fireErrorCallback(new RenameOperationError());
            }
        });
        inputDialog.show(file.getName());
    }

    public static class RenameOperationData extends OperationData {
        private boolean isRenamed;
        private String oldFileName;
        private String newFileName;

        public RenameOperationData(File file, boolean isRenamed, String oldFileName, String newFileName) {
            super(file);
            this.isRenamed = isRenamed;
            this.oldFileName = oldFileName;
            this.newFileName = newFileName;
        }

        public boolean isRenamed() {
            return isRenamed;
        }

        public String getOldFileName() {
            return oldFileName;
        }

        public String getNewFileName() {
            return newFileName;
        }
    }

    public static class RenameOperationError extends OperationError {
        public RenameOperationError() {

        }
    }
}

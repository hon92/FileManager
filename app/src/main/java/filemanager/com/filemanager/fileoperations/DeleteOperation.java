package filemanager.com.filemanager.fileoperations;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.io.File;
import java.util.List;

import filemanager.com.filemanager.utils.FileUtils;

/**
 * Created by Honza on 02.12.2017.
 */

public class DeleteOperation extends Operation {
    private Context context;
    private List<File> files;

    public DeleteOperation(Context context, List<File> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public void perform() {
        if (files.size() == 0) {
            fireErrorCallback(new DeleteOperationError());
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        boolean isMultiDelete = files.size() > 1;
        File first = files.get(0);
        String multiFileMessage = String.format("Do you want to delete %d files?", files.size());
        String singleFileMessage = String.format("Do you want to delete file \"%s\"?",first.getName());
        builder.setTitle("Delete file");
        if (isMultiDelete) {
            builder.setMessage(multiFileMessage);
        } else {
            builder.setMessage(singleFileMessage);
        }
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int deleteCount = 0;
                for(File file: files) {
                    boolean deleted;
                    if (file.isDirectory()) {
                        deleted = FileUtils.deleteDirectory(file);
                    } else {
                        deleted = FileUtils.deleteFile(file);
                    }
                    if (deleted) {
                        deleteCount++;
                    }
                }
                fireSuccessCallback(new DeleteOperationData(files, deleteCount));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fireErrorCallback(new DeleteOperationError());
            }
        });
        builder.show();
    }

    public static class DeleteOperationData extends Operation.OperationData {
        private int deleteCount;
        private List<File> files;

        public DeleteOperationData(List<File> files, int deleteCount) {
            super(null);
            this.deleteCount = deleteCount;
            this.files = files;
        }

        public int getDeleteCount() {
            return deleteCount;
        }

        public List<File> getFiles() {
            return files;
        }
    }

    public static class DeleteOperationError extends Operation.OperationError {

    }

}

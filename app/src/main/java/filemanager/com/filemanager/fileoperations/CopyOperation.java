package filemanager.com.filemanager.fileoperations;

import android.app.ProgressDialog;
import android.content.Context;

import java.io.File;
import java.util.List;

import filemanager.com.filemanager.utils.FileUtils;

/**
 * Created by Honza on 02.12.2017.
 */

public class CopyOperation extends Operation {
    private Context context;
    private List<File> files;
    private File pasteDirectory;
    private boolean removeOriginalFiles;

    public CopyOperation(Context context, List<File> files, File pasteDirectory, boolean removeOriginalFiles) {
        this.context = context;
        this.files = files;
        this.removeOriginalFiles = removeOriginalFiles;
        this.pasteDirectory = pasteDirectory;
    }

    @Override
    public void perform() {
        if (files.size() == 0) {
            fireErrorCallback(new CopyOperationError());
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Pasting files from copyboard");
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.setSecondaryProgress(0);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int incDiff = 100 / files.size();
                int copiedFiles = 0;
                for(int i = 0; i < files.size(); i++) {
                    File sourceFile = files.get(i);
                    File destinationFile = new File(pasteDirectory, sourceFile.getName());
                    File destinationDirectory = pasteDirectory;

                    if (!sourceFile.exists()) {
                        continue;
                    }
                    if (sourceFile.getAbsolutePath().equals(destinationFile.getAbsolutePath())) {
                        continue;
                    }

                    FileUtils.FileOperationCallback foc = new FileUtils.FileOperationAdapter() {

                        @Override
                        public void copyFileProgress(File file, int current, long length) {
                            if (length == 0) {
                                progressDialog.setSecondaryProgress(100);
                                return;
                            }
                            int secondaryProgress = (int)((100 / (double) length) * current);
                            progressDialog.setSecondaryProgress(secondaryProgress);
                        }
                    };

                    boolean finished;
                    boolean isDirectory = sourceFile.isDirectory();
                    if (!removeOriginalFiles) {
                        if (isDirectory) {
                            finished = FileUtils.copyDirectory(sourceFile, destinationDirectory, foc);
                        } else {
                            finished = FileUtils.copyFile(sourceFile, destinationFile, foc);
                        }
                    } else {
                        if (isDirectory) {
                            finished = FileUtils.moveDirectory(sourceFile, destinationDirectory, foc);
                        } else {
                            finished = FileUtils.moveFile(sourceFile, destinationFile, foc);
                        }
                    }
                    if (finished) {
                        copiedFiles++;
                    }
                    progressDialog.incrementProgressBy(incDiff);
                }
                progressDialog.dismiss();
                fireSuccessCallback(new CopyOperationData(files, copiedFiles));
            }
        }).start();
    }

    public static class CopyOperationData extends OperationData {
        private List<File> files;
        private int copiedFilesCount;

        public CopyOperationData(List<File> files, int copiedFilesCount) {
            super(null);
            this.files = files;
            this.copiedFilesCount = copiedFilesCount;
        }

        public List<File> getFiles() {
            return files;
        }

        public int getCopiedFilesCount() {
            return copiedFilesCount;
        }
    }

    public static class CopyOperationError extends OperationError {

    }
}

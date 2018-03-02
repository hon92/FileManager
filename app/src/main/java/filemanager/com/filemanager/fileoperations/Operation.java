package filemanager.com.filemanager.fileoperations;

import android.support.v7.app.AppCompatActivity;

import java.io.File;

/**
 * Created by Honza on 02.12.2017.
 */

public abstract class Operation {
    protected SuccessCallback successCallback;
    protected ErrorCallback errorCallback;

    public abstract void perform();

    public void setSuccessCallback(SuccessCallback callback) {
        this.successCallback = callback;
    }

    public void setErrorCallback(ErrorCallback callback) {
        this.errorCallback = callback;
    }

    protected void fireSuccessCallback(OperationData operationData) {
        if (successCallback != null) {
            successCallback.onSuccess(operationData);
        }
    }

    protected void fireErrorCallback(OperationError operationError) {
        if (errorCallback != null) {
            errorCallback.onError(operationError);
        }
    }

    public interface SuccessCallback<T extends OperationData> {
        void onSuccess(T data);
    }

    public interface ErrorCallback<T extends OperationError> {
        void onError(T error);
    }

    public static class OperationData {
        private File file;
        public OperationData(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }

    public static class OperationError {
        public OperationError() {

        }
    }
}

package filemanager.com.filemanager.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import filemanager.com.filemanager.R;

/**
 * Created by Honza on 26.11.2017.
 */

public class InputDialog {
    private AlertDialog.Builder builder;
    private InputDialogCallback callback;

    public InputDialog(@NonNull Context context, String title, boolean cancelable) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(cancelable);
    }

    public void setCallbacks(InputDialogCallback callback) {
        this.callback = callback;
    }

    private void showDialog(final String placeholder) {
        LayoutInflater layoutInflater = LayoutInflater.from(builder.getContext());
        View view  = layoutInflater.inflate(R.layout.input_dialog_layout, null);
        final EditText editText = view.findViewById(R.id.inputEditText);
        final InputMethodManager imm = builder.getContext().getSystemService(InputMethodManager.class);
        editText.setText(placeholder);

        if (editText.requestFocus()) {
            editText.post(new Runnable() {
                @Override
                public void run() {
                    imm.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
            });
        }
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onSuccess(editText.getText().toString());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onFail();
                }
            }
        });
        builder.setView(view);
        builder.show();
    }

    public void show(String placeholder) {
        showDialog(placeholder);
    }

    public void show() {
        showDialog( "");
    }

    public interface InputDialogCallback {
        void onSuccess(String newName);
        void onFail();
    }

}

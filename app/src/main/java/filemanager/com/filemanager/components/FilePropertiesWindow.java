package filemanager.com.filemanager.components;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import filemanager.com.filemanager.R;

/**
 * Created by Honza on 07.12.2017.
 */

public class FilePropertiesWindow {
    private AlertDialog.Builder builder;

    public FilePropertiesWindow(Context context, File file) {
        builder = new AlertDialog.Builder(context);
        if (file.isDirectory()) {
            prepareFolderView(file);
        } else {
            prepareFileView(file);
        }
    }

    private void prepareFileView(File file) {
        LayoutInflater inflater = LayoutInflater.from(builder.getContext());
        View view = inflater.inflate(R.layout.file_properties_layout, null);
        TextView nameTextView = view.findViewById(R.id.filenameTextView);
        TextView pathTextView = view.findViewById(R.id.pathTextView);
        TextView lastModifiedTextView = view.findViewById(R.id.lastModifiedTextView);
        TextView sizeTextView = view.findViewById(R.id.sizeTextView);
        nameTextView.setText(file.getName());
        pathTextView.setText(file.getAbsolutePath());
        lastModifiedTextView.setText(DateFormat.getDateTimeInstance().format(new Date(file.lastModified())));
        sizeTextView.setText(Formatter.formatFileSize(builder.getContext(), file.length()));
        builder.setView(view);
    }

    private void prepareFolderView(File file) {
        LayoutInflater inflater = LayoutInflater.from(builder.getContext());
        View view = inflater.inflate(R.layout.folder_properties_layout, null);
        TextView nameTextView = view.findViewById(R.id.filenameTextView);
        TextView pathTextView = view.findViewById(R.id.pathTextView);
        TextView lastModifiedTextView = view.findViewById(R.id.lastModifiedTextView);
        TextView sizeTextView = view.findViewById(R.id.sizeTextView);
        TextView foldersCountTextView = view.findViewById(R.id.foldersCountTextView);
        TextView filesCountTextView = view.findViewById(R.id.filesCountTextView);
        nameTextView.setText(file.getName());
        pathTextView.setText(file.getAbsolutePath());
        lastModifiedTextView.setText(DateFormat.getDateTimeInstance().format(new Date(file.lastModified())));
        sizeTextView.setText(Formatter.formatFileSize(builder.getContext(), file.length()));

        int folders = 0;
        int files = 0;

        File [] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for(File f: listFiles) {
                if (f.isDirectory()) {
                    folders++;
                } else {
                    files++;
                }
            }
        }

        foldersCountTextView.setText(Integer.toString(folders));
        filesCountTextView.setText(Integer.toString(files));
        builder.setView(view);
    }

    public void show() {
        builder.show();
    }
}

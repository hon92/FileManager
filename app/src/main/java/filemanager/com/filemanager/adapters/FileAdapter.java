package filemanager.com.filemanager.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import filemanager.com.filemanager.R;
import filemanager.com.filemanager.components.FileFilter;
import filemanager.com.filemanager.utils.FileComparator;
import filemanager.com.filemanager.utils.FileUtils;

/**
 * Created by Honza on 22.11.2017.
 */

public class FileAdapter extends SelectableAdapter<FileAdapter.ViewHolder> {
    private static final int NORMAL_ITEM_TYPE = 2;
    private static final int SELECTED_ITEM_TYPE = 4;
    private static final String FILE_EXTENSION = "file";
    private static final String FOLDER_EXTENSION = "folder";
    private static final Dictionary<String, Integer> extensionsIcons = new Hashtable<>();
    private List<File> fileArrayList = new ArrayList<>();
    private FileFilter fileFilter;
    private OnSelectItemListener onSelectItemListener;
    private static Context context;

    public FileAdapter(Context context) {
        this.context = context;
        fileFilter = new FileFilter();
        initIcons();
    }

    private void initIcons() {
        extensionsIcons.put(FOLDER_EXTENSION, R.drawable.folder);
        extensionsIcons.put(FILE_EXTENSION, R.drawable.file);
        extensionsIcons.put("ppt", R.drawable.ppt);
        extensionsIcons.put("xls", R.drawable.xls);
        extensionsIcons.put("gif", R.drawable.gif);
        extensionsIcons.put("jpg", R.drawable.jpg);
        extensionsIcons.put("png", R.drawable.png);
        extensionsIcons.put("doc", R.drawable.doc);
        extensionsIcons.put("wav", R.drawable.wav);
        extensionsIcons.put("mp3", R.drawable.mp3);
        extensionsIcons.put("wma", R.drawable.wma);
        extensionsIcons.put("csv", R.drawable.csv);
        extensionsIcons.put("txt", R.drawable.txt);
        extensionsIcons.put("apk", R.drawable.apk);
        extensionsIcons.put("zip", R.drawable.zip);
        extensionsIcons.put("xml", R.drawable.xml);
        extensionsIcons.put("pdf", R.drawable.pdf);
        extensionsIcons.put("rar", R.drawable.rar);
        extensionsIcons.put("exe", R.drawable.exe);
        extensionsIcons.put("html", R.drawable.html);
        extensionsIcons.put("py", R.drawable.py);
        extensionsIcons.put("mpg", R.drawable.mpg);
        extensionsIcons.put("avi", R.drawable.avi);
        extensionsIcons.put("flv", R.drawable.flv);
        extensionsIcons.put("7z", R.drawable.sevenzip);
        extensionsIcons.put("aac", R.drawable.aac);
        extensionsIcons.put("ogg", R.drawable.ogg);
        extensionsIcons.put("tar", R.drawable.tar);
        extensionsIcons.put("tif", R.drawable.tif);
        extensionsIcons.put("dll", R.drawable.dll);
        extensionsIcons.put("json", R.drawable.json);
        extensionsIcons.put("c++", R.drawable.cplusplus);
        extensionsIcons.put("js", R.drawable.js);
        extensionsIcons.put("css", R.drawable.css);
        extensionsIcons.put("cs", R.drawable.cs);
    }

    public static Integer getExtensionResource(String key) {
        try {
            return extensionsIcons.get(key);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public void addFile(File file) {
        fileArrayList.add(file);
        fileFilter.add(file);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addFiles(List<File> files) {
        int currentCount = getItemCount();
        fileArrayList.addAll(files);
        fileFilter.addAll(files);
        notifyItemRangeInserted(currentCount, files.size());
    }

    public File getFile(int position) {
        if (position >= 0 && position < fileArrayList.size()) {
            return fileArrayList.get(position);
        }
        return null;
    }

    public void clear() {
        int itemsCount = getItemCount();
        fileArrayList.clear();
        fileFilter.clear();
        notifyItemRangeRemoved(0, itemsCount);
    }

    public void setOnClickListener(OnSelectItemListener listener) {
        onSelectItemListener = listener;
    }

    public void selectAll() {
        for(int i = 0; i < fileArrayList.size(); i++) {
            selectFileAt(i);
        }
    }

    public void filterFiles(String query) {
        fileArrayList = fileFilter.applyFilter(query);
        notifyDataSetChanged();
    }

    public List<File> getFiles() {
        return fileArrayList;
    }

    public void sortItems(FileComparator comparator) {
        List<File> sortedFiles = comparator.sort(fileArrayList);
        fileArrayList.clear();
        fileArrayList.addAll(sortedFiles);
        notifyDataSetChanged();
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item_layout, parent, false);
        if (viewType == SELECTED_ITEM_TYPE) {
            Resources resources = context.getResources();
            view.setBackgroundColor(resources.getColor(R.color.textColorSecondary, context.getTheme()));
            TextView fileNameTextView = view.findViewById(R.id.fileNameTextView);
            TextView sizeTextView = view.findViewById(R.id.sizeTextView);
            TextView dateTextView = view.findViewById(R.id.dateTextView);
            int color = resources.getColor(R.color.textColorPrimary, context.getTheme());
            fileNameTextView.setTextColor(color);
            sizeTextView.setTextColor(color);
            dateTextView.setTextColor(color);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (isSelected(position)) {
            return SELECTED_ITEM_TYPE;
        }
        return NORMAL_ITEM_TYPE;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final File file = fileArrayList.get(position);
        holder.setFileNameText(file.getName());
        holder.setIcon(file.getAbsolutePath(), file.isDirectory());
        holder.setDate(file.lastModified());
        holder.setSize(file.length());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectItemListener != null) {
                    onSelectItemListener.onClickItem(file, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onSelectItemListener != null) {
                    return onSelectItemListener.onLongClickItem(file, position);
                }
                return true;
            }
        });
        holder.getInfoButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectItemListener != null) {
                    onSelectItemListener.onInfoButtonClick(v, file, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView dateText;
        private TextView sizeText;
        private ImageView iconView;
        private ImageButton infoButton;

        public ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.iconImageView);
            nameText = itemView.findViewById(R.id.fileNameTextView);
            infoButton = itemView.findViewById(R.id.fileInfoButtonView);
            dateText = itemView.findViewById(R.id.dateTextView);
            sizeText = itemView.findViewById(R.id.sizeTextView);
        }

        public TextView getNameText() {
            return nameText;
        }

        public ImageButton getInfoButton() {
            return infoButton;
        }

        public ImageView getIconView() {
            return iconView;
        }

        public void setFileNameText(String filename) {
            getNameText().setText(filename);
        }

        public void setIcon(String absolutePath, boolean isDirectory) {
            if (isDirectory) {
                setDirectoryIcon();
            } else {
                boolean showImageThumbnail = PreferenceManager
                        .getDefaultSharedPreferences(context)
                        .getBoolean("show_image_thumbnail", false);
                if (showImageThumbnail) {
                    setFileIconWithThumbnail(absolutePath);
                } else {
                    setFileIcon(absolutePath);
                }
            }
        }

        public void setDate(long time) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateText.setText(simpleDateFormat.format(new Date(time)));
        }

        public void setSize(long bytes) {
            sizeText.setText(Formatter.formatFileSize(context, bytes));
        }

        private void setDirectoryIcon() {
            ImageView iconView = getIconView();
            iconView.setImageBitmap(null);
            iconView.setBackgroundResource(getExtensionResource(FOLDER_EXTENSION));
        }

        private void setFileIcon(String absolutePath) {
            String extension = FileUtils.getFileExtension(absolutePath);
            if (extension != null) extension = extension.toLowerCase();
            Integer resource = getExtensionResource(extension);
            int resId = resource == null ? getExtensionResource(FILE_EXTENSION) : resource;
            ImageView iconView = getIconView();
            iconView.setImageBitmap(null);
            iconView.setBackgroundResource(resId);
        }

        private void setFileIconWithThumbnail(String absolutePath) {
            String contentType = URLConnection.guessContentTypeFromName(absolutePath);
            setFileIcon(absolutePath);

            if (contentType != null && contentType.startsWith("image")) {
                new ImageThumbnailLoader().execute(absolutePath);
            }
        }

        private class ImageThumbnailLoader extends AsyncTask<String, Integer, Bitmap> {
            private static final int THUMBNAIL_WIDTH = 48;
            private static final int THUMBNAIL_HEIGHT = 48;

            @Override
            protected Bitmap doInBackground(String... paths) {
                Bitmap bitmap = BitmapFactory.decodeFile(paths[0]);
                if (bitmap != null) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, false);
                    if (scaledBitmap != null) {
                        return scaledBitmap;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                getIconView().setImageBitmap(bitmap);
            }
        }
    }

    public interface OnSelectItemListener {
        void onClickItem(File file, int position);
        boolean onLongClickItem(File file, int position);
        void onInfoButtonClick(View view, File file, int position);
    }

}

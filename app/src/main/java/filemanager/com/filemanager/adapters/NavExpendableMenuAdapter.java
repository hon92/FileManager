package filemanager.com.filemanager.adapters;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import filemanager.com.filemanager.R;

/**
 * Created by Honza on 19.12.2017.
 */

public class NavExpendableMenuAdapter extends ExpendableListAdapter {

    public NavExpendableMenuAdapter(Context context) {
        super(context);
    }

    public MenuGroupItem createMenuGroupItem(String name) {
        MenuGroupItem menuGroupItem = new MenuGroupItem(context, name);
        addGroupItem(menuGroupItem);
        return menuGroupItem;
    }

    public StorageMenuChildItem createStorageMenuChildItem(String name, String itemId, File directory, int iconResourceId, MenuGroupItem parent) {
        StorageMenuChildItem storageMenuChildItem = new StorageMenuChildItem(context, parent, name, iconResourceId, itemId, directory);
        addChildItem(storageMenuChildItem);
        return storageMenuChildItem;
    }

    public NavMenuChildItem createNavMenuChildItem(String name, String itemId, File directory, int iconResourceId, MenuGroupItem parent) {
        NavMenuChildItem navMenuChildItem = new NavMenuChildItem(context, parent, name, iconResourceId, itemId, directory);
        addChildItem(navMenuChildItem);
        return navMenuChildItem;
    }

    public SimpleMenuChildItem createSimpleChildItem(String name, String itemId, int iconResourceId, MenuGroupItem parent) {
        SimpleMenuChildItem simpleMenuChildItem = new SimpleMenuChildItem(context, parent, name, iconResourceId, itemId);
        addChildItem(simpleMenuChildItem);
        return simpleMenuChildItem;
    }

    public static class MenuGroupItem extends ExpendableListAdapter.GroupItem{
        private String name;

        public MenuGroupItem(Context context, String name) {
            super(context);
            this.name = name;
        }

        @Override
        public View getView(ViewGroup parent, boolean attachToRoot) {
            View view = LayoutInflater.from(context).inflate(R.layout.group_item_layout, parent, false);
            TextView groupTextView = view.findViewById(R.id.groupTextView);
            groupTextView.setText(name);
            return view;
        }
    }

    public static abstract class MenuChildItem extends ExpendableListAdapter.ChildItem {
        private String name;
        private int iconResId;
        private String itemId;

        public MenuChildItem(Context context, GroupItem parent, String name, int iconResId, String itemId) {
            super(context, parent);
            this.name = name;
            this.iconResId = iconResId;
            this.itemId = itemId;
        }

        public String getId() {
            return itemId;
        }

        public String getName() {
            return name;
        }

        public int getIconResId() {
            return iconResId;
        }
    }

    public static class NavMenuChildItem extends MenuChildItem {
        private File directory;

        public NavMenuChildItem(Context context, GroupItem parent, String name, int iconResId, String itemId, File directory) {
            super(context, parent, name, iconResId, itemId);
            this.directory = directory;
        }

        public File getDirectory() {
            return directory;
        }

        @Override
        public View getView(ViewGroup parent, boolean attachToRoot) {
            View view = LayoutInflater.from(context).inflate(R.layout.nav_child_item_layout, parent, false);
            ImageView iconImageView = view.findViewById(R.id.iconImageView);
            iconImageView.setBackgroundResource(getIconResId());
            TextView nameTextView = view.findViewById(R.id.nameTextView);
            TextView pathTextView = view.findViewById(R.id.pathTextView);
            nameTextView.setText(getName());
            pathTextView.setText(directory.getAbsolutePath());
            return view;
        }
    }

    public static class StorageMenuChildItem extends NavMenuChildItem {

        public StorageMenuChildItem(Context context, GroupItem parent, String name, int iconResId, String itemId, File directory) {
            super(context, parent, name, iconResId, itemId, directory);
        }

        @Override
        public View getView(ViewGroup parent, boolean attachToRoot) {
            View view = LayoutInflater.from(context).inflate(R.layout.storage_child_item_layout, parent, false);
            ImageView iconImageView = view.findViewById(R.id.iconImageView);
            TextView nameTextView = view.findViewById(R.id.nameTextView);
            TextView freeTextView = view.findViewById(R.id.freeTextView);
            TextView usedTextView = view.findViewById(R.id.usedTextView);
            TextView totalTextView = view.findViewById(R.id.totalTextView);

            File directory = getDirectory();
            if (directory.getAbsolutePath().equals("/")) {
                directory = new File("/system");
            }
            long totalSpace = directory.getTotalSpace();
            long freeSpace = directory.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;

            iconImageView.setBackgroundResource(getIconResId());
            nameTextView.setText(getName());
            freeTextView.setText(Formatter.formatFileSize(context, freeSpace));
            usedTextView.setText(Formatter.formatFileSize(context, usedSpace));
            totalTextView.setText(Formatter.formatFileSize(context, totalSpace));
            return view;
        }
    }

    public static class SimpleMenuChildItem extends MenuChildItem {

        public SimpleMenuChildItem(Context context, GroupItem parent, String name, int iconResId, String itemId) {
            super(context, parent, name, iconResId, itemId);
        }

        @Override
        public View getView(ViewGroup parent, boolean attachToRoot) {
            View view = LayoutInflater.from(context).inflate(R.layout.simple_child_item_layout, parent, false);
            ImageView iconImageView = view.findViewById(R.id.iconImageView);
            iconImageView.setBackgroundResource(getIconResId());
            TextView nameTextView = view.findViewById(R.id.nameTextView);
            nameTextView.setText(getName());
            return view;
        }
    }

}

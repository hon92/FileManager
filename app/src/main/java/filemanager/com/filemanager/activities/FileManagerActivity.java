package filemanager.com.filemanager.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import filemanager.com.filemanager.R;
import filemanager.com.filemanager.adapters.ExpendableListAdapter;
import filemanager.com.filemanager.adapters.NavExpendableMenuAdapter;
import filemanager.com.filemanager.adapters.PageAdapter;
import filemanager.com.filemanager.components.FileOperations;
import filemanager.com.filemanager.components.ViewPager;
import filemanager.com.filemanager.pages.Page;
import filemanager.com.filemanager.utils.FileComparator;
import filemanager.com.filemanager.utils.FileSortByDate;
import filemanager.com.filemanager.utils.FileSortByName;
import filemanager.com.filemanager.utils.FileSortBySize;

/**
 * Created by Honza on 22.11.2017.
 */

public class FileManagerActivity extends AppCompatActivity {
    private static final int READ_EX_STORAGE_PERMISSION = 1000;
    private static final int WRITE_EX_STORAGE_PERMISSION = 1001;

    private static final int MAX_PAGE_COUNT = 10;
    private static final String BOOKMARK_PREFIX = "__bookmark";

    private boolean doubleTimesBackPressed = false;
    private DrawerLayout drawerLayout;
    private FileOperations fileOperations;
    private PageAdapter pageAdapter;
    private ViewPager viewPager;
    private NavExpendableMenuAdapter navExpendableMenuAdapter;
    private NavExpendableMenuAdapter.MenuGroupItem bookmarksGroup;
    private NavExpendableMenuAdapter.MenuGroupItem storageGroup;
    private SearchView searchView;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer_layout);
        showRequestPermissionIfNeeded();
        prepareLayout();
        fileOperations = new FileOperations(this);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        addPage();
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(MAX_PAGE_COUNT);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private void showRequestPermissionIfNeeded() {
        int readExStorageCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExStorageCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readExStorageCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EX_STORAGE_PERMISSION);
        }
        if (writeExStorageCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EX_STORAGE_PERMISSION);
        }
    }

    @SuppressWarnings("deprecation")
    private void prepareLayout() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.main_drawer_layout_open,
                R.string.main_drawer_layout_close
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.syncState();
        prepareNavigationView();
    }

    private void prepareNavigationView() {
        navExpendableMenuAdapter = new NavExpendableMenuAdapter(this);
        storageGroup = navExpendableMenuAdapter.createMenuGroupItem("Storages");
        navExpendableMenuAdapter.createStorageMenuChildItem(
                "Root directory",
                "_.root",
                new File("/"),
                R.drawable.internal_storage,
                storageGroup);
        navExpendableMenuAdapter.createStorageMenuChildItem(
                "Internal directory",
                "_.internal",
                Environment.getExternalStorageDirectory(),
                R.drawable.internal_storage,
                storageGroup);
        navExpendableMenuAdapter.createStorageMenuChildItem(
                "External directory",
                "_.external",
                getMicroSDDirectory(),
                R.drawable.external_storage,
                storageGroup);

        bookmarksGroup = navExpendableMenuAdapter.createMenuGroupItem("Bookmarks");
        navExpendableMenuAdapter.createNavMenuChildItem(
                "Downloads",
                "_.downloads",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                R.drawable.documents_folder,
                bookmarksGroup);
        navExpendableMenuAdapter.createNavMenuChildItem(
                "Camera",
                "_.camera",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                R.drawable.pictures_folder,
                bookmarksGroup);
        navExpendableMenuAdapter.createNavMenuChildItem(
                "Documents",
                "_.documents",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                R.drawable.documents_folder,
                bookmarksGroup);
        navExpendableMenuAdapter.createNavMenuChildItem(
                "Music",
                "_.music",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                R.drawable.music_folder,
                bookmarksGroup);
        navExpendableMenuAdapter.createNavMenuChildItem(
                "Pictures",
                "_.pictures",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                R.drawable.pictures_folder,
                bookmarksGroup);
        navExpendableMenuAdapter.createNavMenuChildItem(
                "Movies",
                "_.movies",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                R.drawable.movies_folder,
                bookmarksGroup);

        NavExpendableMenuAdapter.MenuGroupItem othersGroup = navExpendableMenuAdapter.createMenuGroupItem("Others");
        navExpendableMenuAdapter.createSimpleChildItem(
                "Ftp server",
                "ftp",
                R.drawable.ftp_folder,
                othersGroup);

        loadBookmarksFromPreferences();

        NavigationView navigationView = findViewById(R.id.navigationView);
        ImageButton imageButton = navigationView.findViewById(R.id.settingImageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FileManagerActivity.this, SettingsActivity.class));
            }
        });

        final ExpandableListView expandableListView = navigationView.findViewById(R.id.expandableListView);
        expandableListView.setAdapter(navExpendableMenuAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ExpendableListAdapter.ChildItem childItem = navExpendableMenuAdapter.getChild(groupPosition, childPosition);
                if (childItem instanceof NavExpendableMenuAdapter.NavMenuChildItem) {
                    NavExpendableMenuAdapter.NavMenuChildItem item = (NavExpendableMenuAdapter.NavMenuChildItem) childItem;
                    File newDirectory = item.getDirectory();
                    Page currentPage = getCurrentPage();
                    if (currentPage != null && newDirectory != null) {
                        currentPage.setDirectory(newDirectory);
                    }
                    drawerLayout.closeDrawer(Gravity.START);
                    return false;
                } else if (childItem instanceof NavExpendableMenuAdapter.SimpleMenuChildItem) {
                    NavExpendableMenuAdapter.SimpleMenuChildItem simpleItem = (NavExpendableMenuAdapter.SimpleMenuChildItem) childItem;
                    switch (simpleItem.getId()) {
                        case "ftp":
                            startActivity(new Intent(FileManagerActivity.this, FtpActivity.class));
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    final int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    final int childPosition = ExpandableListView.getPackedPositionChild(id);

                    ExpendableListAdapter.ChildItem childItem = navExpendableMenuAdapter.getChild(groupPosition, childPosition);
                    if (childItem instanceof NavExpendableMenuAdapter.NavMenuChildItem) {
                        final NavExpendableMenuAdapter.NavMenuChildItem navItem = (NavExpendableMenuAdapter.NavMenuChildItem) childItem;
                        if (navItem.getId().startsWith("_.")) return true;

                        PopupMenu popupMenu = new PopupMenu(FileManagerActivity.this, view, Gravity.DISPLAY_CLIP_VERTICAL);
                        popupMenu.inflate(R.menu.bookmark_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.bookmarkRemoveItem:
                                        removeBookmark(navItem, groupPosition, childPosition);
                                        break;
                                }
                                drawerLayout.closeDrawer(Gravity.START);
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                }
                return true;
            }
        });
    }

    private void loadBookmarksFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, Object> preferences = (Map<String, Object>) sharedPreferences.getAll();
        for(Map.Entry<String, Object> entry: preferences.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(BOOKMARK_PREFIX)) {
                String bookmarkName = key.replace(BOOKMARK_PREFIX, "");
                String bookmarkPath = (String) entry.getValue();
                navExpendableMenuAdapter.createNavMenuChildItem(
                        bookmarkName,
                        "_" + bookmarkName,
                        new File(bookmarkPath),
                        R.drawable.folder,
                        bookmarksGroup);
            }
        }
    }

    public Page getCurrentPage() {
        return pageAdapter.getPage(viewPager.getCurrentItem());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.searchItem).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFiles(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFiles(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                filterFiles("");
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
            return;
        }

        boolean changed = changeDirectoryUp();
        if (!changed) {

            if (doubleTimesBackPressed) {
                super.onBackPressed();
                return;
            }

            doubleTimesBackPressed = true;
            Toast.makeText(getBaseContext(), "Press one more time to close application.", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTimesBackPressed = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EX_STORAGE_PERMISSION:
            case WRITE_EX_STORAGE_PERMISSION:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
                break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean changeDirectoryUp() {
        Page currentPage = getCurrentPage();
        if (currentPage != null) {
            return currentPage.moveToParentDirectory();
        }
        return false;
    }

    public FileOperations getFileOperations() {
        return fileOperations;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exitItem:
                finish();
                break;
            case R.id.pasteItem:
                pasteFiles();
                break;
            case R.id.addPageItem:
                addPage();
                break;
            case R.id.closePageItem:
                closePage();
                break;
            case R.id.createFolderItem:
                createFolder();
                break;
            case R.id.createFileItem:
                createFile();
                break;
            case R.id.sortByDateItem:
                sortFiles(new FileSortByDate());
                break;
            case R.id.sortByNameItem:
                sortFiles(new FileSortByName());
                break;
            case R.id.sortBySizeItem:
                sortFiles(new FileSortBySize());
                break;
            case R.id.addBookmarkItem:
                addDirectoryToBookmarks();
                break;
        }
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem pasteMenuItem = menu.findItem(R.id.pasteItem);
        pasteMenuItem.setVisible(!fileOperations.hasEmptyClipboard());

        MenuItem closePageItem = menu.findItem(R.id.closePageItem);
        closePageItem.setVisible(pageAdapter.getCount() > 1);

        MenuItem addPageItem = menu.findItem(R.id.addPageItem);
        addPageItem.setVisible(pageAdapter.getCount() < MAX_PAGE_COUNT);

        MenuItem addBookmarkItem = menu.findItem(R.id.addBookmarkItem);
        addBookmarkItem.setVisible(!containCurrentDirInBookmarks());
        return super.onPrepareOptionsMenu(menu);
    }

    private void pasteFiles() {
        Page currentPage = getCurrentPage();
        if (currentPage != null) {
            fileOperations.pasteFile(currentPage.getDirectory());
        }
    }

    private void addPage() {
        Page page = new Page();
        page.setFileManagerActivity(this);
        pageAdapter.addPage(page);
    }

    private void closePage() {
        if (pageAdapter.getCount() > 1) {
            pageAdapter.removePage(viewPager.getCurrentItem());
            if (viewPager != null) {
                viewPager.setCurrentItem(0);
            }
        }
    }

    private void createFolder() {
        Page currentPage = getCurrentPage();
        if (currentPage != null) {
            fileOperations.createFolder(currentPage.getDirectory());
        }
    }

    private void createFile() {
        Page currentPage = getCurrentPage();
        if (currentPage != null) {
            fileOperations.createFile(currentPage.getDirectory());
        }
    }

    public void reloadPageFiles() {
        Page currentPage = getCurrentPage();
        if (currentPage != null) {
            currentPage.reloadFiles();
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    private File getMicroSDDirectory() {
        File emulatedDir = Environment.getExternalStorageDirectory().getParentFile();
        if (emulatedDir == null || !emulatedDir.exists()) return null;
        File storageDir = emulatedDir.getParentFile();
        if (storageDir == null || !storageDir.exists()) return null;
        File[] storageFiles = storageDir.listFiles();
        if (storageFiles == null) return null;

        List<String> defaultDirectories = new ArrayList<>();
        defaultDirectories.add("emulated");
        defaultDirectories.add("remote");
        defaultDirectories.add("self");

        for(File file: storageFiles) {
            if (!defaultDirectories.contains(file.getName())) {
                return file;
            }
        }
        return null;
    }

    private void sortFiles(FileComparator comparator) {
        Page currentPage = getCurrentPage();
        if (currentPage != null) {
            boolean showFoldersFirst = PreferenceManager.getDefaultSharedPreferences(
                    getBaseContext())
                    .getBoolean("show_folders_first", false);
            comparator.setSortDirectorySeparatory(showFoldersFirst);
            currentPage.getFileAdapter().sortItems(comparator);
        }
    }

    private void filterFiles(String query) {
        Page page = getCurrentPage();
        if (page != null) {
            page.filterFiles(query);
        }
    }

    private void addDirectoryToBookmarks() {
        Page page = getCurrentPage();
        if (page != null) {
            File directory = page.getDirectory();
            String dirName = directory.getName();
            navExpendableMenuAdapter.createNavMenuChildItem(
                    dirName,
                    "_" + dirName,
                    directory,
                    R.drawable.folder,
                    bookmarksGroup);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(BOOKMARK_PREFIX + dirName, directory.getAbsolutePath());
            editor.commit();
            invalidateOptionsMenu();
            Snackbar.make(
                    findViewById(R.id.constraintLayout),
                    String.format("Bookmark \"%s\" was added to bookmarks.", dirName),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void removeBookmark(NavExpendableMenuAdapter.NavMenuChildItem item, int groupPos, int childPos) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(BOOKMARK_PREFIX + item.getName());
        editor.commit();
        navExpendableMenuAdapter.removeChildItem(groupPos, childPos);
        invalidateOptionsMenu();
        Snackbar.make(
                findViewById(R.id.constraintLayout),
                String.format("Bookmark \"%s\" was removed from bookmarks.", item.getName()),
                Snackbar.LENGTH_SHORT).show();
    }

    private boolean containCurrentDirInBookmarks() {
        Page page = getCurrentPage();
        if (page == null) return true; // pretend that dir exists when page is null -> cant add bookmark
        File currDirectory = page.getDirectory();
        return containNavChildDirectory(storageGroup, currDirectory) || containNavChildDirectory(bookmarksGroup, currDirectory);
    }

    private boolean containNavChildDirectory(NavExpendableMenuAdapter.MenuGroupItem groupItem, File currDirectory) {
        for(ExpendableListAdapter.ChildItem item: groupItem.getChildren()) {
            if (item instanceof NavExpendableMenuAdapter.NavMenuChildItem) {
                NavExpendableMenuAdapter.NavMenuChildItem navItem = (NavExpendableMenuAdapter.NavMenuChildItem) item;
                if (currDirectory.getAbsolutePath().equals(navItem.getDirectory().getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }
}

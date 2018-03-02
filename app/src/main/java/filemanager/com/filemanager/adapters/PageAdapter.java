package filemanager.com.filemanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import filemanager.com.filemanager.pages.Page;

/**
 * Created by Honza on 04.12.2017.
 */

public class PageAdapter extends FragmentStatePagerAdapter {
    private List<Page> pages;

    public PageAdapter(FragmentManager fm) {
        super(fm);
        pages = new ArrayList<>();
    }

    public void addPage(Page page) {
        pages.add(page);
        notifyDataSetChanged();
    }

    public void removePage(int position) {
        if (position >= 0 && position < pages.size()) {
            pages.remove(position);
            notifyDataSetChanged();
        }
    }

    public Page getPage(int position) {
        if (position >= 0 && position < pages.size()) {
            return pages.get(position);
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

}

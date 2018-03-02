package filemanager.com.filemanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Honza on 26.11.2017.
 */

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private SparseBooleanArray selectedItems;
    private SelectionListener selectionListener;

    public SelectableAdapter() {
        selectedItems = new SparseBooleanArray();
    }

    public void setSelectionListener(SelectionListener listener) {
        selectionListener = listener;
    }

    private void notifySelectionChange() {
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(getSelectedItems());
        }
    }

    public void selectFileAt(int position) {
        selectedItems.put(position, true);
        notifyItemChanged(position);
        notifySelectionChange();
    }

    public void unSelectFileAt(int position) {
        selectedItems.delete(position);
        notifyItemChanged(position);
        notifySelectionChange();
    }

    public void toggleSelectionAt(int position) {
        if (isSelected(position)) {
            unSelectFileAt(position);
        } else {
            selectFileAt(position);
        }
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
        notifySelectionChange();
    }

    public boolean isSelected(int position) {
        return selectedItems.get(position);
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for(int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public interface SelectionListener {
        void onSelectionChanged(List<Integer> positions);
    }

}

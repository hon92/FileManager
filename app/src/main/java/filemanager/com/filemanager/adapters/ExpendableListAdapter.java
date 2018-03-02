package filemanager.com.filemanager.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Honza on 19.12.2017.
 */

public abstract class ExpendableListAdapter extends BaseExpandableListAdapter {
    private List<GroupItem> groups;
    protected Context context;

    public ExpendableListAdapter(Context context) {
        this.context = context;
        groups = new ArrayList<>();
    }

    public void addGroupItem(GroupItem groupItem) {
        groups.add(groupItem);
        notifyDataSetChanged();
    }

    public void addChildItem(ChildItem childItem) {
        childItem.getParent().addChild(childItem);
        notifyDataSetChanged();
    }

    public void removeChildItem(int groupPos, int childPos) {
        if (groupPos >= 0 && groupPos < groups.size()) {
            GroupItem groupItem = groups.get(groupPos);
            groupItem.removeChild(childPos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getChildren().size();
    }

    @Override
    public GroupItem getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public ChildItem getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupItem groupItem = getGroup(groupPosition);
        return groupItem.getView(parent, false);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildItem childItem = getChild(groupPosition, childPosition);
        return childItem.getView(parent, false);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static abstract class GroupItem {
        protected final Context context;
        private List<ChildItem> children;

        public GroupItem(Context context) {
            this.context = context;
            this.children = new ArrayList<>();
        }
        public abstract View getView(ViewGroup parent, boolean attachToRoot);

        public void addChild(ChildItem item) {
            this.children.add(item);
        }

        public void removeChild(int pos) {
            if (pos >= 0 && pos < children.size()) {
                children.remove(pos);
            }
        }

        public List<ChildItem> getChildren() {
            return children;
        }
    }

    public static abstract class ChildItem {
        protected final Context context;
        protected final GroupItem parent;

        public ChildItem(Context context, GroupItem parent) {
            this.context = context;
            this.parent = parent;
        }

        public abstract View getView(ViewGroup parent, boolean attachToRoot);

        public GroupItem getParent() {
            return parent;
        }

    }
}

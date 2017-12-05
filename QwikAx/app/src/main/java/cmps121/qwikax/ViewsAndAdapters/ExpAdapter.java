package cmps121.qwikax.ViewsAndAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cmps121.qwikax.MainActivity;
import cmps121.qwikax.R;

/**
 * Created by matthew on 12/4/17.
 */

public class ExpAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<Group> _groups;

    public ExpAdapter(Context context, ArrayList<Group> groups) {
        this._context = context;
        this._groups = groups;
    }


    @Override
    public Child getChild(int i, int j) {
        ArrayList<Child> childList = _groups.get(i).getItems();
        return childList.get(j);
    }

    @Override
    public long getChildId(int i, int j) {
        return j;
    }

    @Override
    public int getChildrenCount(int i) {
        ArrayList<Child> childList = _groups.get(i).getItems();
        return childList.size();
    }

    @Override
    public Object getGroup(int i) {
        return _groups.get(i);
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int i, int j) {
        return true;
    }

    @Override
    public View getChildView(int i, int j, boolean isLastChild, View convertView, ViewGroup parent) {

        Child child = (Child) getChild(i, j);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) _context
                    .getSystemService(_context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView appName = (TextView) convertView.findViewById(R.id.lblListItem);
        ImageView appFlag = (ImageView) convertView.findViewById(R.id.priorityFlag);

        appName.setText(child.getName().toString());
        appFlag.setImageDrawable(child.getImage());

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Group group = (Group) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.list_group, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.lblListHeader);
        tv.setText(group.getName());
        return convertView;
    }

}

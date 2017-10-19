package cmps121.qwikax.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

import cmps121.qwikax.Node_Related.IndividualNode;
import cmps121.qwikax.R;

/**
 * Created by andrew on 10/4/2017.
 */

// Allows us to set up the grid view in a custom manor rather than the existing adapter.
public class CustomGridAdapter extends ArrayAdapter<IndividualNode>
{

    // FIELDS

    private Activity _activity;
    private int _resourceLayoutID;
    private List<IndividualNode> _items;
    private int _rows;
    private int _columns;
    private int _yDistance;

    // FIELDS

    // CONSTRUCTOR

    //Constructor to initialize values
    public CustomGridAdapter(Activity activity, int resourceLayout, List<IndividualNode> items, int rows, int columns, int yDistance) {
        super(activity, 0, items);
        _activity = activity;
        _resourceLayoutID = resourceLayout;
        _items = items;
        _rows = rows;
        _columns = columns;
        _yDistance = yDistance;
    }

    // CONSTRUCTOR


    // METHODS

    // Gets the amount of objects in the grid view
    @Override
    public int getCount() {

        // Number of times getView method call depends upon the number of rows and columns
        return _rows * _columns;
    }

    // Does nothing right now, might never need a use for this, but it was necessary for construction
    @Override
    public long getItemId(int position) {
        return 0;
    }


    // Number of times getView method call depends upon gridValues.length
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null)
        {
            LayoutInflater inflater = _activity.getLayoutInflater();
            convertView = inflater.inflate(_resourceLayoutID, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();

        IndividualNode item = _items.get(position);
        convertView.setLayoutParams(new ViewGroup.LayoutParams(GridView.AUTO_FIT, _yDistance));

        ViewGroup.LayoutParams params = convertView.getLayoutParams();
        params.height = _yDistance;
        convertView.setLayoutParams(params);

        if(item.isHighLight())
            holder.background.setBackgroundColor(Color.BLUE);
        else
            holder.background.setBackgroundColor(Color.TRANSPARENT);

        return convertView;
    }

    // METHODS

    // HANDLER CLASS

    // This class handles the individual objects inside the grid layout
    private static class ViewHolder {
        private ImageView image;
        private ViewGroup background;

        public ViewHolder(View v) {
            image = (ImageView) v.findViewById(R.id.nodeImageView);
            background = (ViewGroup) v.findViewById(R.id.nodeLayout);
        }
    }

    // HANDLER CLASS
}

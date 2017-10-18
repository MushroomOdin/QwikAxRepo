package cmps121.qwikax;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by andrew on 10/4/2017.
 */

public class CustomGridAdapter extends BaseAdapter
{

    private Context _context;
    private int _rows;
    private int _columns;

    //Constructor to initialize values
    public CustomGridAdapter(Context context, int rows, int columns) {

        _context = context;
        _rows = rows;
        _columns = columns;
    }

    @Override
    public int getCount() {

        // Number of times getView method call depends upon gridValues.length
        return _rows * _columns;
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }


    // Number of times getView method call depends upon gridValues.length
    public View getView(int position, View convertView, ViewGroup parent) {

        // LayoutInflator to call external grid_item.xml file
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(_context);
            // get layout from grid_item.xml ( Defined Below )
            gridView = inflater.inflate(R.layout.node , null);

        } else {

            gridView = (View) convertView;
        }

        return gridView;
    }
}

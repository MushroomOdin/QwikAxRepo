package cmps121.qwikax;


import android.content.ClipData;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    // FIELDS

    private GridView _gridView;
    private int _rows;
    private int _columns;
    private int _startPosition;

    private CustomGridAdapter _adapter;
    private ArrayList<NodeOfGridView> _items;
    private ListView _listView;
    // FIELDS

    // METHODS

    private View FindViewByLocation(float x, float y, View parentView) {
        float[][] areasOfViews = new float[4][_rows*_columns];
        View view;
        // Find out how to tell location from
        view = _gridView.getChildAt(1);

        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _rows = 15;
        _columns = 15;

        _gridView = (GridView) findViewById(R.id.gridView);
        _gridView.setNumColumns(_columns);
        SetAdapter();

        String[] arrayItemsInList = new String[] { "cat", "dog", "llama", "small", "retrieve", "ball", "animal"};

        ArrayList<String> itemsInList = new ArrayList<String>();
        itemsInList.addAll(Arrays.asList(arrayItemsInList));
        _listView = (ListView) findViewById(R.id.applicationListView);
        _listView.setAdapter(new ArrayAdapter<String>(this, R.layout.main_row, itemsInList));

        // Using a click on an item inside the grid view as a means to start the highlighting.
        _gridView.setOnTouchListener(new CustomTouchListener());
        //_gridView.setOnDragListener(new CustomDragListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(id) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "you clicked settings", Toast.LENGTH_LONG).show();
                // I believe i need to use intents and then launch the layout wiht the intent
                break;

            case R.id.action_profile:
                Toast.makeText(getApplicationContext(), "you clicked profile", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Sets the adapter to the grid view.
    private void SetAdapter() {
        _items = new ArrayList<NodeOfGridView>();

        for (int i = 0; i < _rows * _columns; i++)
            _items.add(i, new NodeOfGridView(R.drawable.main, false));

        _adapter = new CustomGridAdapter(this, R.layout.node, _items, _rows, _columns);
        _gridView.setAdapter(_adapter);
    }

    // METHODS

    // CUSTOM LISTENER

    private final class CustomTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean value = false;

            View node;

            switch(motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    node = FindViewByLocation(motionEvent.getX(), motionEvent.getY(), view);
                    node.setBackgroundColor(Color.BLUE);
                    value = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    node = FindViewByLocation(motionEvent.getX(), motionEvent.getY(), view);
                    if(((ColorDrawable)node.getBackground()).getColor() == Color.TRANSPARENT)
                        node.setBackgroundColor(Color.BLUE);
                    else
                        node.setBackgroundColor(Color.TRANSPARENT);

                    value = true;
                    break;
            }

            return value;
        }
    }

    /*private class CustomDragListener implements View.OnDragListener{

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch(event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Toast.makeText(getApplicationContext(), "you started a drag", Toast.LENGTH_SHORT).show();
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:

                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    Toast.makeText(getApplicationContext(), "you finished a drag", Toast.LENGTH_SHORT).show();
                    break;

                case DragEvent.ACTION_DROP:

                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.TRANSPARENT);

                default:
                    break;
            }

            return true;
        }

    }*/
    // CUSTOM LISTENER
}

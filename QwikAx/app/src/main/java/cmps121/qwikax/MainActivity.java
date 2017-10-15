package cmps121.qwikax;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import cmps121.qwikax.Adapters.CustomGridAdapter;
import cmps121.qwikax.Node_Related.CoordinateSystem;
import cmps121.qwikax.Node_Related.IndividualNode;

public class MainActivity extends AppCompatActivity {

    // FIELDS

    // Testing for push

    private GridView _gridView;
    private int _rows;
    private int _columns;

    private CustomGridAdapter _adapter;
    private ArrayList<IndividualNode> _items;
    private ListView _listView;
    private ArrayList<Integer> _pointsHit;
    // FIELDS

    // METHODS

    // TODO: Need to clean this and its called fucntions, i am missing something such as padding or something in the calculation.
    private View FindViewByLocation(float x, float y, View parentView) {
        View view;
        int count = 0;
        // Find out how to tell location from
        for (IndividualNode node: _items) {
            if((node.get_coordinateSystem().isWithinBounds(x,y)) && (!node.isHighLight())){
                node.setHighLight(true);
                _pointsHit.add(count);
                break;
            }

            count++;
        }

        if(count < _items.size())
            view = _gridView.getChildAt(count);
        else
            view = null;

        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _rows = 8;
        _columns = 8;

        _gridView = (GridView) findViewById(R.id.gridView);
        _gridView.setNumColumns(_columns);
        SetAdapter();

        final String[] arrayItemsInList = new String[] { "cat", "dog", "llama", "small", "retrieve", "ball", "animal", "mammal", "Hello", "is", "it", "me", "your", "looking", "for"};

        ArrayList<String> itemsInList = new ArrayList<String>();
        itemsInList.addAll(Arrays.asList(arrayItemsInList));
        _listView = (ListView) findViewById(R.id.applicationListView);
        _listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_view_row, itemsInList));
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), arrayItemsInList[i], Toast.LENGTH_SHORT).show();
            }
        });

        // Using a click on an item inside the grid view as a means to start the highlighting.
        _gridView.setOnTouchListener(new CustomTouchListener());
        _pointsHit = new ArrayList<>();

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
                //Toast.makeText(getApplicationContext(), "you clicked settings", Toast.LENGTH_LONG).show();

                // Create new intent and launch the layout with the intent
                Intent startSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(startSettings);

                break;

            case R.id.action_profile:
                Toast.makeText(getApplicationContext(), "you clicked profile", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Sets the adapter to the grid view.
    private void SetAdapter() {
        _items = new ArrayList<IndividualNode>();
        int xPos = 0, yPos = 0, xDistance, yDistance;
        double weightFactor = .666666;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        xDistance = size.x / _columns;
        yDistance = (int) (size.y * weightFactor) / _rows;
        // TODO: need to fix this so it give the proper coordinates
        for (int i = 0; i < _rows * _columns; i++)
        {
            _items.add(i, new IndividualNode(R.drawable.main, false,
                    new CoordinateSystem(xPos,yPos,xDistance,yDistance,getWindowManager().getDefaultDisplay().getRotation())));

            if((xPos += xDistance) >= size.x) {
                xPos = 0;
                yPos += yDistance;
            }



        }


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
                    if(node != null){
                        if(((ColorDrawable)node.getBackground()).getColor() == Color.TRANSPARENT)
                            node.setBackgroundColor(Color.BLUE);
                        
                        value = true;
                    }

                    break;

                case MotionEvent.ACTION_MOVE:
                    node = FindViewByLocation(motionEvent.getX(), motionEvent.getY(), view);
                    if(node != null)
                    {
                        if(((ColorDrawable)node.getBackground()).getColor() == Color.TRANSPARENT)
                            node.setBackgroundColor(Color.BLUE);

                        value = true;
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    // Using this to restore the grid view back to its default view.
                    StringBuilder sentence = new StringBuilder();
                    for (int point: _pointsHit) {
                        sentence.append(point + " ");
                        _items.get(point).setHighLight(false);
                        node = _gridView.getChildAt(point);
                        node.setBackgroundColor(Color.TRANSPARENT);
                    }

                    Toast.makeText(getApplicationContext(), sentence.toString(), Toast.LENGTH_LONG).show();
                    break;
            }

            return value;
        }
    }
    // CUSTOM LISTENER
}

package cmps121.qwikax;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
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
import cmps121.qwikax.Data_Base.Movement;
import cmps121.qwikax.Node_Related.CoordinateSystem;
import cmps121.qwikax.Node_Related.IndividualNode;

public class MainActivity extends AppCompatActivity {

    // FIELDS

    private GridView _gridView;
    private int _rows;
    private int _columns;

    private CustomGridAdapter _adapter;
    private ArrayList<IndividualNode> _items;
    private ListView _listView;
    private ArrayList<Integer> _pointsHit;
    private Movement _movements;

    // FIELDS

    // METHODS

    // TODO: Need to clean this and its called fucntions, i am missing something such as padding or something in the calculation.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _rows = 10;
        _columns = 10;

        _gridView = (GridView) findViewById(R.id.gridView);
        _gridView.setNumColumns(_columns);
        SetAdapter();

        _movements = new Movement(_items, _rows, _columns);
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
        _items = new ArrayList<IndividualNode>();
        double xPos = 0, yPos = 0, xDistance, yDistance;

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();

        xDistance = width  / _columns;
        yDistance = (height *.8) / _rows - 20;

        //_gridView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, (int) (height*.75)));

        // TODO: need to fix this so it give the proper coordinates
        for (int i = 0; i < _rows * _columns; i++)
        {
            _items.add(i, new IndividualNode(R.drawable.main, false,
                    new CoordinateSystem(xPos,yPos,xDistance,yDistance,getWindowManager().getDefaultDisplay().getRotation())));

            if((xPos += xDistance) >= width) {
                xPos = 0;
                yPos += yDistance;
            }
        }

        _adapter = new CustomGridAdapter(this, R.layout.node, _items, _rows, _columns, (int) yDistance);
        _gridView.setAdapter(_adapter);
    }

    // METHODS

    // CUSTOM LISTENER
    
    private final class CustomTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean value = false;
            View node = null;
            int position;

            switch(motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    position = _movements.InitialPosition(motionEvent.getX(), motionEvent.getY());
                    if(position < _items.size())
                        node = _gridView.getChildAt(position);

                    if(node != null){
                        if(((ColorDrawable)node.getBackground()).getColor() == Color.TRANSPARENT)
                            node.setBackgroundColor(Color.BLUE);
                        
                        value = true;
                    }

                    break;

                case MotionEvent.ACTION_MOVE:
                    position = _movements.MovementOccurred(motionEvent.getX(), motionEvent.getY());
                    if(position < _items.size())
                        node = _gridView.getChildAt(position);

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
                    for (Movement.MovementType point: _movements.get_movementsMade()) {
                        sentence.append(point.toString() + " ");
                    }

                    Toast.makeText(getApplicationContext(), sentence.toString(), Toast.LENGTH_LONG).show();
                    break;
            }

            return value;
        }
    }
    // CUSTOM LISTENER
}

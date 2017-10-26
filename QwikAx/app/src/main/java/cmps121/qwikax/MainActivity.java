package cmps121.qwikax;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cmps121.qwikax.Adapters.CustomGridAdapter;
import cmps121.qwikax.App_List.ListOps;
import cmps121.qwikax.Data_Base.Movement;
import cmps121.qwikax.Node_Related.CoordinateSystem;
import cmps121.qwikax.Node_Related.IndividualNode;

public class MainActivity extends AppCompatActivity {

    // FIELDS

    // Testing for push

    private GridView _gridView;
    private int _rows;
    private int _columns;
    private boolean _runMode;

    private CustomGridAdapter _adapter;
    private ArrayList<IndividualNode> _items;
    private ListView _listView;
    private ArrayList<Integer> _pointsHit;
    private Movement _movements;

    // FIELDS

    // METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _rows = 8;
        _columns = 8;
        _runMode = true;

        _gridView = (GridView) findViewById(R.id.gridView);
        _gridView.setNumColumns(_columns);
        SetAdapter();

        //Populates _listView and creates appInfo(list of "com.~~~~~")
        ListOps apps = new ListOps(getPackageManager(), getBaseContext());
        final List<String> appInfo = apps.getInfo(getPackageManager());
        ArrayAdapter<String> appAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, apps.getName());
        _listView = (ListView) findViewById(R.id.applicationListView);
        _listView.setAdapter(appAdapter);

        // Attempts launch procedure.... only need the "com.~~~~" to launch any app
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(), arrayItemsInList[i], Toast.LENGTH_SHORT).show();
                if(_runMode == true) {
                    String chosenApp = appInfo.get(i).toString();
                    if (chosenApp != null) {
                        Intent Launch = getPackageManager().getLaunchIntentForPackage(chosenApp);
                        if (Launch != null) {
                            startActivity(Launch);
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Cannot run in save mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Using a click on an item inside the grid view as a means to start the highlighting.
        _gridView.setOnTouchListener(new CustomGridViewTouchListener());
        _pointsHit = new ArrayList<>();
        _movements = new Movement(_items, _rows, _columns);
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
                //Intent startSettings = new Intent(MainActivity.this, SettingsActivity.class);
                //startActivity(startSettings);

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popup = inflater.inflate(R.layout.settings_menu, null);

                View main = (View) findViewById(R.id.activity_main);
                PopupWindow settings = new PopupWindow(popup, LinearLayout.LayoutParams.WRAP_CONTENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT, true);

                settings.showAtLocation(main, Gravity.CENTER, 0, 0);

                break;

            case R.id.action_profile:
                Toast.makeText(getApplicationContext(), "you clicked profile", Toast.LENGTH_LONG).show();
                break;

            case R.id.action_run_save:
                _runMode = !_runMode;
                String status = "";
                if (_runMode == true){
                    status = "run ";
                }else{
                    status = "save ";
                }
                Toast.makeText(getApplicationContext(), "Now in " + status + "mode", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // Sets the adapter to the grid view.
    private void SetAdapter() {
        _items = new ArrayList<IndividualNode>();
        double xPos = 0, yPos = 0, xDistance, yDistance;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        xDistance = width  / _columns;
        yDistance = (height *.8) / _rows - 22;

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
    
    private final class CustomGridViewTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean value = false;
            View node = null;
            int position;

            switch(motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    position = _movements.InitialPosition(motionEvent.getX(), motionEvent.getY());
                    if((position < _items.size()) && (position > 0))
                        node = _gridView.getChildAt(position);

                    if(node != null){
                        if(((ColorDrawable)node.getBackground()).getColor() != Color.BLUE) {
                            node.setBackgroundColor(Color.BLUE);
                            _items.get(position).setHighLight(true);
                        }
                        
                        value = true;
                    }

                    break;

                case MotionEvent.ACTION_MOVE:
                    position = _movements.MovementOccurred(motionEvent.getX(), motionEvent.getY());
                    if((position < _items.size()) && (position > 0))
                        node = _gridView.getChildAt(position);

                    if(node != null)
                    {
                        if(((ColorDrawable)node.getBackground()).getColor() != Color.BLUE){
                            _items.get(position).setHighLight(true);
                            node.setBackgroundColor(Color.BLUE);
                        }

                        value = true;
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    // Using this to restore the grid view back to its default view.
                    StringBuilder sentence = new StringBuilder();
                    for (Movement.MovementType point: _movements.get_movementsMade()) {
                        sentence.append(point.toString() + " ");
                    }

                    for (int point:_movements.get_movementPositions()) {
                        node = _gridView.getChildAt(point);
                        _items.get(point).setHighLight(false);
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

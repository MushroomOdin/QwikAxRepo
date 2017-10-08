package cmps121.qwikax;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    // FIELDS

    private GridView _gridView;
    private int _rows;
    private int _columns;
    private int _position = -1;
    private int _currentPosition = -1;
    private int _maxPosition;
    private TimerTask _timeTask;

    private CustomGridAdapter _adapter;
    private ArrayList<NodeOfGridView> _items;

    // FIELDS

    // METHODS

    // Ends the timer we have running.
    private void cancelTimerTask(){
        if(_timeTask != null)
            _timeTask.cancel();
    }

    private void highLight(){
        if((_position >= 0) || (_position < _maxPosition)){
            _adapter.getItem(_position).setHighLight(true);
            _adapter.notifyDataSetChanged();
        }else {
            cancelTimerTask();
            _position = -1;
            _currentPosition = -1;
        }
    }

    private void launchHighlightWork(){
        final Handler handler = new Handler();
        Timer timer = new Timer();
        _timeTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            HighLightTask highlightBackgroundTask = new HighLightTask();
                            highlightBackgroundTask.execute();
                        }catch (Exception e){

                        }
                    }
                });
            }
        };

        timer.schedule(_timeTask,0,1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _rows = 4;
        _columns = 3;
        _maxPosition = _rows * _columns;

        _gridView = (GridView) findViewById(R.id.gridView);
        _gridView.setNumColumns(_columns);
        SetAdapter();

        // Using a click on an item inside the grid view as a means to start the highlighting.
        _gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                launchHighlightWork();
            }
        });
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

    @Override
    protected void onPause(){
        super.onPause();
        cancelTimerTask();
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

    // ASYNC CLASS

    private class HighLightTask extends AsyncTask<Void, Integer, Void> {

        protected void onPostExecution(Void result){
            if(_position < _maxPosition)
                highLight();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(_position < _maxPosition)
                publishProgress(Integer.valueOf(_position));

            return null;
        }

        protected void onProgressUpdate(Integer... values){
            if(_position < _maxPosition)
                _position++;
        }
    }

    // ASYNC CLASS
}

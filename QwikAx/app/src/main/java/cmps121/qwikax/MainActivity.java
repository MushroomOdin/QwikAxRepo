package cmps121.qwikax;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import cmps121.qwikax.AdaptersAndStuff.DrawingView;
import cmps121.qwikax.App_List.ListOps;
import cmps121.qwikax.Data_Base.AppStorage;
import cmps121.qwikax.Data_Base.DataBaseHandler;
import cmps121.qwikax.Node_Related.Movement;
import cmps121.qwikax.Node_Related.CoordinateSystem;

public class MainActivity extends AppCompatActivity {

    // FIELDS

    private int _rows;
    private int _columns;
    private boolean _runMode;

    private ListView _listView;
    private DrawingView _drawingView;
    private Movement _movements;
    private DataBaseHandler _dataBase;

    private String _selectedAppName;
    private String _selectedAppRunnable;
    private boolean _hasSelection = false;

    // FIELDS

    // METHODS

    // Restores our Data Base object from a file.
    private void LoadDataBaseFromFile(String fileName) {
        try{
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            _dataBase = (DataBaseHandler) is.readObject();
            is.close();
            fis.close();
        }catch(Exception ex){
            Log.e("Error", ex.getMessage().toString());
            _dataBase = new DataBaseHandler();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _rows = 10;
        _columns = 10;
        _runMode = true;

        LoadDataBaseFromFile(getApplicationContext().getFilesDir() + "/QwikAxSaveFile.txt");
        _dataBase = new DataBaseHandler();

        _drawingView = (DrawingView) findViewById(R.id.drawingView);
        CoordinateSystem[][] items = SetAdapter();

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

                if(_runMode == true) {
                    String chosenApp = appInfo.get(i).toString();
                    if (chosenApp != null) {
                        Intent Launch = getPackageManager().getLaunchIntentForPackage(chosenApp);
                        if (Launch != null) {
                            startActivity(Launch);
                        }
                    }
                }else{
                    _selectedAppName = _listView.getItemAtPosition(i).toString();
                    _selectedAppRunnable = appInfo.get(i).toString();
                    Toast.makeText(getApplicationContext(), "Please enter your gesture for "
                            + _selectedAppName, Toast.LENGTH_SHORT).show();
                    _hasSelection = true;
                }


            }
        });

        // Using a click on an item inside the grid view as a means to start the highlighting.
        _drawingView.setOnTouchListener(new CustomGridViewTouchListener());
        _movements = new Movement(items, _rows, _columns);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        SaveDataBaseToFile("QwikAxSaveFile.txt");
        super.onDestroy();
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

                String status = (_runMode)? "run": "save";
                Toast.makeText(getApplicationContext(), "Now in " + status + "mode", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public String removeChars(String s){
        String chopped = "Could Not Find";
        Pattern pattern = Pattern.compile("(\\S+om\\S+)");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()){
            chopped = matcher.group(1).substring(0,matcher.group(1).length() - 1);
        }
        return chopped;
    }

    // Sets the adapter to the grid view.
    private CoordinateSystem[][] SetAdapter() {
        CoordinateSystem[][] items = new CoordinateSystem[_rows][_columns];
        double xPos = 0, yPos = 0, xDistance, yDistance;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        xDistance = width  / _columns;
        // TODO: get rid of the constant.
        yDistance = (height *.8) / _rows;

        for (int i = 0; i < _rows; i++)
        {
            for(int j = 0; j < _columns; j++){
                items[i][j] = new CoordinateSystem(xPos,yPos,xDistance,yDistance,getWindowManager().getDefaultDisplay().getRotation());

                if((xPos += xDistance) >= width) {
                    xPos = 0;
                    yPos += yDistance;
                }
            }
        }

        return items;
    }

    //Lists all the available apps on device
    //Need to only show the useful apps like phone, text, ... instead of the system apps
    //Clean up by adding the app pictures and setting to grid view
    //Need to make items clickable to open the app itself DONE
    public void setList(ListView apps){
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> allApps = pm.getInstalledApplications(0);
        String[] appArray = new String[allApps.size()];
        for(int i=0; i<allApps.size(); i++){
            String app = removeChars(allApps.get(i).toString());
            appArray[i] = app;
        }

        ArrayList<String> appList = new ArrayList<String>();
        appList.addAll(Arrays.asList(appArray));
        ArrayAdapter<String> appAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appList);

        apps.setAdapter(appAdapter);
    }

    // Used to save the data base to a file.
    private void SaveDataBaseToFile(String fileName){
        try{
            FileOutputStream out = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(_dataBase);
            os.close();
        }catch (Exception ex){
            Log.e("Error", ex.getMessage());
            Toast.makeText(this,"Data Base was not saved", Toast.LENGTH_LONG).show();
        }
    }

    // METHODS

    // CUSTOM LISTENER
    
    private final class CustomGridViewTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean value = false;
            float x,y;
            String matchingAppNames = "";


            x = motionEvent.getX();
            y = motionEvent.getY();

            switch(motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    _movements.InitialPosition(x, y);
                    _dataBase.InitialMovement();
                    _drawingView.touch_start(x, y);
                    _drawingView.ClearCanvas();
                    value = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    int[] position = _movements.FindInitialViewByLocation(x,y);
                    _drawingView.touch_move(x,y);
                    //Passing in a fake list for testing
                    if(_runMode && _movements.MovementOccurred(x, y)){
                        _dataBase.NextMovement(_movements.GetMovements());
                        ArrayList<AppStorage> matchingApps = new ArrayList<>(_dataBase.get_currentMatches());

                        matchingAppNames = "Matched with:";
                        for(AppStorage current : matchingApps){
                            matchingAppNames = matchingAppNames + " " + current.get_relativeName();
                        }
                    }

                    value = true;
                    break;

                case MotionEvent.ACTION_UP:
                    _drawingView.touch_up();
                    StringBuilder sentence = new StringBuilder();
                    for(Movement.MovementType move : _movements.get_movementsMade()){
                        sentence.append(move.toString() + " ");
                    }

                    Toast.makeText(getApplicationContext(), sentence.toString(), Toast.LENGTH_SHORT).show();
                    if(!_runMode) {
                        if (_hasSelection) {
                            // Save the selection
                            _dataBase.AddNewItemToTree(new AppStorage(AppStorage.AccessibilityLevels.NONE, _selectedAppRunnable, _selectedAppName), _movements.get_movementsMade());
                            Toast.makeText(getApplicationContext(), "Gesture saved!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Please select an app", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), matchingAppNames, Toast.LENGTH_SHORT).show();
                    }

                    value = true;
                    break;
            }

            _drawingView.postInvalidate();
            return value;
        }
    }

    // CUSTOM LISTENER

}

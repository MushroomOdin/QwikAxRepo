package cmps121.qwikax;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import cmps121.qwikax.ViewsAndAdapters.DrawingView;
import cmps121.qwikax.App_List.ListOps;
import cmps121.qwikax.Data_Base.AppStorage;
import cmps121.qwikax.Data_Base.DataBaseHandler;
import cmps121.qwikax.Node_Related.Movement;


public class MainActivity extends AppCompatActivity {

    // FIELDS

    private int _rows;
    private int _columns;
    private boolean _runMode;
    private int _inputNum;

    private ListView _listView;
    private DrawingView _drawingView;
    private Movement _movements;
    private DataBaseHandler _dataBase;
    private ListOps _apps;

    private String _selectedAppName;
    private String _selectedAppRunnable;
    private boolean _hasSelection = false;

    private PopupWindow _settings;

    // FIELDS

    // METHODS

    // Restores our Data Base object from a file.
    private void LoadDataBaseFromFile(String fileName, ArrayList<String> appList) {
        try{
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            _dataBase = (DataBaseHandler) is.readObject();
            is.close();
            fis.close();
        }catch(Exception ex){
            Log.e("Error", ex.getMessage().toString());
            Toast.makeText(getApplicationContext(), "Data base was not loaded.", Toast.LENGTH_LONG).show();
            _dataBase = new DataBaseHandler(appList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _rows = 50;
        _columns = 50;
        _runMode = true;

        _drawingView = (DrawingView) findViewById(R.id.drawingView);

        SetOnLayoutChange();

        //Populates _listView and creates appInfo(list of "com.~~~~~")
         _apps = new ListOps(getPackageManager(), getBaseContext());
        final List<String> appInfo = _apps.getInfo(getPackageManager());
        ArrayAdapter<String> appAdapter = new ArrayAdapter<>(this, R.layout.list_view_row, _apps.getName());
        _listView = (ListView) findViewById(R.id.applicationListView);
        _listView.setAdapter(appAdapter);
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
        _drawingView.setElevation(6);
        _movements = new Movement(_rows, _columns);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart(){
        LoadDataBaseFromFile("QwikAxSaveFile.txt", _apps.getName());
        ArrayList<AppStorage> appList = new ArrayList<>();
        // TODO: remove this, just to make sure that at start things are being loaded in the database.
        _dataBase.FindAllPossibleApplicationsPastNode(_dataBase.get_masterNode(), appList);
        Toast.makeText(getApplicationContext(), "Number of applications in tree: " + appList.size(), Toast.LENGTH_SHORT).show();
        super.onStart();
    }

    @Override
    protected void onStop() {
        SaveDataBaseToFile("QwikAxSaveFile.txt");
        super.onStop();
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

                break;

            case R.id.action_profile:
                Toast.makeText(getApplicationContext(), "you clicked profile", Toast.LENGTH_LONG).show();
                break;

            case R.id.action_run_save:
                _runMode = !_runMode;

                String status = (_runMode)? "run": "save";
                Toast.makeText(getApplicationContext(), "Now in " + status + "mode", Toast.LENGTH_LONG).show();

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popup = inflater.inflate(R.layout.settings_menu, null);
                popup.setElevation(5);

                View main = (View) findViewById(R.id.activity_main);
                _settings = new PopupWindow(popup, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, true);

                final TextView txtInputNum = (TextView) popup.findViewById(R.id.txtInputNum);
                Button closePopup = (Button) popup.findViewById(R.id.btnDone);


                closePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _inputNum = Integer.valueOf(txtInputNum.getText().toString());
                        _settings.dismiss();
                    }
                });

                _settings.showAtLocation(main, Gravity.CENTER, 0, 0);
                break;

            case R.id.toggle_Grid:
                _drawingView.ToggleGrid();
                _drawingView.postInvalidate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Sets the on layout change to the drawing view.
    private void SetOnLayoutChange(){
        _drawingView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,  int oldLeft, int oldTop, int oldRight, int oldBottom) {
                _movements.ResizeCoordinateSystem(left, top, right, bottom, getWindowManager().getDefaultDisplay().getRotation());
            }
        });
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
            String matchingAppNames;

            x = motionEvent.getX();
            y = motionEvent.getY();

            switch(motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    _movements.InitialPosition(x, y);
                    if(_runMode)
                        _dataBase.InitialMovement();

                    _drawingView.touch_start(x, y);
                    _drawingView.ClearCanvas();
                    value = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                        if (!_movements.get_errorThrown() && !_dataBase.get_errorThrown()) {
                            _drawingView.touch_move(x, y);
                            //Passing in a fake list for testing
                            if (_movements.MovementOccurred(x, y)) {
                                if (_runMode) {
                                    _dataBase.NextMovement(_movements.get_currentMovements());

                                    ArrayList<AppStorage> matchingApps = new ArrayList<>(_dataBase.get_currentMatches());

                                    matchingAppNames = "Matched with:";
                                    for (AppStorage current : matchingApps) {
                                        matchingAppNames = matchingAppNames + " " + current.get_relativeName();
                                    }
                                }
                            }

                            value = true;
                        } else
                            value = false;

                    break;

                case MotionEvent.ACTION_UP:
                    _drawingView.touch_up();
                    //StringBuilder sentence = new StringBuilder();
                    //for(Movement.MovementType move : _movements.get_movementsMade())
                    //    sentence.append(move.toString() + " ");

                    //Toast.makeText(getApplicationContext(), sentence.toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "Number of Movements made: " + _movements.get_movementsMade().size(), Toast.LENGTH_SHORT).show();
                    if(!_movements.get_errorThrown() && !_dataBase.get_errorThrown()) {
                        if (!_runMode) {
                            if (_hasSelection) {
                                // Save the selection
                                _dataBase.AddNewItemToTree(new AppStorage(AppStorage.AccessibilityLevels.NONE, _selectedAppRunnable, _selectedAppName, _movements.get_movementsMade(), _drawingView.get_bitmap()));
                                _inputNum--;

                            } else {
                                Toast.makeText(getApplicationContext(), "Please select an app", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String chosenApp;
                            if (_dataBase.get_currentMatches().size() > 0) {
                                chosenApp = _dataBase.get_currentMatches().get(0).get_abesoluteName();
                                _dataBase.get_currentMatches().get(0).IncrementTimesAccessed();
                            } else {
                              // _dataBase.FindAppByAbstraction(_movements.get_movementsMade());
                               // if(_dataBase.get_currentMatches().size() > 0)
                              //      chosenApp = _dataBase.get_currentMatches().get(0).get_abesoluteName();
                              //  else
                              //      chosenApp = null;
                                chosenApp = null;
                            }

                            if (!TextUtils.isEmpty(chosenApp)) {
                                Intent Launch = getPackageManager().getLaunchIntentForPackage(chosenApp);
                                if (Launch != null) {
                                    startActivity(Launch);
                                }
                            }
                        }

                        if (!_runMode) {
                            if (_inputNum > 0) {
                                Toast.makeText(getApplicationContext(), "Enter gesture " + Integer.toString(_inputNum) + " more times", Toast.LENGTH_SHORT).show();
                            } else {
                                _runMode = true;
                                Toast.makeText(getApplicationContext(), "Gesture saved! Now in run mode", Toast.LENGTH_SHORT).show();
                            }
                        }
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

package cmps121.qwikax;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
import java.util.HashMap;
import java.util.List;

import cmps121.qwikax.Settings.SettingsActivity;
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
    /////////////////
    private ExpandableListView expListView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    /////////////////
    private DrawingView _drawingView;
    private Movement _movements;
    private DataBaseHandler _dataBase;
    private ListOps _apps;

    private List<String> _appList;
    private List<String> _appInfo;
    private String _selectedAppName;
    private String _selectedAppRunnable;
    private boolean _hasSelection = false;

    private PopupWindow _settings;

    // FIELDS

    // METHODS

    // Restores our Data Base object from a file.
    private void LoadDataBaseFromFile(String fileName, ArrayList<String> appList) {
        try {
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            _dataBase = (DataBaseHandler) is.readObject();
            is.close();
            fis.close();
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage().toString());
            Toast.makeText(getApplicationContext(), "Data base was not loaded.", Toast.LENGTH_LONG).show();
            _dataBase = new DataBaseHandler(appList);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _rows = 20;
        _columns = 20;
        _runMode = true;

        _drawingView = (DrawingView) findViewById(R.id.drawingView);

        SetOnLayoutChange();

        //Populates _listView and creates appInfo(list of "com.~~~~~")
      //  _apps = new ListOps(getPackageManager(), getBaseContext());
       // final List<String> appInfo = _apps.getInfo(getPackageManager());

        ///////////////
        expListView = (ExpandableListView) findViewById(R.id.applicationListView);
        // initialize group/children
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader.add("Apps");
        //get list of apps
        _apps = new ListOps(getPackageManager(), getBaseContext());
        _appInfo = _apps.getInfo(getPackageManager());
        _appList = new ArrayList<String>();
        //add each app to group
        for(int i = 0; i < _apps.getName().size(); i++){
            _appList.add(_apps.getName().get(i));
        }

        listDataChild.put(listDataHeader.get(0), _appList);

        //
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView adapterView, View view, int i, int j, long l) {
            if (_runMode == true) {
                String chosenApp = _appInfo.get(i).toString();
                if (chosenApp != null) {
                    Intent Launch = getPackageManager().getLaunchIntentForPackage(chosenApp);
                    if (Launch != null) {
                        startActivity(Launch);
                    }
                }
            } else {
                _selectedAppName = _listView.getItemAtPosition(i).toString();
                _selectedAppRunnable = _appInfo.get(i).toString();
                Toast.makeText(getApplicationContext(), "Please enter your gesture for "
                        + _selectedAppName, Toast.LENGTH_SHORT).show();
                _hasSelection = true;
            }
            return true;
        }
    });
        ///////////////


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
    protected void onStart() {
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
        switch (id) {
            case R.id.action_settings:
                //Toast.makeText(getApplicationContext(), "you clicked settings", Toast.LENGTH_LONG).show();

                // Create new intent and launch the layout with the intent
                Intent startSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(startSettings);

                break;

            case R.id.action_profile:
                Toast.makeText(getApplicationContext(), "you clicked profile", Toast.LENGTH_LONG).show();
                break;

            case R.id.action_run_save:


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
                        _runMode = !_runMode;
                        String status = (_runMode) ? "run" : "save";
                        Toast.makeText(getApplicationContext(), "Now in " + status + "mode", Toast.LENGTH_LONG).show();
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
    private void SetOnLayoutChange() {
        _drawingView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                _movements.ResizeCoordinateSystem(left, top, right, bottom, getWindowManager().getDefaultDisplay().getRotation());
            }
        });
    }

    // Used to save the data base to a file.
    private void SaveDataBaseToFile(String fileName) {
        try {
            FileOutputStream out = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(_dataBase);
            os.close();
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
            Toast.makeText(this, "Data Base was not saved", Toast.LENGTH_LONG).show();
        }
    }

    // METHODS

    // CUSTOM LISTENER

    private final class CustomGridViewTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean value = false;
            float x, y;
            String matchingAppNames;

            x = motionEvent.getX();
            y = motionEvent.getY();

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    _movements.InitialPosition(x, y);
                    if (_runMode)
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
                                    String currentAppName = current.get_relativeName();
                                    matchingAppNames = matchingAppNames + " " + current.get_relativeName();
                                    for(int i=0; i<_appList.size(); i++) {
                                        if(_appList.get(i) == currentAppName){
                                            _appList.remove(i);
                                        }
                                    }
                                    _appList.add(0, currentAppName);
                                }
                                listAdapter.notifyDataSetChanged();
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
                    if (!_movements.get_errorThrown() && !_dataBase.get_errorThrown()) {
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


    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<String>> _listDataChild;

        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
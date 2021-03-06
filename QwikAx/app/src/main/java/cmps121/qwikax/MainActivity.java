package cmps121.qwikax;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
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

import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cmps121.qwikax.Data_Base.DataBaseNode;
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
    private String _FILE_NAME = "QwikAxSaveFile.txt";
    private boolean _hasSelection = false;

    private PopupWindow _settings;



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
        } catch (Exception ex) {
            //Log.e("Error", ex.getMessage());
            Toast.makeText(getApplicationContext(), "Data base was not loaded.", Toast.LENGTH_LONG).show();
            _dataBase = new DataBaseHandler();
        }
    }




    public void notifyThis(String title, String message) {

        NotificationCompat.Builder b = new NotificationCompat.Builder(this.getApplicationContext());
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker("{your tiny message}")
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("INFO");

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        b.setContentIntent(contentIntent);


        NotificationManager nm = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1, b.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //send the notifcation
        if (Build.VERSION.SDK_INT < 26) {
            notifyThis("QwikAx", "Click here!");
        }



        _rows = 20;
        _columns = 20;
        _runMode = true;

        _drawingView = (DrawingView) findViewById(R.id.drawingView);

        SetOnLayoutChange();

        expListView = (ExpandableListView) findViewById(R.id.applicationListView);
        // initialize group/children
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        if(!listDataChild.containsKey("Apps"))
            listDataHeader.add("Apps");

        //get list of apps
        _apps = new ListOps(getPackageManager(), getBaseContext());
        _appInfo = _apps.getInfo(getPackageManager());
        _appList = new ArrayList<>();
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
                String chosenApp = _appInfo.get(j).toString();
                if (chosenApp != null) {
                    Intent Launch = getPackageManager().getLaunchIntentForPackage(chosenApp);
                    if (Launch != null) {
                        startActivity(Launch);
                    }
                }
            } else {
                _selectedAppName = expListView.getItemAtPosition(j + 1).toString();
                //_selectedAppName = expListView.getItemAtPosition(i).toString();

                _selectedAppRunnable = _appInfo.get(j).toString();
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
    protected void onStart(){
        LoadDataBaseFromFile(_FILE_NAME);
        ArrayList<AppStorage> appList = new ArrayList<>();
        // TODO: remove this, just to make sure that at start things are being loaded in the database.
        _dataBase.FindAllPossibleApplicationsPastNode(_dataBase.get_masterNode(), appList);
        Toast.makeText(getApplicationContext(), "Number of applications in tree: " + appList.size(), Toast.LENGTH_SHORT).show();
        super.onStart();
    }

    @Override
    protected void onStop() {
        SaveDataBaseToFile(_FILE_NAME);
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

            case R.id.clear_data_base:
                Toast.makeText(getApplicationContext(), "Clearing Data Base", Toast.LENGTH_LONG).show();
                _dataBase = new DataBaseHandler();
                SaveDataBaseToFile(_FILE_NAME);
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
                        _runMode = false;
                        String status = (_runMode) ? "run" : "save";
                        Toast.makeText(getApplicationContext(), "Now in " + status + "mode", Toast.LENGTH_LONG).show();
                        _settings.dismiss();
                    }
                });

                _settings.showAtLocation(main, Gravity.CENTER, 0, 0);
                break;

            case R.id.delete_item:
                // TODO: add in the popup / listview

                // This uses the relative name rather than the exact.
                _dataBase.DeleteItemFromTree(null);
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
    private void SaveDataBaseToFile(String fileName){
        try{
            FileOutputStream out = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(out);
            _dataBase.ReinstateDataBase();
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
                    _dataBase.InitialMovement();
                    _drawingView.touch_start(x, y);
                    _drawingView.ClearCanvas();
                    value = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    _drawingView.touch_move(x, y);
                    if (!_movements.get_errorThrown() && !_dataBase.get_errorThrown()) {
                        //Passing in a fake list for testing
                        if (_movements.MovementOccurred(x, y)) {
                            if (_runMode) {
                                _dataBase.NextMovement(_movements.get_currentMovements());

                                ArrayList<AppStorage> matchingApps = new ArrayList<>(_dataBase.get_currentMatches());

                                matchingAppNames = "Matched with:";
                                ArrayList<String> temp = new ArrayList<>();
                                for (AppStorage current : matchingApps) {
                                    String currentAppName = current.get_relativeName();
                                    matchingAppNames = matchingAppNames + " " + current.get_relativeName();
                                    for(int i=0; i<_appList.size(); i++) {
                                        if((_appList.get(i) != currentAppName) && (!temp.contains(_appList.get(i)))){
                                            temp.add(0,_appList.get(i));
                                        }
                                    }
                                    temp.add(0, currentAppName);
                                }

                                if(matchingApps.size() > 0){
                                    _appList.clear();
                                    _appList.addAll(temp);
                                    listAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        value = true;
                    } else
                        value = false;

                    break;

                case MotionEvent.ACTION_UP:
                    _drawingView.touch_up();
                    if (!_movements.get_errorThrown() && !_dataBase.get_errorThrown()) {
                        if (!_runMode) {
                            if (_hasSelection) {
                                // Save the selection
                                _dataBase.AddNewItemToTree(new AppStorage(AppStorage.AccessibilityLevels.NONE, _selectedAppRunnable, _selectedAppName, _movements.get_movementsMade()));
                                _inputNum--;

                            } else {
                                Toast.makeText(getApplicationContext(), "Please select an app before inputting the gesture", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String chosenApp;
                            if (_dataBase.get_currentMatches().size() == 0) {
                                ArrayList<DataBaseNode> nodes = new ArrayList<>();
                                nodes.add(_dataBase.get_masterNode());
                                if (_dataBase.FinalSearch(_movements.get_movementsMade(),nodes))
                                    chosenApp = _dataBase.get_MostlikelyMatchName();
                                else
                                    chosenApp = null;
                            }else
                                chosenApp = _dataBase.get_MostlikelyMatchName();

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
                    }else{
                        if(_runMode)
                            Toast.makeText(getApplicationContext(), "Error occurred during run.", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "Error occurred during save. Did not save gesture", Toast.LENGTH_LONG).show();
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

            //if(!isExpanded){
                //set layout_weight to 1
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        expListView.getLayoutParams().width,
                        expListView.getLayoutParams().height,
                        1.5f
                );
                expListView.setLayoutParams(param);
            //}

            /*if(isExpanded){
                //set layout_weight to .3

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        expListView.getLayoutParams().width,
                        expListView.getLayoutParams().height,
                        0.3f
                );
                expListView.setLayoutParams(param);

            }*/

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
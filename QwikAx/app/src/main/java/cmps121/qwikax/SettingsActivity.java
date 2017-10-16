package cmps121.qwikax;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SettingsActivity extends AppCompatActivity {

    String _extApp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_menu);
        // Hide keyboard on activity startup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Back button to return to MainActivity
        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startMain = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(startMain);
            }
        });

        final ListView apps = (ListView) findViewById(R.id.lvwApps);
        final TextView test = (TextView) findViewById(R.id.txtTest);

        // Retrieves all apps on the device and stores them in the listview
        Button btnGetAllApps = (Button) findViewById(R.id.btnGetAllApps);
        btnGetAllApps.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setList(apps);
            }
        }));


        // Formats and saves the selected item string for launch
        apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                _extApp = removeChars(apps.getItemAtPosition(i).toString());
                test.setText(_extApp);
            }
        });


        // Attempts launch procedure.... only need the "com.~~~~" to launch any app
        // _extApp contains the external app "com.~~~~"
        Button btnLaunchApp = (Button) findViewById(R.id.btnLaunchApp);
        btnLaunchApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_extApp != null){
                    Intent Launch = getPackageManager().getLaunchIntentForPackage(_extApp);
                    if(Launch != null) {
                        startActivity(Launch);
                    }
                }
            }
        });
    }

    //Lists all the available apps on device
    //Need to only show the useful apps like phone, text, ... instead of the system apps
    //Clean up by adding the app pictures and setting to grid view
    //Need to make items clickable to open the app itself
    public void setList(ListView apps){
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> allApps = pm.getInstalledApplications(0);
        String[] appArray = new String[allApps.size()];
        for(int i=0; i<allApps.size(); i++){
            appArray[i] = allApps.get(i).toString();
        }

        ArrayList<String> appList = new ArrayList<String>();
        appList.addAll(Arrays.asList(appArray));
        ArrayAdapter<String> appAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appList);

        apps.setAdapter(appAdapter);
    }

    public String removeChars(String s){
        return s.substring(24,s.length()-1);
    }

}
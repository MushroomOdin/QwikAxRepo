package cmps121.qwikax.App_List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matthew on 10/26/17.
 */

public class ListOps extends Activity{

    private List<ApplicationInfo> allApps;
    private List<String> runnableAppInfo;
    private List<String> runnableAppName;
    private String[] presentableAppArray;
    private Context context;

    // constructor
    public ListOps (PackageManager pm, Context con){
        this.allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        this.runnableAppInfo = new ArrayList<String>();
        this.runnableAppName = new ArrayList<String>();
        this.context = con;
    }

    //Lists all the available apps on device DONE
    //Need to only show the useful apps like phone, text, ... instead of the system apps DONE
    //Clean up by adding the app pictures and setting to grid view
    //Need to make items clickable to open the app itself DONE
    public List<String> getInfo(PackageManager pm){

        //Checks to make sure app is runnable before adding it to the list
        for(ApplicationInfo currentApp : allApps){
            if(getRunnableApps(currentApp, pm)) {
                runnableAppInfo.add(currentApp.packageName);
                runnableAppName.add(currentApp.loadLabel(context.getApplicationContext().getPackageManager()).toString());
            }
        }

        this.presentableAppArray = new String[runnableAppName.size()];

        for(int i=0; i<runnableAppInfo.size(); i++){
            String app = runnableAppInfo.get(i);
            String app2 = runnableAppName.get(i);
            this.presentableAppArray[i] = app2;
        }

        return runnableAppInfo;
    }

    //Determines if an application can be launched
    private boolean getRunnableApps(ApplicationInfo pkg, PackageManager pm) {
        return (pm.getLaunchIntentForPackage(pkg.packageName) != null);
    }

    //Returns a list of runnable app names
    public ArrayList<String> getName(){
        ArrayList<String> appList = new ArrayList<String>();
        appList.addAll(Arrays.asList(presentableAppArray));
        return appList;
    }

}

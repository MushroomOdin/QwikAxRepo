package cmps121.qwikax.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import cmps121.qwikax.App_List.ListOps;
import cmps121.qwikax.Data_Base.AppStorage;
import cmps121.qwikax.Data_Base.DataBaseHandler;
import cmps121.qwikax.Data_Base.DataBaseNode;
import cmps121.qwikax.MainActivity;
import cmps121.qwikax.R;

/**
 * Created by Juve on 12/5/2017.
 */

public class QwikViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private Context context=null;
    private int appWidgetId;
    private ListOps widgetApp;
    private List<String> appList = new ArrayList<>();
    private List<String> appInfo;
    private String selectedAppName;
    private String selectedAppRunnable;
    private DataBaseHandler dataBase = new DataBaseHandler();
    private DataBaseHandler _widgetBaseFac = new MainActivity()._widgetBase;
    private DataBaseNode _widnode = new DataBaseNode();

    public QwikViewsFactory(Context context, Intent intent){
        this.context = context;
        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate(){
        PackageManager pm = context.getPackageManager();
        widgetApp = new ListOps( pm , context);
        appInfo = widgetApp.getInfo(pm);

        for(int i = 0; i < widgetApp.getName().size(); i++){
            appList.add(widgetApp.getName().get(i));
        }
        List<AppStorage> widgetList = new ArrayList<>();
        _widgetBaseFac.FindAllPossibleApplicationsPastNode(_widgetBaseFac.get_masterNode() , widgetList);
        _widgetBaseFac.FindAllPossibleApplicationsPastNode(_widgetBaseFac.get_masterNode(), pm);

    }

    @Override
    public void onDestroy(){
        //mp
    }

    @Override
    public int getCount(){
        return(appList.size());
    }

    @Override
    public RemoteViews getViewAt(int position){
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.row);
        row.setTextViewText(android.R.id.text1, items[position]);
        Intent i = new Intent();
        Bundle extras = new Bundle();

        extras.putString(WidgetProvider., items[position]);
        i.putExtras(extras);
        row.setOnClickFillInIntent(android.R.id.text1, i);

        return(row);
    }

    @Override
    public RemoteViews getLoadingView(){
        return(null);
    }

    @Override
    public int getViewTypeCount(){
        return(1);
    }

    @Override
    public long getItemId(int position){
        return appList.get(position).getId();
    }

    @Override
    public boolean hasStableIds(){
        return(true);
    }

    @Override
    public void onDataSetChanged(){
        //mp
    }
}

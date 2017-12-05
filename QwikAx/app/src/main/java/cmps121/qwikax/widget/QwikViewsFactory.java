package cmps121.qwikax.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import cmps121.qwikax.R;

/**
 * Created by Juve on 12/5/2017.
 */

public class QwikViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private Context context=null;
    private int appWidgetId;

    public QwikViewsFactory(Context context, Intent intent){
        this.context = context;
        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate(){
        //mp
    }

    @Override
    public void onDestroy(){
        //mp
    }

    @Override
    public int getCount(){
        return(items.length);
    }

    @Override
    public RemoteViews getViewAt(int position){
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.row);
        row.setTextViewText(android.R.id.text1, items[position]);
        Intent i = new Intent();
        Bundle extras = new Bundle();

        extras.putString(WidgetProvider.EXTRA_WORD, items[position]);
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
        return(position);
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

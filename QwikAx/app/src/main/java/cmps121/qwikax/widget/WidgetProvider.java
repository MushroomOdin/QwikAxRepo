package cmps121.qwikax.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import cmps121.qwikax.MainActivity;
import cmps121.qwikax.R;

/**
 * Created by Juve on 12/4/2017.
 */

public class WidgetProvider extends AppWidgetProvider{
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        int N = appWidgetIds.length;
        for(int i=0; i<N; i++){
            int appWidgetId = appWidgetIds[i];
            Intent intent=new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));


            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            views.setRemoteAdapter(R.id.WidgetListView, intent);

            Intent clickIntent= new Intent(context, MainActivity.class);
            PendingIntent clickPend = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.words, clickPend);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

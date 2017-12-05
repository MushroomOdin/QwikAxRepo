package cmps121.qwikax.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Juve on 12/4/2017.
 */

public class WidgetProvider extends AppWidgetProvider{
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        int N = appWidgetIds.length;
        for(int i=0; i<N; i++){
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context,)
        }
    }
}

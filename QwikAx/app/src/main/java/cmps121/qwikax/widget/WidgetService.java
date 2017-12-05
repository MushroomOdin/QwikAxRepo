package cmps121.qwikax.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Juve on 12/5/2017.
 */

public class WidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent){
        return(new QwikViewsFactory(this.getApplicationContext(), intent));
    }
}

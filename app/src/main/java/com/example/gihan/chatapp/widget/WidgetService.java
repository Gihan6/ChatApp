package com.example.gihan.chatapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Gihan on 11/18/2017.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemotFactory(this.getApplicationContext());
    }
}

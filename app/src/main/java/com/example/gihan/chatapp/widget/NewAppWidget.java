package com.example.gihan.chatapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.example.gihan.chatapp.R;
import com.example.gihan.chatapp.ui.MainActivity;

/**
 * Implementation of App Widget functionality.
 */

public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);


        views.setTextColor(R.id.tv_widget, Color.WHITE);
        views.setTextViewText(R.id.tv_widget,"Requests ");
        views.setOnClickPendingIntent(R.id.tv_widget, PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));


        views.setRemoteAdapter(R.id.listview_widget, new Intent(context, WidgetService.class));
        views.setPendingIntentTemplate(R.id.listview_widget, PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));


       //  Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onReceive(Context context, Intent intent) {



        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, WidgetRemotFactory.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listview_widget);
        }
        super.onReceive(context, intent);

    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
package com.project.wppt;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

//service usado para la lectura de datos de Parse y adapter del LisViewPlayers
public class WidgetService extends RemoteViewsService {

	private static final String TAG = WidgetService.class.getSimpleName();

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		Log.d(TAG, "start onGetViewFactory()");
//devuelve la lista
		return (new ListProvider(this.getApplicationContext(), intent));
	}

	@Override
	public void onDestroy() {
	}

}
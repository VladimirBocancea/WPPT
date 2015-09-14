package com.project.wppt;

import java.io.IOException;
import com.parse.ParseUser;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * @author Vladimir Bocancea
 * @version 1.3
 */
public class WidgetProvider extends AppWidgetProvider {

	// Constantes//
	private static final String TAG = WidgetProvider.class.getSimpleName();
	static final String LAYOUT_TO_APPLY = "LAYOUT_TO_APPLY";

	static final String ACTION_GO_TO_PLAY = "ACTION_GO_TO_PLAY";
	static final String ACTION_PLAY_PIEDRA = "ACTION_PLAY_PIEDRA";
	static final String ACTION_PLAY_AGAIN = "PLAY_AGAIN";
	static final String ACTION_GO_TO_MENU_MAIN = "ACTION_GO_TO_MENU_MAIN";
	static final String ACTION_GO_TO_MENU_SETTINGS = "ACTION_GO_TO_MENU_SETTINGS";
	static final String ACTION_GO_TO_MENU_LOGROS = "ACTION_GO_TO_MENU_LOGROS";
	static final String ACTION_GO_TO_MENU_MULTIPLAYER = "ACTION_GO_TO_MENU_MULTIPLAYER";
	static final String ACTION_SELECT_PLAYER_FROM_LIST = "ACTION_SELECT_PLAYER_FROM_LIST";
	static final String EXTRA_STRING = "EXTRA_STRING";
	static final String ENEMY_IP = "ENEMY_IP";

	/************************** onUpdate ****************************************************************************************************/
	/************************** onUpdate ****************************************************************************************************/

	/*
	 * medoto que se ejecuta cada vez que pasa el tiempo definido en widget_info
	 * y ademas vamos a usar manualmente para actualizar nuestras vistas
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d(TAG, "start onUpdate()");
		for (int i = 0; i < appWidgetIds.length; ++i) {
			// creamos una nueva vista remota
			RemoteViews layout = buildLayout(context, appWidgetIds[i]);
			// actualizamos el widget
			appWidgetManager.updateAppWidget(appWidgetIds[i], layout);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/************************** buildLayout ****************************************************************************************************/
	/************************** buildLayout ****************************************************************************************************/
	@SuppressWarnings("deprecation")
	private RemoteViews buildLayout(Context context, int appWidgetId) {

		// instanciamos SharedPreferences
		SharedPreferences prefs = context.getSharedPreferences(LAYOUT_TO_APPLY, Context.MODE_PRIVATE);
		// instanciamos AppWidgetManager
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		// instanciamos nuestra vista remota
		RemoteViews remoteViews = null;
		// identifica a nuestro widget
		ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
		/*
		 * Accedemos al shared preference LAYOUT_TO_APPLY y tratamos de leer, si
		 * hay algun error mensaje tomara el valor "layout_menu_main"
		 */
		String layout_to_apply = prefs.getString(LAYOUT_TO_APPLY, "layout_menu_main");

		if (layout_to_apply.equals("layout_play")) {
			// accedemos a la vista y cambiamos el layout segun layout_to_apply
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_play);
		}
		if (layout_to_apply.equals("play_piedra")) {
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_play);
			/*
			 * modifica la vista con el resultado de la partida segun la
			 * elección tomada
			 */
			setViewPlay(context, remoteViews, "piedra");
		}
		if (layout_to_apply.equals("play_papel")) {
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_play);
			setViewPlay(context, remoteViews, "papel");
		}
		if (layout_to_apply.equals("play_tijera")) {
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_play);
			setViewPlay(context, remoteViews, "tijera");
		}
		if (layout_to_apply.equals("play_again")) {
			/*
			 * para resetear la vista layout_play hacemos un breve salto al
			 * layout_menu_main para luego volver a layout_play
			 */
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.layout_menu_main);
			appWidgetManager.updateAppWidget(thisWidget, rv);
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_play);
		}
		if (layout_to_apply.equals("layout_menu_main")) {
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_menu_main);
		}
		if (layout_to_apply.equals("layout_menu_ajustes")) {
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_menu_ajustes);
		}
		if (layout_to_apply.equals("layout_menu_logros")) {
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_menu_logros);
			/*
			 * lee de memoria las puntuaciones para preparar la vista antes de
			 * mostrarla
			 */
			setViewLogros(context, remoteViews);
		}

		if (layout_to_apply.equals("layout_menu_multiplayer")) {
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_menu_multiplayer);
			/*
			 * Creamos un nuevo intent a nuestro service, el adapter del
			 * listViewPlayers
			 */
			Intent svcIntent = new Intent(context, WidgetService.class);
			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
			remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewPlayers, svcIntent);
			remoteViews.setEmptyView(R.id.listViewPlayers, R.id.empty_view);
		}

		/*
		 * Creamos un intent a nuestra propia clase. Seleccionamos la accion a
		 * realizar. Equivalente a setOnClickListener de un boton comun lo
		 * asociamos con el layout ya que al tocar este se ejecutara la accion
		 * con pendingIntent
		 */

		Intent intentMenuMainToPlay = new Intent(context, WidgetProvider.class);
		intentMenuMainToPlay.setAction(ACTION_GO_TO_PLAY);
		intentMenuMainToPlay.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntentMenuMainToPlay = PendingIntent.getBroadcast(context, 0, intentMenuMainToPlay, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.button_menu_to_play, pendingIntentMenuMainToPlay);

		Intent intentPlayPiedra = new Intent(context, WidgetProvider.class);
		intentPlayPiedra.setAction(ACTION_PLAY_PIEDRA);
		intentPlayPiedra.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntentPlayPiedra = PendingIntent.getBroadcast(context, 0, intentPlayPiedra, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.imageview_play_piedra, pendingIntentPlayPiedra);

		Intent intentPlayAgain = new Intent(context, WidgetProvider.class);
		intentPlayAgain.setAction(ACTION_PLAY_AGAIN);
		intentPlayAgain.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntentPlayAgain = PendingIntent.getBroadcast(context, 0, intentPlayAgain, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.button_play_playagain, pendingIntentPlayAgain);

		Intent intentGoToMenuMain = new Intent(context, WidgetProvider.class);
		intentGoToMenuMain.setAction(ACTION_GO_TO_MENU_MAIN);
		intentGoToMenuMain.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntentGoToMenuMain = PendingIntent.getBroadcast(context, 0, intentGoToMenuMain, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.button_play_to_menu, pendingIntentGoToMenuMain);
		remoteViews.setOnClickPendingIntent(R.id.button_config_to_menu, pendingIntentGoToMenuMain);
		remoteViews.setOnClickPendingIntent(R.id.button_multiplayer_to_menu, pendingIntentGoToMenuMain);

		Intent intentMenuMainToAjustes = new Intent(context, WidgetProvider.class);
		intentMenuMainToAjustes.setAction(ACTION_GO_TO_MENU_SETTINGS);
		intentMenuMainToAjustes.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntentMenuMainToAjustes = PendingIntent.getBroadcast(context, 0, intentMenuMainToAjustes, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.button_menu_to_config, pendingIntentMenuMainToAjustes);

		Intent intentMenuMainToLogros = new Intent(context, WidgetProvider.class);
		intentMenuMainToLogros.setAction(ACTION_GO_TO_MENU_LOGROS);
		intentMenuMainToLogros.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntentMenuMainToLogros = PendingIntent.getBroadcast(context, 0, intentMenuMainToLogros, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.button_menu_main_logros, pendingIntentMenuMainToLogros);

		Intent intentMenuMainToMultiplayer = new Intent(context, WidgetProvider.class);
		intentMenuMainToMultiplayer.setAction(ACTION_GO_TO_MENU_MULTIPLAYER);
		intentMenuMainToMultiplayer.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntentMenuMainToMultiplayer = PendingIntent.getBroadcast(context, 0, intentMenuMainToMultiplayer, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.button_menu_to_multiplayer, pendingIntentMenuMainToMultiplayer);

		Intent onItemClick = new Intent(context, WidgetProvider.class);
		onItemClick.setAction(ACTION_SELECT_PLAYER_FROM_LIST);
		onItemClick.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));
		PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onItemClick, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setPendingIntentTemplate(R.id.listViewPlayers, onClickPendingIntent);

		return remoteViews;
	}

	/******************************* onReceive ***********************************************************************************************/
	/******************************* onReceive ***********************************************************************************************/

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "start onReceive()");
		// instanciamos SharedPreferences
		SharedPreferences prefs = context.getSharedPreferences(LAYOUT_TO_APPLY, Context.MODE_PRIVATE);
		// instanciamos el editor para poder escribir en SharedPreferences
		SharedPreferences.Editor editor = prefs.edit();
		// instanciamos AppWidgetManager
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		// definimos los id's de los widgets que tenemos activos
		int widgetIDs[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WidgetProvider.class));

		if (intent.getAction().equals(ACTION_GO_TO_PLAY)) {
			// escribimos en SharedPreferences LAYOUT_TO_APPLY el nuevo layout
			editor.putString(LAYOUT_TO_APPLY, "layout_play");
		}
		if (intent.getAction().equals(ACTION_PLAY_PIEDRA)) {
			editor.putString(LAYOUT_TO_APPLY, "play_piedra");
		}
		if (intent.getAction().equals(ACTION_PLAY_AGAIN)) {
			editor.putString(LAYOUT_TO_APPLY, "play_again");
		}
		if (intent.getAction().equals(ACTION_GO_TO_MENU_MAIN)) {
			editor.putString(LAYOUT_TO_APPLY, "layout_menu_main");
		}
		if (intent.getAction().equals(ACTION_GO_TO_MENU_SETTINGS)) {
			editor.putString(LAYOUT_TO_APPLY, "layout_menu_ajustes");
		}
		if (intent.getAction().equals(ACTION_GO_TO_MENU_LOGROS)) {
			editor.putString(LAYOUT_TO_APPLY, "layout_menu_logros");
		}

		if (intent.getAction().equals(ACTION_GO_TO_MENU_MULTIPLAYER)) {
			Log.d(TAG, "if ACTION_GO_TO_MENU_MULTIPLAYER");
			editor.putString(LAYOUT_TO_APPLY, "layout_menu_multiplayer");
		}
		if (intent.getAction().equals(ACTION_SELECT_PLAYER_FROM_LIST)) {
			Log.d(TAG, "if CLICK_ITEM");
			// recojemos la informacion del intent enviada por el ListProvider y
			// hacemos un Toast
			String item = intent.getExtras().getString(EXTRA_STRING);
			Toast.makeText(context, item, Toast.LENGTH_LONG).show();
			Log.d(TAG, " item: " + item);
			try {
				InternalStorage.writeObject(context, ENEMY_IP, item);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// guardamos lo escrito en SharedPreferences
		editor.commit();
		// actualizamos el widget con la nueva vista
		onUpdate(context, widgetManager, widgetIDs);
		super.onReceive(context, intent);
	}

	/******************************* setViewPlay ***********************************************************************************************/
	/******************************* setViewPlay ***********************************************************************************************/

	/*
	 * metodo usado para modificar el layout_play segun el resultado de cada
	 * partida
	 */
	public static void setViewPlay(Context context, RemoteViews remoteViews, String myPick) {

		if (myPick.contains("piedra")) {
			remoteViews.setInt(R.id.imageview_play_mypick, "setBackgroundResource", R.drawable.piedra_ic);
		}
		if (myPick.contains("papel")) {
			remoteViews.setInt(R.id.imageview_play_mypick, "setBackgroundResource", R.drawable.papel_ic);
		}
		if (myPick.contains("tijera")) {
			remoteViews.setInt(R.id.imageview_play_mypick, "setBackgroundResource", R.drawable.tijera_ic);
		}

		String pcPick = Game.getRandomPick();//
		if (pcPick.contains("piedra")) {
			remoteViews.setInt(R.id.imageview_play_pcpick, "setBackgroundResource", R.drawable.piedra_ic);
		}
		if (pcPick.contains("papel")) {
			remoteViews.setInt(R.id.imageview_play_pcpick, "setBackgroundResource", R.drawable.papel_ic);
		}
		if (pcPick.contains("tijera")) {
			remoteViews.setInt(R.id.imageview_play_pcpick, "setBackgroundResource", R.drawable.tijera_ic);
		}

		String resultado = Game.CheckIfWin(myPick, pcPick);//
		if (resultado.contains("win")) {
			remoteViews.setInt(R.id.imageview_play_resultado, "setBackgroundResource", R.drawable.win_ic);
		}
		if (resultado.contains("lose")) {
			remoteViews.setInt(R.id.imageview_play_resultado, "setBackgroundResource", R.drawable.lose_ic);
		}
		if (resultado.contains("draw")) {
			remoteViews.setInt(R.id.imageview_play_resultado, "setBackgroundResource", R.drawable.draw_ic);
		}

		remoteViews.setViewVisibility(R.id.imageview_play_piedra, View.INVISIBLE);
		remoteViews.setViewVisibility(R.id.imageview_play_papel, View.INVISIBLE);
		remoteViews.setViewVisibility(R.id.imageview_play_tijera, View.INVISIBLE);

		remoteViews.setViewVisibility(R.id.imageview_play_mypick, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.imageview_play_pcpick, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.imageview_play_resultado, View.VISIBLE);

		try {
			// Escribimos en memoria los nuevos resultados
			Game.addGameResults(context, resultado);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

	}

	/******************************* setViewLogros ***********************************************************************************************/
	/******************************* setViewLogros ***********************************************************************************************/
	// metodo usado para preparar la vista Logros
	public void setViewLogros(Context context, RemoteViews remoteViews) {
		try {
			remoteViews.setTextViewText(R.id.textView_logros_totalwin, String.valueOf(InternalStorage.readObject(context, "TOTAL_GAMES_WIN")));
			remoteViews.setTextViewText(R.id.textView_logros_totallose, String.valueOf(InternalStorage.readObject(context, "TOTAL_GAMES_LOSE")));
			remoteViews.setTextViewText(R.id.textView_logros_totaldraw, String.valueOf(InternalStorage.readObject(context, "TOTAL_GAMES_DRAW")));
			remoteViews.setTextViewText(R.id.textView_logros_totalscore, String.valueOf(InternalStorage.readObject(context, "TOTAL_SCORE")));
			remoteViews.setTextViewText(R.id.textView_logros_maxconsecutive, String.valueOf(InternalStorage.readObject(context, "MAX_CONSECUTIVE_WIN")));
			remoteViews.setTextViewText(R.id.textView_logros_row3, String.valueOf(InternalStorage.readObject(context, "WIN_ROW_3")));
			remoteViews.setTextViewText(R.id.textView_logros_row5, String.valueOf(InternalStorage.readObject(context, "WIN_ROW_5")));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onEnabled(Context context) {
		// no lo necesitamos ya que tenemos una Configuration Activity
	}

	@Override
	public void onDisabled(Context context) {
		ParseUser.logOut();
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		RemoteViews layout = buildLayout(context, appWidgetId);
		appWidgetManager.updateAppWidget(appWidgetId, layout);
	}

}

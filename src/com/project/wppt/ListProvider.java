package com.project.wppt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

/**
 * @author Vladimir Bocancea
 * @version 1.3
 */
public class ListProvider implements RemoteViewsFactory {
	private static final String TAG = ListProvider.class.getSimpleName();

	// Lista con objetos players leida de Parse
	private ArrayList<Player> arrayListPlayers = new ArrayList<Player>();
	private Context mContext = null;

	// arrays usados para ordenar las puntuaciones.. se ha de mejorar
	int[] playerScoreList;
	String[] playerNameList;

	public ListProvider(Context context, Intent intent) {
		Log.d(TAG, "start ListProvider()");
		mContext = context;
		populateListItem();
	}

	private void populateListItem() {
		Log.d(TAG, "start populateListItem()");
		List<ParseUser> results = null;
		// creamos una consulta a Parse
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		// buscando los siguientes atributos
		query.selectKeys(Arrays.asList("username", "playerScore"));

		try {
			results = query.find();
		} catch (ParseException e) {
			Log.d(TAG, "ParseException populateListItem()");
			e.printStackTrace();
		}
		// definimos el tamaño de los arrays
		playerScoreList = new int[results.size()];
		playerNameList = new String[results.size()];

		// añadimos los datos de forma desordenada
		for (int i = 0; i < results.size(); i++) {
			Player player = new Player();
			player.playerName = results.get(i).getString("username");
			player.playerScore = results.get(i).getString("playerScore");

			playerNameList[i] = player.playerName;
			playerScoreList[i] = Integer.parseInt(player.playerScore);

			Log.d(TAG, "qq " + player.playerName + " : " + player.playerScore);
			arrayListPlayers.add(player);

		}
		// ordenamos la lista de forma descendente
		ordSelDesc(playerScoreList, playerNameList);
	}

	@Override
	public int getCount() {
		return arrayListPlayers.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * similar al getView de un adapter, devuelve una vista remota
	 */
	@Override
	public RemoteViews getViewAt(int position) {
		Log.d(TAG, "start getViewAt()");
		// instanciamos la vista a devolver
		final RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.list_row);
		// definimos los valores que tomaran los campos
		remoteView.setTextViewText(R.id.textview_playerName, playerNameList[position]);
		remoteView.setTextViewText(R.id.textview_playerScore, String.valueOf(playerScoreList[position]));

		// creamos un intent para el click sobre esta vista
		final Intent fillInIntent = new Intent();
		fillInIntent.setAction(WidgetProvider.ACTION_SELECT_PLAYER_FROM_LIST);
		final Bundle bundle = new Bundle();
		// guardamos la informacion que nos interesa pasar al WidgetProvider
		bundle.putString(WidgetProvider.EXTRA_STRING, playerNameList[position]);
		fillInIntent.putExtras(bundle);
		remoteView.setOnClickFillInIntent(R.id.relativeRow, fillInIntent);

		return remoteView;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
	}

	@Override
	public void onDestroy() {
	}

	/*********************************************************************/

	// metodo usado para ordenar de forma descendente nuestra lista
	static void ordSelDesc(int[] scores, String[] names) {
		// iteramos sobre los elementos
		for (int i = 0; i < scores.length - 1; i++) {
			int max = i;
			// buscamos el mayor número
			for (int j = i + 1; j < scores.length; j++) {
				if (scores[j] > scores[max]) {
					max = j; // encontramos el mayor número

				}
			}

			if (i != max) {
				// permutamos los valores
				int aux = scores[i];
				String maxName = names[i];

				scores[i] = scores[max];
				names[i] = names[max];

				scores[max] = aux;
				names[max] = maxName;
			}
		}
	}

}
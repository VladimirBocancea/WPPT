package com.project.wppt;

import com.parse.ParseException;
import com.parse.ParseUser;
import java.io.IOException;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

//Activity usada para la configuracion inicial del widget
public class ActivityStartWidget extends Activity {

	private static final String TAG = ActivityStartWidget.class.getSimpleName();
	// variables
	EditText editTextUserName;
	Button buttonSetUserName;
	ProgressBar progressBarUserName;
	String playerName;
	ParseUser currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_multiplayer_nickname);
		editTextUserName = (EditText) findViewById(R.id.edittext_player_name);
		progressBarUserName = (ProgressBar) findViewById(R.id.progressbar_player_name);
		buttonSetUserName = (Button) findViewById(R.id.button_set_player_name);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void onClickSetInitialConfig(View view) {
		Log.d(TAG, "start  onClickSetInitialConfig()");
		// comprobamos que el nombre insertado sea mayor de 0 caracteres
		if (editTextUserName.getText().length() == 0) {
			return;
		} else {
			playerName = editTextUserName.getText().toString();
			Log.d(TAG, "playerName: " + playerName);
		}
		// modificamos la vista a la espera del login to Parse
		editTextUserName.setVisibility(View.INVISIBLE);
		buttonSetUserName.setVisibility(View.INVISIBLE);
		progressBarUserName.setVisibility(View.VISIBLE);
		// XXX falta comprobar si ya hay datos
		try {
			InternalStorage.writeObject(getBaseContext(), "TOTAL_GAMES_WIN", 0);
			InternalStorage.writeObject(getBaseContext(), "TOTAL_GAMES_LOSE", 0);
			InternalStorage.writeObject(getBaseContext(), "TOTAL_GAMES_DRAW", 0);
			InternalStorage.writeObject(getBaseContext(), "TOTAL_SCORE", 0);
			InternalStorage.writeObject(getBaseContext(), "MAX_CONSECUTIVE_WIN", 0);
			InternalStorage.writeObject(getBaseContext(), "WIN_ROW_3", "no");
			InternalStorage.writeObject(getBaseContext(), "WIN_ROW_5", "no");
			InternalStorage.writeObject(getBaseContext(), "WIN_ROW_10", "no");

			// en este punto currentUser siempre deberia ser null
			currentUser = ParseUser.getCurrentUser();
			if (currentUser == null) {
				Log.d(TAG, "qq join old user :" + playerName);
				// pero el usuario puede haber jugado antes y tener una cuenta
				// por lo que intentamos hacer un login
				loginParse(playerName);
			}
			// volvemos a checkear si estamos logueados
			currentUser = ParseUser.getCurrentUser();
			if (currentUser == null) {
				// en caso de error intentamos crear el usuario y loguearlo
				Log.d(TAG, "qq create and join new user");
				signUpParse(playerName);
				// problemas de multithreading.. se ejecutaba antes login que
				// signUp
				// Thread.sleep(5000);
				loginParse(playerName);
			}
			// si todo ha fallado.. cancelamos el widget
			currentUser = ParseUser.getCurrentUser();
			if (currentUser == null) {
				Log.d(TAG, "qq all failed");
				onBackPressed();
			}
			// si finalmente estamos logueados.. permitimos que se active el
			// widget y finalizamos esta Activity
			setResult(RESULT_OK);
			finish();

		} catch (IOException e) {
			Log.d(TAG, "onClickSetInitialConfig IOException InternalStorage.writeObject");
			e.printStackTrace();
		}

	}

	public void signUpParse(final String user) {
		Log.d(TAG, "start signUpParse() as : " + user);
		ParseUser newUser;
		newUser = new ParseUser();
		newUser.setUsername(user);
		newUser.setPassword("wppt");
		newUser.put("playerScore", "0");
		try {
			newUser.signUp();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// newUser.signUpInBackground(new SignUpCallback() {
		// public void done(ParseException e) {
		// if (e == null) {
		// Log.d(TAG, "Username Registered to Parse");
		// } else {
		// Log.d(TAG, "qq Registration to Parse Faild");
		// e.printStackTrace();
		// }
		// }
		// });

	}

	public void loginParse(final String user) {
		Log.d(TAG, "start loginParse() as : " + user);
		try {
			ParseUser.logIn(user, "wppt");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// ParseUser.logInInBackground(user, "wppt", new LogInCallback() {
		// public void done(ParseUser user, ParseException e) {
		// if (user != null) {
		// Log.d(TAG, "Login Parse OK");
		// } else {
		// Log.d(TAG, "Login Parse Failded");
		// e.printStackTrace();
		// }
		// }
		// });
	}

}

package com.project.wppt;

import java.io.IOException;
import java.util.Random;
import com.parse.*;
import android.content.Context;
import android.util.Log;

public class Game {
	static final String TAG = Game.class.getSimpleName();

	static final String TOTAL_GAMES_WIN = "TOTAL_GAMES_WIN";
	static final String TOTAL_GAMES_LOSE = "TOTAL_GAMES_LOSE";
	static final String TOTAL_GAMES_DRAW = "TOTAL_GAMES_DRAW";
	static final String WIN_ROW_3 = "WIN_ROW_3";
	static final String WIN_ROW_5 = "WIN_ROW_5";
	static final String WIN_ROW_10 = "WIN_ROW_10";
	static final String TOTAL_SCORE = "TOTAL_SCORE";
	static final String MAX_CONSECUTIVE_WIN = "MAX_CONSECUTIVE_WIN";

	static int totalScore = 0;
	static int totalGamesWin = 0;
	static int totalGamesLose = 0;
	static int totalGamesDraw = 0;
	static boolean winLastGame = false;
	static int consecutiveWin = 1;
	
	

	public static String getRandomPick() {
		String randomPick = null;

		// random.nextInt(max - min + 1) + min
		int randomNr = new Random().nextInt(3 - 1 + 1) + 1;

		if (randomNr == 1) {
			randomPick = "piedra";
		}
		if (randomNr == 2) {
			randomPick = "papel";
		}
		if (randomNr == 3) {
			randomPick = "tijera";
		}
		return randomPick;
	}

	public static String CheckIfWin(String myPick, String pcPick) {
		if (myPick == "piedra") {
			if (pcPick == "piedra") {
				return "draw";
			}
			if (pcPick == "papel") {
				return "lose";
			}
			if (pcPick == "tijera") {
				return "win";
			}
		}
		if (myPick == "papel") {
			if (pcPick == "piedra") {
				return "win";
			}
			if (pcPick == "papel") {
				return "draw";
			}
			if (pcPick == "tijera") {
				return "lose";
			}
		}
		if (myPick == "tijera") {
			if (pcPick == "piedra") {
				return "lose";
			}
			if (pcPick == "papel") {
				return "win";
			}
			if (pcPick == "tijera") {
				return "draw";
			}
		}
		return null;
	}

	public static void addGameResults(final Context context, String resultado) throws IOException, ClassNotFoundException {
		Log.d(TAG, "start addGameResults()");
		totalScore = (int) InternalStorage.readObject(context, TOTAL_SCORE);

		switch (resultado) {
		case "win":
			totalGamesWin = (int) InternalStorage.readObject(context, TOTAL_GAMES_WIN);
			InternalStorage.writeObject(context, TOTAL_GAMES_WIN, totalGamesWin + 1);
			Log.d(TAG, "case win, before: "+totalGamesWin + " ,now: " +totalGamesWin+1);

			if (winLastGame) {
				consecutiveWin++;
			}
			
			if (consecutiveWin == 3) {
				InternalStorage.writeObject(context, WIN_ROW_3, "yes");
				InternalStorage.writeObject(context, TOTAL_SCORE, totalScore + 10);
			}
			
			if (consecutiveWin == 5) {
				InternalStorage.writeObject(context, WIN_ROW_5, "yes");
				InternalStorage.writeObject(context, TOTAL_SCORE, totalScore + 50);
			}
			
			if (consecutiveWin == 10) {
				InternalStorage.writeObject(context, WIN_ROW_10, "yes");
				InternalStorage.writeObject(context, TOTAL_SCORE, totalScore + 100);
			}
			if (consecutiveWin > (int) InternalStorage.readObject(context, MAX_CONSECUTIVE_WIN)) {
				InternalStorage.writeObject(context, MAX_CONSECUTIVE_WIN, consecutiveWin);
			}
			InternalStorage.writeObject(context, TOTAL_SCORE, totalScore + 2);
			winLastGame = true;
			
			
			ParseUser user = ParseUser.getCurrentUser();
			user.put("playerScore", String.valueOf(totalScore));
			user.saveInBackground();
			

			break;
		case "lose":
			totalGamesLose = (int) InternalStorage.readObject(context, TOTAL_GAMES_LOSE);
			InternalStorage.writeObject(context, TOTAL_GAMES_LOSE, totalGamesLose + 1);
			Log.d(TAG, "case win, before: "+totalGamesLose + " ,now: " +totalGamesLose+1);
			winLastGame = false;
			consecutiveWin = 1;
			break;
		case "draw":
			totalGamesDraw = (int) InternalStorage.readObject(context, TOTAL_GAMES_DRAW);
			InternalStorage.writeObject(context, TOTAL_GAMES_DRAW, totalGamesDraw + 1);
			Log.d(TAG, "case win, before: "+totalGamesDraw + " ,now: " +totalGamesDraw+1);
			InternalStorage.writeObject(context, TOTAL_SCORE, totalScore++);
			winLastGame = false;
			consecutiveWin = 1;
			break;

		}

	}

}

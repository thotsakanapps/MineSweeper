package com.thotsakan.minesweeper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.thotsakan.minesweeper.gameview.Expertise;
import com.thotsakan.minesweeper.gameview.GameView;

public final class MainActivity extends Activity {

	private InterstitialAd adMobAd;

	private GameView gameView;

	private Expertise getExpertise() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String expertise = preferences.getString(getString(R.string.expertise_key), null);
		if (getString(R.string.expertise_intermediate).equals(expertise)) {
			return Expertise.INTERMEDIATE;
		} else if (getString(R.string.expertise_expert).equals(expertise)) {
			return Expertise.EXPERT;
		} else {
			return Expertise.BEGINNER;
		}
	}

	@Override
	public void onBackPressed() {
		if (adMobAd.isLoaded()) {
			adMobAd.show();
		}
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ImageButton newGameButton = (ImageButton) findViewById(R.id.new_game_button);
		newGameButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				gameView.newGame();
			}
		});

		Expertise expertise = getExpertise();
		TextView flaggedCellsCount = (TextView) findViewById(R.id.flagged_cells);
		flaggedCellsCount.setText(expertise.mines + "");

		ScrollView gameBoard = (ScrollView) findViewById(R.id.game_view);
		gameView = new GameView(this, expertise, (Timer) findViewById(R.id.timer));
		gameBoard.addView(gameView);

		// Show Help
		showInfoBeforeGame();

		// AdMob
		if (adMobAd == null) {
			adMobAd = new InterstitialAd(this);
			adMobAd.setAdUnitId(getString(R.string.ad_unit_id));
			AdRequest adRequest = new AdRequest.Builder().build();
			adMobAd.loadAd(adRequest);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_info:
			showInfo();
			break;
		case R.id.action_settings:
			Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("InflateParams")
	private void showInfo() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.info_dialog_title);
		dialogBuilder.setView(LayoutInflater.from(this).inflate(R.layout.dialog_info, null));
		dialogBuilder.setNeutralButton(R.string.info_dialog_close_button_text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogBuilder.show();
	}

	@SuppressLint("InflateParams")
	private void showInfoBeforeGame() {
		if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(getString(R.string.info_dialog_do_not_show_again_button_key), false)) {
			return;
		}

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.info_dialog_title);
		dialogBuilder.setView(LayoutInflater.from(this).inflate(R.layout.dialog_info, null));
		dialogBuilder.setNegativeButton(R.string.info_dialog_do_not_show_again_button_text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				editor.putBoolean(getString(R.string.info_dialog_do_not_show_again_button_key), true);
				editor.apply();
				dialog.dismiss();
			}
		});
		dialogBuilder.setNeutralButton(R.string.info_dialog_close_button_text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogBuilder.show();
	}
}

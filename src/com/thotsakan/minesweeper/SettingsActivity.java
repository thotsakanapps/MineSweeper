package com.thotsakan.minesweeper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

final class SettingsActivity extends PreferenceActivity {

	private class HeadersFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
			PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings, false);
		}

		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
			String key = preference.getKey();

			if (key.equals(getString(R.string.best_scores_key))) {
				showBestScores();
			} else if (key.equals(getString(R.string.market_rate_app_key))) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.class.getPackage().getName()));
				startActivity(intent);
			} else if (key.equals(getString(R.string.market_other_apps_by_us_key))) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://play.google.com/store/search?q=pub:" + getString(R.string.market_other_apps_by_us_publisher)));
				startActivity(intent);
			}
			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
	}

	@Override
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		Fragment fragment = new HeadersFragment();
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(android.R.id.content, fragment);
		transaction.commit();
	}

	@SuppressLint("InflateParams")
	private void showBestScores() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.best_scores_title);

		// best scores
		String defaultScore = getString(R.string.best_scores_default);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		View scoresView = LayoutInflater.from(this).inflate(R.layout.best_scores, null);

		TextView beginnerScoreView = (TextView) scoresView.findViewById(R.id.best_scrores_beginner);
		beginnerScoreView.setText(prefs.getString(getString(R.string.best_scores_beginner), defaultScore) + " seconds");

		TextView intermediateScoreView = (TextView) scoresView.findViewById(R.id.best_scrores_intermediate);
		intermediateScoreView.setText(prefs.getString(getString(R.string.best_scores_intermediate), defaultScore) + " seconds");

		TextView expertScoreView = (TextView) scoresView.findViewById(R.id.best_scrores_expert);
		expertScoreView.setText(prefs.getString(getString(R.string.best_scores_expert), defaultScore) + " seconds");

		dialogBuilder.setView(scoresView);

		// reset scores
		dialogBuilder.setNeutralButton(getString(R.string.best_scores_resetScores), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String[] keys = new String[] { getString(R.string.best_scores_beginner), getString(R.string.best_scores_intermediate), getString(R.string.best_scores_expert) };
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				for (String key : keys) {
					editor.putString(key, getString(R.string.best_scores_default));
				}
				editor.apply();
				dialog.dismiss();
			}
		});

		// close dialog
		dialogBuilder.setPositiveButton(getString(R.string.best_scores_close), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		dialogBuilder.show();
	}
}

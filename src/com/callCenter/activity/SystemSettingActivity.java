package com.callCenter.activity;

import java.io.InputStream;
import java.net.URI;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SystemSettingActivity extends PreferenceActivity implements
		Preference.OnPreferenceClickListener,
		Preference.OnPreferenceChangeListener {
	private Handler handler = new Handler();
	private ListPreference bellPreference;
	private MediaPlayer player;
	private SharedPreferences myPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.system_setting);
		bellPreference = (ListPreference) findPreference("bell_value");
		bellPreference.setOnPreferenceChangeListener(this);
		bellPreference.setOnPreferenceClickListener(this);
		myPreference = PreferenceManager.getDefaultSharedPreferences(this);
		player = new MediaPlayer();
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object object) {
		// TODO Auto-generated method stub

		if (preference == bellPreference) {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						AssetFileDescriptor fileDescriptor = getAssets()
								.openFd(bellPreference.getValue());
						player.setDataSource(
								fileDescriptor.getFileDescriptor(),
								fileDescriptor.getStartOffset(),
								fileDescriptor.getLength());
						player.prepare();
						player.start();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}
			}, 500);

		}
		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if (player.isPlaying()) {
			player.stop();
			player.reset();
		}
		return false;
	}

	@SuppressLint("CommitPrefEdits")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (player.isPlaying()) {
			player.stop();
			player.release();

		}
		String my = myPreference.getString("bell_value", "bell.mp3");
		Editor editor = myPreference.edit();
		editor.putString("myBell", my);
		editor.commit();
	}
}
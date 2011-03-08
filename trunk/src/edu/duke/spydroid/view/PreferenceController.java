package edu.duke.spydroid.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import edu.duke.spydroid.R;

public final class PreferenceController extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String KEY_NETWORK_SERVER_PREFERENCE="pref_edit_server_addr";	
	private static final String KEY_CLEAR_DATA_PREFERENCE="pref_clear_data";
	
	private Preference clearDataPref;
	private EditTextPreference serverAddressPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		clearDataPref=(Preference) getPreferenceScreen().findPreference(KEY_CLEAR_DATA_PREFERENCE);
		serverAddressPref=(EditTextPreference) getPreferenceScreen().findPreference(KEY_NETWORK_SERVER_PREFERENCE);	
		
		//Setup Clear Data Preference for on-click listening
		clearDataPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(PreferenceController.this, ClearDataPaneController.class);
				startActivity(i);
				return true;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		serverAddressPref.setSummary(serverAddressPref.getText());
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_NETWORK_SERVER_PREFERENCE)) {
			serverAddressPref.setSummary(serverAddressPref.getText());
		}
		
	}
	
	

}

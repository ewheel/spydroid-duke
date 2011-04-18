package edu.duke.spydroid.view;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import edu.duke.spydroid.R;

public final class PreferenceController extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String KEY_NETWORK_SERVER_PREFERENCE="pref_edit_server_addr";	
	private static final String KEY_CLEAR_DATA_PREFERENCE="pref_clear_data";
	private static final String KEY_FILE_SYSTEM_PREFERENCE = "coll_file_system_text";
	private static final int ERROR_DIALOG_NUMBER = 1;	
	
	private Preference clearDataPref;
	private EditTextPreference serverAddressPref;
	private EditTextPreference fileSystemSearchPref;
	private String previousSearchDirectory, mErrorMsg, mErrorTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		clearDataPref=(Preference) getPreferenceScreen().findPreference(KEY_CLEAR_DATA_PREFERENCE);
		serverAddressPref=(EditTextPreference) getPreferenceScreen().findPreference(KEY_NETWORK_SERVER_PREFERENCE);	
		fileSystemSearchPref=(EditTextPreference) getPreferenceScreen().findPreference(KEY_FILE_SYSTEM_PREFERENCE);		
		previousSearchDirectory=getString(R.string.coll_file_system_default);

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
		fileSystemSearchPref.setSummary(fileSystemSearchPref.getText());
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
			return;
		}
		if (key.equals(KEY_FILE_SYSTEM_PREFERENCE)) {
			String searchDir = sharedPreferences.getString(
					KEY_FILE_SYSTEM_PREFERENCE, previousSearchDirectory);
			File searchRoot = new File(searchDir);
			if (!searchRoot.canRead() || !searchRoot.exists()) {

				// Show error message
				mErrorTitle = (searchRoot.exists()) ? getString(R.string.coll_file_system_error_title_read) 
						: getString(R.string.coll_file_system_error_title_exist);
				mErrorMsg = (searchRoot.exists()) ? getString(R.string.coll_file_system_error_descr_read)
						+ searchDir
						: getString(R.string.coll_file_system_error_descr_exist1)
								+ " " + searchDir + " "
								+ getString(R.string.coll_file_system_error_descr_exist2);
				
				showDialog(ERROR_DIALOG_NUMBER);

			} else
				previousSearchDirectory = searchDir;
			
			// Write previous search directory to the preference field
			Editor e = sharedPreferences.edit();
			e.putString(key, previousSearchDirectory).commit();
			fileSystemSearchPref.setSummary(sharedPreferences.getString(KEY_FILE_SYSTEM_PREFERENCE, ""));
			fileSystemSearchPref.setText(previousSearchDirectory);
		}

	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog=super.onCreateDialog(id);
		switch(id) {
		case ERROR_DIALOG_NUMBER:
			dialog=buildFSErrorDialog(mErrorTitle,mErrorMsg);
			break;
		}
		return dialog;
	}
	
	/**
	 * Creates an <code>AlertDialog</code> to display general information. 
	 * @return The dialog box to be displayed
	 */
	private Dialog buildFSErrorDialog(String errorTitle, String errorMsg) {
		Builder builder= new AlertDialog.Builder(this);
		builder.setTitle(errorTitle).setMessage(errorMsg)
		.setCancelable(true)
		.setNeutralButton(R.string.info_close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return builder.create();
	}
	

}

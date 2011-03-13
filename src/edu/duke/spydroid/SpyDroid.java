package edu.duke.spydroid;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import edu.duke.spydroid.view.PreferenceController;

public final class SpyDroid extends Activity {
	SharedPreferences preferences;
	Button viewDataButton;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        //Setup data collection button
        viewDataButton = (Button) findViewById(R.id.collectedButton);
        viewDataButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onDataCollectButtonPress();
			}
		});
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.preferences://Show Preference Screen
			Intent i = new Intent(SpyDroid.this, PreferenceController.class);
			startActivity(i);
			break;
		case R.id.info://Show Info Dialog
			showDialog(R.id.info);
			break;
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog=super.onCreateDialog(id);
		switch(id) {
		case R.id.info:
			dialog=buildInfoDialog();
			break;
		}
		return dialog;
	}
	
	/**
	 * Creates an <code>AlertDialog</code> to display general information. 
	 * @return The dialog box to be displayed
	 */
	private Dialog buildInfoDialog() {
		Builder builder= new AlertDialog.Builder(this);
		builder.setTitle(R.string.info_title).setMessage(R.string.info_descr)
		.setCancelable(true)
		.setNeutralButton(R.string.info_close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return builder.create();
	}
	
	private void onDataCollectButtonPress() {
		PackageManager pm = this.getPackageManager();

		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
		String appList = "Installed Applications\n";
		for (ResolveInfo rInfo : list) {
			appList += rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
			appList += ", \n";
		//Log.w(“Installed Applications”, rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
		}
		TextView tv = new TextView(this);
	    tv.setText(appList);
	    ScrollView sv = new ScrollView(this);
		sv.addView(tv);
	    setContentView(sv);
		//TODO implement switch to DataCollection Activity
//		Intent i = new Intent(SpyDroid.this, null);
//		startActivity(i);
	}
}
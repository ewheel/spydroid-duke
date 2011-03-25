package edu.duke.spydroid;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import edu.duke.spydroid.view.DataCollectionPaneController;
import edu.duke.spydroid.view.PreferenceController;

public final class SpyDroid extends Activity {
	//private SharedPreferences preferences;
	private Button viewDataButton;
	//private CollectorManager collectorManager;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       // preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        
        //TODO implement data collection setup here.
        
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
		Intent i = new Intent(SpyDroid.this, DataCollectionPaneController.class);
		startActivity(i);
	}
	
	}
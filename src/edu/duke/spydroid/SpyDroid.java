package edu.duke.spydroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import edu.duke.spydroid.CollectorService.CollectorBinder;
import edu.duke.spydroid.collectors.AccountInfoCollector;
import edu.duke.spydroid.collectors.AndroidVersionCollector;
import edu.duke.spydroid.collectors.FileSystemCollector;
import edu.duke.spydroid.collectors.IMEICollector;
import edu.duke.spydroid.collectors.InstalledAppCollector;
import edu.duke.spydroid.collectors.MACCollector;
import edu.duke.spydroid.collectors.PhoneNumberCollector;
import edu.duke.spydroid.collectors.SMSCollector;
import edu.duke.spydroid.collectors.SSIDCollector;
import edu.duke.spydroid.view.DataCollectionPaneController;
import edu.duke.spydroid.view.PreferenceController;

public final class SpyDroid extends Activity {
	private Button mViewDataButton;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			CollectorBinder binder = (CollectorBinder) service;
			CollectorService collService = binder.getService();
			setupCollecting(collService);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// Really if this happens you are really screwed so may as well not write code.
		}
		
	};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        //Start the Collection Service
        Intent service = new Intent(this,CollectorService.class);
        bindService(service,mConnection,BIND_AUTO_CREATE);
        
        //Setup data collection button
        mViewDataButton = (Button) findViewById(R.id.collectedButton);
        mViewDataButton.setOnClickListener(new View.OnClickListener() {
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

	@Override
	protected void onDestroy() {
		 this.unbindService(mConnection);
		super.onDestroy();
	}

	private void setupCollecting(CollectorService collService) {
		//Intent Filters
		IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		
		//Collector Declarations
		StaticCollector imeiColl= new IMEICollector(getApplicationContext(),"coll_IMEI",null);
		StaticCollector appColl= new InstalledAppCollector(getApplicationContext(),"coll_installed_apps",collService);
		BroadcastCollector smsColl= new SMSCollector(collService,"coll_received_SMS",smsFilter);
		StaticCollector fileColl= new FileSystemCollector(getApplicationContext(),"coll_file_system",collService);
	    StaticCollector accountColl= new AccountInfoCollector(getApplicationContext(),"coll_account_info",null);
	    StaticCollector MACColl= new MACCollector(getApplicationContext(),"coll_MAC",null);    
	    StaticCollector PNColl= new PhoneNumberCollector(getApplicationContext(),"coll_phone_number",null);
	    StaticCollector SSIDColl= new SSIDCollector(getApplicationContext(),"coll_SSID",null);
	    StaticCollector versionColl= new AndroidVersionCollector(getApplicationContext(),"coll_version",null);
	    
		//Scheduling
		collService.scheduleCollector(imeiColl);
		collService.scheduleCollector(MACColl);
		collService.scheduleCollector(PNColl);
		collService.scheduleCollector(SSIDColl);
		collService.scheduleCollector(versionColl);
		collService.scheduleCollector(appColl);
		collService.scheduleCollector(smsColl);
		collService.scheduleCollector(fileColl);
        collService.scheduleCollector(accountColl);
	}
	
	
}
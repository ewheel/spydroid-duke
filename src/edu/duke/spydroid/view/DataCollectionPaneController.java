package edu.duke.spydroid.view;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.SimpleAdapter;
import edu.duke.spydroid.AbstractCollector;
import edu.duke.spydroid.CollectorService;
import edu.duke.spydroid.CollectorService.CollectorBinder;
import edu.duke.spydroid.R;

public class DataCollectionPaneController extends ListActivity {
	private SimpleAdapter mAdapter;
	private CollectorService mService;
	private boolean mServiceBound;
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mServiceBound = true;
			CollectorBinder binder = (CollectorBinder) service;
			mService=binder.getService(); 
			bindViewData();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// If this happens, you have bigger problems.
			mServiceBound = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_collection_pane);
		
		//Bind Service
		Intent service = new Intent(this,CollectorService.class);
		this.bindService(service, mConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mAdapter!=null)
			mAdapter.notifyDataSetChanged();//Update when the screen becomes visible.
	}

	private void bindViewData() {
		if(mServiceBound) {
			String [] from ={AbstractCollector.TITLE_KEY, AbstractCollector.CONTENT_KEY};
			int [] to = {android.R.id.text1, android.R.id.text2 };
			mAdapter = new SimpleAdapter(this, mService.getDisplayData(),android.R.layout.two_line_list_item, from, to);
			setListAdapter(mAdapter);
			mServiceBound = false;
			unbindService(mConnection);	
		}
	}
}

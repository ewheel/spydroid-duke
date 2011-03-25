package edu.duke.spydroid.view;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.SimpleAdapter;
import edu.duke.spydroid.AbstractCollector;
import edu.duke.spydroid.CollectorManager;
import edu.duke.spydroid.R;
import edu.duke.spydroid.collectors.FileSystemCollector;
import edu.duke.spydroid.collectors.AccountInfoCollector;
import edu.duke.spydroid.collectors.IMEICollector;
import edu.duke.spydroid.collectors.InstalledAppCollector;

public class DataCollectionPaneController extends ListActivity {
	private CollectorManager collectorMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_collection_pane);
		
		//Schedule Collections
        collectorMgr = new CollectorManager(getApplicationContext());
        AbstractCollector imeiColl= new IMEICollector(getApplicationContext(),"coll_IMEI");
        AbstractCollector appColl= new InstalledAppCollector(getApplicationContext(),"coll_installed_apps");
        AbstractCollector fileColl= new FileSystemCollector(getApplicationContext(),"coll_file_system");
        AbstractCollector accountColl= new AccountInfoCollector(getApplicationContext(),"coll_account_info");
        
        collectorMgr.scheduleCollector(imeiColl);
        collectorMgr.scheduleCollector(appColl);
        collectorMgr.scheduleCollector(fileColl);
        collectorMgr.scheduleCollector(accountColl);
        Log.d("Bubbles", ""+collectorMgr.getDisplayData().size());
		
		//Bind Data to View
		String [] from ={AbstractCollector.TITLE_KEY, AbstractCollector.CONTENT_KEY};
		int [] to = {android.R.id.text1, android.R.id.text2 };
		SimpleAdapter adapter = new SimpleAdapter(this, collectorMgr.getDisplayData(),android.R.layout.two_line_list_item, from, to);
		setListAdapter(adapter);
		
		//Debug
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		Log.d("Bubbles", ""+sharedPrefs.getBoolean("coll_installed_apps", false));
	}

	@Override
	protected void onResume() {
		//So apparently Android, just calls onCreate constantly, making this potentially obsolete....
		super.onResume();
		
		
	}

}

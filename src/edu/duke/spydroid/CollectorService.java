package edu.duke.spydroid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class CollectorService extends IntentService implements OnSharedPreferenceChangeListener,
		Observer {

	private static final String collectionKey="pref_enable_collection";
	
	private Collection<AbstractCollector> collectors;
	private SharedPreferences sharedPrefs;
	private List<Map<String, ?>> displayData;
	private Map<AbstractCollector, Map<String,?>> visibleData;
	private final IBinder mBinder;
	private Comparator<Map<String,?>> displayComparator;
	private NetworkManager myNM;
	
	public class CollectorBinder extends Binder {
		public CollectorService getService() {
			return CollectorService.this;
		}
	}

	public CollectorService() {
		super("CollectorWorker");//Name of worker thread
		mBinder= new CollectorBinder();
		collectors = new HashSet<AbstractCollector>();
		displayData = new ArrayList<Map<String, ?>>();
		visibleData= new HashMap<AbstractCollector, Map<String, ?>>();
		displayComparator = new Comparator<Map<String,?>>(){
			@Override
			public int compare(Map<String, ?> o1, Map<String, ?> o2) {
				String title=AbstractCollector.TITLE_KEY;
				return ((String)o1.get(title)).compareTo((String)o2.get(title));
			}
		};
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);
		myNM = new NetworkManager(getApplicationContext());
	}
	
	@Override
	public void onDestroy() {
		sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}



	/**
	 * Schedule a collector with this CollectorManager.
	 * @param collector The collector to be scheduled. 
	 */
	public void scheduleCollector(AbstractCollector collector) {
		collectors.add(collector);
		collector.addObserver(this);
		String key = collector.getPreferenceKey();
		boolean isEnabled = sharedPrefs.getBoolean(key, false);
		if (isEnabled && isCollectingOn()) {
			collector.startCollect();
		}
	}

	/**
	 * Stop a collector from collecting and remove it from the collectors this CollectorService manages.
	 * @param collector The collector to be stopped and removed from management.
	 */
	public void cancelCollector(AbstractCollector collector) {
		if (collectors.remove(collector)) {
			if (collector.isCollecting())
				collector.stopCollect();
			collector.deleteObserver(this);
		}

	}

	/*
	 * Enables or disables collection based on user preferences. If a preference
	 * key is not found for a collector than that collector is disabled, but
	 * theoretically that should never happen.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals(collectionKey)) {
			handleFullCollectingEnableDisable();
			}
		//Handle the individual collector updated
		for (AbstractCollector col : collectors) {
			if (col.getPreferenceKey().startsWith(key)) {
				updateCollecting(sharedPreferences, key, col);
				//TODO: Check this
				col.onSharedPreferenceChanged(sharedPreferences, key);
			}
		}
	}



	/**
	 * Sets enables and disables for all collectors due to a change in the
	 * global collecting preference.
	 */
	private void handleFullCollectingEnableDisable() {
		if(isCollectingOn()) {
			for (AbstractCollector coll: collectors) {
				boolean isEnabled=sharedPrefs.getBoolean(coll.getPreferenceKey(), false);
				if(!coll.isCollecting() && isEnabled) 
					coll.startCollect();
			}
				
		}
			else for (AbstractCollector coll: collectors)
				if(coll.isCollecting()) {
					stopAndClearForCollector(coll);
			}
	}

	/**
	 * Updates the collecting state of an individual collector.
	 */
	private void updateCollecting(SharedPreferences sharedPreferences,
			String key, AbstractCollector col) {
		boolean isEnabled = sharedPreferences.getBoolean(key, false);
		if (!isEnabled && col.isCollecting())
			stopAndClearForCollector(col);
		else if (isEnabled && !col.isCollecting())
			col.startCollect();
		else if (isCollectingOn() && col.isCollecting())
			stopAndClearForCollector(col);
	}
	
	/**
	 * Stops the collector and then clears its data from the data to be
	 * displayed.
	 */
	private void stopAndClearForCollector(AbstractCollector collector) {
		collector.stopCollect();
		displayData.remove(visibleData.get(collector));
		visibleData.remove(collector);
		Collections.sort(displayData,displayComparator);
	}
	
//TODO: Move server code here.  Try using visibileData.get(collector).get(AbstractCollector.TITLE_KEY) for getting name.
	//Change server code to take IMEI at all times as first JSON key and the other collector's string as the second.
	
	@Override
	public void update(Observable observable, Object data) {
		AbstractCollector collector;
		if(observable instanceof AbstractCollector) {
			collector = (AbstractCollector) observable;
			Map<String,?> dataMap = collector.getDisplayableData();
			myNM.packageAndSendUpdate(dataMap);
			if(visibleData.containsKey(collector)) {//Already being displayed
				displayData.remove(visibleData.get(collector)); //Remove its entry from displayData
			}
				visibleData.put(collector, dataMap); //Update collector to display mapping
				displayData.add(dataMap);
				Collections.sort(displayData,displayComparator); 
		}//else probably throw an exception.

	}

	/**
	 * Access method for the backing resource to displayable data for this
	 * <code>CollectorManager</code>'s collectors. This backing source is
	 * designed to be used in conjunction with an <code>Adapter</code> such as
	 * <code>SimpleAdapter</code>.
	 * 
	 * @return Map of view categories to collector data for that category.
	 */
	public List<? extends Map<String, ?>> getDisplayData() {
		return displayData;
	}
	
	private boolean isCollectingOn() {
		return sharedPrefs.getBoolean(collectionKey, false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//Match based on classes, this might be faster if we reuse preference keys
		String collectorName=intent.getStringExtra(AbstractCollector.COLLECTOR);
		Intent externalIntent = intent.getParcelableExtra(AbstractCollector.EXTERNAL_INTENT);
		//if(s==null) TODO: Throw an exception
		for (AbstractCollector coll: collectors) {
			if (coll.getClass().getName().equals(collectorName)) {
				coll.collect(externalIntent);
				return;
			}
		}
		//TODO if this for loop finishes, no collector was found throw an exception
	}

}

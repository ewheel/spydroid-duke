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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class CollectorManager implements OnSharedPreferenceChangeListener,
		Observer {

	private static final String collectionKey="pref_enable_collection";
	
	private Collection<AbstractCollector> collectors;
	private Context appContext;
	private SharedPreferences sharedPrefs;
	private List<Map<String, ?>> displayData;
	private Map<AbstractCollector, Map<String,?>> visibleData;

	public CollectorManager(Context ctxt) {
		appContext = ctxt;
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(appContext.getApplicationContext());
		collectors = new HashSet<AbstractCollector>();
		displayData = new ArrayList<Map<String, ?>>();
		visibleData= new HashMap<AbstractCollector, Map<String, ?>>();
	}

	public void scheduleCollector(AbstractCollector collector) {
		collectors.add(collector);
		collector.addObserver(this);
		String key = collector.getPreferenceKey();
		boolean isEnabled = sharedPrefs.getBoolean(key, false);
		if (isEnabled && isCollectingOn()) {
			collector.startCollect();
		}
	}

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
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		for (AbstractCollector col : collectors) {
			if (col.getPreferenceKey().equals(key)) {
				boolean isEnabled = sharedPreferences.getBoolean(key, false);
				if (!isEnabled && col.isCollecting())
					col.stopCollect();
				else if (isEnabled && !col.isCollecting())
					col.startCollect();
				else if (isCollectingOn() && col.isCollecting())
					col.stopCollect();
			}
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		AbstractCollector collector;
		if(observable instanceof AbstractCollector) {
			collector = (AbstractCollector) observable;
			Map<String,?> dataMap = collector.getDisplayableData();
			
			if(visibleData.containsKey(collector)) {//Already being displayed
				displayData.remove(visibleData.get(collector)); //Remove its entry from displayData
			}
				visibleData.put(collector, dataMap); //Update collector to display mapping
				displayData.add(dataMap);
				Collections.sort(displayData, new Comparator<Map<String,?>>(){
					@Override
					public int compare(Map<String, ?> o1, Map<String, ?> o2) {
						String title=AbstractCollector.TITLE_KEY;
						return ((String)o1.get(title)).compareTo((String)o2.get(title));
					}
				});
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

}

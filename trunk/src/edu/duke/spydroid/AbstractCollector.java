package edu.duke.spydroid;

import java.util.Map;
import java.util.Observable;

import android.content.Context;

public abstract class AbstractCollector extends Observable {
	public static final String TITLE_KEY="title";
	public static final String CONTENT_KEY="content";
	
	private String prefKey;
	private Context appContext;
	private boolean isCollecting;

	public AbstractCollector(Context ctxt, String preferenceKey) {
		prefKey = preferenceKey;
		appContext = ctxt;
		isCollecting = false;
	}

	/**
	 * The key for the Preference associated with this collector. The key can be
	 * used to allow users to enable or disable this collector.
	 */
	public final String getPreferenceKey() {
		return prefKey;
	}

	/**
	 * Determines whether this collector is actively collecting data or not.
	 * 
	 * @return true if actively collecting data, false otherwise
	 */
	public final boolean isCollecting() {
		return isCollecting;
	}

	/**
	 * Gives access to the currently available collected data.
	 * 
	 * @return The collected data
	 */
	public abstract Object getData();

	/**
	 * Returns the collected data in some displayable format, such that data is
	 * grouped into categories and mapped between display categories (i.e. title, summary) and data.
	 * 
	 * The number of categories that will actually be displayed is dependent on
	 * the implementation of the displaying view.
	 * 
	 * @return A <code>Map</code> between display categories and formatted data.
	 */
	public abstract Map<String, ?> getDisplayableData();

	/**
	 * Provides access to the appication's <code>Context</code> to facilitate
	 * data collection.
	 * 
	 * @return The application's <code>Context</code>
	 */
	protected final Context getContext() {
		return appContext;
	}

	/**
	 * This method is called to signal subclasses of
	 * <code>AbstractCollector</code> to begin collecting duties. Setup of
	 * collection infrastructure and collection should be implemented here.
	 */
	public final void startCollect() {
		isCollecting = true;
		onStart();
	}

	/**
	 * This method signals a collector to halt all collection operations and
	 * release all resources used for collection purposes.
	 */
	public final void stopCollect() {
		onStop();
		isCollecting = false;
	}

	/**
	 * Subclasses of <code>AbstractCollector</code> should implement this method
	 * to setup and carry out data collection.
	 */
	protected abstract void onStart();

	/**
	 * Implementations of <code>onStop</code> should immediately halt collection
	 * and perform any necessary cleanup and release resources associated with
	 * collecting data.
	 */
	protected abstract void onStop();

	// TODO Consider adding an onPause and onResume.

}

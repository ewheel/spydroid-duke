package edu.duke.spydroid;

import java.util.Map;
import java.util.Observable;

import android.content.Context;
import android.content.Intent;

public abstract class AbstractCollector extends Observable {
	public static final String TITLE_KEY = "title";
	public static final String CONTENT_KEY = "content";
	public static final String COLLECTOR = "edu.duke.spydroid.Collector";
	public static final String EXTERNAL_INTENT = "edu.duke.spydroid.ExternIntent";

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
	 * @return The collected data, or null if no data has been collected.
	 */
	public abstract Object getData();

	/**
	 * Returns the collected data in some displayable format, such that data is
	 * grouped into categories and mapped between display categories (i.e.
	 * title, summary) and data.
	 * 
	 * The number of categories that will actually be displayed is dependent on
	 * the implementation of the displaying view.
	 * 
	 * @return A <code>Map</code> between display categories and formatted data.
	 */
	public abstract Map<String, ?> getDisplayableData();

	/**
	 * Provides access a <code>Context</code> in order to facilitate
	 * communication with other application components and the system.
	 * 
	 * @return a <code>Context</code> object which is not guaranteed to be this
	 *         application's context
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
	 * Called to cause this collector to handle collection once all necessary
	 * collection resources have become available such as a particular system
	 * state or a received <code>Intent</code>.
	 * 
	 * Many Collectors will have this method called as the result of an
	 * <code>Intent</code> external to this application. In this case, the
	 * intent parameter is that <code>Intent</code> otherwise the
	 * <code>Intent</code> may be null.
	 * 
	 * @param intent
	 *            An <code>Intent</code> generated either by the system or
	 *            another application that resulted in this Collector being
	 *            chosen to handle that <code>Intent</code>, otherwise null;
	 */
	public final void collect(Intent intent) {
		if (onCollect(intent)) {
			setChanged();
			notifyObservers(getData());
		}
	}

	/**
	 * Subclasses of <code>AbstractCollector</code> should implement this method
	 * to perform any setup necessary for data collection. Collectors which
	 * perform collection statically (i.e. Do not need to wait on system
	 * messages or state) should call <method>onCollect</method> from this
	 * method to perform data collection.
	 */
	protected abstract void onStart();

	/**
	 * Implementations of <code>onStop</code> should immediately halt collection
	 * and perform any necessary cleanup and release resources associated with
	 * collecting data.
	 */
	protected abstract void onStop();

	/**
	 * All data collection should be performed within this method after
	 * <method>onStart</method> is called. This method will be called from the
	 * public method <method>collect</method>.
	 * 
	 * If collected data has changed then calls to this method should return
	 * true and subsequent calls to <method>getData</method> and
	 * <method>getDisplayableData</data> should return updated values.
	 * 
	 * Many Collectors will have this method called as the result of an
	 * <code>Intent</code> external to this application. In this case, the
	 * intent parameter is that <code>Intent</code> otherwise the
	 * <code>Intent</code> may be null.
	 * 
	 * @param intent
	 *            An <code>Intent</code> generated either by the system or
	 *            another application that resulted in this Collector being
	 *            chosen to handle that <code>Intent</code>, otherwise null;
	 * 
	 * @return true if new data was collected, false otherwise.
	 */
	protected abstract boolean onCollect(Intent intent);

}

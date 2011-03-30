package edu.duke.spydroid;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

public abstract class StaticCollector extends AbstractCollector {
	private Service mBackgroundService;
	Object mData;

	/**
	 * Default constructor for <code>StaticCollector</code>.
	 * 
	 * @param ctxt
	 *            Any valid context within the application (may be used for
	 *            system communication and access to SharedPreferences)
	 * @param preferenceKey
	 *            A key used to identify preferences associated with this
	 *            collector, such as enable and disable.
	 * @param service
	 *            The service used to execute this collection task on, if
	 *            service is null then collection is executed on the main
	 *            thread.
	 */
	public StaticCollector(Context ctxt, String preferenceKey, Service service) {
		super(ctxt, preferenceKey);
		mBackgroundService = service;
	}

	@Override
	protected void onStart() {
		if (mBackgroundService != null) {
			Intent service = new Intent(getContext(),
					mBackgroundService.getClass());
			service.putExtra(AbstractCollector.COLLECTOR, this.getClass()
					.getName());
			getContext().startService(service);
		} else
			collect(null);
	}

	@Override
	protected void onStop() {
		// Future implementations might want to stop a service here
		// Intent service= new
		// Intent(getContext(),mBackgroundService.getClass());
		// if(mBackgroundService!=null) getContext().stopService(service);
	}

	@Override
	protected abstract boolean onCollect(Intent intent);

}

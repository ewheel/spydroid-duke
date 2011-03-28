package edu.duke.spydroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class BroadcastCollector extends AbstractCollector {
	private IntentFilter mFilter;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Intent service = new Intent(getContext(), getContext().getClass());
			service.putExtra(AbstractCollector.COLLECTOR,
					BroadcastCollector.this.getClass().getName());
			service.putExtra(AbstractCollector.EXTERNAL_INTENT, intent);
			getContext().startService(service);
		}

	};

	/**
	 * 
	 * @param ctxt
	 *            A service context capable of calling this class's
	 *            <method>onCollect</method>, generally on a different thread.
	 * @param preferenceKey
	 * @param filter
	 */
	public BroadcastCollector(Context ctxt, String preferenceKey,
			IntentFilter filter) {
		super(ctxt, preferenceKey);
		mFilter = filter;
	}

	@Override
	protected void onStart() {
		getContext().registerReceiver(mReceiver, mFilter);

	}

	@Override
	protected void onStop() {
		getContext().unregisterReceiver(mReceiver);

	}

	/**
	 * Provides access to the <code>IntentFilter</code> this BroadcastCollector
	 * is using to listen for broadcasts.
	 * 
	 * @return the <code>IntentFilter</code> in use by this
	 *         <code>BroadcastCollector</code>.
	 */
	protected IntentFilter getIntentFilter() {
		return mFilter;
	}

}

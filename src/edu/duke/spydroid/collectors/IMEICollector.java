package edu.duke.spydroid.collectors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import edu.duke.spydroid.R;
import edu.duke.spydroid.StaticCollector;

public class IMEICollector extends StaticCollector {

	public IMEICollector(Context ctxt, String preferenceKey, Service service) {
		super(ctxt, preferenceKey, service);
		setDisplayTitle(R.string.title_IMEI);
	}

	@Override
	protected boolean onCollect(Intent intent) {
		TelephonyManager tm = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
		setData(tm.getDeviceId());
		return true;
	}

}

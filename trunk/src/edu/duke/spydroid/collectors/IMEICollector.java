package edu.duke.spydroid.collectors;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import edu.duke.spydroid.AbstractCollector;
import edu.duke.spydroid.R;

public class IMEICollector extends AbstractCollector {
	private String IMEIData;

	public IMEICollector(Context ctxt, String preferenceKey) {
		super(ctxt, preferenceKey);
	}

	@Override
	public Object getData() {
		return IMEIData;
	}

	@Override
	public Map<String, ?> getDisplayableData() {
		Map<String,String> displayMap=new HashMap<String,String>();
		String value=getContext().getString(R.string.title_IMEI);
		displayMap.put(AbstractCollector.TITLE_KEY, value);
		displayMap.put(AbstractCollector.CONTENT_KEY, getData().toString());
		return displayMap;
	}

	@Override
	protected void onStart() {
		collect(null);
	}

	@Override
	protected void onStop() {
		// No cleanup required
	}

	@Override
	protected boolean onCollect(Intent intent) {
		TelephonyManager tm = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
		IMEIData=tm.getDeviceId();
		return true;
	}

}

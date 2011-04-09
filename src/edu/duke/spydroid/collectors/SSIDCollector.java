package edu.duke.spydroid.collectors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import edu.duke.spydroid.R;
import edu.duke.spydroid.StaticCollector;

public class SSIDCollector extends StaticCollector {

	public SSIDCollector(Context ctxt, String preferenceKey, Service service) {
		super(ctxt, preferenceKey, service);
		setDisplayTitle(R.string.title_SSID);
	}

	@Override
	protected boolean onCollect(Intent intent) {
		WifiManager wm = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
		String ssid = wm.getConnectionInfo().getSSID();
		if(ssid == null)
		{
			ssid = "";
		}
		setData(ssid);
		return true;
	}

}

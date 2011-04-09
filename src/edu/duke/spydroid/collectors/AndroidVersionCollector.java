package edu.duke.spydroid.collectors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import edu.duke.spydroid.R;
import edu.duke.spydroid.StaticCollector;

public class AndroidVersionCollector extends StaticCollector {

	public AndroidVersionCollector(Context ctxt, String preferenceKey, Service service) {
		super(ctxt, preferenceKey, service);
		setDisplayTitle(R.string.title_version);
	}

	@Override
	protected boolean onCollect(Intent intent) {
		setData(android.os.Build.VERSION.RELEASE);
		return true;
	}

}

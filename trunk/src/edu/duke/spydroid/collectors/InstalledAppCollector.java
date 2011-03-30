package edu.duke.spydroid.collectors;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import edu.duke.spydroid.R;
import edu.duke.spydroid.StaticCollector;

public class InstalledAppCollector extends StaticCollector {


	public InstalledAppCollector(Context ctxt, String preferenceKey,
			Service service) {
		super(ctxt, preferenceKey, service);
		setDisplayTitle(R.string.title_installed_apps);
	}

	@Override
	protected boolean onCollect(Intent intent) {
		PackageManager pm = getContext().getPackageManager();

		Intent queryIntent = new Intent(Intent.ACTION_MAIN, null);
		queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> list = pm.queryIntentActivities(queryIntent, PackageManager.PERMISSION_GRANTED);
		StringBuilder appList = new StringBuilder();
		for (ResolveInfo rInfo : list) {
			appList.append(rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
			appList.append(", ");
		}
		appList.delete(appList.length()-2, appList.length());
		setData(appList.toString());
		return true;
		
	}

}

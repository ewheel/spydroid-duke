package edu.duke.spydroid.collectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import edu.duke.spydroid.AbstractCollector;
import edu.duke.spydroid.R;

public class InstalledAppCollector extends AbstractCollector {
	private String installedApps;

	public InstalledAppCollector(Context ctxt, String preferenceKey) {
		super(ctxt, preferenceKey);
	}

	@Override
	public Object getData() {
		return installedApps;
	}

	@Override
	public Map<String, ?> getDisplayableData() {
		Map<String,String> displayMap=new HashMap<String,String>();
		String value=getContext().getString(R.string.title_installed_apps);
		displayMap.put(AbstractCollector.TITLE_KEY, value);
		displayMap.put(AbstractCollector.CONTENT_KEY, getData().toString());
		return displayMap;
	}

	@Override
	protected void onStart() {
		
		PackageManager pm = getContext().getPackageManager();

		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
		StringBuilder appList = new StringBuilder();
		for (ResolveInfo rInfo : list) {
			appList.append(rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
			appList.append(", ");
		}
		appList.delete(appList.length()-2, appList.length());
		installedApps=appList.toString();
		setChanged();
		notifyObservers(installedApps);
	}

	@Override
	protected void onStop() {
		// No cleanup required so not implemented. 
	}

}

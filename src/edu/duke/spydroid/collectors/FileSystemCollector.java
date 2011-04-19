package edu.duke.spydroid.collectors;

import java.io.File;
import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.duke.spydroid.R;
import edu.duke.spydroid.StaticCollector;

public class FileSystemCollector extends StaticCollector {
	private static final String PREF_KEY_SEARCH_POSTFIX="_text";

	public FileSystemCollector(Context ctxt, String preferenceKey,
			Service service) {
		super(ctxt, preferenceKey, service);
		setDisplayTitle(R.string.title_file_system);
	}
	
	

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		startCollect();
	}



	@Override
	protected boolean onCollect(Intent intent) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		String searchDirectory=sharedPrefs.getString(getPreferenceKey()+PREF_KEY_SEARCH_POSTFIX, "");
		Log.d("SD Card", "Attempting to read from dir"+searchDirectory);
		String fileSystemInfo = "";
		fileSystemInfo = printDirectory("", searchDirectory, fileSystemInfo);
		if(fileSystemInfo == "")
		{
			fileSystemInfo = "Could not read directory.";
		}
		setData(fileSystemInfo);
		return true;
	}

	private String printDirectory(String prolog, String dir, String toPrint) {
		File root = new File(dir);
		if(!root.exists()) Log.d("SD Card2", "Attempting to read from: "+dir);
		File[] filelist = root.listFiles();
		ArrayList<File> directories = new ArrayList<File>();
		if(filelist != null){
			for (int i = 0; i < filelist.length; i++) {
				if (filelist[i].isFile()) {
					toPrint += (prolog + "File " + filelist[i].getName() + "\n");
				} else if (filelist[i].isDirectory()) {
					toPrint += (prolog + "Directory " + filelist[i].getName() + "\n");
					directories.add(filelist[i]);
				}
			}
			for (File f : directories) {
				Log.v("Directory", f.getAbsolutePath());
				if (f.canRead()) {
					try {
						toPrint = printDirectory(prolog + "\t",
								f.getAbsolutePath(), toPrint);
					} catch (NullPointerException e) {
						continue;
					}
				}
			}
		}
		return toPrint;
	}

}

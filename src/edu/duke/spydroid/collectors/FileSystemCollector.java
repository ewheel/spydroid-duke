package edu.duke.spydroid.collectors;

import java.io.File;
import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.duke.spydroid.R;
import edu.duke.spydroid.StaticCollector;

public class FileSystemCollector extends StaticCollector {

	public FileSystemCollector(Context ctxt, String preferenceKey,
			Service service) {
		super(ctxt, preferenceKey, service);
		setDisplayTitle(R.string.title_file_system);
	}

	@Override
	protected boolean onCollect(Intent intent) {
		// PackageManager pm = getContext().getPackageManager();

		// Intent intent = new Intent(Intent.ACTION_MAIN, null);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);

		String fileSystemInfo = "";
		fileSystemInfo = printDirectory("", "/sdcard/download", fileSystemInfo);
		setData(fileSystemInfo);
		return true;
	}

	private String printDirectory(String prolog, String dir, String toPrint) {
		File root = new File(dir);
		File[] filelist = root.listFiles();
		ArrayList<File> directories = new ArrayList<File>();
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
		return toPrint;
	}

}

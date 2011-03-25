package edu.duke.spydroid.collectors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
import android.util.Log;
import edu.duke.spydroid.AbstractCollector;
import edu.duke.spydroid.R;

public class FileSystemCollector extends AbstractCollector {
	private String fileSystem;

	public FileSystemCollector(Context ctxt, String preferenceKey) {
		super(ctxt, preferenceKey);
	}

	@Override
	public Object getData() {
		return fileSystem;
	}

	@Override
	public Map<String, ?> getDisplayableData() {
		Map<String,String> displayMap=new HashMap<String,String>();
		String value=getContext().getString(R.string.title_file_system);
		displayMap.put(AbstractCollector.TITLE_KEY, value);
		displayMap.put(AbstractCollector.CONTENT_KEY, getData().toString());
		return displayMap;
	}

	@Override
	protected void onStart() {
		
//		PackageManager pm = getContext().getPackageManager();

//		Intent intent = new Intent(Intent.ACTION_MAIN, null);
//		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		String fileSystemInfo = "";
		fileSystemInfo = printDirectory("","/sdcard",fileSystemInfo);
		fileSystem=fileSystemInfo;
		setChanged();
		notifyObservers(fileSystem);
	}

	@Override
	protected void onStop() {
		// No cleanup required so not implemented. 
	}
	private String printDirectory(String prolog, String dir, String toPrint){
		File root = new File(dir);
		File [] filelist = root.listFiles();
		ArrayList<File> directories = new ArrayList<File>();
		for (int i = 0; i < filelist.length; i++) {
		      if (filelist[i].isFile()) {
		        toPrint += (prolog + "File " + filelist[i].getName() + "\n");
		      } else if (filelist[i].isDirectory()) {
		        toPrint += (prolog + "Directory " + filelist[i].getName() + "\n");
		        directories.add(filelist[i]);
		      }
		}
		for(File f: directories)
		{
			Log.v("Directory", f.getAbsolutePath());
			if(f.canRead()){
			try{
				toPrint = printDirectory(prolog + "\t", f.getAbsolutePath(), toPrint);
			}
			catch(NullPointerException e){
				continue;
			}
			}
		}
		return toPrint;
	}

}

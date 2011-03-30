package edu.duke.spydroid.collectors;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import edu.duke.spydroid.R;
import edu.duke.spydroid.StaticCollector;

public class AccountInfoCollector extends StaticCollector {


	public AccountInfoCollector(Context ctxt, String preferenceKey,
			Service service) {
		super(ctxt, preferenceKey, service);
		setDisplayTitle(R.string.title_account_info);
	}

	@Override
	protected boolean onCollect(Intent intent) {
		AccountManager am=(AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
		Account[] accounts = am.getAccounts();
		StringBuilder appList = new StringBuilder();
		for (Account account : accounts) {
			String type=account.type;
			String account_email = account.name;
			appList.append(type);
			appList.append(": ");
			appList.append(account_email);
			appList.append(", ");
		}
		appList.delete(appList.length()-2, appList.length());
		setData(appList);
		return true;
	}

	        

}

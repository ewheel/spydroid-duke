package edu.duke.spydroid.collectors;

import java.util.HashMap;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;

import edu.duke.spydroid.AbstractCollector;
import edu.duke.spydroid.R;

public class AccountInfoCollector extends AbstractCollector {
	private String myAccountInfo;

	public AccountInfoCollector(Context ctxt, String preferenceKey) {
		super(ctxt, preferenceKey);
	}

	@Override
	public Object getData() {
		return myAccountInfo;
	}

	@Override
	public Map<String, ?> getDisplayableData() {
		Map<String,String> displayMap=new HashMap<String,String>();
		String value=getContext().getString(R.string.title_account_info);
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
		// TODO Auto-generated method stub

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
		myAccountInfo=appList.toString();
		return true;
	}

	        

}

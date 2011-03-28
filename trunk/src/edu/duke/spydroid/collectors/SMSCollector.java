package edu.duke.spydroid.collectors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import edu.duke.spydroid.AbstractCollector;
import edu.duke.spydroid.BroadcastCollector;
import edu.duke.spydroid.R;

public class SMSCollector extends BroadcastCollector {
	private String mMessageData;

	public SMSCollector(Context ctxt, String preferenceKey, IntentFilter filter) {
		super(ctxt, preferenceKey, filter);
	}

	@Override
	public Object getData() {
		return mMessageData;
	}

	@Override
	public Map<String, ?> getDisplayableData() {
		Map<String,String> displayMap=new HashMap<String,String>();
		String value=getContext().getString(R.string.title_received_SMS);
		displayMap.put(AbstractCollector.TITLE_KEY, value);
		displayMap.put(AbstractCollector.CONTENT_KEY, getData().toString());
		return displayMap;
	}



	@Override
	protected boolean onCollect(Intent intent) {
		Iterator<String> filterActions=getIntentFilter().actionsIterator();
		String action=intent.getAction();
		while(filterActions.hasNext()) {
			if(filterActions.next().equals(action)) {
				mMessageData=processTextMessage(intent);
				return true;
			}
		}
		return false;
	}
	
	private String processTextMessage(Intent intent) {
		Bundle bundle = intent.getExtras();         
		StringBuilder sb =new StringBuilder();
		if (bundle != null) {
        	SmsMessage[] msgs;  
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);  
                sb.append("From: ").append(msgs[i].getOriginatingAddress());
                sb.append("\nBody: ").append(msgs[i].getMessageBody().toString());
                sb.append("\n");
            }
        }
		 return sb.toString();
	}
	
}

	
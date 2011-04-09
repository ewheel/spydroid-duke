package edu.duke.spydroid;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetworkManager {

	private String myUID;
	private static Context myContext;
	private static DefaultHttpClient httpClient = null;
	private static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
	
	public NetworkManager(Context c)
	{
		myContext = c;
		myUID = ((TelephonyManager)myContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}
	
	public String getMyUID() {
		return myUID;
	}
	
    public static String sendToServer( String request ) throws IOException {
        String result = null;
        maybeCreateHttpClient();
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(myContext);
        String serverLocation = sPref.getString("pref_edit_server_addr", "http://barko.zapto.org:8080");
        HttpPost post = new HttpPost(serverLocation);
        post.addHeader( "Content-Type", "text/vnd.aexp.json.req" );
        post.setEntity( new StringEntity( request ) );    
        HttpResponse resp = httpClient.execute( post );
// Execute the POST transaction and read the results
        int status = resp.getStatusLine().getStatusCode(); 
        if( status != HttpStatus.SC_OK )
                throw new IOException( "HTTP status: "+Integer.toString( status ) );
        DataInputStream is = new DataInputStream( resp.getEntity().getContent() );
        result = is.readLine();
        return result;
    }

    private static void maybeCreateHttpClient() {
        if ( httpClient == null) {
            httpClient = new DefaultHttpClient();
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
            ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
        }
    }
    
	public JSONObject toJSON(String ident, String keyName, String key) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put( "id",ident);
        obj.put("name", keyName);
        obj.put( "content",key );
        return obj;
}
	public void packageAndSendUpdate(Map<String, ?> dataMap) {
		
		JSONArray elements = new JSONArray();
		try {
			elements.put(toJSON(myUID, (String)dataMap.get(AbstractCollector.TITLE_KEY), (String)dataMap.get(AbstractCollector.CONTENT_KEY)));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			String response = sendToServer(elements.toString());
			Log.w("Responses", response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
}
}

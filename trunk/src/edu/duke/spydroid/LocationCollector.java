package edu.duke.spydroid;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationCollector extends AbstractCollector {
	private static final String LOCATION_KEY = "LOC_KEY";
	
	private LocationManager mLocationMgr;
	private Location mLastLocation;
	private LocationListener mLocationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	        // Called when a new location is found by the network location provider.
	        //Test here, if sending the intent causes issues.
	        Intent i = new Intent();
	        i.putExtra(LOCATION_KEY, location);
	    	collect(i);
	      }

	      public void onStatusChanged(String provider, int status, Bundle extras) {}
	      public void onProviderEnabled(String provider) {}
	      public void onProviderDisabled(String provider) {
	    	 stopCollect();
	      }
	    };

	public LocationCollector(Context ctxt, String preferenceKey) {
		super(ctxt, preferenceKey);
		mLocationMgr = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
		setDisplayTitle(R.string.title_location);
	}

	@Override
	protected void onStart() {
		mLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
	}

	@Override
	protected void onStop() {
		mLocationMgr.removeUpdates(mLocationListener);
	}

	@Override
	protected boolean onCollect(Intent intent) {
		Location currentLocation = intent.getExtras().getParcelable(LOCATION_KEY);
		if(currentLocation==null) return false;
		if(mLastLocation==null) {
			mLastLocation = currentLocation;
		} else if (mLastLocation.equals(currentLocation)) {
			return false;
		}	
		double lat=currentLocation.getLatitude();
		double lon=currentLocation.getLongitude();	
		String location = "Latitude: "+lat+"\nLongitude: "+lon;
		setData(location);
		return true;
	}

}

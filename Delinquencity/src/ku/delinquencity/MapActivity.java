package ku.delinquencity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;


public class MapActivity extends FragmentActivity implements LocationListener{
	
	
	private GoogleMap map;
	private boolean firstLocationSet = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// Update layout
		setContentView(R.layout.main_map);
		
		// Get services status
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		 
		// Google services enabled?
		if(status != ConnectionResult.SUCCESS){

			// Show error
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, 10);
            dialog.show();
		}
		
		// Successful connect to services
		else {

			// Get support fragment and map
            SupportMapFragment supportMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            map = supportMap.getMap();
 
            // Enabling location
            map.setMyLocationEnabled(true);
 
            // Get location manager and best provider
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(new Criteria(), true);
 
            // Request updates
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
            
            TextView locText = (TextView) findViewById(R.id.locText);
            locText.setText("Waiting for location....");
            
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
 
		TextView locText = (TextView) findViewById(R.id.locText);
		
		// Current latitude and long
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        
        
        // Necessary to update camera?
        if(!firstLocationSet){
        
        	
        	// Zoom in the Google Map
        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));		
        	
        	//place a marker representing the npc
        	Marker npc1 = map.addMarker(new MarkerOptions()
        			.position(new LatLng(latitude+.0025, longitude+.0025))
        			.title("Test")
        			.icon(BitmapDescriptorFactory.fromAsset("ic_launcher.png")));
        	
        	// comment to enable or disable zoom updating 
        	firstLocationSet = true;
        }
        
        
        // Update text box
        locText.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );
        
    }
	@Override
	public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }
	@Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }
	@Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}

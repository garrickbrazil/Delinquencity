package ku.delinquencity;

import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;


public class MapActivity extends FragmentActivity implements LocationListener{
	
	// Globals
	private GoogleMap map;
	private LatLng player_pos;
	private boolean setupComplete = false;
	private boolean firstLocationSet = false;
	private ProgressDialog load_dialog;
	private IconGenerator iconGen;
	
	// Constants
	private final boolean CONTROLS_SHOWN = false;
	private final int REQUEST_FREQ = 5000;
	private final int ITEM_SIZE = 80;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
        // Setup loading dialog
        this.load_dialog = new ProgressDialog(this);
        this.load_dialog.setCanceledOnTouchOutside(false);
        this.load_dialog.setIndeterminate(true);
        this.load_dialog.setTitle("In progress");
        this.load_dialog.setMessage("Loading...");
        
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
            map.getUiSettings().setAllGesturesEnabled(CONTROLS_SHOWN);
			map.getUiSettings().setZoomControlsEnabled(CONTROLS_SHOWN);
			map.getUiSettings().setMyLocationButtonEnabled(CONTROLS_SHOWN);
			
			// Hack to make markers not clickable
        	map.setOnMarkerClickListener(new OnMarkerClickListener() {

        	    public boolean onMarkerClick(Marker marker) {
        	        return true;
        	    }
        	});
            
            // Get location manager and best provider
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(new Criteria(), true);
 
            // Request updates
            locationManager.requestLocationUpdates(provider, REQUEST_FREQ, 0, this);
            
            TextView locText = (TextView) findViewById(R.id.locText);
            locText.setText("Waiting for location....");
            
            player_pos = new LatLng(0,0);
            
            
            // Begin showing loading dialog
            load_dialog.show();
            
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
        
        	iconGen = new IconGenerator(latLng);

        	
        	//place a marker representing the npc
        	Marker npc1 = map.addMarker(new MarkerOptions()
        			.draggable(false)
        			.position(new LatLng(latitude+.0025, longitude+.0025))
        			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        					((BitmapDrawable) getResources().getDrawable(R.drawable.ic_cop).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));

        			
        	//place a marker representing the npc
        	Marker npc2 = map.addMarker(new MarkerOptions()
        			.draggable(false)
        			.position(new LatLng(latitude-.0025, longitude+.0025))
        			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        					((BitmapDrawable) getResources().getDrawable(R.drawable.ic_cop).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));
        	
        	//place a marker representing the npc
        	Marker npc3 = map.addMarker(new MarkerOptions()
		        	.draggable(false)		
		        	.position(new LatLng(latitude-.0025, longitude-.0025))
        			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        					((BitmapDrawable) getResources().getDrawable(R.drawable.ic_cop).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));

        	//place a marker representing the npc
        	Marker npc4 = map.addMarker(new MarkerOptions()
        			.draggable(false)
        			.position(new LatLng(latitude+.0025, longitude-.0025))
        			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        					((BitmapDrawable) getResources().getDrawable(R.drawable.ic_cop).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));

        	//place a marker representing the npc
        	Marker m1 = map.addMarker(new MarkerOptions()
        			.position(new LatLng(latitude-.003, longitude+.001))
        			.title("Money")
        			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        					((BitmapDrawable) getResources().getDrawable(iconGen.getRandomIcon()).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));


        	//place a marker representing the npc
        	Marker m2 = map.addMarker(new MarkerOptions()
        			.position(new LatLng(latitude-.0027, longitude-.0012))
        			.title("Money")
        			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        					((BitmapDrawable) getResources().getDrawable(iconGen.getRandomIcon()).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));


        	//place a marker representing the npc
        	Marker m3 = map.addMarker(new MarkerOptions()
        			.position(new LatLng(latitude+.0015, longitude+.00295))
        			.title("Money")
        			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        					((BitmapDrawable) getResources().getDrawable(iconGen.getRandomIcon()).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));

        	
        	//place a marker representing the npc
        	Marker m4 = map.addMarker(new MarkerOptions()
        			.position(new LatLng(latitude+.003, longitude-.0021))
        			.title("Money")
        			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        					((BitmapDrawable) getResources().getDrawable(iconGen.getRandomIcon()).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));

        	//place a marker representing the npc
        	Marker m5 = map.addMarker(new MarkerOptions()
        			.position(new LatLng(latitude+.002, longitude+.002))
        			.title("Money")
        			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        					((BitmapDrawable) getResources().getDrawable(iconGen.getRandomIcon()).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));


        	
        	SnapParams[] markersToSnap = new SnapParams[9];
        	
        	// Create an array of markers that require snapping
			markersToSnap[0] =new SnapParams(npc1);
			markersToSnap[1] =new SnapParams(npc2);
			markersToSnap[2] =new SnapParams(npc3);
        	markersToSnap[3] =new SnapParams(npc4);
			markersToSnap[4] =new SnapParams(m1);
			markersToSnap[5] =new SnapParams(m2);
			markersToSnap[6] =new SnapParams(m3);
        	markersToSnap[7] =new SnapParams(m4);
        	markersToSnap[8] =new SnapParams(m5);

        	// Update location
        	player_pos = latLng;
            locText.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );   
        	
        	new DownloadSnapTask().execute(markersToSnap);        	
        	firstLocationSet = true;
        	
        }
        
        else if(setupComplete){

        	player_pos = latLng;

            // Update text box
            locText.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );   

        }
        
    }

	/********************************************************************
     * Class: SnapParams
     * Purpose: contains parameters necessary to send into download task
    /*******************************************************************/
	private class SnapParams{
		
		boolean success;
		Marker marker;
		double lat;
		double lng;
		
		public SnapParams(Marker m){
			marker = m;
			success = false;
			lat = m.getPosition().latitude;
			lng = m.getPosition().longitude;
		}
		
	}
	
	/********************************************************************
     * Task: DownloadSnapTask
     * Purpose: used to snap items to the road
    /*******************************************************************/
	private class DownloadSnapTask extends AsyncTask<SnapParams, Void, SnapParams[]> {
    	
    	@Override
        protected SnapParams[] doInBackground(SnapParams... markersToUpdate) {  
    		
    		// No markers ?
    		if (markersToUpdate.length < 1){
    			markersToUpdate[0].success = false; 
    			return markersToUpdate;
    		}
    		
    		
    		try {
    		
    			// For all markers
    			for(SnapParams marker : markersToUpdate){
    			
    				// Convert latitude and longitude to string
	    			String start = marker.lat + "," + marker.lng;
	    			String end = marker.lat + "," + marker.lng;
	    						
	        		// Download from Mapquest and parse
	    			Document xml = Jsoup.parse(new URL("http://open.mapquestapi.com/directions/v2/route?key=Fmjtd%7Cluur21u7nl%2Crx%3Do5-90txq6&outFormat=xml&fullShape=true&from=" + start + "&to=" + end), 10000);
	    			
	    			// Make sure shapepoints exist
	    			if(xml.getElementsByTag("shapePoints").size() > 0 
	    					&& xml.getElementsByTag("shapePoints").get(0).getElementsByTag("lat").size() > 0 
	    					&& xml.getElementsByTag("shapePoints").get(0).getElementsByTag("lng").size() > 0){
	    				
	    				
	    				try{
	    					
	    					// Convert ANY shape point into a double
	    					Double lat = Double.parseDouble(xml.getElementsByTag("shapePoints").get(0).getElementsByTag("lat").get(0).text()); 
	    					Double lng = Double.parseDouble(xml.getElementsByTag("shapePoints").get(0).getElementsByTag("lng").get(0).text());
	    					
	    					
	    					// Store new position
	    					marker.lat = lat;
	    					marker.lng = lng;
	    					marker.success = true;
	    				}
	    				
	    				// Invalid double conversion !
	    				catch(Exception e){ markersToUpdate[0].success = false; return markersToUpdate;}	
	    			}
	    			
	    			// No shape points !
	    			else{
	    				markersToUpdate[0].success = false; return markersToUpdate;
	    			}
				}
    			
    			// Return the markers that have been updated
    			return markersToUpdate;
    		} 
    		
    		// Could not download !
    		catch (Exception e) { e.printStackTrace(); markersToUpdate[0].success = false; return markersToUpdate; }
        }      

        @Override
        protected void onPostExecute(SnapParams[] result) { 
        		
        		// For each marker, update the position
        		for(SnapParams r : result){
        			
        			// Only update if it was successful
        			if(r.success){
        				r.marker.setPosition(new LatLng(r.lat, r.lng));
        			}
        			
        			// Failure !
        			else {
        				// Global error
        				// TODO
        			}
        		}
        
        		
        		// If setup is not complete, then animate to location
        		if(!setupComplete){

        			// Zoom in the Google Map
        			map.animateCamera(CameraUpdateFactory.newLatLngZoom(player_pos, 15));
        			setupComplete = true;
        		}
        		
        		// Dismiss loading dialog
        		if(load_dialog.isShowing()) load_dialog.dismiss();
        }
        	
        
        @Override
        protected void onPreExecute() { load_dialog.show();}

        @Override
        protected void onProgressUpdate(Void... values) { }
    }
	
	/********************************************************************
     * Task: DownloadRouteTask
     * Purpose: used to gather shape points for a route
    /*******************************************************************/
/*	private class DownloadRouteTask extends AsyncTask<Cop, Void, Boolean> {
    	
    	@Override
        protected Boolean doInBackground(Cop... copToUpdate) {  
    		
    		if (copToUpdate.length < 1) return false;
    		
    		try {
    			
    			String start = copToUpdate[0].start.latitude + "," + copToUpdate[0].start.longitude;
    			String end = copToUpdate[0].destination.latitude + "," + copToUpdate[0].destination.longitude;
    			
        		// Download from mapquest
    			Document xml = Jsoup.parse(new URL("http://www.mapquestapi.com/directions/v2/route?key=Fmjtd%7Cluur21u7nl%2Crx%3Do5-90txq6&outFormat=xml&fullShape=truefrom=" + start + "&to=" + end), 10000);    			
    		
    			LatLng startCoord;
    			LatLng endCoord;
    			Double lat, lng;
    			
    		
	    		if(xml.getElementsByTag("shapePoints").size() > 0 
	                  && xml.getElementsByTag("shapePoints").get(0).getElementsByTag("latLng").size() > 0){
	    				Element firstLatLng = xml.getElementsByTag("shapePoints").get(0).getElementsByTag("latLng").get(0);
	    				if(firstLatLng.getElementsByTag("lat").size() > 0
	    					&& firstLatLng.getElementsByTag("lng").size() > 0){
	    					try{
	    						lat = Double.parseDouble(firstLatLng.getElementsByTag("lat").text());
	    						lng = Double.parseDouble(firstLatLng.getElementsByTag("lng").text());
	    					}
	    					catch(Exception e){ return false; }
	    					
	    					startCoord = new LatLng(lat, lng);
	    					xml.getElementsByTag("shapePoints").get(0).getElementsByTag("latLng").remove(0);
	    				}
	    				else return false;
	    			}
	    			else return false;
	    			
	    			for (Element latlngEl : xml.getElementsByTag("shapePoints").get(0).getElementsByTag("latLng")){
	    				
	    				try{
							lat = Double.parseDouble(latlngEl.getElementsByTag("lat").text());
							lng = Double.parseDouble(latlngEl.getElementsByTag("lng").text());
						}
						catch(Exception e){ return false; }
	    				
		    		}
	    			
    			return true;
			} 
    		catch (Exception e) { e.printStackTrace(); return false; }
        }      

        @Override
        protected void onPostExecute(Boolean result) {
        	
			if(result){
				
				// Parse JSON
				// Create shape points
				
			}

			else{
			
				// Send blank shape list (should restart later)
			}
		}
        
        @Override
        protected void onPreExecute() { }

        @Override
        protected void onProgressUpdate(Void... values) { }
    }
   
	// TEMP!!!!!!
	private class Cop{
		public LatLng start;
		public LatLng destination;
	}
	*/
	
	@Override
	public void onProviderDisabled(String provider) { }
	@Override
    public void onProviderEnabled(String provider) { }
	@Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
	
}

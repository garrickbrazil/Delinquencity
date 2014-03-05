package ku.delinquencity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
	public static final int MODE_COP = 0, MODE_ROBBER = 1;
	public final int SPEED_GRANDPA = 0, SPEED_AVERAGE = 1, SPEED_MARATHON = 2;
	public final int AREA_TINY = 0, AREA_MEDIUM = 1, AREA_LARGE = 2, AREA_MASSIVE = 3;
	private GoogleMap map;
	private int mode;
	private double speed;
	private int area;
	private int numCops;
	private int numItems;
	private boolean setupComplete = false;
	private boolean firstLocationSet = false;
	private ProgressDialog load_dialog;
	private IconGenerator iconGen;
	private Range range;
	private long lastTime;
	private AI[] cops;
	private List<Marker> items;
	private CoordCompare coordComparer = new CoordCompare();
	
	// Constants
	private final boolean CONTROLS_SHOWN = false;
	private final int REQUEST_FREQ = 500;
	private final int ITEM_SIZE = 68;
	private final int AI_SIZE = 90;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// Grab extras
		Bundle extras = getIntent().getExtras();
		mode = extras.getInt("mode");
		speed = extras.getInt("speed");
		area = extras.getInt("area");
		
		speed = (speed + 1) * .00003;
		numCops = (area + 1) * 4;
		numItems = (int)((area + 1) * 4.5);
		
		area = 17 - area;
		
        // Setup loading dialog
        this.load_dialog = new ProgressDialog(this);
        this.load_dialog.setCanceledOnTouchOutside(false);
        this.load_dialog.setIndeterminate(true);
        this.load_dialog.setTitle("In progress");
        this.load_dialog.setMessage("Loading...");
        
        this.lastTime = -1;
        
		// Update layout
		setContentView(R.layout.main_map);
		items = new ArrayList<Marker>();
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
            
            new LatLng(0,0);
            
            
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

        	map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, area));
        	range = new Range(area, map.getProjection().getVisibleRegion().latLngBounds);
        	
        	cops = new AI[this.numCops];
        	
        	for (int i = 0; i < cops.length; i++){
            	
        		//place a marker representing the npc
            	Marker npc = map.addMarker(new MarkerOptions()
            			.draggable(false)
            			.visible(true)
            			.position(range.random())
            			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
            					((BitmapDrawable) getResources().getDrawable((mode == MODE_COP)?R.drawable.ic_cop_color:R.drawable.ic_robber_color).getCurrent()).getBitmap(), AI_SIZE, AI_SIZE, false))));
            	
            	cops[i] = new AI(speed, npc, map,this.mode,items);
            	cops[i].setDestination(range.random());
            	
            	// TODO check to see if you are close to player!
            	cops[i].movingToPlayer = false; 
            	cops[i].waiting = true;
        	}
        	        	
        	SnapParams[] markersToSnap = new SnapParams[numItems];
        	
        	// Create an array of markers that require snapping
        	for(int i = 0; i < numItems; i++){
        		
        		//place a marker representing the npc
            	Marker m1 = map.addMarker(new MarkerOptions()
    		        	.visible(false)
    					.position(range.random())
            			.title("Money")
            			.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
            					((BitmapDrawable) getResources().getDrawable(iconGen.getRandomIcon()).getCurrent()).getBitmap(), ITEM_SIZE, ITEM_SIZE, false))));
            	items.add(m1);
            	markersToSnap[i] = new SnapParams(m1);
            	
        	}
        	

        	locText.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );   
        	
        	new DownloadSnapTask().execute(markersToSnap);
        	new DownloadRouteTask().execute(cops);
        	firstLocationSet = true;
        	
        }
        
        else if(setupComplete){

        	// Update text box
            locText.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );
        	long newTime = System.currentTimeMillis();
            
            // If there exist an older time
            if(lastTime > 0){
            	
            	// Move cops !
            	for(AI cop : cops){
            		
            		// Move cop based on time elapsed
            		if(!cop.wantedDeadOrAlive() &&!cop.waiting && !cop.move(newTime - lastTime,latLng)){
            			
            			// TODO get that cop a new route !!
            			cop.waiting = true;
            			cop.updatePositionProperty();
            			cop.setDestination(range.random());
            			new DownloadRouteTask().execute(cop);
            		}
            		else if(cop.wantedDeadOrAlive() && mode== MODE_COP){
            			locText.setText("YOU DIED!!!");
            		}
            	}	
            }
            
            if(mode == MODE_ROBBER)
            {
            	for(AI robber : cops)
            	{
            		if(coordComparer.isClose(robber.getPosition(),latLng))
            		{
            			robber.copMarker.remove();
            		}
            			
            	}
            	for(AI robber : cops)
            	{
            		for(Marker item : items)
            		{
            			if(coordComparer.isClose(robber.getPosition(), item.getPosition()))
            			{
            				item.remove();
            			}
            		}
            	}
            }
            
            lastTime = System.currentTimeMillis();
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
        				r.marker.setVisible(true);
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
	private class DownloadRouteTask extends AsyncTask<AI, Void, AI[]> {
    	
    	@Override
        protected AI[] doInBackground(AI... copsToUpdate) {  
    		
    		if (copsToUpdate.length < 1) return copsToUpdate;
    
    		for (AI cop : copsToUpdate){
    		
    			List<Shape> shapes = new ArrayList<Shape>();
    			boolean success = true;
    			
	    		try {
	    			
	    			// Compose start and end parameters
	    			String start = cop.getPosition().latitude + "," + cop.getPosition().longitude;
	    			String end = cop.getDestination().latitude + "," + cop.getDestination().longitude;
	    			
	        		// Download from mapquest
	    			Document xml = Jsoup.parse(new URL("http://www.mapquestapi.com/directions/v2/route?key=Fmjtd%7Cluur21u7nl%2Crx%3Do5-90txq6&outFormat=xml&fullShape=true&from=" + start + "&to=" + end), 10000);    			
	    		
	    			// Used parameters
	    			LatLng startCoord;
	    			LatLng endCoord;
	    			Double lat, lng;
	    		
	    			// Check that shape point objects exist
		    		if(xml.getElementsByTag("shapePoints").size() > 0 
		                  && xml.getElementsByTag("shapePoints").get(0).getElementsByTag("latLng").size() > 1){
		    			
		    			// Get first coord
		    			Element firstLatLng = xml.getElementsByTag("shapePoints").get(0).getElementsByTag("latLng").get(0);
		    			
		    			if(firstLatLng.getElementsByTag("lat").size() > 0
		    				&& firstLatLng.getElementsByTag("lng").size() > 0){
		    		
							lat = Double.parseDouble(firstLatLng.getElementsByTag("lat").text());
							lng = Double.parseDouble(firstLatLng.getElementsByTag("lng").text());

							// Store first coord and remove it			
							startCoord = new LatLng(lat, lng);
	    					xml.getElementsByTag("shapePoints").get(0).getElementsByTag("latLng").remove(0);
							
	    					// For each coordinate
	    					for (Element latlngEl : xml.getElementsByTag("shapePoints").get(0).getElementsByTag("latLng")){
			    				
	    						if(latlngEl.getElementsByTag("lat").size() > 0
	    							&& latlngEl.getElementsByTag("lng").size()> 0){
	    						
									lat = Double.parseDouble(latlngEl.getElementsByTag("lat").text());
									lng = Double.parseDouble(latlngEl.getElementsByTag("lng").text());

									// Make end coord and store
				    				endCoord = new LatLng(lat, lng);
				    				
				    				Shape shape = new Shape(startCoord, endCoord);
				    				shapes.add(shape);
				    				startCoord = endCoord;
								}
	    						else{ success = false; break; }
				    		}
	    					
	    					if(success){
	    						cop.setRoute(shapes);
	    					}
		    			}
		    		}
	    		}
	    		catch (Exception e) { e.printStackTrace(); return copsToUpdate; }
    		}
    		
    		return copsToUpdate;
        }      

        @Override
        protected void onPostExecute(AI[] result) {
        	
        	for(AI cop : result){
        		
        		cop.waiting = false;
        	}
        
        }
        
        @Override
        protected void onPreExecute() { }

        @Override
        protected void onProgressUpdate(Void... values) { }
    }
   

	
	@Override
	public void onProviderDisabled(String provider) { }
	@Override
    public void onProviderEnabled(String provider) { }
	@Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
	
}

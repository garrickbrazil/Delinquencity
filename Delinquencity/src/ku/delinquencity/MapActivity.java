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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

/********************************************************************
 * Class: MapActivity
 * Purpose: handles location updates and the main game mechanics
/*******************************************************************/
public class MapActivity extends FragmentActivity implements LocationListener{
	
	
	// Settings constants
	public static final int MODE_COP = 1, MODE_ROBBER = 0;
	public final int SPEED_GRANDPA = 0, SPEED_AVERAGE = 1, SPEED_MARATHON = 2;
	public final int AREA_TINY = 0, AREA_MEDIUM = 1, AREA_LARGE = 2, AREA_MASSIVE = 3;
	
	private final String ROBBER_LOSE_MESSAGE = "You were caught.\nYou are "
			+ "in jail forever.\n\nYou lose!";
	
	private final String COP_WIN_MESSAGE = "You caught all the robbers! "
			+ "They are now in jail forever and you are awesome.\n\nYou win!";
	
	private final String ROBBER_WIN_MESSAGE = "You stole all the times! You"
			+ " are rich forever.\n\nYou win!";
	
	private final String COP_LOSE_MESSAGE = "The thieves have finished stealing!"
			+ " Everything in the city is now gone.\n\nYou lose!";
	
	// Game constants
	private final int REQUEST_FREQ = 500;
	private final int ITEM_SIZE = 73;
	private final int AI_SIZE = 90;
	private final double DEFAULT_SPEED = .00003;
	private final int DEFAULT_BOTS = 4;
	private final double DEFAULT_ITEMS = 4.5;
	private final int DEFAULT_ZOOM = 17;
	
	private GoogleMap map;				// used to store and manipulate map
	private int mode;					// the current mode
	private double speed;				// the current speed
	private int area;					// current zoom setting 
	private int numBots;				// number of bots on the map
	private int numItems;				// number of items on the map
	private boolean setupComplete;		// whether or not setup has completed
	private boolean firstLocationSet;	// whether or not first location was found
	private ProgressDialog load_dialog;	// loading dialog
	private IconGenerator iconGen;		// generates random icons for map
	private Range range;				// determines placement for icons and AI
	private long lastTime;				// stores the last update time
	private static long startTime;		// stores the time in the start of the application
	private AI[] bots;					// stores the bots (cops or robbers depending on mode) 
	private List<Item> items;			// contains the items put on the map
	private boolean gameOver;			// whether or not the game has ended 
	public static int score;			// current score of the user 
	
	/********************************************************************
     * Method: onCreate
     * Purpose: called when the activity is first created
    /*******************************************************************/
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Grab settings from last activity
		Bundle extras = getIntent().getExtras();
		mode = extras.getInt("mode");
		speed = extras.getInt("speed");
		area = extras.getInt("area");
		
		// Calculate settings for game
		speed = (speed + 1) * DEFAULT_SPEED;
		numBots = (area + 1) * DEFAULT_BOTS;
		numItems = (int)((area + 1) * DEFAULT_ITEMS);
		area = -area + DEFAULT_ZOOM;
		
		// Defaults
		setupComplete = false;
		firstLocationSet = false;
		gameOver = false;
		lastTime = -1;
		startTime = System.currentTimeMillis();
		items = new ArrayList<Item>();
		iconGen = new IconGenerator();
		bots = new AI[this.numBots];
		
        // Setup loading dialog
        this.load_dialog = new ProgressDialog(this);
        this.load_dialog.setCanceledOnTouchOutside(false);
        this.load_dialog.setCancelable(true);
        this.load_dialog.setIndeterminate(true);
        this.load_dialog.setTitle("In progress");
        this.load_dialog.setMessage("Loading...");
        
         
		// Show main map screen
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
            map.getUiSettings().setAllGesturesEnabled(false);
			map.getUiSettings().setZoomControlsEnabled(false);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			
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
            
            
            // Show user that we are waiting for location update
            TextView locText = (TextView) findViewById(R.id.locText);
            locText.setText("Waiting for location....");
            
            // Begin showing loading dialog
            load_dialog.show();
            
		}
	}
	
	/********************************************************************
     * Method: calculateScore
     * Purpose: calculates score based on start time and current time
    /*******************************************************************/
	public static long calculateScore(){
		
		// Calculated time elapsed
		long dx = startTime - System.currentTimeMillis();
		
		// Based score
		long score = 100;
		
		// Bonus points based on time elapsed 
		long bonus = 18000/((dx/1000) + 600);
		bonus = (bonus/5) * 5;
		
		return score + bonus;
	}
	
	/********************************************************************
     * Method: onLocationChanged
     * Purpose: called when a new GPS position is available
    /*******************************************************************/
	@Override public void onLocationChanged(Location location) {
 
		// Game over ?
		if(gameOver) return;
		
		// New updated coordinate pair
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        
        
        // First location? Necessary to place items and AI
        if(!firstLocationSet){
        	
        	// Location is now set
        	firstLocationSet = true;
        	
        	// Move camera to correct spot and make range object
        	map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, area));
        	range = new Range(area, map.getProjection().getVisibleRegion().latLngBounds);
        	
        	
        	// Create bots
        	for (int i = 0; i < bots.length; i++){
            	
        		// Place bot marker at random locations
            	Marker npc = map.addMarker(new MarkerOptions()
            		.draggable(false)
            		.visible(true)
            		.position(range.random())
            		.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        				((BitmapDrawable) getResources()
        				.getDrawable((mode == MODE_ROBBER)?R.drawable.ic_cop_color
        				:R.drawable.ic_robber_color).getCurrent())
        				.getBitmap(), AI_SIZE, AI_SIZE, false))));
            	
            	// Make new AI and set give it a random destination
            	bots[i] = new AI(speed, npc, map,this.mode,items);
            	bots[i].setDestination(range.random());
            	
            	// Set defaults
            	bots[i].movingToPlayer = false; 
            	bots[i].waiting = true;
            	
            	// Allow AI to download its route
            	new DownloadRouteTask().execute(bots[i]);
            	
        	}
        	        	
        	// Create icons to be snapped
        	SnapParams[] markersToSnap = new SnapParams[numItems];
        	
        	// Create an array of markers that require snapping
        	for(int i = 0; i < numItems; i++){
        		
        		int icon = iconGen.getRandomIcon();
        		int width = ((BitmapDrawable) getResources().getDrawable(icon).getCurrent()).getBitmap().getWidth();
        		int height = ((BitmapDrawable) getResources().getDrawable(icon).getCurrent()).getBitmap().getHeight();
        		
        		int adjustedW, adjustedH;
        		
        		if(width > height){
        			adjustedW = ITEM_SIZE;
        			adjustedH = (int)(height * ITEM_SIZE)/width;
        		}
        		else{
        			adjustedH = ITEM_SIZE;
        			adjustedW = (int)(width * ITEM_SIZE)/height;
        		}
        		
        		//place a marker representing the npc
            	Marker m1 = map.addMarker(new MarkerOptions()
    		        .visible(false)
    				.position(range.random())
            		.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
            			((BitmapDrawable) getResources().getDrawable(
            			icon).getCurrent()).getBitmap(),
            			adjustedW, adjustedH, false))));
            	
            	// Add new item to list of items and to markersToSnap
            	items.add(new Item(m1));
            	markersToSnap[i] = new SnapParams(m1);
            	
        	}   
        	
        	// Execute all items at the same time
        	new DownloadSnapTask().execute(markersToSnap);
        }
        
        // Only perform any updates if all downloads have finished
        else if(setupComplete){

        	// Display current score
        	TextView scoreText = (TextView) findViewById(R.id.locText);
        	scoreText.setText("Score:  " + score);
        	
        	// Get new time
        	long newTime = System.currentTimeMillis();
            
        	// Default logic
        	boolean anyRobbersLeft=false;
            
            if(lastTime > 0){
            	
            	// Move bots
            	for(AI bot : bots){
            		
            		// Move bot based on time elapsed
            		// Do not move if the bot is dead or waiting for a route
            		if(!bot.wantedDeadOrAlive() && !bot.waiting && !bot.move(newTime - lastTime,latLng)){
            			
            			// Bot needs a new route
            			bot.waiting = true;
            			bot.updatePositionProperty();
            			bot.setDestination(range.random());
            			new DownloadRouteTask().execute(bot);
            		}
            		
            		// Has this cop caught the you (the robber)?
            		else if(bot.wantedDeadOrAlive() && mode == MODE_ROBBER){
            			
            			// End the game
            			gameOver = true;
            			
            			// Create a dialog builder
            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            			alertDialogBuilder.setTitle("Game over");
            		
            			
            			// Setup for robber lose dialog
	            		alertDialogBuilder
	            			.setMessage(ROBBER_LOSE_MESSAGE)
	            			.setCancelable(false)
	            			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	            				public void onClick(DialogInterface dialog,int id) {
	            					
	            					onBackPressed();
	            				}
	            			 }
	            		);
	             
	            		// Create and show dialog
	            		AlertDialog alertDialog = alertDialogBuilder.create();
	            		alertDialog.show();

            		}

            		// Logic to determine win condition for cop mode 
            		anyRobbersLeft = !bot.wantedDeadOrAlive() || anyRobbersLeft;
            	}
            	
            	// Has the player (cop) caught all the robbers? 
            	if(mode == MODE_COP && !anyRobbersLeft){
        			
            		// End the game
            		gameOver = true;
        			
            		// Make new dialog for game over
        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        			alertDialogBuilder.setTitle("Game over");
        		
        			// Setup for dialog
            		alertDialogBuilder
            			.setMessage(COP_WIN_MESSAGE)
            			.setCancelable(false)
            			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            				public void onClick(DialogInterface dialog,int id) {
            					
            					onBackPressed();
            				}
            		});
             
            		// create alert dialog
            		AlertDialog alertDialog = alertDialogBuilder.create();
             
            		// show it
            		alertDialog.show();
            	}
            }
            
            
            // Robber checks
            if(mode == MODE_ROBBER){
            	
            	// Used to determine if all items are taken
            	boolean itemsGone = true;
                
            	// Coordinate compare object
	            CoordCompare cc = new CoordCompare();
	            
	            // Check all items
	            for(Item item : items){
	            	
	            	// If they are close
	            	if(!item.getDead() && cc.isClose(latLng, item.getMarker().getPosition())){
	            		
	            		// Add to users score
	            		score += calculateScore();
	            		
	            		// Remove item from map and set dead
	            		item.getMarker().remove();
	            		item.setDead(true);
	            	}
	            	
	            	itemsGone = itemsGone && item.getDead();
	            }

            	
	            // Winning end condition
            	if(itemsGone){
            		
            		// End the game
            		gameOver = true;
    			
            		// Make dialog for game over
	    			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	    			alertDialogBuilder.setTitle("Game over");
	    		
	    			// Setup dialog
	        		alertDialogBuilder
	        			.setMessage(ROBBER_WIN_MESSAGE)
	        			.setCancelable(false)
	        			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	        				public void onClick(DialogInterface dialog,int id) {
	        					
	        					onBackPressed();
	        				}
	        		});
	         
	        		// Show dialog
	        		AlertDialog alertDialog = alertDialogBuilder.create();
	        		alertDialog.show();
            	}
            }
            
            // Checks for cop mode
            else if(mode == MODE_COP){
            	
            	// Check if any items are left
            	boolean itemsGone = true;
            	for(Item i : items) itemsGone = itemsGone && i.getDead();
            	
            	// Cop lose condition
            	if(itemsGone){
            		gameOver = true;
    			
            		// Create dialog builder
	    			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	    			alertDialogBuilder.setTitle("Game over");
	    		
	    			// Setup dialog 
	        		alertDialogBuilder
	        			.setMessage(COP_LOSE_MESSAGE)
	        			.setCancelable(false)
	        			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	        				public void onClick(DialogInterface dialog,int id) {
	        					
	        					onBackPressed();	        				
	        			}
	        		});
	         
	        		// Show dialog
	        		AlertDialog alertDialog = alertDialogBuilder.create();
	        		alertDialog.show();
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
	    			String extraParams = "&routeType=pedestrian"; 
	    					
	        		// Download from Mapquest and parse
	    			Document xml = Jsoup.parse(new URL("http://open.mapquestapi.com/directions/v2/route?key=Fmjtd%7Cluur21u7nl%2Crx%3Do5-90txq6&outFormat=xml&fullShape=true&from=" + start + "&to=" + end + extraParams), 10000);
	    			
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
        protected void onPreExecute() {}// load_dialog.show();}

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
	
	@Override
	public void onBackPressed() {
	    load_dialog.cancel();
	    load_dialog.dismiss();
		finish();
	}
}

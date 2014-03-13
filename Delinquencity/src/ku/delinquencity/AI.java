package ku.delinquencity;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class AI {
	CoordCompare comp = new CoordCompare();
	private Handler handler;
	private GoogleMap map;
	private long averageTime;
	boolean movingToPlayer = false;
	boolean waiting = false; //this is waiting for async
	Marker copMarker = null; //this is the cop icon
	private double speed = 0; //speed of the cop
	private List<Shape> shapePoints = new ArrayList<Shape>();
	private LatLng position = null; // needed for AsyncTask (cannot access map objects)
	private LatLng destination = null; // needed for AsyncTask (cannot access map objects)
	private int index = 0;//used to keep track of the index in the shape point array
	private int mode = 0;	
	private boolean markerDead = false;
	private List<Item> items;
	
	public AI(double speed, Marker copMarker, GoogleMap map, int mode, List<Item> items){
		this.items = items;
		this.speed = speed;
		this.copMarker = copMarker;
		this.position = copMarker.getPosition();
		this.map = map;
		this.mode = mode;
		this.averageTime = -1;

	}
	
	public boolean move(long dxTime, LatLng playerPosition){
		
		if(waiting) return true;	
		
		// Calculate distance needed to move
		double distance = (double) speed * (dxTime * .001);
		
		// SPEED ~ .021746 degrees / mile
		// 15 mph ~ .0000906833 degrees/second
		
		// Navigate through shapes	
		// convert meters to degrees
		while (distance > 0){
			
			// Need a new route
			if(index >= shapePoints.size())return false;
			
			if(distance > shapePoints.get(index).distance){
				distance -= shapePoints.get(index).distance;
				index++;
			}
			else{
			
				// Move marker distance along line shape 
				//(a,b) = (x1 + d/d'(x2-x1)),(y1 + d/d'(y2-y1))
				//where d = distance to move
				//where d' = distance between (x1,y1) and (x2,y2)
				double dist = distance/shapePoints.get(index).distance;
				double newLat = shapePoints.get(index).start.latitude + dist*(shapePoints.get(index).end.latitude - shapePoints.get(index).start.latitude);
				double newLong = shapePoints.get(index).start.longitude + dist*(shapePoints.get(index).end.longitude - shapePoints.get(index).start.longitude);
								
				LatLng moveTo = new LatLng(newLat,newLong);
				shapePoints.get(index).updateStart(moveTo);;
				//copMarker.setPosition(moveTo);
				
				if (averageTime <= 0){
					averageTime = dxTime;
				}
				else{
					averageTime = (averageTime + dxTime)/2;
				}
				if (handler == null) handler = animateMarker(copMarker,moveTo,false, averageTime, playerPosition);
				else {
					handler.removeCallbacksAndMessages(null);
					handler = animateMarker(copMarker,moveTo,false, averageTime, playerPosition);
				}
				
				distance = 0;
			}
		}
		// Return true -> Successful Update! or false -> Need new route
		return true;
	}
	
	private Handler animateMarker(final Marker marker, final LatLng toPosition,
            final boolean hideMarker, final long duration, 
            final LatLng playerPosition) {
        
		final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                
                //place player position check here
                if(comp.isClose(new LatLng(lat, lng), playerPosition))
                {
                
                	markerDead = true;
                	if(mode==MapActivity.MODE_COP){
                		copMarker.remove();
                		MapActivity.score += MapActivity.calculateScore();
                	}
                }
                if(mode==MapActivity.MODE_COP)
	                for(int indexer = 0; indexer < items.size();){
	                	
	                	if(!items.get(indexer).getDead() && comp.isClose(new LatLng(lat,lng), items.get(indexer).getMarker().getPosition())){
	                		items.get(indexer).getMarker().remove(); //delete from map
	                		items.get(indexer).setDead(true);  //delete from list
	                		//MapActivity.score += MapActivity.calculateScore();
	                	} else indexer++;	                	
	                }
                
                if(!markerDead){
                	marker.setPosition(new LatLng(lat, lng));

	                if (t < 1.0) {
	                	
	                    // Post again 16ms later.
	                    handler.postDelayed(this, 20);
	                } else {
	                    if (hideMarker) {
	                        marker.setVisible(false);
	                    } else {
	                        marker.setVisible(true);
	                    }
	                }
                }
            }
        });
        return handler;
    }
	
	public void updatePositionProperty(){
		this.position = this.copMarker.getPosition();
	}
	public LatLng getPosition(){
		return this.position;
	}
	public void setRoute(List<Shape> shapePoints){
		index = 0;//reset index
		this.shapePoints = shapePoints;
	}
	
	public LatLng getDestination(){
		return this.destination;
	}
	
	public void setDestination(LatLng destination){
		this.destination = destination;
	}
	
	public boolean wantedDeadOrAlive(){
		return markerDead;
	}
}

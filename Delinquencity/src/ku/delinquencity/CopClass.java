package ku.delinquencity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class CopClass {
	boolean movingToPlayer = false;
	boolean waiting = false; //this is waiting for async
	Marker copMarker = null; //this is the cop icon
	private double speed = 0; //speed of the cop
	private Shape[] shapePoints = null;
	private LatLng destination = null;//not sure of the purpose of this...
	private int index = 0;//used to keep track of the index in the shape point array
	
	public void Cop(double speed, Marker copMarker){
		this.speed = speed;
		this.copMarker = copMarker;
	}
	
	public boolean move(int dxTime){
		if(waiting) return true;	
		// Calculate distance needed to move
		double distance = (double)speed * dxTime;
		// Navigate through shapes	
		//convert meters to degrees
		while (distance > 0){
			if(index >= shapePoints.length)return false;//need new route	
			if(distance > shapePoints[index].distance){
				distance -= shapePoints[index].distance;
				copMarker.setPosition(shapePoints[index].end);//used so the cop doesn't jump too much
				index++;
			}
			else{
				// Move marker distance along line shape 
				//(a,b) = (x1 + d/d'(x2-x1)),(y1 + d/d'(y2-y1))
				//where d = distance to move
				//where d' = distance between (x1,y1) and (x2,y2)
				double dist = distance/shapePoints[index].distance;
				double newLat = shapePoints[index].start.latitude + dist*(shapePoints[index].end.latitude - shapePoints[index].start.latitude);
				double newLong = shapePoints[index].start.longitude + dist*(shapePoints[index].end.longitude - shapePoints[index].start.longitude);
								
				LatLng moveTo = new LatLng(newLat,newLong);
				shapePoints[index].start = moveTo;
				copMarker.setPosition(moveTo);
				distance = 0;
			}
		}
		// Return true -> Successful Update! or false -> Need new route
		return true;
	}
	
	public void setRoute(Shape[] shapePoints){
		index = 0;//reset index
		this.shapePoints = shapePoints;
	}
	
	public void setDestination(LatLng destination){
		this.destination = destination;
	}
}

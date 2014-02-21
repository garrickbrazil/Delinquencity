package ku.delinquencity;

import com.google.android.gms.maps.model.LatLng;

public class Shape {
	/*Global variables*/
	public LatLng start;
	public LatLng end;
	public double distance;
	/*Calculates the difference between lat and lng points in km*/
	public Shape(LatLng posStart, LatLng posEnd)
	{
		start = posStart;
		end = posEnd;
		/*Fancy mathamatical function to calculate distance of lat lng points to km*/
		int R = 6371;
		double dLat = Math.toRadians(posEnd.latitude - posStart.latitude);
		double dLon = Math.toRadians(posEnd.longitude - posStart.longitude);
		double startLat = Math.toRadians(posStart.latitude);
		double endLat = Math.toRadians(posEnd.latitude);
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(startLat) * Math.cos(endLat);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		distance = R * c;
	}
	/*Recalculate the distance with a new starting point*/
	public void updateStart(LatLng newStart)
	{
		start = newStart;
		
		/*Fancy mathamatical function to calculate distance of lat lng points to km*/
		int R = 6371;
		double dLat = Math.toRadians(end.latitude - start.latitude);
		double dLon = Math.toRadians(end.longitude - start.longitude);
		double startLat = Math.toRadians(start.latitude);
		double endLat = Math.toRadians(end.latitude);
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(startLat) * Math.cos(endLat);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		distance = R * c;
		
	}
}

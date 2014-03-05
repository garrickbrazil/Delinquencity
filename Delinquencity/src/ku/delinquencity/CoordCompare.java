package ku.delinquencity;
import com.google.android.gms.maps.model.LatLng;

public class CoordCompare
{
    final double THRESHOLD_DISTANCE = .012; //.0004;
	
	public boolean isClose(LatLng coord1, LatLng coord2)
	{
		return THRESHOLD_DISTANCE >= getMeters(coord1,coord2);
	}
	
	public double getDistance(LatLng coord1, LatLng coord2)
	{
		return Math.sqrt(Math.pow(coord2.latitude-coord1.latitude,2) + Math.pow(coord2.longitude-coord1.longitude, 2));
	}
	
	public double getMeters(LatLng start, LatLng end){				
		/*Fancy mathamatical function to calculate distance of lat lng points to km*/
		int R = 6371;
		double dLat = Math.toRadians(end.latitude - start.latitude);
		double dLon = Math.toRadians(end.longitude - start.longitude);
		double startLat = Math.toRadians(start.latitude);
		double endLat = Math.toRadians(end.latitude);
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(startLat) * Math.cos(endLat);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return R * c;
	}
}
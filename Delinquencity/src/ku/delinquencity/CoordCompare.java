package ku.delinquencity;
import com.google.android.gms.maps.model.LatLng;

public class CoordCompare
{
    final double THRESHOLD_DISTANCE = .0006;
	
	public boolean isClose(LatLng coord1, LatLng coord2)
	{
		return THRESHOLD_DISTANCE >= getDistance(coord1,coord2);
	}
	
	public double getDistance(LatLng coord1, LatLng coord2)
	{
		return Math.sqrt(Math.pow(coord2.latitude-coord1.latitude,2) + Math.pow(coord2.longitude-coord1.longitude, 2));
	}
}
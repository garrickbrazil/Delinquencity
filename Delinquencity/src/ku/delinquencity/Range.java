package ku.delinquencity;

import java.util.Random;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Range {
	/*Global variables*/
	public double heightDegrees;
	public double widthDegrees;
	public double zoomLevel;
	public LatLngBounds boundaries;
    /*Used to calculate height and width of boundary*/
	public void Range(double zoom, LatLngBounds bounds)
	{
		zoomLevel = zoom;
		LatLng northeast;
		LatLng southwest;
		
		northeast = bounds.northeast;
		southwest = bounds.southwest;
		
		heightDegrees = northeast.latitude - southwest.latitude;
		widthDegrees = northeast.longitude - southwest.longitude;
		
	}
	/*Easier way to check if within bounds*/
	public boolean withinBounds(LatLng compareCoord)
	{
		boolean contains;
		contains = boundaries.contains(compareCoord);
		return contains;
	}
	/*A random latlng point within the bounds*/
	public LatLng randomPoint()
	{
		Random generator = new Random();
		
		double randNumLat = -89;
		double randNumLong = -180;
		int set = 0;
		LatLng randCoord = new LatLng(randNumLat,randNumLong);
		/*Generate a random number until a set is produced within the bounds*/
		while(set == 0)
		{
			randNumLat = (-90) + (90 - (-90)) * generator.nextDouble();
			randNumLong = (-180) + (180 - (-180)) * generator.nextDouble();
			randCoord = new LatLng(randNumLat,randNumLong);
			if(withinBounds(randCoord) == true)
			{
				set = 1;
			}
		}
		/*Return the latlng within the bounds*/
		return randCoord;
	}
}

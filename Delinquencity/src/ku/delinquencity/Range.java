package ku.delinquencity;

import java.util.Random;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Range {
	/*Global variables*/
	public double heightDegrees;
	public double widthDegrees;
	public double zoomLevel;
	public Random generator;
	public LatLngBounds boundaries;
    /*Used to calculate height and width of boundary*/
	public Range(double zoom, LatLngBounds bounds)
	{
		generator = new Random();
		zoomLevel = zoom;
		LatLng northeast;
		LatLng southwest;
		
		boundaries = bounds;
		
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
	
	public LatLng random(){
		
		
		double lat = boundaries.northeast.latitude - widthDegrees * .045 - (generator.nextDouble() * widthDegrees*.91);
		double lng = boundaries.northeast.longitude - heightDegrees * .045 - (generator.nextDouble() * heightDegrees*.91);
		
		return new LatLng(lat, lng);
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

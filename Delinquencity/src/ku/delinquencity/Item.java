package ku.delinquencity;

import com.google.android.gms.maps.model.Marker;

public class Item{
	
	private Marker marker;
	private boolean dead;
	
	public Item(Marker marker){
		
		this.marker = marker;
		this.dead = false;
	}
	
	public boolean getDead(){ return this.dead;}
	public void setDead(boolean dead){this.dead = dead; }
	public Marker getMarker(){ return this.marker; }
}
package ku.delinquencity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import java.lang.Math;

public IconGenerator(){
	public static final int COP=0;
	public static final int COPSHIELD=1;
	public static final int ROBBER=2;
	public static final int GUN=3;
	public static final int CAR=4;
	public static final int PICKUP=5;
	public static final int BIKE=6;
	public static final int BEER=7;
	public static final int BREAD=8;
	public static final int COPMUTER=9;
	public static final int MOENYBAG=10;
	public static final int CASINO=11;
	
	private final int START = 3; //start of randomable icons
	private final int END = 11;  //end of randomable icons
	
	int iconList[] = {
		R.drawable.ic_cop,
		R.drawable.ic_copshield,
		R.drawable.ic_robber,
		R.drawable.ic_gun,
		R.drawable.ic_car,
		R.drawable.ic_pickup,
		R.drawable.ic_bike,
		R.drawable.ic_beer,
		R.drawable.ic_bread,
		R.drawable.ic_computer,
		R.drawable.ic_moneybag,
		R.drawable.ic_casino
		};
	
	int icon;
	LatLng center();
	
	IconGenerator(LatLng center){
		center.this = center;
		//will only random non cop or robber icons
		icon = iconList[(int)(((Math.random()*((END-START)+1))+START)];
	}
	
	IconGenerator(LatLng center, int icon){
		center.this = center;
		if (0<icon<11)//If icon is not valid use random icon 
			//will only random non cop or robber icons
			icon = iconList[(int)(((Math.random()*((END-START)+1))+START)];
		else
			icon.this = iconList[icon];
	}
	
	public void setIcon(int icon){
		icon.this = iconList[icon];
	}
	
	public int getIcon(){
		return icon;
	}
	
	public int getIcon(int icon){
		return iconList[icon];
	}
}
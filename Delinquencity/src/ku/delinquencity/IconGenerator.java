package ku.delinquencity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public IconGenerator(){
	public static int COP=0;
	public static int ROBBER=1;
	public static int COPSHIELD=2;
	public static int GUN=3;
	public static int CAR=4;
	public static int PICKUP=5;
	public static int BIKE=6;
	public static int BEER=7;
	public static int BREAD=8;
	public static int COPMUTER=9;
	public static int MOENYBAG=10;
	public static int CASINO=11;
	public static int BIOHAZARD=12;

	int iconList[] = {
		R.drawable.ic_cop,
		R.drawable.ic_robber,
		R.drawable.ic_copshield,
		R.drawable.ic_gun,
		R.drawable.ic_car,
		R.drawable.ic_pickup,
		R.drawable.ic_bike,
		R.drawable.ic_beer,
		R.drawable.ic_bread,
		R.drawable.ic_computer,
		R.drawable.ic_moneybag,
		R.drawable.ic_casino,
		R.drawable.ic_biohazard
		};
	
	int icon;
	LatLng center();
	IconGenerator(LatLng center){
		
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
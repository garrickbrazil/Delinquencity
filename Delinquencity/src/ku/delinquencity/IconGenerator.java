package ku.delinquencity;

import java.lang.Math;

import com.google.android.gms.maps.model.LatLng;

public class IconGenerator{

	public static final int COP=0;
	public static final int COPSHIELD=1;
	public static final int ROBBER=2;
	public static final int BACON=3;
	public static final int BANKCARDS=4;
	public static final int BEER=5;
	public static final int BIKE=6;
	public static final int BREAD=7;
	public static final int CAR=8;
	public static final int CASINO=9;
	public static final int CIGARETTES=10;
	public static final int COMPUTER=11;
	public static final int GASSTATION=12;
	public static final int GATLINGGUN=13;
	public static final int GUN=14;
	public static final int MONEY=15;
	public static final int MOENYBAG=16;
	public static final int MOTORCYCLE=17;
	public static final int PICKUP=18;
	public static final int PIZZA=19;
	public static final int RESTAURANT=20;
	public static final int RIFLE=21;
	public static final int TESTTUBE=22;
	
	static final int iconList[] = {
		R.drawable.ic_cop,
		R.drawable.ic_copshield,
		R.drawable.ic_robber,
		R.drawable.ic_bacon,
		R.drawable.ic_bankcards,
		R.drawable.ic_beer,
		R.drawable.ic_bike,
		R.drawable.ic_bread,
		R.drawable.ic_car,
		R.drawable.ic_casino,
		R.drawable.ic_cigarettes,
		R.drawable.ic_computer,
		R.drawable.ic_gasstation,
		R.drawable.ic_gatlinggun,
		R.drawable.ic_gun,
		R.drawable.ic_money,
		R.drawable.ic_moneybag,
		R.drawable.ic_motorcycle,
		R.drawable.ic_pickup,
		R.drawable.ic_pizza,
		R.drawable.ic_restaurant,
		R.drawable.ic_rifle,
		R.drawable.ic_testtube
		};

	
	private final int START = 3; //start of randomable icons
	private final int END = 22;  //end of randomable icons
	
	int icon;
	LatLng center;
	
	public IconGenerator(LatLng center){
		this.center = center;
		
		//will only random non cop or robber icons
		icon = getRandomIcon();
	}
	
	public IconGenerator(LatLng center, int icon){
		this.center = center;
		
		if (icon >  0 && icon < 11)//If icon is not valid use random icon 
			//will only random non cop or robber icons
			this.icon = getRandomIcon();
		else
			this.icon = iconList[icon];
		}
	
	public int getRandomIcon(){
		//will only random non cop or robber icons
		return iconList[(int)(((Math.random()*((END-START)+1))+START))];
	}
	
	public int getIcon(){
		return icon;
	}
	
	public int getIcon(int icon){
		return iconList[icon];
	}
	public LatLng getLatLng(){
		return center;
	}
}
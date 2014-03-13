package ku.delinquencity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/********************************************************************
 * Class: MainActivity
 * Purpose: runs the main program, starting with the start screen
/*******************************************************************/
public class MainActivity extends Activity {

	final String DISCLAIMER = 
			"Use Delinquencity with extreme caution. Do not break any "
			+ "laws while playing the game. Do not use this application "
			+ "while driving. Pay very close attention to your surroundings"
			+ " as to not cause harm to any others or yourself. Delinquencity"
			+ " is not responsible for any injuries incurred while playing "
			+ "Delinquencity." + "\n\nIcons by Icons8.com";
	
	final String ROBBER_HELP = "There are many valuable items "
			+ "throughout the city!\n\nSteal them without being caught "
			+ "by the patrolling coppers.";
	
	final String COP_HELP = "There are filthy robbers throughout the city!\n\n"
			+ "Catch them before they steal all the precious items.";
	
	
	/********************************************************************
     * Method: onCreate
     * Purpose: called when the activity is first created
    /*******************************************************************/
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Show start screen
		setContentView(R.layout.start_screen);
		
		// Gather items on display
		TextView title = (TextView) findViewById(R.id.title);
		Button start = (Button)findViewById(R.id.start);
		Button modeButton = (Button)findViewById(R.id.mode_button);
		Button speedButton = (Button)findViewById(R.id.speed_button);
		Button areaButton = (Button)findViewById(R.id.area_button);
		Button howtoButton = (Button)findViewById(R.id.howto_button);
		Spinner mode = (Spinner)findViewById(R.id.game_mode);
		Spinner speed = (Spinner)findViewById(R.id.game_speed);
		Spinner area = (Spinner)findViewById(R.id.game_area);
		
		
		// Set defaults
		mode.setSelection(0);
		speed.setSelection(0);
		area.setSelection(1);
		
		// Set Font
		Typeface titleFont = Typeface.createFromAsset(getAssets(),"boston.ttf");
		Typeface buttonFont = Typeface.createFromAsset(getAssets(),"ops.ttf");
		title.setTypeface(titleFont);
		start.setTypeface(buttonFont);
		modeButton.setTypeface(buttonFont);
		speedButton.setTypeface(buttonFont);
		areaButton.setTypeface(buttonFont);
		howtoButton.setTypeface(buttonFont);
		
	}
	
	/********************************************************************
     * Method: openMode
     * Purpose: opens the mode spinner so the user can select a setting
    /*******************************************************************/
	public void openMode(View v){
		
		Spinner mode = (Spinner) findViewById(R.id.game_mode);
		mode.performClick();
	}
	
	/********************************************************************
     * Method: openSpeed
     * Purpose: opens the speed spinner so the user can select a setting
    /*******************************************************************/
	public void openSpeed(View v){
		
		Spinner speed = (Spinner) findViewById(R.id.game_speed);
		speed.performClick();
	}
	
	/********************************************************************
     * Method: openArea
     * Purpose: opens the area spinner so the user can select a setting
    /*******************************************************************/
	public void openArea(View v){
		
		Spinner area = (Spinner) findViewById(R.id.game_area);
		area.performClick();
	}
	
	/********************************************************************
     * Method: howTo
     * Purpose: opens the how to so the user can understand gameplay
    /*******************************************************************/
	public void howTo(View v){
		
		// Create a dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		// Get current game mode spinner
		final Spinner mode = (Spinner)findViewById(R.id.game_mode);
		String message;
		
		// Robber Mode 
		if(mode.getSelectedItemPosition() == 0){
			message = ROBBER_HELP;
			alertDialogBuilder.setTitle("Robber Mode");
		}
		
		// Cop Mode
		else{
			message = COP_HELP;
			alertDialogBuilder.setTitle("Cop Mode");
		}
		
		// Setup dialog
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
				}
			}
		);
	
		
		// Create dialog and show
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		
	}
	
	/********************************************************************
     * Method: startButton
     * Purpose: starts the map activity
    /*******************************************************************/
	public void startButton(View v){
		
		// Gather the various setting spinners
		final Spinner mode = (Spinner)findViewById(R.id.game_mode);
		final Spinner speed = (Spinner)findViewById(R.id.game_speed);
		final Spinner area = (Spinner)findViewById(R.id.game_area);
		
		// Create builder and set title
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Disclaimer");
		
		// Setup dialog
		alertDialogBuilder
			.setMessage(DISCLAIMER)
			.setCancelable(false)
			
			// Agree with disclaimer
			.setPositiveButton("Agree",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {

					// Start new activity
					Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
					myIntent.putExtra("mode", mode.getSelectedItemPosition());
					myIntent.putExtra("speed", speed.getSelectedItemPosition());
					myIntent.putExtra("area", area.getSelectedItemPosition());
			    	MainActivity.this.startActivity(myIntent);
				}
			})
			
			// Disagree with disclaimer
			.setNegativeButton("Disagree",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			}
		);
 
		// Create dialog and show
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}

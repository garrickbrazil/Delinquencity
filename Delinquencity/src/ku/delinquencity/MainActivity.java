package ku.delinquencity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	final String disclaimer = 
			"Use Delinquencity with extreme caution. Do not break any laws while playing the game. Do not use this application while driving. Pay very close attention to your surroundings as to not cause harm to any others or yourself. Delinquencity is not responsible for any injuries incurred while playing Delinquencity." + "\n\nIcons by Icons8.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_screen);
		
		TextView title = (TextView) findViewById(R.id.title);
		Button start = (Button)findViewById(R.id.start);
		Button modeButton = (Button)findViewById(R.id.mode_button);
		Button speedButton = (Button)findViewById(R.id.speed_button);
		Button areaButton = (Button)findViewById(R.id.area_button);
		Button howtoButton = (Button)findViewById(R.id.howto_button);
		Spinner mode = (Spinner)findViewById(R.id.game_mode);
		Spinner speed = (Spinner)findViewById(R.id.game_speed);
		Spinner area = (Spinner)findViewById(R.id.game_area);
		mode.setSelection(0);
		speed.setSelection(0);
		area.setSelection(1);
		Typeface type = Typeface.createFromAsset(getAssets(),"boston.ttf");
		Typeface type2 = Typeface.createFromAsset(getAssets(),"ops.ttf");
		title.setTypeface(type);
		start.setTypeface(type2);
		modeButton.setTypeface(type2);
		speedButton.setTypeface(type2);
		areaButton.setTypeface(type2);
		howtoButton.setTypeface(type2);
	}
	public void openMode(View v){
		
		Spinner mode = (Spinner) findViewById(R.id.game_mode);
		mode.performClick();
	}
	public void openSpeed(View v){
		
		Spinner speed = (Spinner) findViewById(R.id.game_speed);
		speed.performClick();
	}
	public void openArea(View v){
		
		Spinner area = (Spinner) findViewById(R.id.game_area);
		area.performClick();
	}
	public void howTo(View v){
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		final Spinner mode = (Spinner)findViewById(R.id.game_mode);
		
		String message = "";
		
		if(mode.getSelectedItemPosition() == 0){
			message = "There are many valuable items throughout the city!\n\nSteal them without being caught by the patrolling coppers.";
			alertDialogBuilder.setTitle("Robber Mode");
		}
		else{
			message ="There are filthy robbers throughout the city!\n\nCatch them before they steal all the precious items.";
			alertDialogBuilder.setTitle("Cop Mode");
		}
		
		// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
						dialog.cancel();
					}
				});
	
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
	
		// show it
		alertDialog.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    
		/*switch(item.getItemId()) {
	    case R.id.help:
	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	    	 
			// set title
		alertDialogBuilder.setTitle("Help");
		
			// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
						dialog.cancel();
					}
				});
 
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
		// show it
		alertDialog.show();
		
	        break;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
		*/
	    return true;
	}
	
	public void startButton(View v){
		
		final Spinner mode = (Spinner)findViewById(R.id.game_mode);
		final Spinner speed = (Spinner)findViewById(R.id.game_speed);
		final Spinner area = (Spinner)findViewById(R.id.game_area);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 
			// set title
		alertDialogBuilder.setTitle("Disclaimer");
		
			// set dialog message
		alertDialogBuilder
			.setMessage(disclaimer)
			.setCancelable(false)
			.setPositiveButton("Agree",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					// Load map activity
					Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
					myIntent.putExtra("mode", mode.getSelectedItemPosition());
					myIntent.putExtra("speed", speed.getSelectedItemPosition());
					myIntent.putExtra("area", area.getSelectedItemPosition());
			    	MainActivity.this.startActivity(myIntent);
				}
			  })
			.setNegativeButton("Disagree",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
						dialog.cancel();
					}
				});
 
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
		// show it
		alertDialog.show();
	}
}

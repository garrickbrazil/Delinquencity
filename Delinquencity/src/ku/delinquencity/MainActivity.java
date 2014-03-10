package ku.delinquencity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

public class MainActivity extends Activity {
	//set help message TODO Write Help Message
	String message = 
			"Insert Help Message Here" +
			"\nIcons by Icons8.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_screen);
		
		Spinner mode = (Spinner)findViewById(R.id.game_mode);
		Spinner speed = (Spinner)findViewById(R.id.game_speed);
		Spinner area = (Spinner)findViewById(R.id.game_area);
		
		mode.setSelection(0);
		speed.setSelection(0);
		area.setSelection(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
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

	    return true;
	}
	
	public void startButton(View v){
		
		final Spinner mode = (Spinner)findViewById(R.id.game_mode);
		final Spinner speed = (Spinner)findViewById(R.id.game_speed);
		final Spinner area = (Spinner)findViewById(R.id.game_area);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 
			// set title
		alertDialogBuilder.setTitle("Help");
		
			// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("Start",new DialogInterface.OnClickListener() {
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
			.setNegativeButton("Cancle",new DialogInterface.OnClickListener() {
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

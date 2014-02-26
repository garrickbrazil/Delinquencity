package ku.delinquencity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Spinner;

public class MainActivity extends Activity {
	
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
	
	
	public void startButton(View v){
		
		Spinner mode = (Spinner)findViewById(R.id.game_mode);
		Spinner speed = (Spinner)findViewById(R.id.game_speed);
		Spinner area = (Spinner)findViewById(R.id.game_area);
		
		// Load map activity
		Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
		myIntent.putExtra("mode", mode.getSelectedItemPosition());
		myIntent.putExtra("speed", speed.getSelectedItemPosition());
		myIntent.putExtra("area", area.getSelectedItemPosition());
    	MainActivity.this.startActivity(myIntent);
    	
	}
}

package comdbewselectronic_swim_coach.httpsgithub.electronic_swim_coach;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class myMenu extends Activity{
	

	@Override
	protected void onCreate(Bundle savedState) {
		// TODO Auto-generated method stub
		super.onCreate(savedState);
		setContentView(R.layout.activity_hello);
		
		//set up the button sound
		final MediaPlayer mpButtonClick = MediaPlayer.create(this, R.raw.button_click);
		
		//button1
		
		Button bStartFilming = (Button) findViewById(R.id.button1);
		bStartFilming.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent("comdbewselectronic_swim_coach.httpsgithub.electronic_swim_coach.TUTORIALONE"));
				mpButtonClick.start();
			}
		});
		
		//button2
		Button bAbout = (Button) findViewById(R.id.button2);
		bAbout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent("comdbewselectronic_swim_coach.httpsgithub.electronic_swim_coach.ABOUT"));
				mpButtonClick.start();
			}
		});
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater awesome = getMenuInflater ();
		awesome.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item){
		switch (item.getItemId()) {
		
		case R.id.menuSweet:
			startActivity(new Intent("comdbewselectronic_swim_coach.httpsgithub.electronic_swim_coach.SWEET"));
			return true;
		case R.id.menuToast:
			Toast display = Toast.makeText(this, "HOWAREYOU?", Toast.LENGTH_SHORT);
			display.show();
			return true;
		}
		return false;
	}
	
}

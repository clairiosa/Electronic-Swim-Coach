package comdbewselectronic_swim_coach.httpsgithub.electronic_swim_coach;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class tutorial1 extends Activity implements OnCheckedChangeListener {
	
	TextView textOut;
	EditText getInput;
	RadioGroup Gunit;
	RadioGroup Sunit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial1);
		
		Gunit = (RadioGroup) findViewById(R.id.rgGravity);
		Gunit.setOnCheckedChangeListener(this);
		Sunit = (RadioGroup) findViewById(R.id.rgStyle);
		Sunit.setOnCheckedChangeListener(this);
		
		textOut = (TextView) findViewById(R.id.tvGetInput);
		getInput = (EditText) findViewById(R.id.etInput);
		Button ok = (Button) findViewById(R.id.bOK);
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				textOut.setText(getInput.getText());
			}
		});
	}
// radio buttons 
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedID) {
		// TODO Auto-generated method stub
		switch (checkedID){
		
		case R.id.rbLeft:
			textOut.setGravity(Gravity.LEFT);
			break;
		case R.id.rbCenter:
			textOut.setGravity(Gravity.CENTER);
			break;
		case R.id.rbRight:
			textOut.setGravity(Gravity.RIGHT);
			break;
		case R.id.rbNormal:
			textOut.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.NORMAL);
			break;
		case R.id.rbBold:
			textOut.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD), Typeface.BOLD);
			break;
		case R.id.rbItalics:
			textOut.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
			break;
			
		}
		
	}
	
	
	

}

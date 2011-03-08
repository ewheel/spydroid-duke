package edu.duke.spydroid.view;

import edu.duke.spydroid.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ClearDataPaneController extends Activity {
	Button mClearDataButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clear_data_pane);
		mClearDataButton= (Button) findViewById(R.id.clearButton);
		
		//Setup clear button on-click
		mClearDataButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				clearData();
			}
		});
	}
	
	/**
	 * Clears all stored SpyDroid data on the local device.
	 */
	private void clearData() {
		//TODO implement clear data method.
	}
	
	

}

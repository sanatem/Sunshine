package com.example.sunshine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.os.Build;
import android.widget.*;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			//This object have the Root View of our project (The FrameView in this case)
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			//With this we create some dummy data.
			String[] forecastArray = {
				"Hoy - Soleado - 15/20",	
				"Mañana - reCopado - 11/16",
				"Sabado - nublado -11/20",
				"Domingo - Asteroides -11/20",
				"Lunes - nublado -11/20",
				"Miercoles - nublado -11/20",
				"Martes - Invasión Extraterrestre -11/20",
			};
			
			//For more simplicity we convert the array in ArrayList, beautiful methods.
			List<String> week = new ArrayList<String>(Arrays.asList(forecastArray));
			/**This is the adapter, with this object we connect the model with te View
			 * The adapter have 4 parameters:
			 * 1) The current context 
			 * 2) ID of the current layout 
			 * 3) ID of the TextView to populate
			 * 4) The dummy data.
			 */
			ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(this.getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,week);
			//Get a reference of ListView (Finding by ID) and we attach to the adapter with setadapter()
			ListView listview = (ListView)rootView.findViewById(R.id.listview_forecast);
			listview.setAdapter(forecastAdapter);
			
			
			return rootView;
		}
	}
}

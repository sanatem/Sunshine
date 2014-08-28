package com.example.sunshine;

//Imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ForecastFragment extends Fragment {

	//Constructor
	public ForecastFragment() {
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        // Overriding this method we can set menu options instead of the default menu without items.
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Overriding this method we indicates which menu we want to load.
    	inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// When someone touch the refresh button we need to override the action.Well
    	// Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        
    	int id = item.getItemId();
        if (id == R.id.action_refresh) {
        	FetchWeatherTask weatherTask = new FetchWeatherTask();
        	//Execute() is a method from aSyncTask, that executes in background our task.
        	weatherTask.execute("1900");
            return true;
        }
        //We use super,obviously, because we want to load the other items right?.
        return super.onOptionsItemSelected(item);
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
	
    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
    	
    	/**This constant is defined to see the name of the class. I dont use a string like.. "FetchWeatherTask"
    	   because if we change the class name throw an exception in the next line.
    	  It's not neccesary the LOG_TAG. 
        **/
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        
        @Override
        protected Void doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            	
            	
            	
            	
            	
            	
            	
            	
            	
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                
                forecastJsonStr = buffer.toString();
                //With this log we can see the forecastJsonStr in LogCat, the data that we received from the API
                Log.v(LOG_TAG,"Forecast JSON string: "+ forecastJsonStr);
                
                
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }

	
}




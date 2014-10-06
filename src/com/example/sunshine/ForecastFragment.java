package com.example.sunshine;

//Imports
import java.text.ParseException;
import java.util.Date;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import data.WeatherContract;
import data.WeatherContract.LocationEntry;
import data.WeatherContract.WeatherEntry;

public class ForecastFragment extends Fragment implements LoaderCallbacks<Cursor> {
	
	    private static final int FORECAST_LOADER = 0;
	    private String mLocation;
	 
	    // For the forecast view we're showing only a small subset of the stored data.
	    // Specify the columns we need.
	    private static final String[] FORECAST_COLUMNS = {
	            // In this case the id needs to be fully qualified with a table name, since
	            // the content provider joins the location & weather tables in the background
	            // (both have an _id column)
	            // On the one hand, that's annoying.  On the other, you can search the weather table
	            // using the location set by the user, which is only in the Location table.
	            // So the convenience is worth it.
	            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
	            WeatherEntry.COLUMN_DATETEXT,
	            WeatherEntry.COLUMN_SHORT_DESC,
	            WeatherEntry.COLUMN_MAX_TEMP,
	            WeatherEntry.COLUMN_MIN_TEMP,
	            LocationEntry.COLUMN_LOCATION_SETTING
	    };
	 
	    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
	    // must change.
	    public static final int COL_WEATHER_ID = 0;
	    public static final int COL_WEATHER_DATE = 1;
	    public static final int COL_WEATHER_DESC = 2;
	    public static final int COL_WEATHER_MAX_TEMP = 3;
	    public static final int COL_WEATHER_MIN_TEMP = 4;
	    public static final int COL_LOCATION_SETTING = 5;
	
	
	//Class variables
	private SimpleCursorAdapter forecastAdapter;
	

	
	public void onActivityCreated(Bundle savedInstanceState) {
	    getLoaderManager().initLoader(FORECAST_LOADER, null, this);
	    super.onActivityCreated(savedInstanceState);
	}
	
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
        	this.updateWeather();
            return true;
        }
        //We use super,obviously, because we want to load the other items right?.
        return super.onOptionsItemSelected(item);
    }
    

    private void updateWeather() {
        String location = Utility.getPreferredLocation(getActivity());
        new FetchWeatherTask(getActivity()).execute(location);
    }
	

	@Override
	public void onStart() {
		super.onStart();
	}
	
	public void onResume() {
	    super.onResume();
	    //This update the Loader if the location changes
	    if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
	        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
	    }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		/**This is the adapter, with this object we connect the model with te View
		 * The adapter have 4 parameters:
		 * 1) The current context 
		 * 2) ID of the current layout 
		 * 3) ID of the TextView to populate
		 * 4) The Array.
		 */
		 this.forecastAdapter=new SimpleCursorAdapter(
		            getActivity(),
		            R.layout.list_item_forecast,
		            null,
		            // the column names to use to fill the textviews
		            new String[]{WeatherContract.WeatherEntry.COLUMN_DATETEXT,
		                    WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
		                    WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
		                    WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
		            },
		            // the textviews to fill with the data pulled from the columns above
		            new int[]{R.id.list_item_date_textview,
		                    R.id.list_item_forecast_textview,
		                    R.id.list_item_high_textview,
		                    R.id.list_item_low_textview
		            },
		            0
		    );
		 	
	        forecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
	            @Override
	            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
	                boolean isMetric = Utility.isMetric(getActivity());
	                switch (columnIndex) {
	                    case COL_WEATHER_MAX_TEMP:
	                    case COL_WEATHER_MIN_TEMP: {
	                        // we have to do some formatting and possibly a conversion
	                        ((TextView) view).setText(Utility.formatTemperature(
	                                cursor.getDouble(columnIndex), isMetric));
	                        return true;
	                    }
	                    case COL_WEATHER_DATE: {
	                        String dateString = cursor.getString(columnIndex);
	                        TextView dateView = (TextView) view;
	                        try {
								dateView.setText(Utility.formatDate(dateString));
							} catch (ParseException e) {
								e.printStackTrace();
							}
	                        return true;
	                    }
	                }
	                return false;
	            }
	        });
		
		//This object have the Root View of our project (The FrameView in this case)
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		
		//Get a reference of ListView (Finding by ID) and we attach to the adapter with setadapter()
		ListView listview = (ListView)rootView.findViewById(R.id.listview_forecast);
		listview.setAdapter(this.forecastAdapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				
	
			public void onItemClick(AdapterView adapterView, View view, int position, long l) {
				
				Cursor cursor = forecastAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    String dateString = null;
					try {
						dateString = Utility.formatDate(cursor.getString(COL_WEATHER_DATE));
					} catch (ParseException e) {

						e.printStackTrace();
					}
                    String weatherDescription = cursor.getString(COL_WEATHER_DESC);
 
                    boolean isMetric = Utility.isMetric(getActivity());
                    String high = Utility.formatTemperature(
                            cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
                    String low = Utility.formatTemperature(
                            cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
 
                    String detailString = String.format("%s - %s - %s/%s",
                            dateString, weatherDescription, high, low);
 
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, detailString);
                    startActivity(intent);
				
                }
            }//End onItemClick
			
			
		});
		
		
		return rootView;
	}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
 
        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());
 
        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";
 
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);
 
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

	@Override
	public void onLoadFinished(Loader loader, Cursor data) {
	    forecastAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader loader) {
	   forecastAdapter.swapCursor(null);
	}
	


	
}




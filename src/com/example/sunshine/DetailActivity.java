package com.example.sunshine;

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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import data.WeatherContract;
import data.WeatherContract.WeatherEntry;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //Starts the Settings activity 
        	startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The clas for the fragment of detail
     */
    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FORECAST_SHARE_HASHTAG = " #AndroideSunshine";
        
        private String mForecastStr;

		private String mLocation;
        private static final int DETAIL_LOADER = 0;

        private static final String[] FORECAST_COLUMNS = {
                WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
                WeatherEntry.COLUMN_DATETEXT,
                WeatherEntry.COLUMN_SHORT_DESC,
                WeatherEntry.COLUMN_MAX_TEMP,
                WeatherEntry.COLUMN_MIN_TEMP,
        };

        
        
        @Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
        }

		public DetailFragment() {    
        }
		
		public void onActivityCreated(Bundle savedInstanceState) {
		    getLoaderManager().initLoader(DETAIL_LOADER, null, this);
		    super.onActivityCreated(savedInstanceState);
		}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            /**
            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.detail_text))
                        .setText(mForecastStr);
            }
			**/
            
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share menu item
            MenuItem share_item = menu.findItem(R.id.action_share);
            // Get the provider and hold onto it to set/change the share intent.
            
            ShareActionProvider mShareActionProvider = new ShareActionProvider(this.getActivity());
            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            mShareActionProvider.setShareIntent(createShareForecastIntent());
            MenuItemCompat.setActionProvider(share_item, mShareActionProvider);
   
        }

        private Intent createShareForecastIntent() {
            //We create this method, because create an Share intent have a lots of lines..
        	Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //Flags because, we dont want to put in the queue of activities, and we return to the main activity. after share
        	shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        	shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mForecastStr + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
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
		    //forecastAdapter.swapCursor(data);
			if(!data.moveToFirst()){return;} // No vino nada.
			try {
				String dateString = Utility.formatDate(data.getString(data.getColumnIndex(WeatherEntry.COLUMN_DATETEXT)));
				((TextView) getView().findViewById(R.id.date_textview))
	            .setText(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String weatherString = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC));
			((TextView) getView().findViewById(R.id.weather_textview)).setText(weatherString);
		
			//Probanding todavia no lo terminé
		
		}
		
		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			// TODO Auto-generated method stub
			
		}
    }
}

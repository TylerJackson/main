package com.example.mustangtutors;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] drawerImages;
    private int[] drawerImagesInt;
    private String[] drawerStrings;
    
    // Populate the navigation drawer.
    public void fillNavDrawer(String type) {
    	if (type.equals("logged out")) {
    		drawerImages = getResources().getStringArray(R.array.logged_out_menu_images);
        	drawerStrings = getResources().getStringArray(R.array.logged_out_menu);
    	}
    	else if (type.equals("logged in")) {
    		drawerImages = getResources().getStringArray(R.array.logged_in_menu_images);
        	drawerStrings = getResources().getStringArray(R.array.logged_in_menu);
        	drawerStrings[0] = sharedPref.getString("name", "[name]");
    	}
    	
    	// Convert the image names to resource IDs
    	drawerImagesInt = new int[drawerImages.length];
    	for (int i = 0; i < drawerImages.length; i++) {
    		drawerImagesInt[i] = getResources().getIdentifier(drawerImages[i], "drawable", getPackageName());
    	}
    	
        // set up the drawer's list view with items and click listener
    	DrawerAdapter customAdapter = new DrawerAdapter(this, R.layout.drawer_list_item, drawerImagesInt, drawerStrings);
        mDrawerList.setAdapter(customAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        if (type.equals("logged out")) {
        	// If the user is not logged in, 'search' is 1st in menu
        	selectItem(0);
        }
        else if (type.equals("logged in")) {
        	// If the user is logged in, 'search' is 2nd in menu
        	selectItem(1);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	StrictMode.ThreadPolicy policy = new StrictMode.
    			ThreadPolicy.Builder().permitAll().build();
    			StrictMode.setThreadPolicy(policy); 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open preferences file
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Fill the navigation drawer with items, depending on whether the 
        // user is logged in or out.
    	if (sharedPref.contains("user_id")) {
            fillNavDrawer("logged in");
    	}
    	else {
            fillNavDrawer("logged out");
    	}
    	
    	AjaxRequest request;
        try {
	        request = new AjaxRequest("GET", "http://mustangtutors.floccul.us/json/searchResults.json");
	    	JSONObject json;
            try {
	            json = request.send();
	            JSONArray jsonTutors = json.getJSONArray("Tutors");
			    ArrayList<Tutor> tutors = new ArrayList<Tutor>();
			    for (int i = 0; i < jsonTutors.length(); i++) {
			    	JSONObject tutor = (JSONObject) jsonTutors.get(i);
			    	int id = tutor.getInt("User_ID");
			    	String name = tutor.getString("First_Name") + " " + tutor.getString("Last_Name");
			    	int numRatings = tutor.getInt("Number_Ratings");
			    	double rating = tutor.getDouble("Average_Rating");
			    	int availability = tutor.getInt("Available");
			    	tutors.add(new Tutor(id, name, numRatings, rating, availability));
			    }
		    	SearchAdapter searchAdapter = new SearchAdapter(this, R.layout.search_list_item, tutors);
		    	ListView listView = (ListView) findViewById(R.id.listview);
		    	listView.setAdapter(searchAdapter);
            } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        } catch (MalformedURLException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
        
        
        /* Example of a POST Ajax Request
        try {
	        request = new AjaxRequest("POST", "http://tacotruck.floccul.us/api/users/login");
	        request.addParam("email", "asdf@asdf.com");
	        request.addParam("password", "asdfasdf");
	    	JSONObject json;
            try {
	            json = request.send();
	            System.out.println(json);
            } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        } catch (MalformedURLException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
        */
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        Switch mySwitch =  (Switch) menu.findItem(R.id.mySwitch).getActionView();
        mySwitch.setTextOff("Busy");
        mySwitch.setTextOn("Available");
		mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	            if (isChecked) {
	                // The toggle is enabled
	            	System.out.println("first");
	            } else {
	                // The toggle is disabled
	            	System.out.println("second");
	            }
	        }
	    });
        	
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
    	// Start the login activity
    	if (drawerStrings[position].equals("Login as a Tutor")) {
    		Intent intent = new Intent(this, LoginActivity.class);
    		int requestCode = 1;
    		startActivityForResult(intent, requestCode);
    	}
    	
    	// Start the tutor profile activity
    	if (drawerStrings[position].equals(sharedPref.getString("name", "[name]"))) {
    		// code
    		System.out.println("tutor profile");
    	}
    	
    	// Start the meeting documentation activity
    	else if (drawerStrings[position].equals("Document a Student Meeting")) {
    		// code
    		System.out.println("document meeting");
    	}
    	
    	// Logout
    	else if (drawerStrings[position].equals("Logout")) {
    		// Delete user data from preferences
    		editor.clear().commit();
    		
    		// Update navigation drawer
    		fillNavDrawer("logged out");
    		
    		// Show a logout toast
    		Toast.makeText(getApplicationContext(), 
    				getString(R.string.logged_out), 
    				Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	// Highlight the item in the drawer, then close the drawer.
    	mDrawerList.setItemChecked(position, true);
    	mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    // Process data received from other activities (login)
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1) {
    		String value = data.getStringExtra(LoginActivity.EXTRA_MESSAGE);
    		if (value.equals("logged in")) {
				// Write user data to the preferences file.
				editor.putString("user_id", data.getStringExtra(LoginActivity.USER_ID));
				editor.putString("name", data.getStringExtra(LoginActivity.NAME));
				editor.commit();
				
				// Update the navigation drawer with links for a logged in user.
    			fillNavDrawer("logged in");
    			
        		// Show a login toast
        		Toast.makeText(getApplicationContext(), 
        				getString(R.string.logged_in), 
        				Toast.LENGTH_SHORT).show();
    		}
    	}
	}

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
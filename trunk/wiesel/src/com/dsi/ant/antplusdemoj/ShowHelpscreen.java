package com.dsi.ant.antplusdemoj;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class ShowHelpscreen extends Activity {
	 private static final String TAG = "helpscreen";
	    private String mOriginalContent;
	    /** Pair to any device. */
	    static final short WILDCARD = 0;

	    // A label for the saved state of the activity
	    private static final String ORIGINAL_CONTENT = "origContent";
	    
	    /** Shared bpm preferences data filename. */
	    public static final String PREFS_BPM_NAME = "lastbpm";
	    
	    private boolean rightanswers=false;
	    
	    
	    /**
	     * This method is called by Android when the Activity is first started. From the incoming
	     * Intent, it determines what kind of editing is desired, and then does it.
	     */
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	    	Log.e(TAG, "kok" );
	    	super.onCreate(savedInstanceState);
	        Log.e(TAG, "kok" );
	        
	        SharedPreferences settings = getSharedPreferences(PREFS_BPM_NAME, 0);
	        
	    	 setContentView(R.layout.helpscreen);
	         
	    	/*
	        * If this Activity had stopped previously, its state was written the ORIGINAL_CONTENT
	        * location in the saved Instance state. This gets the state.
	        */
	    	Log.e(TAG, "kok" );

	       if (savedInstanceState != null) {
	           mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
	       }
	 	   //
	      
	 	  // drawbpmseries();
	    	
	    }
	    
	    /**
	     * This method is called when the Activity is about to come to the foreground. This happens
	     * when the Activity comes to the top of the task stack, OR when it is first starting.
	     *
	     * Moves to the first note in the list, sets an appropriate title for the action chosen by
	     * the user, puts the note contents into the TextView, and saves the original text as a
	     * backup.
	     */
	    @Override
	    protected void onResume() {
	        super.onResume();
	        
	    }

	    /**
	     * This method is called when the Activity loses focus.
	     *
	     * For Activity objects that edit information, onPause() may be the one place where changes are
	     * saved. The Android application model is predicated on the idea that "save" and "exit" aren't
	     * required actions. When users navigate away from an Activity, they shouldn't have to go back
	     * to it to complete their work. The act of going away should save everything and leave the
	     * Activity in a state where Android can destroy it if necessary.
	     *
	     * If the user hasn't done anything, then this deletes or clears out the note, otherwise it
	     * writes the user's work to the provider.
	     */
	    @Override
	    protected void onPause() {
	        super.onPause();
	    }
	    
	    @Override
	    protected void onStart() {
	        super.onStart();
	        Log.d(TAG,"onstart");
	       // drawbpmseries();
	    }
}

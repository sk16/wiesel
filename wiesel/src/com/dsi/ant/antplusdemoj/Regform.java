package com.dsi.ant.antplusdemoj;

import java.net.URLEncoder;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class Regform extends Activity {
	
	 private static final String TAG = "regform";
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
	         boolean regformsuccess= settings.getBoolean("regformsuccess2", false);
	         
	         if(!regformsuccess){
	    	 setContentView(R.layout.regform);}else {finish();}
	         
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
	    
	     public void sendFeedback(View button) {  
	    	        // Do click handling here  
	    	 
	    	 clickity();
	    	    }  
	     
	     public void clickity(){
	    	 /* 1. final EditText nameField = (EditText) findViewById(R.id.EditTextName);  
	    	   2. String name = nameField.getText().toString();  
	    	   3.   
	    	   4. final EditText emailField = (EditText) findViewById(R.id.EditTextEmail);  
	    	   5. String email = emailField.getText().toString();  
	    	   6.   
	    	   7. final EditText feedbackField = (EditText) findViewById(R.id.EditTextFeedbackBody);  
	    	   8. String feedback = feedbackField.getText().toString();  */
	    	 final Pattern rfc2822 = Pattern.compile(
	    		        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
	    		);
	    	 int age=-1;int weight=-1;
	    	 rightanswers=true;
	    	 
	    	 final EditText nameField = (EditText) findViewById(R.id.user);
	    	 final EditText pw1Field = (EditText) findViewById(R.id.userpw1);  
	    	 final EditText pw2Field = (EditText) findViewById(R.id.userpw2);  
	    	 final EditText emailField = (EditText) findViewById(R.id.EditTextEmail);  
	    	 final EditText ageField = (EditText) findViewById(R.id.age);
	    	 final EditText weightField = (EditText) findViewById(R.id.weight);  
	    	 final Spinner spinner = (Spinner) findViewById(R.id.spinner1);  
	    	 
	    	 String name = nameField.getText().toString();  
	    	 String pw1 = pw1Field.getText().toString();  
	    	 String pw2 = pw2Field.getText().toString();  
	    	 String email = emailField.getText().toString();  
	    	 String unit=spinner.getSelectedItem().toString();
	    	 
	    	 try{   	  age = Integer.parseInt( ageField.getText().toString());  
	    	 }catch(Exception e){
		    	 age=-1;
	    	 }
	    	 try{   	  weight = Integer.parseInt(weightField.getText().toString());  
	    	 }catch(Exception e){
	    	 weight=-1;
	    	 }
	    	 
	    	 if(pw1.length()<5){toaster("Your password must be at least 5 characters long");
	    	 }else{
	    	 if(!pw1.equals(pw2))toaster("Passwords do not match");}
	    	 if(name.equals("")){toaster("Please specify a username");}
	    	 else
	    	 
	    	 //length
	    	 if(name.length()<5)toaster("Your username must be at least 5 characters long");
	    	 
	    	 //email
	    		if (!rfc2822.matcher(email).matches()) {
	    		   toaster("Please specify a real email address");
	    		}
	    		try{
	    		if(!name.equals(URLEncoder.encode(name,"UTF-8")))toaster("Do not use special characters in your name");
	    			//TODO test if it works
	    		}
	    		catch(Exception e){
	    			Log.d(TAG,"special");
	    			toaster("Unsupported characters in your name");
	    			
	    		}
	    			
	    	 if(rightanswers){
	    		 //
	    		 //write data into prefs
	    		 //
	    		 if(unit.equals("lbs"))weight=(int)(0.454*weight);
	    		
	    		 
	    		 SharedPreferences settings = getSharedPreferences(PREFS_BPM_NAME, 0);
	    		 SharedPreferences.Editor editor = settings.edit();
	   	      
	   	      editor.putString("antid", name);//1 bedeutet true
	   	      editor.putString("antpw", pw1);
	   	      editor.putString("antemail", email);
	   	      editor.putInt("antage", age);
	   	      editor.putInt("antweight", weight);
	   	      editor.putBoolean("regformsuccess2", true);
	   	      editor.commit();//0 false
	    		 
	   	   Intent intent = new Intent();
	       intent.putExtra("Color", "Green");
	       setResult(RESULT_OK, intent);
	   	      //close activity
	    		 finish();
	    	 }
	    	 
	     }
	     
	     private void toaster(String s){
	    	 Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();
		    	rightanswers=false;
	     }
	     
}

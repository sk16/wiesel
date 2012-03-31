package com.dsi.ant.antplusdemoj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
//import android.content.Intent;
import android.content.SharedPreferences;
//import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
//import android.app.Activity;

public class Syncreg  {
	
final String TAG="syncreg";

public static final String PREFS_BPM_NAME = "lastbpm";
private Context context;
private boolean syncdone=false;
	
//private String mOriginalContent;
/** Pair to any device. */
static final short WILDCARD = 0;
private boolean failed=false;
private String errors =" ";

// A label for the saved state of the activity
//private static final String ORIGINAL_CONTENT = "origContent";

String shttpresponse="";
Handler handler=new Handler();

//@Override
protected void onCreate( Context contextk) {
	//super.onCreate(savedInstanceState);
	Log.i(TAG, "reset1");
	context=contextk;
	checkUpdate.start();
	 Toast.makeText(context, "Sending registration credentials. Please wait...",Toast.LENGTH_LONG).show();
	
	 handler.postDelayed(new Runnable() { 
         public void run() { 
         	// if already running do not start
        	 boolean inet=false;
        	 shttpresponse=postData(0);
      	   //Toast.makeText(context, "trying to send registration data",Toast.LENGTH_SHORT).show();
         	if(shttpresponse.equals("s")){
         		Log.i(TAG, "resetkk1");
         		syncok();
         		inet=true;
         		failed=false;
         		//finish();
         	} 
         	if(!syncdone){
         		if(shttpresponse.equals("d")){
         			Log.d(TAG,"d2");
         	   		Toast.makeText(context, "Username already taken",Toast.LENGTH_SHORT).show();//funktioniert nicht
         	   		resetregprocess();   
         	   		failed=false;
         	  		inet=true;
         		}
         	}
            if(shttpresponse.startsWith("e")){
         	   Toast.makeText(context, shttpresponse.substring(2),Toast.LENGTH_SHORT).show();
         	  failed=false;
         	  inet=true;}
            if(!inet)Toast.makeText(context, "No internet connection. Try again",Toast.LENGTH_SHORT).show();
        	Log.i(TAG, "resetk1");
        	if(failed)
            	Toast.makeText(context, "No internet connection. Try again",Toast.LENGTH_SHORT).show();
       
         } 
    }, 1*1*1000); //hier stand 30
	// if (savedInstanceState != null) {
   //      mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);     }
	
}
/**
 * This method is called when the Activity is about to come to the foreground. This happens
 * when the Activity comes to the top of the task stack, OR when it is first starting.
 *
 * Moves to the first note in the list, sets an appropriate title for the action chosen by
 * the user, puts the note contents into the TextView, and saves the original text as a
 * backup.
 */
//@Override
protected void onResume() {
 //   super.onResume();
    
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
//@Override
protected void onPause() {
 //   super.onPause();
}

//@Override
protected void onStart() {
  //  super.onStart();
    Log.d(TAG,"onstart");
   // drawbpmseries();
}
private void syncok(){
	
	 SharedPreferences settings = context.getSharedPreferences("lastbpm", 0);
	 SharedPreferences.Editor editor = settings.edit();
	      syncdone=true;
	     /* editor.putString("antid", name);//1 bedeutet true
	      editor.putString("antpw", pw1);
	      editor.putString("antemail", email);
	      editor.putInt("antage", age);
	      editor.putInt("antweight", weight);*/
	      editor.putBoolean("regformsuccess", true);
	      editor.commit();//0 false
	      Toast.makeText(context, "Success. Look at nsteinbock.de for your BPM data",Toast.LENGTH_SHORT).show();
	      
	      
}

private Thread checkUpdate = new Thread() {
	   //TODO check if already running destroy after 2 minutes
	  
	
    public void run() {
    	 failed=false;
        try {
     	   shttpresponse=postData(0);
           if(shttpresponse.equals("s"))syncok();
           //TODO php was passiert wenn user belegt
           if(shttpresponse.equals("d")){
        	   Log.d(TAG,"d");
        	   Toast.makeText(context, "Username already taken",Toast.LENGTH_SHORT).show();//TODO forbidden
        	   resetregprocess();   
           }
           if(shttpresponse.startsWith("e")){
        	   
        	   errors =shttpresponse.substring(2);
           Toast.makeText(context, errors,Toast.LENGTH_SHORT).show();}
            Log.d(TAG,"k"+shttpresponse+"k");
        } catch (Exception e) {
        	Log.d(TAG,"error in run"+e.getMessage());
        	failed=true;
        	
        }
         
    }

	
};

	private void resetregprocess() {
		SharedPreferences settings = context.getSharedPreferences("lastbpm", 0);
		 SharedPreferences.Editor editor = settings.edit();
		      
		     /* editor.putString("antid", name);//1 bedeutet true
		      editor.putString("antpw", pw1);
		      editor.putString("antemail", email);
		      editor.putInt("antage", age);
		      editor.putInt("antweight", weight);*/
		      editor.putBoolean("regformsuccess2", false);
		      editor.commit();//0 false
	
	}
	
	//TODO (fe)male abfragen
	 private String postData(int daysbeforetoday) {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    //String webserver="http://192.168.0.21/r.php";
		   // if(!debug)
		    	String webserver="http://heart.julian-steinbock.de/r.php";
		    HttpPost httppost = new HttpPost(webserver);

		    HttpResponse response =null;
		    
		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("i", getID()));
		        nameValuePairs.add(new BasicNameValuePair("p", getpw()));
		        nameValuePairs.add(new BasicNameValuePair("e", gete()));
		        nameValuePairs.add(new BasicNameValuePair("a", getage()));
		        nameValuePairs.add(new BasicNameValuePair("w", getweight()));
		        if(getant())nameValuePairs.add(new BasicNameValuePair("w", "true"));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		         response = httpclient.execute(httppost);
		        
		    } catch (ClientProtocolException e) {
		       
		    	return "error clientprotokol";
		    } catch (IOException e) {
		    	Toast.makeText(context, "no internet connection",Toast.LENGTH_SHORT).show();
		    	Log.d(TAG,"no inet");
		        return "ioexception";
		    	
		    }
		    String k="";
		    try {
		    	k=inputStreamToString(response.getEntity().getContent()).toString();
		    	
		    } catch(Exception e){
		    Log.e(TAG,"response http error");	
		    return "error response tostring";
		    }
		    return k;
		} 
	   


private StringBuilder inputStreamToString(InputStream is) {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    try{
	    // Read response until the end
	    while ((line = rd.readLine()) != null) { 
	        total.append(line); 
	    }
	    }catch (Exception E){
	    	total.append("error");
	    }
	    // Return full string
	    return total;
	}

private String getpw(){
	   SharedPreferences settings = context.getSharedPreferences("lastbpm", 0);
	   String id=settings.getString("antpw", "nopw");
	   Log.d(TAG,id);
	   return id;
	   
}

private String gete(){
	   SharedPreferences settings = context.getSharedPreferences("lastbpm", 0);
	   String id=settings.getString("antemail", "noe");
	   Log.d(TAG,id);
	   return id;
	   
}
private String getage(){
	   SharedPreferences settings = context.getSharedPreferences("lastbpm", 0);
	   int id=settings.getInt("antage", -1);
	   //Log.d(TAG,id);
	   return (new Integer(id)).toString();
	   
}
private String getweight(){
	   SharedPreferences settings = context.getSharedPreferences("lastbpm", 0);
	   int id=settings.getInt("antweight", -1);
	   //Log.d(TAG,id);
	   
	   return (new Integer(id)).toString();
	   
}
private boolean getant(){
	
		   SharedPreferences settings = context.getSharedPreferences("lastbpm", 0);
		   int b=settings.getInt("bpm0", -1);
		   if(b==-1)return false;
		   return true;
}



private String getID(){
	   SharedPreferences settings = context.getSharedPreferences("lastbpm", 0);
	  
	   
	   String id=settings.getString("antid", "60");
	   Log.d(TAG,id);
	   return id;
	   
}
}

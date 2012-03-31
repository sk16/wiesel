/*
 * Copyright 2010 Dynastream Innovations Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dsi.ant.antplusdemoj;

//import java.io.FileOutputStream;

import java.util.Timer; 
import java.util.TimerTask;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

import com.dsi.ant.AntDefine;
//import com.example.android.notepad.Syncreg;
//import com.example.android.notepad.Regform;
//import com.example.android.notepad.showGraphview;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
//import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File; 
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.PowerManager;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.content.Intent;
import android.content.ComponentName; 
import android.content.Context;
import android.content.res.Configuration;

import android.os.Handler;
import android.provider.Settings;
/*import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.GraphViewDemo;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;*/
import org.apache.http.NameValuePair;
import java.util.List;
import java.util.ArrayList;

/**
 * ANT+ Demo Activity.
 * 
 * This class implements the GUI functionality and basic interaction with the AntPlusManager class.
 * For the code that does the Ant interfacing see the AntPlusManager class.
 */
public class ANTPlusDemoj extends Activity implements View.OnClickListener, AntPlusManager.Callbacks
{
   
   /** The Log Tag. */
   public static final String TAG = "ANTApp";   
   
   /**
    * The possible menu items (when pressed menu key).
    */
   private enum MyMenu
   {
      /** No menu item. */
      MENU_NONE,
      
      /** Exit menu item. */
      MENU_EXIT,      
      
      /** Pair HRM menu item. */
      MENU_PAIR_HRM,
      
      /** Pair SDM menu item. */
      MENU_PAIR_SDM,
      
      /** Pair weight scale menu item. */
      MENU_PAIR_WEIGHT,
      
      /** Sensor Configuration menu item. */
      MENU_CONFIG,
      
      /** Configure HRM menu item. */
      MENU_CONFIG_HRM,
      
      /** Configure SDM menu item. */
      MENU_CONFIG_SDM,
      
      /** Configure Weight Scale menu item. */
      MENU_CONFIG_WGT,
      
      /** Configure Proximity menu item. */
      MENU_CONFIG_PROXIMITY,
      
      /** Configure Buffer Threshold menu item. */
      MENU_CONFIG_BUFFER_THRESHOLD,
      
      /** Send a request to claim the ANT Interface. */
      MENU_REQUEST_CLAIM_INTERFACE,
      
      //julian
      MENU_CONFIG_HRM_PERIOD,
      MENU_DRAW_GRAPH,
      MENU_LOG,
      MENU_SYNC,
      MENU_KEEPLOGGING,
      MENU_HELP,
      MENU_CONFIG_FLIGHT_MODE,
      MENU_CONFIG_WRITE_PERIOD,
      MENU_CHANGE_BEEP,
      MENU_CHANGE_BEEPMIN
   }

   /** The key for referencing the requestClaimInterface variable in saved instance data. */
   static final String REQUESTING_CLAIM_ANT_INTERFACE_KEY = "request_claim_ant_interface";
   
   //julian wakelock für screen off
    PowerManager.WakeLock wl;
    PowerManager pm;
    

   /** Displays ANT state. */
   private TextView mAntStateText;

   /** Formatter used during printing of data */
   private DecimalFormat mOutputFormatter;
   
   /** Pair to any device. */
   static final short WILDCARD = 0;
   
   /** The default proximity search bin. */
   private static final byte DEFAULT_BIN = 7;
   
   /** The default event buffering buffer threshold. */
   private static final short DEFAULT_BUFFER_THRESHOLD = 0;
   
   /** Device ID valid value range. */
   private static final String mDeviceIdHint = AntDefine.MIN_DEVICE_ID +" - "+ (AntDefine.MAX_DEVICE_ID & 0xFFFF);
   
   /** Proximity Bin valid value range. */
   private static final String mBinHint = AntDefine.MIN_BIN +" - "+ AntDefine.MAX_BIN;
   
   /** Buffer Threshold valid value range. */
   private static final String mBufferThresholdHint = AntDefine.MIN_BUFFER_THRESHOLD +" - "+ AntDefine.MAX_BUFFER_THRESHOLD;

   private static final String mHRMPeriodHint= "0 - 20000";
   /** Shared preferences data filename. */
   public static final String PREFS_NAME = "ANTDemoPrefs";
   
  
   /** Class to manage all the ANT messaging and setup */
   private AntPlusManager mAntManager;
   
   //julian
   /** Shared bpm preferences data filename. */
   public static final String PREFS_BPM_Name = "lastbpm";
   private int lastmin=-1;

   private int lasthour=-1;
   private int firstrun=1;
   private int screenoff=1;
   private int screenoff2=1;
   private Timer timer = new Timer();
   private boolean servicerunning=false;
   //private boolean graphrunning=false;
   private int[] writtenbpm={0,0,0,0,0,0,0,0,0};
   private boolean firstrunbpm=true;
  // private boolean firstrun=true;
   private boolean dirnotcreated=false;
   private String filenamesync="N.A.";
   public String shttpresponse="";
   public boolean httpsuccess=false;
   public String synclastline="";
   private boolean foundline=false;
   /** counter for how much lines are written gets reset after 20 */
   private int filecounter=0;
   private boolean debug=false;
   
   /** Defines (probably) if the http post process is stuck in a loop or dead. */
   private boolean syncdead=false;
   
   private int mintosync=18;
   private int daysbeforetoday=0;
   private String synclastlineold="";
   private boolean firstsync=true;
   private int writeperiod=1;
   private String bpmline="";
   private String writePeriodHint="1 - 60";
   
   private Handler handlerresume = new Handler(); 
   
   private Handler lastday=new Handler();
  
   private Handler handler = new Handler();
   private short hrmbuttone = 0;
   
   private Runnable runnable = new Runnable() {

    public void run() {

     //doStuff();
    	Calendar cal = Calendar.getInstance();
	  	   
			
	  	   int cmin = cal.get(Calendar.MINUTE);
			try{
			if(lastmin==cmin||(lastmin+1)==cmin||lastmin==59)
				Log.d(TAG, "fine");
			else Log.d(TAG, "not fine");
			
			if(lastmin==-1)Log.d(TAG, "the Beginning");
			} catch(Exception e)
			{
			}
			
     /*
      * Now register it for running next time
      */

     handler.postDelayed(this, 5*60*1000);
    }

      
   };

@Override
   public void onCreate(Bundle savedInstanceState) 
   {
	   
       super.onCreate(savedInstanceState);
       Log.d(TAG, "onCreate enter");
       
       if(!this.isFinishing())
       {
    	   Log.d(TAG,"notfinishing");
    	   try{
           setContentView(R.layout.main);
    	   }catch(Exception e){
    		   Log.d(TAG,"error view"+e.getMessage());
    		   if(debug)
    		   Toast.makeText(getBaseContext(), "error view",1000);
    	   }
           initControls();
       }
       
       mAntManager = (AntPlusManager) getLastNonConfigurationInstance();
       Log.d(TAG, "onCreate enter2");
       if(mAntManager == null)
       {
           mAntManager = new AntPlusManager(this, savedInstanceState, this);
           
           // Always have ANT service connected
           mAntManager.start();
       }
       else
       {
           mAntManager.resetCallbacks(this, this);
       }
       
       mOutputFormatter = new DecimalFormat(getString(R.string.DataFormat));
       
       int iwriteperiod=getsecperiod();
       if(iwriteperiod==0)iwriteperiod=1;
	      mAntManager.setHRMPeriod((short)(8070+(iwriteperiod-1)*2200/60));
 
       try{
       File sdCard = Environment.getExternalStorageDirectory();
       
       File wallpaperDirectory = new File(sdCard.getAbsolutePath()+"/bpm/");
    // have the object build the directory structure, if needed.
    wallpaperDirectory.mkdirs();}catch(Exception e){
    	dirnotcreated=true;
    }
    colorsyncbutton();
    changeorientation(getResources().getConfiguration());
       Log.d(TAG, "onCreate exit");
       //timerforrunningcheck(180);
       //setContentView(R.layout.main);
       isitfirstrun();
      // Intent svc = new Intent(this, com.dsi.ant.bgservicewritefile.class);
	    //  startService(svc); //, Bundle.EMPTY
   }


   
   
   @Override
   public Object onRetainNonConfigurationInstance() 
   {
       //Save the current service connection
       return mAntManager;
   }
  //TODO android:configChanges="orientation" in your manifest and implement onConfigurationChanged

   @Override
   public void onConfigurationChanged(Configuration newConfig){
	   super.onConfigurationChanged(newConfig);
	   try{
	  changeorientation(newConfig);
	    }catch(Exception e){
	    	Log.d(TAG,"e oncofig");
	    }
	   
	   
   }
   
   @Override protected void onDestroy()
   {
	   
	   //julian  foreground service stoppen wenn er an ist
      Log.d(TAG, "onDestroy enter");
      
      if(isFinishing())
      {
          mAntManager.shutDown();
      }
      
      handlerresume.removeCallbacks(startant);
      mAntManager = null;
      //TODO 2.1 does not have these methods
      Display display = ((WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
      int rot = display.getRotation();
      Log.d(TAG,"rot="+(new Integer(rot)).toString());
      if(rot==Surface.ROTATION_270||rot==Surface.ROTATION_90)
      unbindDrawables(findViewById(R.id.RootView));
      if(rot==Surface.ROTATION_0||rot==Surface.ROTATION_180)
    	  unbindDrawables(findViewById(R.id.LandRootView));
      System.gc();

      if (this.isFinishing()) {
          // WAHT YOU WANT TO DO BEFORE DESTROYING...
     
    	  if(checkloggingalive())Toast.makeText(getBaseContext(), "No Logging possible",Toast.LENGTH_SHORT).show();
      
    	  if(servicerunning){//stopservice();
      
    		  Intent svc = new Intent(this, com.dsi.ant.antplusdemoj.bgservicewritefile.class);
	   	 //     svc.putExtra("cantplusdemo", this);
	   	    //  startService(svc); //, Bundle.EMPTY
	   	     // startForeground(svc);
	   	   
    		  stopService(svc); //, Bu
		   	  servicerunning=false;
    	  }
      }
      super.onDestroy();

      Log.d(TAG, "onDestroy exit");
   }   

   private void unbindDrawables(View view) {
	   try{
       if (view.getBackground() != null) {
       view.getBackground().setCallback(null);
       }
	   }catch(Exception e){
    	   Log.d(TAG,"bg");
       }
	   try{
       if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
    	   Log.d(TAG,"kee");
           for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
           unbindDrawables(((ViewGroup) view).getChildAt(i));
           }
       ((ViewGroup) view).removeAllViews();
       } }catch(Exception e){
    	   Log.d(TAG,"ke");
       }
   }
   
   private void changeorientation(Configuration newConfig){
	   ImageButton lButton = (ImageButton) findViewById(R.id.button_hrm2);
	    ImageButton pButton = (ImageButton) findViewById(R.id.button_hrm);
	    ImageButton sButton = (ImageButton) findViewById(R.id.button_sync);
	    int w=95;
	
	    TableRow.LayoutParams shareParams=new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    TableRow.LayoutParams pParams=new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	  
	    Log.d(TAG,(new Integer(pParams.width)).toString() + " "+(new Integer(shareParams.width)).toString());
	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	shareParams.width=(int)(0.5+getBaseContext().getResources().getDisplayMetrics().density*w);
	    	shareParams.height=(int)(0.5+getBaseContext().getResources().getDisplayMetrics().density*w);
	    	shareParams.rightMargin=(int)(0.5+getBaseContext().getResources().getDisplayMetrics().density*8);
	    	shareParams.topMargin=(int)(0.5+getBaseContext().getResources().getDisplayMetrics().density*8);
	    	pParams.width=0;
	    	pParams.height=0;
	 
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    
	    	pParams.width=(int)(0.5+getBaseContext().getResources().getDisplayMetrics().density*w);
	    	pParams.height=(int)(0.5+getBaseContext().getResources().getDisplayMetrics().density*w);
	    	shareParams.topMargin=0;	    	
	    	shareParams.width=0;
	    	shareParams.height=0;
	    	shareParams.rightMargin=0;
	    	
	    }
	    
	    lButton.setLayoutParams(shareParams);
	    pButton.setLayoutParams(pParams);
	    sButton.setLayoutParams(pParams);
   }
   /**
    * julian writes bpm to file 
    * @param context
    * @param data to append to file
    */
   public void WriteSettings(Context context, String data){
	   Calendar cal = Calendar.getInstance();

	   int month = cal.get(Calendar.MONTH) + 1;
	   
	   int dom = cal.get(Calendar.DAY_OF_MONTH);
	   
	  // int doy = cal.get(Calendar.DAY_OF_YEAR);
	   int min = cal.get(Calendar.MINUTE);
	   int hour = cal.get(Calendar.HOUR_OF_DAY);
	   int sec = cal.get(Calendar.SECOND);
	   
	  String sdoy="";
	  String sdom="";
	   if (dom<10)sdoy="0";
	   if (month<10)sdom="0";
	   
	   if(firstrun<5) firstrun=firstrun+1;
	   else
		   //minute anders?
	 //  if((lastmin!=min)||(lasthour!=hour)) {
		   if(checksecperiod(data)){
		   lastmin=min;
		   lasthour=hour;
		   File file=null;
		   boolean filesucess=false;
		   //TODO it still crashes
		   try {
	   File sdCard = Environment.getExternalStorageDirectory(); 
	   Log.d(TAG, "write to file");
	   //TODO in unterordner  string dazu in preference
   	//   File dir = new File (sdcard.getAbsolutePath() + "/dir1/dir2"); 
	    file = new File(sdCard.getAbsolutePath() + "/bpm/bpm" +sdom +(new Integer(month)).toString()+ sdoy + (new Integer(dom)).toString() + ".txt");
	    filesucess=true;
		   } catch(Exception E){filesucess=false;
		   Log.d(TAG, "sdcard error");}
		   if(filesucess){
			   try {
		  
		    // Create file if it does not exist
		    boolean success = file.createNewFile();
		    if (success) {
		        // File did not exist and was created
		    } else {
		        // File already exists
		    }
		    
		} catch (IOException e) {
			Log.d(TAG, "create error");
		}

	FileWriter k=null;

       try{


    	    k=new FileWriter(file,true);
    	   // Log.d(TAG, "write to file");//already crashed
           k.append(writeline(data)+"\n");
           bpmline="";
           
           
           k.flush();

        //Toast.makeText(context, path2,Toast.LENGTH_SHORT).show();//"Settings saved",Toast.LENGTH_SHORT).show();

        }catch(IOException e){
        	Log.d(TAG, "write error");
        	Toast.makeText(context, "can not write file",Toast.LENGTH_SHORT).show();
        
        }

        catch (Exception e) {      

        e.printStackTrace();

        Toast.makeText(context, "can not write file",Toast.LENGTH_SHORT).show();

        } 
       
        finally {

           try {
        	   if(k!=null){
                   k.close();
                   fileendhandling();}
                 // fOut.close();

                  } catch (IOException e) {

                  e.printStackTrace();
                  Log.d(TAG, "close error");
                  } catch (Exception e){
                	  Log.d(TAG, "close error");
                  }

        }
   }
		   }
		   
	   
	   //minute anders?
   }
   
   private void everyminute(){
	  // Log.d(TAG,"1min"); //öfter als 1mal pro min
	   if(firstrun>4){
		   if(writtenbpm[0]>getbeephrm(true))makesound();
		   if(writtenbpm[0]<getbeephrm(false))makesound();
	   }
	   
   }
   private String writeline(String data){
	   
	   return data+bpmline;//TODO das bpm von data muss ans ende 
   }
   
   private boolean checksecperiod(String data) {
	   Calendar cal = Calendar.getInstance();
	      		 
		   int min = cal.get(Calendar.MINUTE);
		   int hour = cal.get(Calendar.HOUR_OF_DAY);
		   int sec = cal.get(Calendar.SECOND);
		   int secperiod=getsecperiod();
		   
		   if(secperiod==0){
			   if((writeperiod%2)==0){
			   bpmline=bpmline+" " + (data.substring(5));
			   if(debug)
			   Log.d(TAG, "writeperiod" + (new Integer(writeperiod)).toString());
			   }
			   writeperiod++;
			//   if(sec>56&&writeperiod>30) {
			   if(writeperiod>476){//60sec 484
				   writeperiod=0;
				   everyminute();
				   return true;}
			   return false;
		   }
		   
		  // if(secperiod)
		   if((lastmin!=min)||(lasthour!=hour)) {
			   everyminute();
			   // save the bpms and write only in the last instance
			  // if(sec%secperiod==0){
			   
			   
			   if(sec==0||sec>secperiod*writeperiod){
				   Log.d(TAG, "writeperiod" +(new Integer(writeperiod)).toString());
				   writeperiod++;
				   
				   if(60/secperiod==writeperiod){ 
					   writeperiod=0;
					   return true;
					   }
				 //changebpmline
				   bpmline=bpmline+" " + (data.substring(5));
				   return false;
			   }
			  /* if(sec>55) {//TODO erledigt? what happens when logging every second
				   writeperiod=1;
				   return true;}*/
			   
			   return false;
		   }else return false;
}




private int getsecperiod() {

	 SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   int kk=settings.getInt("secperiod", 30);
	   
	   
	   if(kk<0){
		   Log.d(TAG,"wierd"+ (new Integer(writeperiod)).toString());
		   return 30;
	   }
	return kk;
}




public void synckillifrunning(){
	   if(syncdead){
		   //kill the thread
		   try{
		   checkUpdate.destroy();
		   }catch(Exception e){
			   
			   Log.d(TAG,"destroy error ");
		   }
	   }
	   
   }

/** 
 * gets called every minute when writing succeeds
 * 
 * 
 * **/
   public void fileendhandling(){
	   
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   int kk=settings.getInt("keepsyncing", 0);
	   filecounter++;
	   if(filecounter>mintosync){
		   if(kk==1){
			   		Log.d(TAG,"18min gone");
			   	   try {
			   		   daysbeforetoday=0;
		        	   shttpresponse=postData(0);
		               if (shttpresponse.endsWith("s")){httpsuccess=true;
		               postDatasuccess();
		               syncreadolderfile(shttpresponse);
		               Log.d(TAG,shttpresponse);}
		               Log.d(TAG,"k"+shttpresponse+"k");
		           } catch (Exception e) {
		        	   Log.e(TAG,"sync send repeat error");
		           }
				  // syncbuttongray();
		   }
		   
		   filecounter=0;
	   }
	   
	   
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {      
       boolean result = super.onCreateOptionsMenu(menu);
       
       if(mAntManager.isServiceConnected())
       {
           menu.add(Menu.NONE, MyMenu.MENU_PAIR_HRM.ordinal(), 0, this.getResources().getString(R.string.Menu_Wildcard_HRM));
           menu.add(Menu.NONE, MyMenu.MENU_PAIR_SDM.ordinal(), 1, this.getResources().getString(R.string.Menu_Wildcard_SDM));
           menu.add(Menu.NONE, MyMenu.MENU_PAIR_WEIGHT.ordinal(), 2, this.getResources().getString(R.string.Menu_Wildcard_Weight));
           //menu.add(Menu.NONE,MyMenu.MENU_DRAW_GRAPH.ordinal(), 4, this.getResources().getString(R.string.Menu_Draw_Graph));
           SubMenu configMenu2 = menu.addSubMenu(Menu.NONE,MyMenu.MENU_LOG.ordinal(),4,this.getResources().getString(R.string.Menu_Log_Config));
           configMenu2.add(Menu.NONE,MyMenu.MENU_SYNC.ordinal(), 0, this.getResources().getString(R.string.Menu_Sync_http));
           configMenu2.add(Menu.NONE,MyMenu.MENU_KEEPLOGGING.ordinal(), 1, this.getResources().getString(R.string.Menu_keep_logging));
           
           SubMenu configMenu = menu.addSubMenu(Menu.NONE, MyMenu.MENU_CONFIG.ordinal(), 3, this.getResources().getString(R.string.Menu_Sensor_Config));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_HRM.ordinal(), 0, this.getResources().getString(R.string.Menu_HRM));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_SDM.ordinal(), 1, this.getResources().getString(R.string.Menu_SDM));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_WGT.ordinal(), 2, this.getResources().getString(R.string.Menu_WGT));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_PROXIMITY.ordinal(), 3, this.getResources().getString(R.string.Menu_Proximity));
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_BUFFER_THRESHOLD.ordinal(), 4, this.getResources().getString(R.string.Menu_Buffer_Threshold));
           //if(flightmodeant(false)){
        	   configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_FLIGHT_MODE.ordinal(), 5, flightmodemenutext());
           //}           else {
        	 //  configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_FLIGHT_MODE.ordinal(), 5, this.getResources().getString(R.string.Menu_Flight_Mode));
           //}
        	   configMenu2.add(Menu.NONE,MyMenu.MENU_CONFIG_WRITE_PERIOD.ordinal(), 2, this.getResources().getString(R.string.Menu_write_period));
        	   configMenu2.add(Menu.NONE,MyMenu.MENU_CHANGE_BEEP.ordinal(), 3, this.getResources().getString(R.string.Menu_Change_beep));
        	   configMenu2.add(Menu.NONE,MyMenu.MENU_CHANGE_BEEPMIN.ordinal(), 3, this.getResources().getString(R.string.Menu_Change_beepmin));
           // delete hrm period
        	   if(debug)
           configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_HRM_PERIOD.ordinal(), 6, this.getResources().getString(R.string.Menu_hrm_period));

           menu.add(Menu.NONE, MyMenu.MENU_REQUEST_CLAIM_INTERFACE.ordinal(), 5, this.getResources().getString(R.string.Menu_Claim_Interface));
       }
       
       menu.add(Menu.NONE,MyMenu.MENU_HELP.ordinal(),6,this.getResources().getString(R.string.Menu_Help_screen));
      
       menu.add(Menu.NONE, MyMenu.MENU_EXIT.ordinal(), 99, this.getResources().getString(R.string.Menu_Exit));
       
       return result;
   }
   
   @Override
   public boolean onPrepareOptionsMenu(Menu menu) {
//TODO call  invalidateOptionsMenu() in 3.0
	   MenuItem deleteItem = menu.findItem(MyMenu.MENU_CONFIG_FLIGHT_MODE.ordinal());
	   deleteItem.setTitle(flightmodemenutext());
	   Log.d(TAG,"p");
	   MenuItem keeplog=menu.findItem(MyMenu.MENU_KEEPLOGGING.ordinal());
	   if(!checkloggingalive())
		   keeplog.setTitle(this.getResources().getString(R.string.Menu_keep_logging));
	   else
		   keeplog.setTitle(this.getResources().getString(R.string.Menu_keep_logging2));
	   
	  // menu.removeItem(MyMenu.MENU_CONFIG_FLIGHT_MODE.ordinal());
	   //menu.
	   // change log always change text
	   
	   return super.onPrepareOptionsMenu(menu);

   }
   
   
   private CharSequence flightmodemenutext() {
	  // Log.d(TAG,"del");
	   if(flightmodeant(false)){
    	   return this.getResources().getString(R.string.Menu_Flight_Mode2);
       }
       else {
    	   return this.getResources().getString(R.string.Menu_Flight_Mode);
       }
	//return null;
}




@Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      MyMenu selectedItem = MyMenu.values()[item.getItemId()];
      switch (selectedItem) 
      {         
      case MENU_DRAW_GRAPH:
    	  creategraph();
    	  break;
         case MENU_EXIT:            
            exitApplication();
            break;
         case MENU_PAIR_HRM:
            mAntManager.setDeviceNumberHRM(WILDCARD);
            break;
         case MENU_CONFIG_HRM:
            showDialog(PairingDialog.HRM_ID);
            break;
         case MENU_PAIR_SDM:
             mAntManager.setDeviceNumberSDM(WILDCARD);
            break;
         case MENU_PAIR_WEIGHT:
             mAntManager.setDeviceNumberWGT(WILDCARD);
             //julian screen
             break;
         case MENU_CONFIG_SDM:
            showDialog(PairingDialog.SDM_ID);
            break;
         case MENU_CONFIG_WGT:
            showDialog(PairingDialog.WGT_ID);
            break;
         case MENU_CONFIG_PROXIMITY:
            showDialog(PairingDialog.PROX_ID);
            break;
         case MENU_CONFIG_BUFFER_THRESHOLD:
            showDialog(PairingDialog.BUFF_ID);
            break;
         case MENU_REQUEST_CLAIM_INTERFACE:
             mAntManager.tryClaimAnt();
             break;
         case MENU_CONFIG_WRITE_PERIOD:
        	 showDialog(PairingDialog.WRITE_PERIOD_ID);
        	 break;
         case MENU_CONFIG_HRM_PERIOD:
        	 //mAntManager.changeperiod();
        	 showDialog(PairingDialog.PERIO_D);
        	 break;
         case  MENU_CHANGE_BEEP:
        	 showDialog(PairingDialog.BEEPMAX);
        	 break;
         case  MENU_CHANGE_BEEPMIN:
        	 showDialog(PairingDialog.BEEPMIN);
        	 break; 
         case MENU_CONFIG_FLIGHT_MODE:
        	 flightmodeant(true);
        	 break;
         case MENU_SYNC:
        	 if (keepsyncbutton(true))
        	 syncbpmdata();
        	 break;
         case MENU_KEEPLOGGING:
        	 keeploggingpress();
        	 checkloggingalive();
        	 break;
         case MENU_HELP:
        	 helpscreen();
        	 break;
         case MENU_CONFIG:
             //fall through to do nothing, as this represents a submenu, not a menu option
         case MENU_NONE:
             //Do nothing for these, as they shouldn't even be registered as menu options
             break;
      }
      return super.onOptionsItemSelected(item);
   }

   
   private void helpscreen() {
	   Intent myIntent = new Intent(getBaseContext(), ShowHelpscreen.class);
	   	//startActivity( new Intent("android.intent.action.SWEET"));
	   	startActivity(myIntent);
	
}

   private void keeploggingpress(){
	   
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	      SharedPreferences.Editor editor = settings.edit();
	     
		   int keep=settings.getInt("keeplogging", 0);
	      if(keep==1){
	      editor.putInt("keeplogging", 0);//1 bedeutet true
	      }else{editor.putInt("keeplogging", 1);}
	      editor.commit();//0 false
   }
   
/** 
 * start bg service if not running and set keeplogging to true
 * 
 **/
private void keeploggingalive() {
	// if already running do not start it again
	     if(!servicerunning){
	     Intent svc2 = new Intent( com.dsi.ant.antplusdemoj.bgservicewritefile.ACTION_FOREGROUND);
//	      svc.putExtra("cantplusdemo", this);
	     svc2.setClass(com.dsi.ant.antplusdemoj.ANTPlusDemoj.this, com.dsi.ant.antplusdemoj.bgservicewritefile.class);
	     
	      startService(svc2); //, Bundle.EMPTY
	     
	      servicerunning=true;}
	      screenoff=0;
	     /* SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      
	      editor.putInt("keeplogging", 1);//1 bedeutet true
	      editor.commit();//0 false*/
   }
   
   private boolean checkloggingalive(){
	   
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   int keep=settings.getInt("keeplogging", 1);
	   if(keep==1)keeploggingalive();
	   if(keep==1)
		   return true;
	   else return false;
   }


@Override
   protected PairingDialog onCreateDialog(int id)
   {
      PairingDialog theDialog = null;
      
      if(id == PairingDialog.HRM_ID)
         theDialog = new PairingDialog(this, id, mAntManager.getDeviceNumberHRM(), mDeviceIdHint, new OnPairingListener());
      else if(id == PairingDialog.SDM_ID)
         theDialog = new PairingDialog(this, id, mAntManager.getDeviceNumberSDM(), mDeviceIdHint, new OnPairingListener());
      else if(id == PairingDialog.WGT_ID)
         theDialog = new PairingDialog(this, id, mAntManager.getDeviceNumberWGT(), mDeviceIdHint, new OnPairingListener());
      else if(id == PairingDialog.PROX_ID)
         theDialog = new PairingDialog(this, id, mAntManager.getProximityThreshold(), mBinHint, new OnPairingListener());
      else if(id == PairingDialog.BUFF_ID)
          theDialog = new PairingDialog(this, id, mAntManager.getBufferThreshold(), mBufferThresholdHint, new OnPairingListener());
      else if(id == PairingDialog.PERIO_D)
          theDialog = new PairingDialog(this, id, mAntManager.getHRMPeriod(), mHRMPeriodHint, new OnPairingListener());
      else if(id == PairingDialog.WRITE_PERIOD_ID)
          theDialog = new PairingDialog(this, id, (short)getsecperiod(), writePeriodHint, new OnPairingListener());
      else if(id == PairingDialog.BEEPMAX)
          theDialog = new PairingDialog(this, id, (short)getbeephrm(true), writePeriodHint, new OnPairingListener());
      else if(id == PairingDialog.BEEPMIN)
          theDialog = new PairingDialog(this, id, (short)getbeephrm(false), writePeriodHint, new OnPairingListener());
      
      
      return theDialog;
   }

   
   /**
    * Listener to updates to the device number.
    *
    * @see OnPairingEvent
    */
   private class OnPairingListener implements PairingDialog.PairingListener 
   {
      
      /* (non-Javadoc)
       * @see com.dsi.ant.antplusdemo.PairingDialog.PairingListener#updateID(int, short)
       */
      public void updateID(int id, short deviceNumber)
      {
         if(id == PairingDialog.HRM_ID)
            mAntManager.setDeviceNumberHRM(deviceNumber);
         else if(id == PairingDialog.SDM_ID)
             mAntManager.setDeviceNumberSDM(deviceNumber);
         else if(id == PairingDialog.WGT_ID)
             mAntManager.setDeviceNumberWGT(deviceNumber);
         else if(id == PairingDialog.BUFF_ID)
         {
             mAntManager.setBufferThreshold(deviceNumber);
             
             mAntManager.setAntConfiguration();
         }
      }
      
      public void updatehrmwriteperiod(int id,int iwriteperiod){
    	 
    	  SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      
	      editor.putInt("secperiod", iwriteperiod);
	      editor.commit();
	      //TODO does work but you have to reset ant
	      Log.d(TAG,"k"+(new Short((short)(8070+(iwriteperiod-1)*2200/60))).toString() );
	      if(iwriteperiod==0)iwriteperiod=1;
	      mAntManager.setHRMPeriod((short)(8070+(iwriteperiod-1)*2200/60));
	      
	      
	      //reset ant
	      mAntManager.openChannel(AntPlusManager.HRM_CHANNEL,true);
	      mAntManager.requestReset();
	      
	      
	      
      }
      
      public void updatebeepmax(int id,int max){
    	  updatebeep(id,true,max);
      }
      
      public void updatebeepmin(int id,int max){
    	  updatebeep(id,false,max);
    	  
      }
      
      /* (non-Javadoc)
       * @see com.dsi.ant.antplusdemo.PairingDialog.PairingListener#updateThreshold(int, byte)
       */
      public void updateThreshold(int id, byte proxThreshold)
      {
         if(id == PairingDialog.PROX_ID)
            mAntManager.setProximityThreshold(proxThreshold);
      }
   }
  

   @Override
   protected void onPrepareDialog(int id, Dialog theDialog)
   {
      super.onPrepareDialog(id, theDialog);
      PairingDialog dialog = (PairingDialog) theDialog;
      dialog.setId(id);
      if(id == PairingDialog.HRM_ID)
      {
         dialog.setDeviceNumber(mAntManager.getDeviceNumberHRM());
      }
      else if(id == PairingDialog.SDM_ID)
      {
         dialog.setDeviceNumber(mAntManager.getDeviceNumberSDM());
      }
      else if(id == PairingDialog.WGT_ID)
      {
         dialog.setDeviceNumber(mAntManager.getDeviceNumberWGT());
      }
      else if(id == PairingDialog.PROX_ID)
      {
         dialog.setProximityThreshold(mAntManager.getProximityThreshold());
      }
      else if(id == PairingDialog.BUFF_ID)
      {
         dialog.setDeviceNumber(mAntManager.getBufferThreshold());
      }
      else if(id == PairingDialog.PERIO_D)
          dialog.sethrmperiod( mAntManager.getHRMPeriod());
      else if(id == PairingDialog.WRITE_PERIOD_ID)
          dialog.setwriteperiod( (short)getsecperiod());  
      else if(id == PairingDialog.BEEPMIN)
          dialog.setbeepmin( (short)getbeephrm(false));   
      else if(id == PairingDialog.BEEPMAX)
          dialog.setbeepmax( (short)getbeephrm(true));   
   }
   

   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState)
   {
      Log.d(TAG, "onRestoreInstanceState");
      
      mAntManager.loadState(savedInstanceState);

      super.onRestoreInstanceState(savedInstanceState);
   }
   //TODO put everything you done here in new class       bgservice no
    // It is important to save persistent data in onPause() instead of onSaveInstanceState(Bundle)
    // because the later is not part of the lifecycle callbacks, so will not be called in every
    // situation as described in its documentation.
   @Override
   protected void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      mAntManager.saveState(outState);
      Log.d(TAG, "onSaveInstanceState");
   }

   //TODO Help Screen say that data gets saved in bpm dir   and make it possible to change if
   // public can access the bpm data or just you

   @Override
   protected void onPause()
   {
      Log.d(TAG, "onPause");

      saveState();

     // timerforrunningcheck(120);
      // disable some processing of ANT messages while UI is not available
      
      //julian disable
      if(screenoff==1)
      mAntManager.pauseMessageProcessing();
      
      //post if program is actively logging or not
      if(screenoff2==0)handler.postDelayed(runnable, 120*60*1000);
      
     
      super.onPause();      
   }
   
   protected void onStart(){
	   Log.d(TAG,"onStart");
	   super.onStart();
   }
   

   @Override    
   protected void onResume()
   {
      Log.d(TAG, "onResume");

      super.onResume();
      
      timer.cancel();
      
      if(screenoff2==0)handler.removeCallbacks(runnable);
      
      loadDefaultConfiguration();

     // Toast.makeText(getBaseContext(), "screen off",Toast.LENGTH_SHORT).show();
      //julian disable
      if(screenoff==1)
      mAntManager.resumeMessageProcessing();
      
     // SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
		keepsyncbutton(false);
		//int month=settings.getInt("datemsync", -1);
		if(checkloggingalive()){
					   	
			   	//changed
			       handlerresume.postDelayed(startant, 3*1000);
			
		}
		
   }
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "onresult");
    
    if (requestCode == 0){ //GET_CODE
     if (resultCode == RESULT_OK) {
     // text.setText(data.getStringExtra("Color"));
    	 if(data.getStringExtra("Color").equals("Green")){
    		 if (keepsyncbutton(true))
  			   syncbpmdata();
    		/* handler.postDelayed(new Runnable() { 
    	         public void run() { 
    	        	 if (keepsyncbutton(true))
    	        	 syncbpmdata();
    	         }},5000);*/
    		 
    		 
    	 }
     }
     else{
    //  text.setText("Cancelled");
    	 
     }
    }
   }
   
  // private void timerforrunningcheck(int periodinseconds){
	 
 //  }
   
   /**
    * Store application persistent data.
    */
   private void saveState()
   {
      // Save current Channel Id in preferences
      // We need an Editor object to make changes
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putInt("DeviceNumberHRM", mAntManager.getDeviceNumberHRM());
      editor.putInt("DeviceNumberSDM", mAntManager.getDeviceNumberSDM());
      editor.putInt("DeviceNumberWGT", mAntManager.getDeviceNumberWGT());
      editor.putInt("ProximityThreshold", mAntManager.getProximityThreshold());
      editor.putInt("BufferThreshold", mAntManager.getBufferThreshold());
      editor.commit();
   }
   
   /**
    * Retrieve application persistent data.
    */
   private void loadDefaultConfiguration()
   {
      // Restore preferences
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      mAntManager.setDeviceNumberHRM((short) settings.getInt("DeviceNumberHRM", WILDCARD));
      mAntManager.setDeviceNumberSDM((short) settings.getInt("DeviceNumberSDM", WILDCARD));
      mAntManager.setDeviceNumberWGT((short) settings.getInt("DeviceNumberWGT", WILDCARD));
      mAntManager.setProximityThreshold((byte) settings.getInt("ProximityThreshold", DEFAULT_BIN));
      mAntManager.setBufferThreshold((short) settings.getInt("BufferThreshold", DEFAULT_BUFFER_THRESHOLD));
   }
   
   /**
    * Initialize GUI elements.
    */
   private void initControls()
   {
	   try{
      mAntStateText = (TextView)findViewById(R.id.text_status);
	   }catch(Exception e){Log.d(TAG,"init text");}
      
      // Set up button listeners and scaling 
      try{
      ((ImageButton)findViewById(R.id.button_heart)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_heart)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_heart)).setBackgroundColor(Color.TRANSPARENT);
      ((ImageButton)findViewById(R.id.button_sdm)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_sdm)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_sdm)).setBackgroundColor(Color.TRANSPARENT);
      ((ImageButton)findViewById(R.id.button_weight)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_weight)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_weight)).setBackgroundColor(Color.TRANSPARENT);
      ((ImageButton)findViewById(R.id.button_hrm)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_hrm)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_hrm)).setBackgroundColor(Color.TRANSPARENT);
      ((ImageButton)findViewById(R.id.button_sync)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_sync)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_sync)).setBackgroundColor(Color.TRANSPARENT);
      ((ImageButton)findViewById(R.id.button_hrm2)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_hrm2)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_hrm2)).setBackgroundColor(Color.TRANSPARENT);
      ((ImageButton)findViewById(R.id.button_syncl)).setOnClickListener(this);
      ((ImageButton)findViewById(R.id.button_syncl)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      ((ImageButton)findViewById(R.id.button_syncl)).setBackgroundColor(Color.TRANSPARENT);
      }catch(Exception e){Log.d(TAG,"oke");}
   }
   
   /**
    * Shows/hides the channels based on the state of the ant service
    */
   private void drawWindow()
   {
       boolean showChannels = mAntManager.checkAntState();
       setDisplay(showChannels,false);
       if(showChannels)
       {
           drawChannel(AntPlusManager.HRM_CHANNEL);
           drawChannel(AntPlusManager.SDM_CHANNEL);
           drawChannel(AntPlusManager.WEIGHT_CHANNEL);
       }
       else
       {
           mAntStateText.setText(mAntManager.getAntStateText());
       }
   }
   
   /**
    * Sets the channel button image and status strings according to the specified channel's state
    * @param channel
    */
   private void drawChannel(byte channel)
   {
	  try{
       switch(channel)//TODO probably error here according to google
       {
       
      // if(AntPlusManager.HRM_CHANNEL==null)Log.e(TAG,"channel");
       
           case AntPlusManager.HRM_CHANNEL:
        	   if(mAntManager.getHrmState()==null)Log.d(TAG,"channel");
               switch (mAntManager.getHrmState()) {
                   case CLOSED:
                       ((ImageButton)findViewById(R.id.button_heart)).setImageResource(R.drawable.ant_hrm_gray);
                       ((TextView)findViewById(R.id.text_status_hrm)).setText(getString(R.string.Closed));
                       break;
                   case OFFLINE:
                       ((ImageButton)findViewById(R.id.button_heart)).setImageResource(R.drawable.ant_hrm_gray);
                       ((TextView)findViewById(R.id.text_status_hrm)).setText(getString(R.string.NoSensor_txt));
                       break;
                   case SEARCHING:
                       //From the user's point of view waiting for a device to connect and
                       //waiting for the channel to open are equivalent.
                   case PENDING_OPEN:
                       ((ImageButton)findViewById(R.id.button_heart)).setImageResource(R.drawable.ant_hrm);
                       ((TextView)findViewById(R.id.text_status_hrm)).setText(getString(R.string.Search));
                       break;
                   case TRACKING_STATUS:
                       //This state should not show up for this channel, but in the case it does
                       //We can consider it equivalent to showing the data.
                   case TRACKING_DATA:
                	   try{
                       ((ImageButton)findViewById(R.id.button_heart)).setImageResource(R.drawable.ant_hrm);
                       ((TextView)findViewById(R.id.text_status_hrm)).setText(getString(R.string.Connected));
                	   }catch (Exception e){Log.d(TAG,"hrmbutton");hrmbuttone++;
                	   }
                       break;
               }
               break;
           case AntPlusManager.SDM_CHANNEL:
               switch (mAntManager.getSdmState()) {
                   case CLOSED:
                	   try{
                       ((ImageButton)findViewById(R.id.button_sdm)).setImageResource(R.drawable.ant_spd_gray);
                       ((TextView)findViewById(R.id.text_status_sdm)).setText(getString(R.string.Closed));
                	   }catch (Exception e){Log.d(TAG,"sdm close");
                	   }
                       break;
                   case OFFLINE:
                       ((ImageButton)findViewById(R.id.button_sdm)).setImageResource(R.drawable.ant_spd_gray);
                       ((TextView)findViewById(R.id.text_status_sdm)).setText(getString(R.string.NoSensor_txt));
                       break;
                   case SEARCHING:
                       //From the user's point of view waiting for a device to connect and
                       //waiting for the channel to open are equivalent.
                   case PENDING_OPEN:
                       ((ImageButton)findViewById(R.id.button_sdm)).setImageResource(R.drawable.ant_spd);
                       ((TextView)findViewById(R.id.text_status_sdm)).setText(getString(R.string.Search));
                       break;
                   case TRACKING_STATUS:
                       //This state should not show up for this channel, but in the case it does
                       //We can consider it equivalent to showing the data.
                   case TRACKING_DATA:
                       ((ImageButton)findViewById(R.id.button_sdm)).setImageResource(R.drawable.ant_spd);
                       ((TextView)findViewById(R.id.text_status_sdm)).setText(getString(R.string.Connected));
                       break;
               }
               break;
           case AntPlusManager.WEIGHT_CHANNEL:
               switch (mAntManager.getWeightState()) {
                   case CLOSED:
                	   try{
                       ((ImageButton)findViewById(R.id.button_weight)).setImageResource(R.drawable.ant_wgt_gray);
                       ((TextView)findViewById(R.id.text_status_weight)).setText(getString(R.string.Closed));
                	   }catch (Exception e){Log.d(TAG,"gray");
                	   }
                       break;
                   case OFFLINE:
                       ((ImageButton)findViewById(R.id.button_weight)).setImageResource(R.drawable.ant_wgt_gray);
                       ((TextView)findViewById(R.id.text_status_weight)).setText(getString(R.string.NoSensor_txt));
                       break;
                   case SEARCHING:
                       //From the user's point of view waiting for a device to connect and
                       //waiting for the channel to open are equivalent.
                   case PENDING_OPEN:
                       ((ImageButton)findViewById(R.id.button_weight)).setImageResource(R.drawable.ant_wgt);
                       ((TextView)findViewById(R.id.text_status_weight)).setText(getString(R.string.Search));
                       break;
                   case TRACKING_DATA:
                       //Data and status are both represented by the same string, so we do not need to
                       //differentiate between those states here.
                   case TRACKING_STATUS:
                       ((ImageButton)findViewById(R.id.button_weight)).setImageResource(R.drawable.ant_wgt);
                       ((TextView)findViewById(R.id.text_status_weight)).setText(mAntManager.getWeightStatus());
                       break;
               }
               break;
       }
       drawChannelData(channel);
	  } catch(Exception e){//channel not null
		  Log.d(TAG,"channel"+ e.getMessage());
	  }
   }
   
   /**
    * Fills in the data fields for the specified ant channel's data display 
    * @param channel
    */
   private void drawChannelData(byte channel)
   {
	   
	   
       switch(channel)
       {
           case AntPlusManager.HRM_CHANNEL:
               switch (mAntManager.getHrmState()) {
                   case CLOSED:
                   case OFFLINE:
                   case SEARCHING:
                   case PENDING_OPEN:
                       //For all these cases we don't have any incoming data, so they all show '--'
                       ((TextView)findViewById(R.id.text_bpm)).setText(getString(R.string.noData) + getString(R.string.heartRateUnits));
                       break;
                   case TRACKING_STATUS:
                       //There is no Status state for the HRM channel, so we will attempt to show latest data instead
                   case TRACKING_DATA:
                
                	   try{
                       ((TextView)findViewById(R.id.text_bpm)).setText(mAntManager.getBPM() + getString(R.string.heartRateUnits));
                	   }catch (Exception e){Log.d(TAG,"bpmtext");
                	   }
                       saveandstorebpm();
                       //if (hour<10)hourn="0";
                       //if (min<10)minn="0";
                       break;
               }
               break;
           case AntPlusManager.SDM_CHANNEL:
               switch (mAntManager.getSdmState()) {
                   case CLOSED:
                   case OFFLINE:
                   case SEARCHING:
                   case PENDING_OPEN:
                       //For all these cases we don't have any incoming data, so they all show '--'
                	   try{
                       ((TextView)findViewById(R.id.text_speed)).setText(getString(R.string.noData) + getString(R.string.speedUnits));
                       ((TextView)findViewById(R.id.text_distance)).setText(getString(R.string.noData) + getString(R.string.distanceUnits));
                       ((TextView)findViewById(R.id.text_strides)).setText(getString(R.string.noData) + getString(R.string.stepUnits));
                       ((TextView)findViewById(R.id.text_cadence)).setText(getString(R.string.noData) + getString(R.string.cadenceUnits));
                	   }catch (Exception e){Log.d(TAG,"sdm data");
                	   }
                       break;
                   case TRACKING_STATUS:
                       //There is no Status state for the SDM channel, so we will attempt to show latest data instead
                   case TRACKING_DATA:
                       ((TextView)findViewById(R.id.text_speed)).setText(mOutputFormatter.format(mAntManager.getSpeed()) + getString(R.string.speedUnits));
                       ((TextView)findViewById(R.id.text_distance)).setText(mOutputFormatter.format(mAntManager.getAccumDistance()) + getString(R.string.distanceUnits));
                       ((TextView)findViewById(R.id.text_strides)).setText(mAntManager.getAccumStrides() + getString(R.string.stepUnits));
                       ((TextView)findViewById(R.id.text_cadence)).setText(mOutputFormatter.format(mAntManager.getCadence()) + getString(R.string.cadenceUnits));
                       break;
               }
               break;
           case AntPlusManager.WEIGHT_CHANNEL:
               switch (mAntManager.getWeightState()) {
                   case CLOSED:
                   case OFFLINE:
                   case SEARCHING:
                   case PENDING_OPEN:
                   case TRACKING_STATUS:
                       //For all these cases we don't have any incoming data, so they all show '--'
                	   try{
                       ((TextView)findViewById(R.id.text_weight)).setText(getString(R.string.noData) + getString(R.string.weightUnits));
                	   }catch (Exception e){Log.d(TAG,"weight data");
                	   }
                       break;
                   case TRACKING_DATA:
                       ((TextView)findViewById(R.id.text_weight)).setText(mOutputFormatter.format(mAntManager.getWeight()/100.0) + getString(R.string.weightUnits));
                       break;
               }
               break;
       }
   }
   
   public void saveandstorebpm(){
	   Calendar cal = Calendar.getInstance();
	   int min = cal.get(Calendar.MINUTE);
	   int hour = cal.get(Calendar.HOUR_OF_DAY);
	   String hourn="";String minn="";
       //((TextView)findViewById(R.id.text_bpm)).setText(mAntManager.getBPM() + getString(R.string.heartRateUnits));
       if (hour<10)hourn="0";
       if (min<10)minn="0";
       int abpm=mAntManager.getaverageBPM(3);
       int cbpm=mAntManager.getBPM();
       writtenbpm[3]=writtenbpm[2];
   	writtenbpm[2]=writtenbpm[1];
   	writtenbpm[1]=writtenbpm[0];
   	writtenbpm[0]=abpm;
   	SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
    int secp=settings.getInt("secperiod", 30);
    if(secp<10)abpm=cbpm;
    
       WriteSettings(getBaseContext(), hourn +(new Integer(hour)).toString() +minn+ (new Integer(min)).toString() + " "+(new Integer(abpm)).toString() );
       //WriteSettings(getBaseContext(), String.format("%02i",hour) + String.format("%02i",min) + " "+(new Integer(mAntManager.getaverageBPM(3))).toString() + ",");
       storebpmsinpref(cbpm);
	   
   }
   
   private void storebpmsinpref(int pbpm){
// Save current Channel Id in preferences
   // We need an Editor object to make changes
   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
   SharedPreferences.Editor editor = settings.edit();
   
   editor.putInt("bpm0", pbpm);
   editor.commit();
   }
   
   //julian 
   public void savebpm(){ // not called from this class
	   
	   Calendar cal = Calendar.getInstance();
	   int min = cal.get(Calendar.MINUTE);
	   int hour = cal.get(Calendar.HOUR_OF_DAY);
	   String hourn="";String minn="";
       //((TextView)findViewById(R.id.text_bpm)).setText(mAntManager.getBPM() + getString(R.string.heartRateUnits));
       if (hour<10)hourn="0";
       if (min<10)minn="0";
       WriteSettings(getBaseContext(), hourn +(new Integer(hour)).toString() +minn+ (new Integer(min)).toString() + " "+(new Integer(mAntManager.getBPM())).toString() + "\n");
       
   }
   
   private void colorsyncbutton(){
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   
		   
		   if(settings.getInt("keepsyncing", 0)==0){
			   syncbuttonlight();
		   } else {
			   syncbuttongray();
		   }
		   
   }
   
   public boolean keepsyncbutton(boolean k){
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   if(settings.getBoolean("regformsuccess", false)){
		   
		   if(settings.getInt("keepsyncing", 0)==0){
			   SharedPreferences.Editor editor = settings.edit();

			   editor.putInt("keepsyncing", 1);//1 ist true
			   editor.commit();//0 ist false
			   syncbuttongray();
			   syncbpmdata();//TODO test in live
			   return true;
		   }
		   if(k){
		   SharedPreferences.Editor editor = settings.edit();

		   editor.putInt("keepsyncing", 0);//1 ist true
		   editor.commit();//0 ist false
		   syncbuttonlight();
		   }
		   return false;
	   }else{
		   if(k)
		  regprocess(settings);
	   return false;
	   }
	   
   }
   
   private void regprocess(SharedPreferences settings){
	   if(!settings.getBoolean("regformsuccess2", false)){
	   Intent myIntent = new Intent(getBaseContext(), Regform.class);
   	
	   //startActivity(myIntent);
	   
	   myIntent.setClass(this,Regform.class);
	     startActivityForResult(myIntent,0);//GET_CODE
	   }else{
		  //Intent myIntent = new Intent(getBaseContext(), Syncreg.class);
		   	
		 //  startActivity(myIntent);
	   Syncreg k=new Syncreg();
	   k.onCreate(getBaseContext());
	   keepsyncbutton(false);
	  // Toast.makeText(getBaseContext(), "Look at nsteinbock.de for your BPM data",Toast.LENGTH_SHORT).show();
		   }
   }
   
   public void syncbpmdata(){
	 //inputStreamToString(response.getEntity().getContent())
	   //permission to use internet
	   //async task
	   //http://www.androidsnippets.com/non-blocking-web-request
	   {
	   
	   syncdead=false;
	// if already running do not start
	   try{
   	if(!checkUpdate.isAlive()){
   		daysbeforetoday=0;
	   checkUpdate.start();}
   	else Log.d(TAG,"thread already running");
	   // start timer for 5minutes later
	   //SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   Handler handler = new Handler(); 
	    
   // the server answers with the last line of the file that 
	   //was created yesterday and the app searches for that
	   //entry and sends the rest
   	Log.i(TAG, "waitandreset");
       handler.postDelayed(new Runnable() { 
            public void run() { 
            	// if already running do not start
            	if(!checkUpdatefollowing.isAlive()){
            		daysbeforetoday=0;
            		checkUpdatefollowing.start();
            	}
           	Log.i(TAG, "reset1");
            } 
       }, 5*60*1000); 
       
	   }catch(Exception e){
		   Log.d(TAG,"already running");
		   syncdead=true;
	   }
	   
	   }
	   
   }
   
   private Thread checkUpdate = new Thread() {
	   //TODO check if already running destroy after 2 minutes
	  // String k="";
       public void run() {
           try {
        	   shttpresponse=postData(0);
               if (shttpresponse.endsWith("s")){httpsuccess=true;
               postDatasuccess();
               syncreadolderfile(shttpresponse);
               Log.d(TAG,shttpresponse);}
               Log.d(TAG,"k"+shttpresponse+"k");
           } catch (Exception e) {
        	   Log.e(TAG,"sync send");
           }
       }
   };
   
   private Thread checkUpdatefollowing = new Thread() {
	   //TODO check if already running destroy after 2 minutes
	//   String k="";
       public void run() {
           try {
              shttpresponse=postData(daysbeforetoday);
              if (shttpresponse.endsWith("s")){httpsuccess=true;
              if(daysbeforetoday==0)
              postDatasuccess();
              syncreadolderfile(shttpresponse);
              Log.d(TAG,shttpresponse);}
              Log.d(TAG,"k"+shttpresponse+"kfollowing");
           } catch (Exception e) {Log.e(TAG,"sync sendf");
           }
       }
   };
   
   private Thread checkUpdateoldfile = new Thread() {
	   // check if already running destroy after 2 minutes
	//   String k="";
       public void run() {
           try {
              shttpresponse=postData(daysbeforetoday);
              if (shttpresponse.endsWith("s")){httpsuccess=true;
              	if(daysbeforetoday==0)
              		postDatasuccess();
              	syncreadolderfile(shttpresponse);//TODO aufrufen mit noch einem tag weniger
              	Log.d(TAG,shttpresponse);}
                Log.d(TAG,"k"+shttpresponse+"kfollowing");
           } catch (Exception e) {Log.e(TAG,"sync send old");
           }
       }
   };
   
   private void postDatasuccess(){
	   Calendar cal = Calendar.getInstance();
  	   	   
	   int month = cal.get(Calendar.MONTH) + 1;	   
	   int dom = cal.get(Calendar.DAY_OF_MONTH);
	
	  
	
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   SharedPreferences.Editor editor = settings.edit();
	   
	   firstsync=false;
	   
	   editor.putString("datesync", filenamesync);//TODO deprecated
	   editor.putString("synclastline", synclastline);
	   editor.putBoolean("synctodays",true );
	   editor.putInt("datemsync", month);
	   editor.putInt("datedsync", dom);	  
	   editor.commit();
	   filecounter=0;
	   
	   //syncbuttongray();
	   
   }
   
   private void syncreadolderfile(String s){
	   
	   int len=s.length();
	   //Log.d(TAG,s.substring(1,len-1));
	   synclastlineold=s.substring(1,len-1);
	   
	  // if(synclastlineold.length()>1){
	   if(true){
	   try{
		  // synckillifrunning();
		   //TODO what happens when old file is not created/present on server
		   //TODO server should send no file then we send the whole file
		   
		   Log.d(TAG, "uploading old file "+(new Integer(daysbeforetoday)).toString());
	       lastday.postDelayed(new Runnable() { 
	            public void run() { 
	            	try{
	            	//checkUpdatefollowing.destroy();
	            	// if already running do not start
	            	if(!checkUpdateoldfile.isAlive()){
	            		synclastline=synclastlineold;
	            		//daysbeforetoday=daysbeforetoday+1;
	            		daysbeforetoday=1;
	            		checkUpdateoldfile.start();
	            	}
	            	}catch(Exception e){ Log.e(TAG,"p");}
	           	Log.d(TAG, "reset1old");
	            } 
	    //   }, mintosync*120*1000/18); 
	       }, 50*1000);
	       
		   }catch(Exception e){
			   Log.d(TAG,"already running");
			   syncdead=true;
		   }
	   }
	   
   }

   private void syncbuttongray(){
	   try {
	   ((ImageButton)findViewById(R.id.button_sync)).setImageResource(R.drawable.synclightgray_gray);
	   ((ImageButton)findViewById(R.id.button_syncl)).setImageResource(R.drawable.syncgray_gray);
	   }catch (Exception e){Log.d(TAG,"gray");
	   }
	   // sync at the beginning and make sure that at the start
	   //of the program the button is grayed according
	   //to syncing
   }
   
   private void syncbuttonlight(){
	   try{
	   ((ImageButton)findViewById(R.id.button_sync)).setImageResource(R.drawable.synclightgray);
	   ((ImageButton)findViewById(R.id.button_syncl)).setImageResource(R.drawable.synclightgray);
   }catch (Exception e){Log.d(TAG,"gray");
   }
       //TODO status kasten neben dem button und cycling zeug weg
	  // Toast.makeText(getBaseContext(), "Look at nsteinbock.de for your BPM data",Toast.LENGTH_SHORT).show();
	   //TODO error hier nachdem screen black war
   }
   
	   private String postData(int daysbeforetoday) {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    String webserver="http://192.168.0.21/w.php";
		   // if(!debug)
		    	webserver="http://heart.julian-steinbock.de/w.php";
		    	Log.d(TAG,"post");
		    HttpPost httppost = new HttpPost(webserver);

		    HttpResponse response =null;
		    
		    try {
		    	String $mes=getMessage(daysbeforetoday);
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("i", getID()));
		        nameValuePairs.add(new BasicNameValuePair("p", getpw()));
		        nameValuePairs.add(new BasicNameValuePair("m", $mes));
		        nameValuePairs.add(new BasicNameValuePair("d", getDateofmessage()));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        if(firstsync||$mes.length()>2)//||daysbeforetoday>0)//lastchange
		        // Execute HTTP Post Request
		         response = httpclient.execute(httppost);
		        
		    } catch (ClientProtocolException e) {
		       
		    	return "error clientprotokol";
		    } catch (IOException e) {
		    	Toast.makeText(getBaseContext(), "no internet connection",Toast.LENGTH_SHORT).show();
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
   
   
   private String getMessage(int daysbeforetoday){
	   Calendar cal = Calendar.getInstance();
  	   
	   if(daysbeforetoday!=0){
		 cal.add(Calendar.DATE, daysbeforetoday*-1); 
		   
	   }
	   
	   int month = cal.get(Calendar.MONTH) + 1;
	   
	   int dom = cal.get(Calendar.DAY_OF_MONTH);
	   
	
	  String smo="";
	  String sdom="";
	   if (dom<10)sdom="0";
	   if (month<10)smo="0";
	   
	   
	   
	 String filename=smo +(new Integer(month)).toString()+ sdom + (new Integer(dom)).toString();
	 filenamesync=filename;
	 
	   Log.d(TAG, "try to read file");
	//   File dir = new File (sdcard.getAbsolutePath() + "/dir1/dir2"); 
	  //  file = new File(sdCard.getAbsolutePath() + "/documents/bpm" +sdoy +(new Integer(month)).toString()+ sdom + (new Integer(dom)).toString() + ".txt");
	   String wholefile=""; 
try{
// Open the file that is the first 
// command line parameter
	File sdCard = Environment.getExternalStorageDirectory();
FileInputStream fstream = new FileInputStream(sdCard.getAbsolutePath() + "/bpm/bpm" +filename + ".txt");
// Get the object of DataInputStream
DataInputStream in = new DataInputStream(fstream);
BufferedReader br = new BufferedReader(new InputStreamReader(in));
String strLine="";
String vorigeLine="";
String zvorigeLine="";
String previous="";
//Read File Line By Line
	int i=0;
	//int ki=0;
	foundline=false;
	SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	
	String slastline=settings.getString("synclastline", "kk");
//	Log.d(TAG, slastline);//null
	boolean cfirstsync=didsynchappentoday();
	if(daysbeforetoday!=0){slastline=synclastlineold;
	cfirstsync=true;
	}
	Log.d(TAG,"w"+slastline);
	while ((strLine = br.readLine()) != null)   {

		wholefile=processreadfile(wholefile,strLine,cfirstsync,slastline);
		//wholefile=wholefile +strLine+"\n";
		previous=zvorigeLine;
		zvorigeLine=vorigeLine;
		vorigeLine=strLine;
		
		i++;
	}
	//Log.d(TAG,previous)
	synclastline=vorigeLine;
	if(synclastline==null){
		synclastline=zvorigeLine;
		if(synclastline==null)
		synclastline=previous;
		
	}

	if(synclastline==null)synclastline="nullerror";
	Log.d(TAG,synclastline);
//Log.d(TAG,(new Integer(abpm[0])).toString()+ " " +(new Integer(i)).toString() );
//Close the input stream
in.close();
  }catch (Exception e){//Catch exception if any
System.err.println("Error: " + e.getMessage());

}

   return wholefile;
   }
   
   
   /**
    * process the lines of the file
    *
    * @param wholefile the whole file in a string
    * @param strLine the one line of the filehandler
    * @param firstsync true if it is the first sync
    */
   private String processreadfile(String wholefile, String strLine,boolean synctoday,String slastline) {
	   
	if(!synctoday){
	  return (wholefile +strLine+"\n");
	}else{
		
		
  		   if(foundline){
  			 Log.d(TAG,"writing");
  			   return (wholefile +strLine+"\n");
  		   }
  		   //if(strLine.substring(0,7).endsWith(slastline.substring(0,7))){
  		   if(strLine.equals(slastline)){
  			   foundline=true;
  			   Log.d(TAG,"found it");
  			 Log.d(TAG,strLine );
  			   return "";
  		   }
  		   
  		   
		
  		   return "";}
	
	
}

   private boolean didsynchappentoday(){
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
		
		//String slastline=settings.getString("synclastline", "kk");
		int month=settings.getInt("datemsync", -1);
		int dom=settings.getInt("datedsync", -1);	
		 Calendar cal = Calendar.getInstance();
	  	   
 	   int cmonth = cal.get(Calendar.MONTH) + 1;
 	   
 	   int cdom = cal.get(Calendar.DAY_OF_MONTH);
 	   if(month==cmonth&&cdom==dom)return true;
 	   Log.d(TAG,"did not happen"+ (new Integer(dom)).toString());
 	   return false;
	   
   }

   private String getpw(){
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   String id=settings.getString("antpw", "nopw");
	  // Log.d(TAG,id);
	   return id;
	   
   }
   
private String getDateofmessage(){
	   Log.d(TAG,filenamesync);
	   return filenamesync;
   }
   
   private String getID(){
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	  
	   
	   String id=settings.getString("antid", "60");
	   Log.d(TAG,id);
	   return id;
	   
   }
   
   /**
    * Set whether buttons etc are visible.
    *
    * @param pVisible buttons visible, status text shown when they are not.
    * @param pgraphrunning if graph is running do not clear ant channelstates
    */
   protected void setDisplay(boolean pVisible,boolean pgraphrunning)
   {
       Log.d(TAG, "setDisplay: visible = "+ pVisible);
       
       int visibility = (pVisible ? View.VISIBLE : View.INVISIBLE);

       try{
       findViewById(R.id.button_heart).setVisibility(visibility);
       findViewById(R.id.hrm_layout).setVisibility(visibility);
       findViewById(R.id.button_sdm).setVisibility(visibility);
       findViewById(R.id.sdm_layout).setVisibility(visibility);
       findViewById(R.id.button_weight).setVisibility(visibility);
       findViewById(R.id.weight_layout).setVisibility(visibility);
       findViewById(R.id.button_hrm).setVisibility(visibility);
       findViewById(R.id.button_sync).setVisibility(visibility);
       
       if(!pVisible&&!pgraphrunning)
       {
           mAntManager.clearChannelStates();
       }
       
       if(!pgraphrunning)
       mAntStateText.setVisibility(pVisible ? TextView.INVISIBLE : TextView.VISIBLE); // Visible when buttons aren't
       }catch(Exception e){Log.d(TAG,"setDisplay");}
   }


   /**
    * Display alert dialog.
    *
    * @param context the context to use
    * @param title the dialog title
    * @param msg the message to display in the dialog
    */
   public void showAlert(Context context, String title, String msg) 
   {
      new AlertDialog.Builder(context).setTitle(title).setIcon(
            android.R.drawable.ic_dialog_alert).setMessage(msg)
            .setNegativeButton(android.R.string.cancel, null).show();
   }
   
   /**
    * Display graph after onclick event
    * 
    */
   private void creategraph(){
	// init example series data  
	  // setDisplay(false,true);
	//   graphrunning=true;
	   Log.d(TAG, "Fdsfds " );

   	
       //showGraphview ka=new showGraphview();
       //ka.onCreate(bundle);
       Intent myIntent = new Intent(getBaseContext(), showGraphview.class);
   	//startActivity( new Intent("android.intent.action.SWEET"));
   	startActivity(myIntent);
	  
	   
   }
   
   
   //julian turns the screen off but leaves the cpu running
   public void disablescreen(){
	 
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
		  
	   SharedPreferences.Editor editor = settings.edit();

	//   editor.putBoolean("regformsuccess", true);//1 ist true
	   editor.commit();//0 ist false
	   
	   if(screenoff==1){
	 
	   
	   
	   
	   } else {if(screenoff==0) {
		   //wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DodDimScreen");
		  // wl.release();
		   if(debug)
		   Toast.makeText(getBaseContext(), "screen off",Toast.LENGTH_SHORT).show();
//screenoff=1;
		
		
	   }}
	   
   
   }
   
   public void disablescreen2(){
	  
	   if(debug)
		   Toast.makeText(getBaseContext(), "screen off",Toast.LENGTH_SHORT).show();
		   try{
	  // playSound(getBaseContext());
			   }
		   catch(Exception e){Log.e(TAG,"p");}
		   if(debug)
		   mAntManager.switchpauseapp();
	 //  flightmodeant();
		 //  syncreadolderfile("2342 299");
	   if(screenoff2==1){
	 
 	      
 	      
	   } else {if(screenoff2==0) {
		   //wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DodDimScreen");
		//  wl.release();
		   if(debug)
		   Toast.makeText(getBaseContext(), "screen off",Toast.LENGTH_SHORT).show();
		   
		
	   }}
	  
   
   
   }
   
   private boolean flightmodeant(boolean change){
	   
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
		  
	   SharedPreferences.Editor editor = settings.edit();

	   boolean id=settings.getBoolean("antflightmode", true);//if true mach ant nicht aus wenn false mache ant aus
	   
	   if(!change)id=!id;
	   
	   Context context=getBaseContext();
	   
	   String airplane=Settings.System.getString(context.getContentResolver(), Settings.System.AIRPLANE_MODE_RADIOS);
	   airplane=airplane.trim();
	   
	   if(id){
		 Log.d(TAG,"ant on");
		 airplane=airplane.replace("ant", "");
		 airplane=airplane.replace(",,","");
		 if(airplane.endsWith(","))
		    airplane=airplane.substring(0, airplane.length()-1);
		 if(airplane.startsWith(","))
			    airplane=airplane.substring(1, airplane.length());
			 
		 	   //Settings.System.putString(context.getContentResolver(), Settings.System.AIRPLANE_MODE_RADIOS, "cell"); 
		 Settings.System.putString(context.getContentResolver(), Settings.System.AIRPLANE_MODE_RADIOS, airplane);
	   id=false;
	   
	   
	   }else{
		   Log.d(TAG,"ant off");
		   
		   if(!airplane.contains("ant"))airplane=airplane+",ant";
		   
	   Settings.System.putString(context.getContentResolver(), 
			    Settings.System.AIRPLANE_MODE_RADIOS, airplane); 
	   id=true;
	   }
	   
	   editor.putBoolean("antflightmode", id);
	   editor.commit();//0 ist false

	   return id;
			
   }
   
   // OnClickListener implementation.
   public void onClick(View v)
   {
	   
	   switch (v.getId()){
		   case R.id.button_weight:
			  disablescreen();
			   break;
		   case R.id.button_sdm:
			   disablescreen2();
			   break;
		   case R.id.button_hrm:
			   creategraph();
			   break;
		   case R.id.button_hrm2:
			   creategraph();
			   break;
		   case R.id.button_sync:
			   if (keepsyncbutton(true))
			   syncbpmdata();
			   break;
		   case R.id.button_syncl:
			   if (keepsyncbutton(true))
			   syncbpmdata();
			   break;
	   }
	  // int i=AntPlusManager.pmBPM;
	 
        // If no channels are open, reset ANT
        if (!mAntManager.isChannelOpen(AntPlusManager.HRM_CHANNEL)
                && !mAntManager.isChannelOpen(AntPlusManager.SDM_CHANNEL)
                && !mAntManager.isChannelOpen(AntPlusManager.WEIGHT_CHANNEL))
        {
            Log.d(TAG, "onClick: No channels open, reseting ANT");
            // Defer opening the channel until an ANT_RESET has been
            // received
            switch (v.getId())
            {
                case R.id.button_heart:
                    mAntManager.openChannel(AntPlusManager.HRM_CHANNEL, true);
                    if(firstrunbpm){
                    checkloggingalive();}
                    firstrunbpm=false;
                    break;
                case R.id.button_sdm:
                    mAntManager.openChannel(AntPlusManager.SDM_CHANNEL, true);
                    break;
                case R.id.button_weight:
                	
                    mAntManager.openChannel(AntPlusManager.WEIGHT_CHANNEL, true);
                    break;
            }
            mAntManager.requestReset();
        }
        else {
            switch (v.getId()) {
                case R.id.button_heart:
                    if (!mAntManager.isChannelOpen(AntPlusManager.HRM_CHANNEL))
                    {
                        // Configure and open channel
                        Log.d(TAG, "onClick (HRM): Open channel");
                        mAntManager.openChannel(AntPlusManager.HRM_CHANNEL, false);
                        if(firstrunbpm)checkloggingalive();
                        firstrunbpm=false;
                    } else
                    {
                        // Close channel
                        Log.d(TAG, "onClick (HRM): Close channel");
                        mAntManager.closeChannel(AntPlusManager.HRM_CHANNEL);
                    }
                    break;
                case R.id.button_sdm:
                    if (!mAntManager.isChannelOpen(AntPlusManager.SDM_CHANNEL))
                    {
                        // Configure and open channel
                        Log.d(TAG, "onClick (SDM): Open channel");
                        mAntManager.openChannel(AntPlusManager.SDM_CHANNEL, false);
                    } else
                    {
                        // Close channel
                        Log.d(TAG, "onClick (SDM): Close channel");
                        mAntManager.closeChannel(AntPlusManager.SDM_CHANNEL);
                    }
                    break;
                case R.id.button_weight:
                    if (!mAntManager.isChannelOpen(AntPlusManager.WEIGHT_CHANNEL))
                    {
                        // Configure and open channel
                        Log.d(TAG, "onClick (Weight): Open channel");
                        mAntManager.openChannel(AntPlusManager.WEIGHT_CHANNEL, false);
                    } else
                    {
                        // Close channel
                        Log.d(TAG, "onClick (Weight): Close channel");
                        mAntManager.closeChannel(AntPlusManager.WEIGHT_CHANNEL);
                    }
                    break;
            }
        }
   }
   
   //Implementations of the AntPlusManager call backs

   @Override
   public void errorCallback()
   {
       setDisplay(false,false);
   }

   @Override
   public void notifyAntStateChanged()
   {
       drawWindow();
   }
   
   @Override
   public void notifyChannelStateChanged(byte channel)
   {
       drawChannel(channel);
   }
   
   @Override
   public void notifyChannelDataChanged(byte channel)
   {
       drawChannelData(channel);
   }
   
   public void playSound(Context context) throws IllegalArgumentException, SecurityException, IllegalStateException,
   IOException {
Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
MediaPlayer mMediaPlayer = new MediaPlayer();
mMediaPlayer.setDataSource(context, soundUri);
final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
   mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
  // mMediaPlayer.setLooping(true);
   mMediaPlayer.prepare();
   mMediaPlayer.start();
}
}

   public void makesound(){
	   Log.d(TAG,"beep");
	  
	   MediaPlayer mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.beep1);
	   
	   mediaPlayer.start();
   }
   
   public void updatebeep(int id,boolean max,int value){
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   SharedPreferences.Editor editor = settings.edit();
	      if(max){
	      editor.putInt("beepmax", value);
	      }else
	    	  editor.putInt("beepmin", value);
	      
	      editor.commit();
   }
   // do the full update of these values in the dialogs 90%
   public int getbeephrm(boolean max){
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	   if(max){
		   
		   int id=settings.getInt("beepmax", 250);
		   return id;
		   
	   }
	   int id=settings.getInt("beepmin", 0);
	   return id;
   }
   
   public void isitfirstrun(){
	   SharedPreferences settings = getSharedPreferences(PREFS_BPM_Name, 0);
	      SharedPreferences.Editor editor = settings.edit();
	    boolean k= settings.getBoolean("isitfirst",true);
		  if(k){
	      editor.putBoolean("isitfirst", false);
	      editor.putInt("keeplogging", 1);//1 bedeutet true
	      editor.commit();//0 false
		  }
   }
   
   /**
    * Exit application.
    */
   private void exitApplication()
   {
      Log.d(TAG, "exitApplication enter");
      
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      
      builder.setMessage(this.getResources().getString(R.string.Dialog_Exit_Check));
      builder.setCancelable(false);

      builder.setPositiveButton(this.getResources().getString(R.string.Dialog_Confirm), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                     Log.i(TAG, "exitApplication: Exit");
                     finish();
                 }
             });

      builder.setNegativeButton(this.getResources().getString(R.string.Dialog_Cancel), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                     Log.i(TAG, "exitApplication: Cancelled");
                     dialog.cancel();
                 }
             });

      AlertDialog exitDialog = builder.create();
      exitDialog.show();
   }
   
   Runnable startant=new Runnable() { 
       public void run() { 
       	try{ 
       		Log.d(TAG,"i");
       		switch (mAntManager.getHrmState()) {//crash
       		case CLOSED:
       			mAntManager.openChannel(AntPlusManager.HRM_CHANNEL, true);
       			Log.d(TAG,"onresume open channel");
       			mAntManager.requestReset();//TODO hier kommt error 
       			//E/ANTSocketInterface( 1486): ANTTxMessage: failed, Ant not enabled

       			break;
       		case OFFLINE:
       			mAntManager.openChannel(AntPlusManager.HRM_CHANNEL, true);
       			Log.d(TAG,"offline open channel");
       			mAntManager.requestReset();
       			break;
       		} }catch(Exception e){
       			Log.e(TAG,"antmanager "+e.getMessage());
       		}

       } 
  };
}

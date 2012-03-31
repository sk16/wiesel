package com.dsi.ant.antplusdemoj;

 import android.app.Service; 
 
 import com.dsi.ant.AntDefine;
 
import android.content.Context;
 import android.content.Intent;
 import android.app.PendingIntent;
 //import android.os.Binder; 
import android.os.Bundle;
 import android.os.Environment;
import android.os.IBinder;
import android.app.Notification;
//import android.widget.Toast;

import java.io.File;
//import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.OutputStreamWriter;
import java.util.Calendar;
 import java.util.Timer; 
import java.util.TimerTask;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import com.dsi.ant.antplusdemoj.android.apis.R;



public class bgservicewritefile extends Service {
	
	static final String ACTION_FOREGROUND = "com.dsi.ant.antplusdemoj.bgservicewritefile.FOREGROUND";
    static final String ACTION_BACKGROUND = "com.dsi.ant.antplusdemoj.bgservicewritefile.BACKGROUND";

    private static final Class<?>[] mSetForegroundSignature = new Class[] {
        boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[] {
        int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[] {
        boolean.class};

    private final String TAG="bgservice";
    private NotificationManager mNM;
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
	
	
	private Timer timer = new Timer();
	private ANTPlusDemoj ko=null;
	public IBinder onBind(Intent k) {
		return null;
		
	}
	
	
	void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            // Should not happen.
            Log.w("ApiDemos", "Unable to invoke method", e);
        } catch (IllegalAccessException e) {
            // Should not happen.
            Log.w("ApiDemos", "Unable to invoke method", e);
        }
    }

    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        // If we have the new startForeground API, then use it.
        if (mStartForeground != null) {
            mStartForegroundArgs[0] = Integer.valueOf(id);
            mStartForegroundArgs[1] = notification;
            invokeMethod(mStartForeground, mStartForegroundArgs);
            return;
        }

        // Fall back on the old API.
        mSetForegroundArgs[0] = Boolean.TRUE;
        invokeMethod(mSetForeground, mSetForegroundArgs);
        mNM.notify(id, notification);
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        // If we have the new stopForeground API, then use it.
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mStopForeground, mStopForegroundArgs);
            return;
        }

        // Fall back on the old API.  Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        mNM.cancel(id);
        mSetForegroundArgs[0] = Boolean.FALSE;
        invokeMethod(mSetForeground, mSetForegroundArgs);
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        try {
            mStartForeground = getClass().getMethod("startForeground",
                    mStartForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground",
                    mStopForegroundSignature);
            return;
        } catch (NoSuchMethodException e) {
            // Running on an older platform.
            mStartForeground = mStopForeground = null;
        }
        try {
            mSetForeground = getClass().getMethod("setForeground",
                    mSetForegroundSignature);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "OS doesn't have Service.startForeground OR Service.setForeground!");
        }
        
        
        startservice();
    }
	
	/*
	@Override
	public void onCreate() {

	super.onCreate();

	startservice();

	}*/
    
    
    @Override
    public void onDestroy() {
        // Make sure our notification is gone.
        stopForegroundCompat(R.string.foreground_service_started);
    }

    // This is the old onStart method that will be called on the pre-2.0
    // platform.  On 2.0 or later we override onStartCommand() so this
    // method will not be called.
    @Override
    public void onStart(Intent intent, int startId) {
        handleCommand(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
//TODO when one clicks the notifier make a 50% visible screen with the bpm data
    //and possibility to click on it to go to the main program
    void handleCommand(Intent intent) {
        if (ACTION_FOREGROUND.equals(intent.getAction())) {
        	 Log.d(TAG, "handlecmd ");
   	      
            // In this sample, we'll use the same text for the ticker and the expanded notification
            CharSequence text = getText(R.string.foreground_service_started);

            // Set the icon, scrolling text and timestamp
            Notification notification = new Notification(R.drawable.blackblue_pulse, text,
                    System.currentTimeMillis());

            // The PendingIntent to launch our activity if the user selects this notification
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, bgservicewritefile.class), 0); //Controller.class

            // Set the info for the views that show in the notification panel.
            notification.setLatestEventInfo(this, getText(R.string.local_service_label),
                           text, contentIntent);

            startForegroundCompat(R.string.foreground_service_started, notification);

        } else if (ACTION_BACKGROUND.equals(intent.getAction())) {
            stopForegroundCompat(R.string.foreground_service_started);
        }
    }
/*
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    */
	
	public void onStart(Intent intent){
		// ko=intent.getParcelableExtra("cantplusdemo");
		
	}
	
	public void onStartCommand(){
		 int myID = 1234;

		 Log.d(TAG, "onstartcmd");
	      
		//The intent to launch when the user clicks the expanded notification
		Intent intent = new Intent(this, com.dsi.ant.antplusdemoj.bgservicewritefile.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);

		//This constructor is deprecated. Use Notification.Builder instead
		Notification notice = new Notification(0, "Ticker text", System.currentTimeMillis());

		//This method is deprecated. Use Notification.Builder instead.
		notice.setLatestEventInfo(this, "Title text", "Content text", pendIntent);

		notice.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(myID, notice);
	}
	
	private void startservice() {

		//ANTPlusDemo.savebpm();
	//	 Toast.makeText(getBaseContext(), "service started",Toast.LENGTH_SHORT).show();
		timer.scheduleAtFixedRate( new TimerTask() {
//alarmmanager
		public void run() {

		//Do whatever you want to do every “INTERVAL”
		//	WriteSettings(getBaseContext(),"k");
		//	initant();
			try{
			//ko.savebpm();
			
			} catch(Exception e)
			{
			}
			
		}


		}, 60000, 60000);

		; 
		}
	
	private void initant() {
		Bundle b= new Bundle();
		AntPlusManager mAntManager = new AntPlusManager(null,b,null);
		
		//AntPlusManager mAntManager = (AntPlusManager) getLastNonConfigurationInstance();
	       if(mAntManager == null)
	       {
	           mAntManager = new AntPlusManager(null, b, null);
	           
	           // Always have ANT service connected
	           mAntManager.start();
	       }
	       else
	       {
	           mAntManager.resetCallbacks(null, null);
	       }
		
	}
	
	 private void WriteSettings(Context context, String data){
		   
		  
		   File sdCard = Environment.getExternalStorageDirectory(); 
	   	//   File dir = new File (sdcard.getAbsolutePath() + "/dir1/dir2"); 
		   File file = new File(sdCard.getAbsolutePath() + "/bpms"  + ".txt");
		   try {
			   //File file = new File(sdCard.getAbsolutePath() + "/bpm.dat");

			    // Create file if it does not exist
			    boolean success = file.createNewFile();
			    if (success) {
			        // File did not exist and was created
			    } else {
			        // File already exists
			    }
			    
			} catch (IOException e) {
				
			}
		   
	     //  FileOutputStream fOut = null;

	       //OutputStreamWriter osw = null;
	      /* try {
			String path = new java.io.File(".").getCanonicalPath();
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		String path2=getFilesDir().toString();*/
		FileWriter k=null;

	       try{

	        //fOut = openFileOutput(sdCard.getAbsolutePath() + "/bpm.dat", MODE_APPEND);      

	        //osw = new OutputStreamWriter(fOut);

	        //osw.write(data);

	        //osw.flush();
	    	    k=new FileWriter(file,true);
	           k.append(data);
	           

	           //osw.flush();
	           k.flush();

	        //Toast.makeText(context, path2,Toast.LENGTH_SHORT).show();//"Settings saved",Toast.LENGTH_SHORT).show();

	        }

	        catch (Exception e) {      

	        e.printStackTrace();

	    //    Toast.makeText(context, "Settings not saved",Toast.LENGTH_SHORT).show();

	        }

	        finally {

	           try {

	                   k.close();

	                 // fOut.close();

	                  } catch (IOException e) {

	                  e.printStackTrace();

	                  }

	        
	   }
	   }
	
	private void stopservice() {

		if (timer != null){

		timer.cancel();

		}


	}
	
}
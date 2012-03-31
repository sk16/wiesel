package com.dsi.ant.antplusdemoj;

//import com.dsi.ant.antplusdemoj.R;
//import com.dsi.ant.antplusdemoj.ANTPlusDemoj.MyMenu;
import com.jjoe64.graphview.GraphView;

import java.io.*;
import java.util.Calendar;

import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;

import android.app.Activity;
/*import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;*/
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.GraphViewDemo;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

/**
 * This Activity handles "editing" a note, where editing is responding to
 * {@link Intent#ACTION_VIEW} (request to view data), edit a note
 * {@link Intent#ACTION_EDIT}, create a note {@link Intent#ACTION_INSERT}, or
 * create a new note from the current contents of the clipboard {@link Intent#ACTION_PASTE}.
 *
 * NOTE: Notice that the provider operations in this Activity are taking place on the UI thread.
 * This is not a good practice. It is only done here to make the code more readable. A real
 * application should use the {@link android.content.AsyncQueryHandler}
 * or {@link android.os.AsyncTask} object to perform operations asynchronously on a separate thread.
 */


public class showGraphview extends Activity {
	 // For logging and debugging purposes
    private static final String TAG = "graphview";
    private String mOriginalContent;
    /** Pair to any device. */
    static final short WILDCARD = 0;

    // A label for the saved state of the activity
    private static final String ORIGINAL_CONTENT = "origContent";
    
    /** Shared bpm preferences data filename. */
    public static final String PREFS_BPM_NAME = "lastbpm";
    
    public void showGraphview(){
    	
    	
    }
    
    //private int[] abpm={0,0,0,0};
    //private int[] atime={0,0,0,0};
    private short[] abpm=new short[2880];
    private short[] atime=new short[2880];
    private int filelines=0;
    /**
     * This method is called by Android when the Activity is first started. From the incoming
     * Intent, it determines what kind of editing is desired, and then does it.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	//Log.e(TAG, "kok" );
    	super.onCreate(savedInstanceState);
       // Log.e(TAG, "kok" );
    	setContentView(R.layout.graph);
    	/*
        * If this Activity had stopped previously, its state was written the ORIGINAL_CONTENT
        * location in the saved Instance state. This gets the state.
        */
    	Log.d(TAG, "showgraphview" );

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
        drawbpmseries();
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {      
        boolean result = super.onCreateOptionsMenu(menu);
        /*
        if(mAntManager.isServiceConnected())
        {
            menu.add(Menu.NONE, MyMenu.MENU_PAIR_HRM.ordinal(), 0, this.getResources().getString(R.string.Menu_Wildcard_HRM));
            menu.add(Menu.NONE, MyMenu.MENU_PAIR_SDM.ordinal(), 1, this.getResources().getString(R.string.Menu_Wildcard_SDM));
            menu.add(Menu.NONE, MyMenu.MENU_PAIR_WEIGHT.ordinal(), 2, this.getResources().getString(R.string.Menu_Wildcard_Weight));
            menu.add(Menu.NONE,MyMenu.MENU_DRAW_GRAPH.ordinal(), 4, this.getResources().getString(R.string.Menu_Draw_Graph));

            SubMenu configMenu = menu.addSubMenu(Menu.NONE, MyMenu.MENU_CONFIG.ordinal(), 3, this.getResources().getString(R.string.Menu_Sensor_Config));
            configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_HRM.ordinal(), 0, this.getResources().getString(R.string.Menu_HRM));
            configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_SDM.ordinal(), 1, this.getResources().getString(R.string.Menu_SDM));
            configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_WGT.ordinal(), 2, this.getResources().getString(R.string.Menu_WGT));
            configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_PROXIMITY.ordinal(), 3, this.getResources().getString(R.string.Menu_Proximity));
            configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_BUFFER_THRESHOLD.ordinal(), 4, this.getResources().getString(R.string.Menu_Buffer_Threshold));
            configMenu.add(Menu.NONE,MyMenu.MENU_CONFIG_HRM_PERIOD.ordinal(), 5, "HRM Period");

            menu.add(Menu.NONE, MyMenu.MENU_REQUEST_CLAIM_INTERFACE.ordinal(), 5, this.getResources().getString(R.string.Menu_Claim_Interface));
        }
       
        menu.add(Menu.NONE, MyMenu.MENU_EXIT.ordinal(), 99, this.getResources().getString(R.string.Menu_Exit));
        */
        return result;
    }
    private void drawbpmseries(){
    	
    	readbpmfile();
      //  SharedPreferences settings = getSharedPreferences(PREFS_BPM_NAME, 0);
        // abpm[0]= settings.getInt("bpm0", 60);
         Log.d(TAG, "kokk" );
  	   GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {  
  	       /*  new GraphViewData(1, 2.0d)  
  	         , new GraphViewData(2, 1.5d)  
  	         , new GraphViewData(3, 2.5d)  
  	         , new GraphViewData(4, 1.0d)  */
  			   new GraphViewData(0, 4)  
  		         , new GraphViewData(2, 5)  
  		         , new GraphViewData(1, 2)  
  		         , new GraphViewData(3, abpm[0])  
  	   });  
  	 GraphViewData[] data = new GraphViewData[filelines];  
 	 double v=0;  
 	// short hour=0;short min=0;
 	 for (int i=0; i<(filelines); i++) {  
 	    v += 0.2;  
 	  //  hour= (short)((int)atime[i]/100);
 	  //  min=(short)((int)atime[i]-hour*100);
 	 //   data[i] = new GraphViewData((hour*60+min), abpm[i]);
 	  // data[i] = new GraphViewData(i, abpm[i]);//working
 	   data[i] = new GraphViewData(i, abpm[i],atime[i]);
 	 //  Log.d(TAG,(new Integer((int)abpm[i])).toString() + " "+(new Integer(hour*60+min)).toString());
 	 }  
  	     
  	  GraphView graphView = new LineGraphView(  
   	         this // context  
   	         , null // heading  
   	         ,true
   	   ) {  
  		
  		   @Override  
  		   protected String formatLabel(double value, boolean isValueX) {  
  		      if (!isValueX) {  
  		         // convert unix time to human time  
  		         return (new Integer((int)value)).toString();  
  		      } else {
  		    	  String minn="";
  		    	  if(atime.length>(int)value&&(int)value>-1){
  		    	short hour= (short)((int)atime[(int)value]/100);
  		 	    short min=(short)((int)atime[(int)value]-hour*100);
  		 	 if (min<10)minn="0";
  		    	 // return super.formatLabel(value, isValueX); // let the y-value be normal-formatted
  		 	    return (((new Integer((int)hour)).toString())+ ":"+minn+((new Integer((int)min)).toString()));
  		    	  } else { 
  		    		Log.d(TAG,(new Integer((int)value)).toString()+" " +(new Integer(atime.length)).toString() );
  		    		  return "00:00";
  		    	 // Log.d(TAG,(new Integer((int)value)).toString()+" "  );
  		    	//Log.d(TAG,(new Double(value)).toString()+" "  );
  		    	  }
  		      }
  		   }  
  		};  
   	 //  graphView.addSeries(exampleSeries); // data  
   	graphView.addSeries(new GraphViewSeries(data));
   	   //TODO 10 oder 11 min?
   	//  graphView.setHorizontalLabels(new String[] {"10 min","5 min",  "now"});
   	int minlines=60;
   	if(filelines<61){minlines=filelines;
   	
   	graphView.setViewPort(0,minlines-1);
   	}else{
   	// graphView.setViewPort((hour*60+min)-minlines, minlines);//size später auf 120
   	//TODO if filelines==1 toast message warten
   	graphView.setViewPort(filelines-minlines-1,minlines);}
   	graphView.setScrollable(true);  
   	// optional - activate scaling / zooming  
   	graphView.setScalable(true);  
   	//graphView.setDrawBackground(true);
  	     
  	   LinearLayout layout = (LinearLayout) findViewById(R.id.graph_layout);  
  	   //crash hier vllt?
  	 //  LinearLayout layout = (LinearLayout) findViewById(0);
  	   layout.addView(graphView);  
    }
    
    private void readbpmfile()
    {
    	 Calendar cal = Calendar.getInstance();
  	   
    	   
    	   int month = cal.get(Calendar.MONTH) + 1;
    	   
    	   int dom = cal.get(Calendar.DAY_OF_MONTH);
    	   
    	 //  int doy = cal.get(Calendar.DAY_OF_YEAR);
    	  
    	  String sdoy="";
    	  String sdom="";
    	   if (dom<10)sdoy="0";
    	   if (month<10)sdom="0";
    	   
    	 
 	   Log.d(TAG, "try to read file");
    	//   File dir = new File (sdcard.getAbsolutePath() + "/dir1/dir2"); 
 	  //  file = new File(sdCard.getAbsolutePath() + "/documents/bpm" +sdoy +(new Integer(month)).toString()+ sdom + (new Integer(dom)).toString() + ".txt");
 	   
    try{
    // Open the file that is the first 
    // command line parameter
    	File sdCard = Environment.getExternalStorageDirectory();
    FileInputStream fstream = new FileInputStream(sdCard.getAbsolutePath() + "/bpm/bpm" +sdom +(new Integer(month)).toString()+ sdoy + (new Integer(dom)).toString() + ".txt");
    // Get the object of DataInputStream
    DataInputStream in = new DataInputStream(fstream);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String strLine;
    //Read File Line By Line
    int i=0;
    while ((strLine = br.readLine()) != null)   {
    // Print the content on the console
   // Log.d(TAG,strLine);
    atime[i]=Short.parseShort(strLine.substring(0, 4));
    int end=8;
    if(strLine.length()<8)end=strLine.length();
    
    abpm[i]=Short.parseShort(strLine.substring(5,end).trim());//TODO error null
    
    i++;
    }
    filelines=i;
    Log.d(TAG,(new Integer(abpm[0])).toString()+ " " +(new Integer(i)).toString() );
    //Close the input stream
    in.close();
      }catch (Exception e){//Catch exception if any
    System.err.println("Error: " + e.getMessage());
    Toast.makeText(getBaseContext(), "no heart rate data available",Toast.LENGTH_SHORT).show();
    }
    }

}

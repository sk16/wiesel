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

import com.dsi.ant.AntDefine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PairingDialog extends Dialog
{
   public static final int MIN_ID = 0;
   public static final int HRM_ID = 0;
   public static final int SDM_ID = 1;
   public static final int PROX_ID = 2;
   public static final int WGT_ID = 3;
   public static final int BUFF_ID = 4;
   public static final int MAX_ID = 8;//4
   public static final int PERIO_D=5;
   public static final int WRITE_PERIOD_ID=6;
   public static final int BEEPMAX=7;
   public static final int BEEPMIN=8;
   
   public interface PairingListener
   {
      public void updateID(int id, short deviceNumber);
      public void updateThreshold(int id, byte proxThreshold);
      public void updatehrmwriteperiod(int id,int b);
      public void updatebeepmax(int id,int max);
      public void updatebeepmin(int id,int max);
      
   }

   
   public int getId()
   {
      return mId;
   }

   public void setId(int id)
   {
      this.mId = id;
   }

   public short getDeviceNumber()
   {
      return mDeviceNumber;
   }

   public void setDeviceNumber(short deviceNumber)
   {
      this.mDeviceNumber = deviceNumber;
   }
   
   public byte getProximityThreshold()
   {
      return mProximityThreshold;
   }
   
   public void setProximityThreshold(byte proximityThreshold)
   {
      this.mProximityThreshold = proximityThreshold;
   }

   public String getHint()
   {
      return mHint;
   }
   
   public short gethrmperiod(){
	   return mHrmperiod;
   }
   
   public void setbeepmin(short s){
	   this.mBeepmin=s;
   }
   
   public void setbeepmax(short s){
	   this.mBeepmax=s;
   }
   
   public short getbeepmax(){
	   return mBeepmax;
   }
   public short getbeepmin(){
	   return mBeepmin;
   }
   
   public short getwriteperiod(){
	   return mWriteperiod;
   }
   public void sethrmperiod(short s){
	   this.mHrmperiod=s;
	   
   }
   
   public void setwriteperiod(short s){
	   
	   this.mWriteperiod=s;
   }
   
   public void setHint(String hint)
   {
      if(null == hint)
      {
         this.mHint = "";
      }
      else
      {   
         this.mHint = hint;
      }
   }

   private int mId;
   private short mDeviceNumber;
   private byte mProximityThreshold;
   private String mHint;
   private PairingListener mPairingListener;
   private short mWriteperiod;
   private short mHrmperiod;
   private short mBeepmax; 
   private short mBeepmin;
   
   public PairingDialog(Context context, int id, short deviceNumber, String hint, PairingListener pairingListener)
   {
      super(context);
      mId = id;
      mDeviceNumber = deviceNumber;
      mWriteperiod=deviceNumber;
      setHint(hint);
      mPairingListener = pairingListener;
   }
   public PairingDialog(Context context, int id, int i, String hint, PairingListener pairingListener)
   {
      super(context);
      mId = id;
      //mDeviceNumber = deviceNumber;
      mWriteperiod=(short)i;
      mBeepmax=(short)i;
      mBeepmin=(short)i;
      setHint(hint);
      mPairingListener = pairingListener;
   }
   
   public PairingDialog(Context context, int id, byte proximityThreshold, String hint, PairingListener pairingListener)
   {
      super(context);
      mId = id;
      mProximityThreshold = proximityThreshold;
      setHint(hint);
      mPairingListener = pairingListener;
   }
   
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      
      if(mId >= MIN_ID && mId <= MAX_ID)
      {
         setContentView(R.layout.pairing_dialog);
         
         if(mId == HRM_ID)
            setTitle(getContext().getResources().getString(R.string.Dialog_Pair_HRM));
         else if(mId == SDM_ID)
            setTitle(getContext().getResources().getString(R.string.Dialog_Pair_SDM));
         else if(mId == WGT_ID)
            setTitle(getContext().getResources().getString(R.string.Dialog_Pair_WGT));
         else if(mId == PROX_ID)
            setTitle(getContext().getResources().getString(R.string.Dialog_Proximity));
         else if(mId == BUFF_ID)
             setTitle(getContext().getResources().getString(R.string.Dialog_Buffer_Threshold));
         else if(mId == WRITE_PERIOD_ID)
        	 setTitle(getContext().getResources().getString(R.string.Dialog_write_period));
         else if(mId == BEEPMAX)
        	 setTitle(getContext().getResources().getString(R.string.Dialog_beep_max));
         else if(mId == BEEPMIN)
        	 setTitle(getContext().getResources().getString(R.string.Dialog_beep_min));
         
        	 
         TextView descr = (TextView) findViewById(R.id.dialog_text);
         EditText input = (EditText) findViewById(R.id.dialog_input);
         Button buttonOK = (Button) findViewById(R.id.dialog_button);
         
         if(mId == PROX_ID)
         {
            descr.setText(getContext().getResources().getString(R.string.Dialog_Prox_Text));
            input.setText("" + (mProximityThreshold & 0xFF));
         }
         else if(mId == BUFF_ID)
         {
            descr.setText(getContext().getResources().getString(R.string.Dialog_Buffer_Threshold_Text));
            input.setText("" + (int) mDeviceNumber);
         }
         else if(mId == WRITE_PERIOD_ID)
         {
        	 descr.setText(getContext().getResources().getString(R.string.Dialog_write_period_text));
        	 input.setText("" + (mWriteperiod));
        	 buttonOK.setText(getContext().getResources().getString(R.string.Dialog_Confirm));
        	 //Log.d(TAG,"p");
         }
         else if(mId == BEEPMAX)
         {
        	 descr.setText(getContext().getResources().getString(R.string.Dialog_beep_max_text));
        	 input.setText("" + (mBeepmax));
        	 buttonOK.setText(getContext().getResources().getString(R.string.Dialog_Confirm));
        	 //Log.d(TAG,"p");
         }
         else if(mId == BEEPMIN)
         {
        	 descr.setText(getContext().getResources().getString(R.string.Dialog_beep_min_text));
        	 input.setText("" + (mBeepmin));
        	 buttonOK.setText(getContext().getResources().getString(R.string.Dialog_Confirm));
        	 //Log.d(TAG,"p");
         }
         else
         {
            descr.setText(getContext().getResources().getString(R.string.Dialog_Pair));
            input.setText("" + (int) mDeviceNumber);
         }
         input.setHint(mHint);
         
         
         buttonOK.setOnClickListener(new OKListener());
      }      
   }
    
   @Override 
   protected void onStart()
   {
      super.onStart();
      EditText input = (EditText) findViewById(R.id.dialog_input);
      if(mId == PROX_ID)
      {
         input.setText(String.valueOf(mProximityThreshold & 0xFF));
      }
      else if(mId == WRITE_PERIOD_ID)
      {
     	 input.setText("" + (mWriteperiod));
      }
      else if(mId == BEEPMAX)
      {
     	 input.setText("" + (mBeepmax));
      }
      else if(mId == BEEPMIN)
      {
     	 input.setText("" + (mBeepmin));
      }
      else
      {
         input.setText(String.valueOf(mDeviceNumber & 0xFFFF));
      }
      input.selectAll();
   }
   
   private class OKListener implements android.view.View.OnClickListener
   {  
      private void resetInput()
      {
          EditText input = (EditText) findViewById(R.id.dialog_input);
          if(PROX_ID == mId)
          {
              input.setText(String.valueOf(mProximityThreshold & 0xFF));
          }
          else if(mId == WRITE_PERIOD_ID)
          { 
         	 input.setText("" + (mWriteperiod));
          }
          else if(mId == BEEPMAX)
          {
         	 input.setText("" + (mBeepmax));
          }
          else if(mId == BEEPMIN)
          {
         	 input.setText("" + (mBeepmin));
          }
          else
          {
              input.setText(String.valueOf(mDeviceNumber & 0xFFFF));
          }
      }
       
      public void onClick(View v)
      {
         try
         {
             EditText input = (EditText) findViewById(R.id.dialog_input);
             Integer tempInt = Integer.parseInt(input.getText().toString());         
             if(tempInt != null && (mId == SDM_ID || mId == HRM_ID || mId == WGT_ID) && tempInt >= AntDefine.MIN_DEVICE_ID && tempInt <= (AntDefine.MAX_DEVICE_ID & 0xFFFF))
             {
                 int temp = tempInt.intValue();
                 mDeviceNumber = (short) (temp & 0xFFFF);
                 mPairingListener.updateID(mId, mDeviceNumber);  // Let main activity know about update
                 PairingDialog.this.dismiss();
             }
             else if(tempInt != null && mId == PROX_ID && tempInt >= AntDefine.MIN_BIN && tempInt <= AntDefine.MAX_BIN)
             {
                 int temp = tempInt.intValue();
                 mProximityThreshold = (byte) (temp & 0xFF);
                 mPairingListener.updateThreshold(mId, mProximityThreshold);
                 PairingDialog.this.dismiss();
             }//TODO for debug set this to 1                               ----      
             else if(tempInt != null && mId == WRITE_PERIOD_ID && tempInt >= 0 && tempInt <= 60)
             {
                 int temp = tempInt.intValue();
                 //mProximityThreshold = (byte) (temp & 0xFF);
                 mPairingListener.updatehrmwriteperiod(mId, temp);
                 PairingDialog.this.dismiss();
             }
             else if(tempInt != null && mId == BEEPMAX && tempInt >= 1 && tempInt <= 300)
             {
                 int temp = tempInt.intValue();
                 //mProximityThreshold = (byte) (temp & 0xFF);
                 mPairingListener.updatebeepmax(mId, temp);
                 PairingDialog.this.dismiss();
             }
             else if(tempInt != null && mId == BEEPMIN && tempInt >= 0 && tempInt <= 250)
             {
                 int temp = tempInt.intValue();
                 //mProximityThreshold = (byte) (temp & 0xFF);
                 mPairingListener.updatebeepmin(mId, temp);
                 PairingDialog.this.dismiss();
             }
             else if(tempInt != null && mId == BUFF_ID && tempInt >= AntDefine.MIN_BUFFER_THRESHOLD && tempInt <= AntDefine.MAX_BUFFER_THRESHOLD)
             {
                 int temp = tempInt.intValue();
                 mDeviceNumber = (short) (temp & 0xFFFF);
                 mPairingListener.updateID(mId, mDeviceNumber);
                 PairingDialog.this.dismiss();
             }
             else
             {
                 resetInput();           
             }
         }
         catch(NumberFormatException e)
         {
             resetInput();
         }
      }
   }
}

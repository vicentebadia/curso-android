package com.example.gui_experiments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	   LinearLayout screenLayout = null;
	 
	   public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_main);
	      screenLayout = (LinearLayout) findViewById(R.id.layoutMain);
	      Log.v("MYLOG", "En onCreate():" + Thread.currentThread().getId());
	   }
	 
	   public void redOnClick(View v) {
	      Log.v("MYLOG", "En redOnClick():"+Thread.currentThread().getId());
	      screenLayout.setBackgroundColor(Color.RED);
	   }
	   public void greenOnClick(View v) {
	      Log.v("MYLOG", "En greenOnClick():"+Thread.currentThread().getId());
	      screenLayout.setBackgroundColor(Color.GREEN);
	   }
	 
	   public void blueOnClick(View v) {
	      Log.v("MYLOG", "En blueOnClick():"+Thread.currentThread().getId());
	      screenLayout.setBackgroundColor(Color.BLUE);
	   }
	}


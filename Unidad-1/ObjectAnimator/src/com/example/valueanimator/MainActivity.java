package com.example.valueanimator;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class MainActivity extends Activity {
       
	private TextView textView;
 
       @Override
       protected void onCreate(Bundle savedInstanceState) {
    
    	   super.onCreate(savedInstanceState);
           
    	   setContentView(R.layout.activity_main);
           textView = (TextView) findViewById(R.id.text_view);
           
           ObjectAnimator animacion = ObjectAnimator.ofFloat(textView, "textSize", 10, 40);
           animacion.setDuration(1000);
           animacion.setInterpolator(new DecelerateInterpolator());
           animacion.setRepeatCount(4); // La animación se realizará 5 veces (se repite 4)
           animacion.setRepeatMode(ValueAnimator.REVERSE);


           animacion.start();
       }
 
 
}


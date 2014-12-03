package com.example.valueanimator;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class MainActivity extends Activity 
                          implements ValueAnimator.AnimatorUpdateListener {
       
	private TextView textView;
 
       @Override
       protected void onCreate(Bundle savedInstanceState) {
    
    	   super.onCreate(savedInstanceState);
           
    	   setContentView(R.layout.activity_main);
           textView = (TextView) findViewById(R.id.text_view);
           
           ValueAnimator animacion = ValueAnimator.ofFloat(10, 40);
           animacion.setDuration(1000);
           animacion.setInterpolator(new DecelerateInterpolator());
           animacion.setRepeatCount(4); // La animación se realizará 5 veces (se repite 4)
           animacion.setRepeatMode(ValueAnimator.REVERSE);
           animacion.addUpdateListener(this);
           animacion.start();
       }
 
       @Override
       public void onAnimationUpdate(ValueAnimator animacion) { // Cada vez que el ValueAnimator actualice el valor venimos aquí y cambiamos el tamaño del textView        
    	   float value =((Float) (animacion.getAnimatedValue())).floatValue();  
    	   textView.setTextSize(value);
       }
}


package com.bluetooth01;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Bluetooth01 extends Activity {
	
	private static final int REQUEST_ENABLE_BT = 0;
	private static final int REQUEST_DISCOVERABLE_BT = 0;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth01);
    	
    	final TextView out = (TextView) findViewById(R.id.out);
    	final Button button1 = (Button) findViewById(R.id.button1);
    	final Button button2 = (Button) findViewById(R.id.button2);
    	final Button button3 = (Button) findViewById(R.id.button3);
    	final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	       if (mBluetoothAdapter == null) {
    	             out.append("Dispositivo BT no soportado!!!");
    	                          
    	       }
        // Bot�n ACTIVAR BT
        button1.setOnClickListener(new View.OnClickListener() {
        	   public void onClick(View v) {
        	       if (!mBluetoothAdapter.isEnabled()) {
        	         Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	         startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	         }
        	   }
        });
        
        // Bot�n BT DETECTABLE
        button2.setOnClickListener(new View.OnClickListener() {
        	   @Override
        	   public void onClick(View arg0) {
        	      if (!mBluetoothAdapter.isDiscovering()) {
        	       // out.append("MAKING YOUR DEVICE DISCOVERABLE");
        	       Context context = getApplicationContext();
        	       CharSequence text = "MAKING YOUR DEVICE DISCOVERABLE";
        	       int duration = Toast.LENGTH_SHORT;
        	       Toast toast = Toast.makeText(context, text, duration);
        	       toast.show();
        	       Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        	       startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BT);
        	       }
        	   }
        	});

        // Bot�n DESACTIVAR BT
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                  mBluetoothAdapter.disable();
                  // out.append("TURN_OFF BLUETOOTH");
                  Context context = getApplicationContext();
                   CharSequence text = "TURNING_OFF BLUETOOTH";
                  int duration = Toast.LENGTH_LONG;
                  Toast toast = Toast.makeText(context, text, 15);
                  toast.show();
            }
     });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bluetooth01, menu);
        return true;
    }
    
}

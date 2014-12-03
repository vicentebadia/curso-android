package com.example.audiolibrosv1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
  
		super.onCreate(savedInstanceState);
  
		setContentView(R.layout.activity_main);
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
		
		Aplicacion app = (Aplicacion) getApplication();
		
		gridview.setAdapter(app.getAdaptador());
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Toast.makeText(MainActivity.this, "Seleccionado el elemento: " + position, Toast.LENGTH_SHORT).show();
			}
		});
	}
}


package com.example.audiolibrosv3;

import android.app.Activity;
import android.os.Bundle;

import com.example.audiolibros.fragments.PreferenciasFragment;

public class PreferenciasActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	 	getFragmentManager().beginTransaction()
	                        .replace(android.R.id.content, new PreferenciasFragment()).commit();
	 	}

}


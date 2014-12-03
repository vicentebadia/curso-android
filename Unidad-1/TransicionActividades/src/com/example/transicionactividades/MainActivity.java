package com.example.transicionactividades;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sepulsa(View view){
		   Intent i = new Intent(this, SegundaActivity.class);
		   startActivity(i);
		   overridePendingTransition(R.anim.salida_izquierda,R.anim.entrada_derecha); // <-- En los apuntes de clase estaba al revés
		}
	
	// Con el método overridePendingTransition especificamos una animación de transición
	// explícita a realizar a continuación. Primero se indica la animación que se aplica a la vista 
	// de la actividad entrante y luego a la saliente.
	
	
	
	/* SI ESTUVIERAMOS EN API 16 O POSTERIOR, PODRÍAMOS UTILIZAR EL METODO SIGUIENTE
	public void sepulsa(View view){
		   Intent i = new Intent(this, SegundaActivity.class);
		   ActivityOptions opts = ActivityOptions.makeCustomAnimation(this, R.anim.entrada_derecha, R.anim.salida_izquierda);
		   startActivity(i, opts.toBundle());
		}
	 */
	
	
	public void sepulsaTercera(View view){
		Intent i = new Intent(this, TerceraActivity.class);
		startActivity(i);		
	}
	
	public void sepulsaCuarta(View view){
		Intent i = new Intent(this, CuartaActivity.class);
		startActivity(i);		
	}

}

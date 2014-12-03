package com.example.audiolibrosv2;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.audiolibros.fragments.DetalleFragment;
import com.example.audiolibros.fragments.SelectorFragment;

public class MainActivity extends FragmentActivity {
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_main);	   
	   
	   if (findViewById(R.id.contenedor_pequeno) != null) { // Si contenedor_pequeno existe es porque estamos en un movil
		   SelectorFragment primerFragment = new SelectorFragment();
		   getSupportFragmentManager().beginTransaction() // Por tanto añadimos un fragment con la lista de libros
		                              .add(R.id.contenedor_pequeno, primerFragment).commit();
		}

	}
	   
	   
		  
	public void mostrarDetalle(int id) {
		
	       DetalleFragment detalleFragment = (DetalleFragment) getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);
		       
	       if (detalleFragment != null) { // Si existe detalleFragment (es que estamos en una tablet)
	             detalleFragment.ponInfoLibro(id); // Por tanto ponemos la info del libro en el panel correspondiente
	       } else { // si no (es que estamos en un movil)
	           DetalleFragment nuevoFragment = new DetalleFragment();
	           Bundle args = new Bundle();
	           args.putInt(DetalleFragment.ARG_ID_LIBRO, id); // Pasamos la ID del libro
	           nuevoFragment.setArguments(args);
	           FragmentTransaction transaccion = getSupportFragmentManager().beginTransaction();
	           transaccion.replace(R.id.contenedor_pequeno, nuevoFragment); // Cambiamos el fragment del listado por el fragment del detalle del libro
	           transaccion.addToBackStack(null); // Al pulsar atrás no cerraremos la app sino que volveremos a la selección anterior
	           transaccion.commit();
	       }
	       
	       // Almacenamos en SharedPreferences el último libro seleccionado
	       SharedPreferences pref = getSharedPreferences("com.example.audiolibros_internal", MODE_PRIVATE);
	       SharedPreferences.Editor editor = pref.edit();
	       editor.putInt("ultimo", id);
	       editor.commit();

	} 
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	   getMenuInflater().inflate(R.menu.main, menu);
	   return super.onCreateOptionsMenu(menu);
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   switch (item.getItemId()) {
	   		
	   case R.id.menu_preferencias:
	   		Toast.makeText(this, "Preferencias", Toast.LENGTH_LONG).show();
	   		break;
	   case R.id.menu_ultimo:
	        irUltimoVisitado ();
	        break;
	   case R.id.menu_buscar:
		   	break;
	   case R.id.menu_acerca:
		   	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		   	builder.setMessage("Mensaje de Acerca De");
		   	builder.setPositiveButton(android.R.string.ok, null);
		   	builder.create().show(); // <-- Se podría haber hecho todo en una línea
		   	break;
	   }
	   return false; // Devolvemos false porque no queremos que se muestre de forma inmediata
	}
	
	// Obitene de las SharedPreferences el último libro que se estaba viendo 
	public void irUltimoVisitado() {
			
		SharedPreferences pref = getSharedPreferences("com.example.audiolibros_internal", MODE_PRIVATE);

		int id = pref.getInt("ultimo", -1);
		if (id >= 0) {
			mostrarDetalle(id);
		} else {
			Toast.makeText(this, "Sin última vista", Toast.LENGTH_LONG).show();
		}
	}


	   
}



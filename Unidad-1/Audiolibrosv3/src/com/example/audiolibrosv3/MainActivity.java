package com.example.audiolibrosv3;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.audiolibros.fragments.DetalleFragment;
import com.example.audiolibros.fragments.PreferenciasFragment;
import com.example.audiolibros.fragments.SelectorFragment;

public class MainActivity extends Activity implements TabListener { // Como la versión mínima es 11 (Android 3.0) Activity ya permite utilizar Fragments sin tener que usar una FragmentActivity
	   
	private AdaptadorLibrosFiltro adaptador;
	
	// Navigation Drawer
	private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView listaMenu; // Opciones del Navigation Drawer
    
    

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);	    
		setContentView(R.layout.activity_main);	   
	   
		// Adaptador       
	    adaptador = ((Aplicacion) getApplicationContext()).getAdaptador();
	    
	   int idContenedor = (findViewById(R.id.contenedor_pequeno) != null) ? R.id.contenedor_pequeno : R.id.contenedor_izquierdo; 
	   // Explicación: Se comprueba si R.id.contendor_pequeño existe. Si es así es porque estamos en un móvil. En ese caso le se asigna a idContendor ese valor.
	   //              En caso contrario (entonces estamos en una tablet), le asignamos a idContendor el LinearLayout con id contenedor_izquierdo correspondiente al layout de la tablet.
	   SelectorFragment primerFragment = new SelectorFragment();	
	   getFragmentManager().beginTransaction() 
       					   .add(idContenedor, primerFragment).commit();
	   
	   // Pestañas
       ActionBar actionBar = getActionBar();         
       actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
       actionBar.addTab(actionBar.newTab()
                        		 .setText("Todos")
                        		 .setIcon(android.R.drawable.ic_menu_view)
                        		 .setTabListener(this));
       actionBar.addTab(actionBar.newTab()
                        		 .setText("Nuevos")
                        		 .setIcon(android.R.drawable.ic_menu_agenda)
                        		 .setTabListener(this));
       actionBar.addTab(actionBar.newTab()
                        		 .setText("Leidos")
                        		 .setIcon(android.R.drawable.ic_menu_my_calendar)
                        		 .setTabListener(this));  

       // Navigation Drawer
       drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
       drawerToggle = new ActionBarDrawerToggle(
               this,                   	// Actividad que nos contiene
               drawerLayout,           	// DrawerLayout 
               R.drawable.ic_drawer, 	// Icono del Nav. Drawer
               R.string.drawer_open,    // Descripcion abrimos Nav. Drawer
               R.string.drawer_close)   // Descripcion cierre Nav. Drawer
       {
           public void onDrawerClosed(View view) {
                getActionBar().setTitle(getTitle()); //Titulo App
              }

              public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Seleccione opción");
             }
       };
    

       listaMenu = (ListView) findViewById(R.id.left_drawer);
       listaMenu.setAdapter(new ArrayAdapter<String>(this,  R.layout.drawer_list_item, Libro.G_ARRAY));
       listaMenu.setItemChecked(0, true);
       listaMenu.setOnItemClickListener(new ListView.OnItemClickListener() {
          @Override   
          public void onItemClick(AdapterView parent, View vista, int pos, long id) {
             if (pos == 0) {
                 adaptador.setGenero("");
             } else {
                  adaptador.setGenero(Libro.G_ARRAY[pos]);
             }
             adaptador.notifyDataSetChanged();
             drawerLayout.closeDrawer(listaMenu);
          }
       });
       
       drawerLayout.setDrawerListener(drawerToggle);
       getActionBar().setDisplayHomeAsUpEnabled(true);
	}
    

       
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
  
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }




	   
		  
	public void mostrarDetalle(int id) {
		
	       DetalleFragment detalleFragment = (DetalleFragment) getFragmentManager().findFragmentById(R.id.detalle_fragment);
		       
	       if (detalleFragment != null) { // Si existe detalleFragment (es que estamos en una tablet)
	               detalleFragment.ponInfoLibro(id); // Por tanto ponemos la info del libro en el panel correspondiente
	       } else { // si no (es que estamos en un movil)
		           DetalleFragment nuevoFragment = new DetalleFragment();
		           Bundle args = new Bundle();
		           args.putInt(DetalleFragment.ARG_ID_LIBRO, id); // Pasamos la ID del libro
		           nuevoFragment.setArguments(args);
		           FragmentTransaction transaccion = getFragmentManager().beginTransaction();
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
	   
	   SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	   SearchView searchView = (SearchView) menu.findItem(R.id.menu_buscar).getActionView();
	   searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

	   
	   return super.onCreateOptionsMenu(menu);
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   
		// Para que el Drawer se pueda sacar pulsando sobre el icono de Drawer además de arrastrando de izquierda a derecha
		if (drawerToggle.onOptionsItemSelected(item)) {
	          return true;
	    }


		switch (item.getItemId()) {
	   		
	   case R.id.menu_preferencias:
	   		//Intent i = new Intent(this, PreferenciasActivity.class);
	   		//startActivity(i);
		    abrePreferencias();
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

	// Muestra las preferencias de la aplicación bien en una nueva pantalla (cambio de fragmento) o en el panel de la izquierda si estamos en una tablet
	public void abrePreferencias() {
	       
		int idContenedor = (findViewById(R.id.contenedor_pequeno) != null) ?  R.id.contenedor_pequeno    : R.id.contenedor_izquierdo;
	    PreferenciasFragment prefFragment = new PreferenciasFragment();
	    
	    getFragmentManager().beginTransaction()
	                        .replace(idContenedor, prefFragment)
	                        .addToBackStack(null)
	                        .commit();
	}

	// Método para filtrar los libros cuando se hace una busqueda desde la searchView
	protected void onNewIntent(Intent intent) {
		   
		if (intent.getAction() != null) {
		      if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
		            adaptador.setBusqueda(intent.getStringExtra(SearchManager.QUERY));
		            adaptador.notifyDataSetChanged();
		      }
		   }
		}

	
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
        switch (tab.getPosition()) {
        case 0: //Todos
               adaptador.setNovedad(false);
               adaptador.setLeido(false);
               break;
        case 1: //Nuevos
               adaptador.setNovedad(true);
               adaptador.setLeido(false);
               break;
        case 2: //Leidos
               adaptador.setNovedad(false);
               adaptador.setLeido(true);
               break;
        }
        adaptador.notifyDataSetChanged();
  }

  @Override
  public void onTabReselected(Tab tab, FragmentTransaction ft) {
  }

  @Override
  public void onTabUnselected(Tab tab, FragmentTransaction ft) {
  }

	   
}



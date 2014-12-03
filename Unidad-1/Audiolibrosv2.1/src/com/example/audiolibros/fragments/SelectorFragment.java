package com.example.audiolibros.fragments;

import java.util.Vector;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.example.audiolibrosv2.AdaptadorLibros;
import com.example.audiolibrosv2.Aplicacion;
import com.example.audiolibrosv2.Libro;
import com.example.audiolibrosv2.MainActivity;
import com.example.audiolibrosv2.R;

public class SelectorFragment extends Fragment implements AnimatorListener {
	  private Activity actividad;
	  private GridView gridview;
	  private AdaptadorLibros adaptador;
	  private Vector<Libro> vectorLibros;
	  
	  @Override
	  public void onAttach(Activity actividad) {
	    super.onAttach(actividad);
	    this.actividad = actividad;
	    Aplicacion app = (Aplicacion) actividad.getApplication();
	    adaptador = app.getAdaptador();
	    vectorLibros = app.getVectorLibros();
	  }
	 
	  @Override
	  public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
	    
		View vista = inflador.inflate(R.layout.fragment_selector, contenedor, false);
	    gridview = (GridView) vista.findViewById(R.id.gridview);
	    gridview.setAdapter(adaptador);
	    gridview.setOnItemClickListener(new OnItemClickListener(){
	        public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
	                ((MainActivity) actividad).mostrarDetalle((int) id);
	        }
	    });
	    
	    // Agregamos un menú contextual al GridView
	    gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
	    	
	    	public boolean onItemLongClick(AdapterView<?> padre, final View v,final int posicion, final long id) { // Si se hace una pulsación larga
	    	             
	    	AlertDialog.Builder menu = new AlertDialog.Builder(actividad); // Creamos un alert dialog...
	    	
	    	CharSequence[] opciones = { "Compartir", "Borrar ", "Insertar" }; // ... con tres opciones
	    	menu.setItems(opciones, new DialogInterface.OnClickListener() { // y un listener sobre cada una de ellas
	    	         public void onClick(DialogInterface dialog, int opcion) {
	    	
	    	        	  switch (opcion) {
	    	        	  
	    	                     case 0: //Compartir
	    	                    	 Libro libro = vectorLibros.elementAt((int) id);            
	    	                    	 Intent i = new Intent(Intent.ACTION_SEND); // Intent para compartir
		    	                     i.setType("text/plain"); 
		    	                     i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo);
		    	                     i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio); 
		    	                     startActivity(Intent.createChooser(i, "Compartir"));
		    	                     break;
		    	                     
	    	                     case 1: // Borrar
	    	                    	   Animator anim = AnimatorInflater.loadAnimator(actividad, R.animator.menguar);
	    	                    	   anim.addListener(SelectorFragment.this);
	    	                    	   anim.setTarget(v);
	    	                    	   anim.start();
	    	                    	   vectorLibros.remove((int) id);
	    	                    	   //adaptador.notifyDataSetChanged();
	    	                    	  break;

		    	                     
	    	                     case 2: //Insertar
	    	                    	 Animation animInsertar=AnimationUtils.loadAnimation(actividad, R.anim.resaltar);
		    	                     v.startAnimation(animInsertar);
	    	                    	 vectorLibros.add(vectorLibros.elementAt((int) id));
	    	                    	 adaptador.notifyDataSetChanged();
	    	                    	 break;
	    	               }
	    	         }
	    		});
	    	menu.create().show();
	    	return true;
	    	}
	    });

	  return vista; // Siempre debemos retornar la vista en el método onCreateView de los Fragments

	  }

	@Override
	public void onAnimationCancel(Animator arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animator arg0) {
		adaptador.notifyDataSetChanged();
		
	}

	@Override
	public void onAnimationRepeat(Animator arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animator arg0) {
		// TODO Auto-generated method stub
		
	}




}

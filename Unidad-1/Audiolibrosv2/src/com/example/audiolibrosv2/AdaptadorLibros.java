package com.example.audiolibrosv2;

import java.util.Vector;

import com.example.audiolibrosv2.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorLibros extends BaseAdapter {

	LayoutInflater inflador;      // Crea Layouts a partir del XML
	protected Vector<Libro> vectorLibros; // Vector con libros a visualizar
	 
	// Constructor
	public AdaptadorLibros(Context contexto, Vector<Libro> vectorLibros) {
	    inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    this.vectorLibros = vectorLibros;
	 }
	
	
	public View getView(int posicion, View vistaReciclada, ViewGroup padre) {
	    
		Libro libro = vectorLibros.elementAt(posicion);
	    if (vistaReciclada == null) {
	        	vistaReciclada = inflador.inflate(R.layout.elemento_selector, null);
	    }
	    
	    TextView titulo = (TextView) vistaReciclada.findViewById(R.id.titulo);
	  	titulo.setText(libro.titulo);
	   	
	  	ImageView portada = (ImageView) vistaReciclada.findViewById(R.id.portada);
	   	portada.setImageResource(libro.recursoImagen);
	   	portada.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	    
	   	return vistaReciclada;
	}
	 
	  
	public int getCount() {
		return vectorLibros.size();
  }
	 

	public Object getItem(int posicion) {
		return vectorLibros.elementAt(posicion);  
	}
	   
	public long getItemId(int posicion) {
		return posicion;
	}
	
}


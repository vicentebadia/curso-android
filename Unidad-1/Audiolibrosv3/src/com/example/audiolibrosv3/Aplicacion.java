package com.example.audiolibrosv3;

import java.util.Vector;

import android.app.Application;


public class Aplicacion extends Application {
	 
    private Vector<Libro> vectorLibros;
    private AdaptadorLibrosFiltro adaptador;

    @Override
    public void onCreate() {
          vectorLibros = Libro.ejemploLibros();
          adaptador = new AdaptadorLibrosFiltro (this, vectorLibros);
    }

    public AdaptadorLibrosFiltro getAdaptador() {
          return adaptador;
    }

    public Vector<Libro> getVectorLibros() {
          return vectorLibros;
    }
}


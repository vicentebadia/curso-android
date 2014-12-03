package com.example.audiolibrosv2;

import java.util.Vector;

import android.app.Application;


public class Aplicacion extends Application {
	 
    private Vector<Libro> vectorLibros;
    private AdaptadorLibros adaptador;

    @Override
    public void onCreate() {
          vectorLibros = Libro.ejemploLibros();
          adaptador = new AdaptadorLibros (this, vectorLibros);
    }

    public AdaptadorLibros getAdaptador() {
          return adaptador;
    }

    public Vector<Libro> getVectorLibros() {
          return vectorLibros;
    }
}


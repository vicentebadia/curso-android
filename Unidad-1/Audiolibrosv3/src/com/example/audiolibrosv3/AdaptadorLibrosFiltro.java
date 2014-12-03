package com.example.audiolibrosv3;

import java.util.Vector;

import android.content.Context;

public class AdaptadorLibrosFiltro extends AdaptadorLibros {
    
	  private Vector<Libro> vectorSinFiltro;// Vector con todos los libros
	  private Vector<Integer> indiceFiltro; // �ndice en vectorSinFiltro de cada elemento de vectorLibros
	                                        
	  
	  private String busqueda = "";         // B�squeda sobre autor o t�tulo
	  private String genero = "";           // G�nero seleccionado
	  private boolean novedad = false;      // Si queremos ver solo novedades
	  private boolean leido = false;        // Si queremos ver solo leidos
	      
	  public AdaptadorLibrosFiltro(Context contexto, Vector<Libro> vectorLibros) {
		  super(contexto, vectorLibros);
		  vectorSinFiltro = vectorLibros;
		  recalculaFiltro();
	  }
	 
	  public void setBusqueda(String busqueda) {
		  this.busqueda = busqueda.toLowerCase();
		  recalculaFiltro();
	  }
	 
	  public void setGenero(String genero) {
		  this.genero = genero;
		  recalculaFiltro();
	  }
	 
	  public void setNovedad(boolean novedad) {
		  this.novedad = novedad;
		  recalculaFiltro();
	  }
	 
	  public void setLeido(boolean leido) {
		  this.leido = leido;
		  recalculaFiltro();
	  }
	 
	  public void recalculaFiltro() {
		  
		  vectorLibros = new Vector<Libro>();
		  indiceFiltro = new Vector<Integer>();
		  
		  for (int i = 0; i < vectorSinFiltro.size(); i++) { // Recorremos todos los libros
			  Libro libro = vectorSinFiltro.elementAt(i); // Tomamos el elemento de la posici�n i correspondiente a esta iteraci�n
			  if ((libro.titulo.toLowerCase().contains(busqueda) || libro.autor.toLowerCase().contains(busqueda)) // ...
					  												&& (libro.genero.startsWith(genero)) 
					  												&& (!novedad || (novedad && libro.novedad)) // Si novedades est� desactivado o bien est� activado y el libro es novedad
					  												&& (!leido || (leido && libro.leido))) { // Si leido esta desactivado o bien est� activado y el libro ha sido leido
	                   	vectorLibros.add(libro); // Se a�ade a vectorLibros
	                    indiceFiltro.add(i); 	 // Se a�ade el �ndice al vector de �ndices. Este �ndice apunta a la posici�n en vectorSinFiltro 
	                    						 // del libro que acabamos de a�adir en vercotrLibros 
			  }
		  }
	  }
	 
	  public long getItemId(int posicion) {
		  return indiceFiltro.elementAt(posicion);     
	  }
	 
	  public void borrar(int posicion){
		  vectorSinFiltro.remove((int)getItemId(posicion));
		  recalculaFiltro();     
	  }
	 
	  public void insertar(Libro libro){
		  vectorSinFiltro.add(libro);
		  recalculaFiltro();    
	  }
	
}


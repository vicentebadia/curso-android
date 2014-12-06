package com.example.edittexttuneado;

import java.util.Vector;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class EditTextTuneado extends EditText {
	  
	private Paint pincel;
	
	private Paint pincel2 =  new Paint(); 	// Para el resaltado en amarillo del texto
	private Path path = new Path();			// Para crear el subrayado sobre el texto
	private Vector<String> resaltar = new Vector<String>(); // Para almacenar las palabras a resaltar 
	
	private boolean dibujarRayas;
	private int posicionNumeros;
	
	
	public EditTextTuneado(Context context, AttributeSet attrs) { // Constructor
	
		super(context, attrs);
		
		float densidad = getResources().getDisplayMetrics().density;
	    
		pincel = new Paint();	    
		pincel.setColor(Color.BLACK);	    
		pincel.setTextAlign(Paint.Align.RIGHT);	    
		pincel.setTextSize(12*densidad); // Para que tenga el mismo aspecto independientemente de la densidad de pantalla del disp.
		
		pincel2.setColor(Color.YELLOW);
	    pincel2.setStyle(Style.FILL);
	    resaltar.add("Android");
	    resaltar.add("curso");
	    
	    // Para procesar los atributos que se definen en xml:
	    TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.EditTextTuneado, 0, 0);
	    try {
	        dibujarRayas = a.getBoolean(R.styleable.EditTextTuneado_dibujarRayas, true);
	        posicionNumeros = a.getInteger(R.styleable.EditTextTuneado_posicionNumeros, 0);
	       
	        switch (posicionNumeros) {
	           case 0:
	              pincel.setTextAlign(Paint.Align.RIGHT);
	              break;
	           case 1:
	              pincel.setTextAlign(Paint.Align.LEFT);
	              break;
	        }

	        int colorNumeros=a.getColor(R.styleable.EditTextTuneado_colorNumeros,Color.BLACK);
	        pincel.setColor(colorNumeros);
	        float tamanyoNumeros = a.getDimension(R.styleable.EditTextTuneado_tamanyoNumeros, 12*densidad);
	        pincel.setTextSize(tamanyoNumeros);
	    } finally {
	       a.recycle();
	    }

	  
	}

	 
	@Override
	protected void onDraw(Canvas canvas) {

		final Layout layout = getLayout();
		final String texto = getText().toString();

		for (String palabra : resaltar) { // Recorremos todos lo elementos del vector con las palabras a resaltar
			int pos = 0;
			do {
				pos = texto.indexOf(palabra, pos); // Vamos a la posición donde empieza la siguiente palabra que hay que resaltar (si existe). Si no, devuelve -1.
				if (pos != -1) { // Si hemos encontrado algo que subrayar ...
					pos++; 		 // Pasamos a la siguiente posición para que no vuelva a encontrar la misma en la siguiente iteración y se que enganchado
					layout.getSelectionPath(pos, pos + palabra.length(), path); // Obtenemos el area ocupada por los caracteres del layout que queremos resaltar
					canvas.drawPath(path, pincel2); // Les aplicamos el resaltado amarillo definido en pincel2 (amarillo)
				}
			} while (pos != -1);
		}

		Rect rect = new Rect();

		for (int linea = 0; linea < getLineCount(); linea++) { // Recorremos todas las lineas del TextView (en nuestro caso 3)
			int lineaBase = getLineBounds(linea, rect);
			if (dibujarRayas){
				canvas.drawLine(rect.left, lineaBase + 2, rect.right, lineaBase + 2, pincel); // Linea horizontal un par de pixeles por debajo de la linea base
			}
			switch (posicionNumeros){
				case 0:
					canvas.drawText("" + (linea + 1), getWidth() - 2, lineaBase, pincel);   // Escribimos el texto correspondiente a la linea (+1 pq empezamos en cero)
					break;																    // El número de linea se escribe en la posición x igual al ancho menos 2
																							// Y en la posición Y de la linea base
				case 1: 
					canvas.drawText("" + (linea+1), 2, lineaBase, pincel); // Similar al caso anterior pero escribiendo el número a la izquierda
			        break;
			}

		}
		super.onDraw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent evento) {
		
		final Layout layout = getLayout();
		final String texto = getText().toString();
		int linea = layout.getLineForVertical((int) evento.getY());
		int offset = layout.getOffsetForHorizontal(linea, evento.getX()) - 1;
		String s = sacaPalabra(texto, offset);
		if (s.length() != 0 && resaltar.indexOf(s) == -1) {
			resaltar.add(s);
			invalidate();
			return true;
		} else {
			return super.onTouchEvent(evento);
		}
	
	}
	
	String sacaPalabra(String texto, int pos) {
		
		int ini = pos;
		while (ini > 0 && texto.charAt(ini) != ' ' && texto.charAt(ini) != '\n') { // Buscamos el inicio de la palabra
			ini--;
		}
		int fin = pos;
		while (fin < texto.length() && texto.charAt(fin) != ' ' && texto.charAt(fin) != '\n') { // Buscamos el fin de la palabra
			fin++;
		}
		return texto.substring(ini, fin).trim(); // Devolvemos el String comprendido entre el ini y fin
	}

}

package com.example.zoomseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZoomSeekBar extends View {

	// Valor a controlar
	private int val = 160; // valor seleccionado
	private int valMin = 100; // valor mínimo que vamos a poder fijar en la escala
	private int valMax = 200; // valor máximo que vamos a poder fijar en la escala
	private int escalaMin = 150; // valor mínimo visualizado inicialmente
	private int escalaMax = 180; // valor máximo visualizado inicialmente
	private int escalaIni = 100; // origen de la escala (desde donde se empieza a contar para poner rayas normales y largas)
	private int escalaRaya = 2; // cada cuantas unidades una rayas
	private int escalaRayaLarga = 5; // cada cuantas rayas una larga
	// Dimensiones en pixels
	private int altoNumeros;
	private int altoRegla;
	private int altoBar;
	private int altoPalanca;
	private int anchoPalanca;
	private int altoGuia;
	// Valores que indican donde dibujar
	private int xIni;
	private int yIni;
	private int ancho;
	// Objetos Rect con diferentes regiones
	private Rect escalaRect = new Rect();
	private Rect barRect = new Rect();
	private Rect guiaRect = new Rect();
	private Rect palancaRect = new Rect();
	// Objetos Paint globales para no tener que crearlos cada vez
	private Paint textoPaint = new Paint();
	private Paint reglaPaint = new Paint();
	private Paint guiaPaint = new Paint();
	private Paint palancaPaint = new Paint();

	// Objetos para el control de la pulsación sobre la vista
	enum Estado {
		SIN_PULSACION, PALANCA_PULSADA, ESCALA_PULSADA, ESCALA_PULSADA_DOBLE
	};

	Estado estado = Estado.SIN_PULSACION;
	int antVal_0, antVal_1;

	public ZoomSeekBar(Context context, AttributeSet attrs) { // Constructor
		super(context, attrs);
		float dp = getResources().getDisplayMetrics().density;
		// Obtenemos el estilo de la mayoria de los parámetros de la vista desde
		// XML
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ZoomSeekBar, 0, 0);
		try {
			altoNumeros = a.getDimensionPixelSize(R.styleable.ZoomSeekBar_altoNumeros, (int) (30 * dp));
			altoRegla = a.getDimensionPixelSize(R.styleable.ZoomSeekBar_altoRegla, (int) (20 * dp));
			altoBar = a.getDimensionPixelSize(R.styleable.ZoomSeekBar_altoBar, (int) (70 * dp));
			altoPalanca = a.getDimensionPixelSize(R.styleable.ZoomSeekBar_altoPalanca, (int) (40 * dp));
			altoGuia = a.getDimensionPixelSize(R.styleable.ZoomSeekBar_altoGuia, (int) (10 * dp));
			anchoPalanca = a.getDimensionPixelSize(R.styleable.ZoomSeekBar_anchoPalanca, (int) (20 * dp));
			textoPaint.setTextSize(a.getDimension(R.styleable.ZoomSeekBar_altoTexto, 16 * dp));
			textoPaint.setColor(a.getColor(R.styleable.ZoomSeekBar_colorTexto, Color.BLACK)); // El color que nos definan en el xml (si no, negro)
			reglaPaint.setColor(a.getColor(R.styleable.ZoomSeekBar_colorRegla, Color.BLACK));
			guiaPaint.setColor(a.getColor(R.styleable.ZoomSeekBar_colorGuia, Color.BLUE));
			palancaPaint.setColor(a.getColor(R.styleable.ZoomSeekBar_colorPalanca, 0xFF00007F));
		} finally {
			a.recycle();
		}
		textoPaint.setAntiAlias(true);
		textoPaint.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) { // Si la vista cambia de tamaño

		super.onSizeChanged(w, h, oldw, oldh); // Llamada al ancestro

		xIni = getPaddingLeft();
		yIni = getPaddingTop();
		ancho = getWidth() - getPaddingRight() - getPaddingLeft();
		barRect.set(xIni, yIni, xIni + ancho, yIni + altoBar);
		escalaRect.set(xIni, yIni + altoBar, xIni + ancho, yIni + altoBar + altoNumeros + altoRegla);
		int y = yIni + (altoBar - altoGuia) / 2;
		guiaRect.set(xIni, y, xIni + ancho, y + altoGuia);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Dibujamos Barra con palanca
		canvas.drawRect(guiaRect, guiaPaint); // Dibuja rectángulo correspondiente a la guía sobre la que se desplaza el rectángulo
		int y = yIni + (altoBar - altoPalanca) / 2;
		int x = xIni + ancho * (val - escalaMin) / (escalaMax - escalaMin) - anchoPalanca / 2;
		palancaRect.set(x, y, x + anchoPalanca, y + altoPalanca);
		canvas.drawRect(palancaRect, palancaPaint); // Dibuja el rectángulo correspondiente a la palanca
		// Una vez dibujado, se aumenta su tamaño para que sea más fácil de pulsar
		palancaRect.set(x - anchoPalanca / 2, y, x + 3 * anchoPalanca / 2, y + altoPalanca); 

		// Dibujamos Escala
		int v = escalaIni;
		while (v <= escalaMax) {
			if (v >= escalaMin) {
				x = xIni + ancho * (v - escalaMin) / (escalaMax - escalaMin);
				if (((v - escalaIni) / escalaRaya) % escalaRayaLarga == 0) {
					y = yIni + altoBar + altoRegla;
					canvas.drawText(Integer.toString(v), x, y + altoNumeros, textoPaint);
				} else {
					y = yIni + altoBar + altoRegla * 1 / 3;
				}
				canvas.drawLine(x, yIni + altoBar, x, y, reglaPaint);
			}
			v += escalaRaya;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int x_0, y_0, x_1, y_1;
		x_0 = (int) event.getX(0); // Coordenada X donde se ha producido la pulsación
		y_0 = (int) event.getY(0); // Coordenada Y donde se ha producido la pulsación
		
		int val_0 = escalaMin + (x_0 - xIni) * (escalaMax - escalaMin) / ancho; // Valor con la escala actual
		
		if (event.getPointerCount() > 1) { // Si tenemos pulsado dos dedos
			x_1 = (int) event.getX(1); // Coordenada X donde se ha producido la segunda pulsación
			y_1 = (int) event.getY(1); // Coordenada Y donde se ha producido la segunda pulsación
		} else {
			x_1 = x_0; // Si solo hay una pulsación les asignamos los mismos valores
			y_1 = y_0;
		}
		int val_1 = escalaMin + (x_1 - xIni) * (escalaMax - escalaMin) / ancho; //
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: // Si es el primer dedo que se pulsa (ACTION_DOWN = primera pulsación)
				if (palancaRect.contains(x_0, y_0)) { // Y además hemos pulsado sobre la palanca
					estado = Estado.PALANCA_PULSADA; // Entonces cambiamos la variable de estado a PALANCA_PULSADA
				} else if (barRect.contains(x_0, y_0)) { // Si por el contrario hemos pulsado en la barra ...
					if (val_0 > val) // ... a la derecha de la palanca
						val++; // Entonces aumentamos el valor en 1
					else
						val--; // Y si no, es que hemos pulsado en la izquierda, así que reducirmos en 1
					invalidate(barRect); // Forzamos a que se redibuje
				} else if (escalaRect.contains(x_0, y_0)) { // Si se ha pulsado en la escala
					estado = Estado.ESCALA_PULSADA; // Entonces cambiamos la variable de estado a ESCALA_PULSADA
					antVal_0 = val_0; // Almacenamos el valor actual como la antigua posición del primer dedo (para la próxima iteración) 
				}
				break;
			case MotionEvent.ACTION_POINTER_DOWN: // Si es el segundo dedo que se pulsa (ACTION_POINTER_DOWN = segunda pulsación)
				if (estado == Estado.ESCALA_PULSADA) { // Y además ya teniamos la escala pulsada
					if (escalaRect.contains(x_1, y_1)) {
						antVal_1 = val_1; // Almacenamos el valor actual como la antigua posición del primer dedo (para la próxima iteración) 
						estado = Estado.ESCALA_PULSADA_DOBLE; // Actualizamos variable de estado
					}
				}
				break;
			case MotionEvent.ACTION_UP: // Si se levanta el dedo (cuando ya solo quedaba uno tocando la pantalla)
				estado = Estado.SIN_PULSACION; // Actualizamos la variable de estado
				break;
			case MotionEvent.ACTION_POINTER_UP: // Si se levanta un dedo, pero aun queda algún otro pulsado
				if (estado == Estado.ESCALA_PULSADA_DOBLE) { // Si teníamos la escala pulsada en dos extremos...
					estado = Estado.ESCALA_PULSADA; // Ahora solo la tenemos pulsada en uno
				}
				break;
			case MotionEvent.ACTION_MOVE: // Si se ha movido algún dedo
				if (estado == Estado.PALANCA_PULSADA) { // Y además la palanca estaba pulsada
					val = ponDentroRango(val_0, escalaMin, escalaMax); // Actualizamos el valor de la palanca
					invalidate(barRect); // Forzamos a redibujar
				}
				if (estado == Estado.ESCALA_PULSADA_DOBLE) { // Si tenemos la escala doble pulsada, hay que recalcular las subdivisiones
					escalaMin = antVal_0 + (xIni - x_0) * (antVal_0 - antVal_1) / (x_0 - x_1);
					escalaMin = ponDentroRango(escalaMin, valMin, val);
					escalaMax = antVal_0 + (ancho + xIni - x_0) * (antVal_0 - antVal_1) / (x_0 - x_1);
					escalaMax = ponDentroRango(escalaMax, val, valMax);
					invalidate(); // Redibujamos todo (palanca y escala)
				}
				break;
		}
		return true;
	}
	
	// Dado un entero val, lo devuelve tal cual si está dentro del intervalo [valMin, valMax]
	// Si está fuera del intervalo, devuelve el extremo más próximo a él
	int ponDentroRango(int val, int valMin, int valMax) {
		if (val < valMin) {
			return valMin;
		} else if (val > valMax) {
			return valMax;
		} else {
			return val;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int altoDeseado = altoNumeros + altoRegla + altoBar + getPaddingBottom() + getPaddingTop(); // Altura que querría tener la vista
		int alto = obtenDimension(heightMeasureSpec, altoDeseado); // Altura que vamos a devolver teniendo en cuenta lo que queremos y lo que impone el padre
		int anchoDeseado = 2 * altoDeseado; // Queremos que el ancho sea al menos el dobe del alto
		int ancho = obtenDimension(widthMeasureSpec, anchoDeseado); // Altura que vamos a devolver teniendo en cuenta lo que queremos y lo que impone el padre
		setMeasuredDimension(ancho, alto); // Aplicamos el alto y ancho calculados
	}

	// Método auxiliar para decidir el alto o el ancho en base al que nuestra vista querría tener y las
	// restricciones que nos impone la vista padre (contenedor)
	private int obtenDimension(int measureSpec, int deseado) {
		int dimension = MeasureSpec.getSize(measureSpec); // La dimensión que querría tener el padre
		int modo = MeasureSpec.getMode(measureSpec); // El modo que impone el padre (EXACTLY, AT_MOST o UNSPECIFIED)
		if (modo == MeasureSpec.EXACTLY) { // Si impone EXACTLY...
			return dimension; // ...devolvemos la dimensión que ha impuesto el padre
		} else if (modo == MeasureSpec.AT_MOST) { // Si impone AT_MOST...
			return Math.min(dimension, deseado); // ...devolvemos el mínimo de lo que impone el padre y lo que la vista quiere ocupar
		} else {
			return deseado; // Si no... (estamos en UNSPECIFIED), por tanto devolvemos lo que quiere la vista
		}
	}

	// Getters y Setters
	public int getVal() {
		return val;
	}

	public void setVal(int val) {
	       if (valMin <= val && val <= valMax) {
	               this.val = val;
	               escalaMin = Math.min(escalaMin, val);
	               escalaMax = Math.max(escalaMax, val);
	               invalidate();
	       }
	}

	// TBD - Añadir el resto de getters y setters
	
	// TBD - Hacer que la vista salga centrada en el padre si sobra espacio
	
	// TBD - Crear un escuchador de eventos para avisar de cambios en val


}

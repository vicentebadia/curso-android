package com.example.bouncingball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BouncingBallActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new BouncingBallView(this, null, 0)); // La actividad principal simplmente contiene un objeto BouncingBallView
	}

}

class BouncingBallView extends SurfaceView implements SurfaceHolder.Callback {

	private BouncingBallAnimationThread bbThread = null;

	// Para customizar la bola
	private int xPosition = getWidth() / 2; // Posición inicial en x
	private int yPosition = getHeight() / 2; // Posición inicial en y
	private int xDirection = 20; // Dirección en x
	private int yDirection = 40; // Dirección en y
	private static int radius = 20; // Radio
	private static int ballColor = Color.RED; // Color

	// Constructor
	public BouncingBallView(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle);
		getHolder().addCallback(this);
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint); // Dibujamos un rectangulo negro del tamaño de toda la pantalla
		paint.setColor(ballColor);
		canvas.drawCircle(xPosition, yPosition, radius, paint); // Dibujamos un círculo en x,y actual con el radio definido y del color definido (rojo)

	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (bbThread != null) // Si el hilo para todavía no existe
			return; // ... nos salimos
		// En caso contrario, creamos el hilo
		bbThread = new BouncingBallAnimationThread(getHolder());
		bbThread.start(); // Y lo arrancamos
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// No necesitamos hacer nada aquí
	}

	public void surfaceDestroyed(SurfaceHolder holder) { // Si el surface es destruido (por ejemplo por el cierre de la app)
		bbThread.stop = true; // Detenemos el hilo
	}

	// Para cambiar la dirección de la bola al pulsar sobre la pantalla
	// Una primera pulsación hace que la bola se pare
	// Una segunda pulsación hace que la bola cambie de dirección para llegar a donde hayamos pulsado
	// Continuará rebotando a una velocidad proporcional a la distancia que había entre el punto donde se paró y la pos. de la segunda pulsación
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() != MotionEvent.ACTION_DOWN) // Si es una pulsación distinta de la pulsación de un primer dedo
			return false; // Nos volvemos sin hacer nada
		
		if (xDirection != 0 || yDirection != 0) // Si la bola está en movimiento
			xDirection = yDirection = 0; // La paramos
		else {
			xDirection = (int) event.getX() - xPosition; // La nueva dirección es el lugar en x donde hemos pulsado menos la posición donde paramos la bola 
			yDirection = (int) event.getY() - yPosition; // La nueva dirección es el lugar en x donde hemos pulsado menos la posición donde paramos la bola 
		}

		return true;
	}

	// Subclase que hereda de Thread para hacer las operaciones necesarias para la animación en segundo plano
	private class BouncingBallAnimationThread extends Thread {

		public boolean stop = false; // Por defecto el hilo tiene orden de "no parar"
		private SurfaceHolder surfaceHolder; // Definimos un SurfaceHolder

		// Constructor
		public BouncingBallAnimationThread(SurfaceHolder surfaceHolder) {
			this.surfaceHolder = surfaceHolder;
		}

		// El método run se llama al hacer start sobre el Thread
		public void run() {
			while (!stop) { // Mientras stop sea falso ("no queremos que el hilo pare")
				xPosition += xDirection; // La posición x se incrementa lo que defina xDirection
				yPosition += yDirection; // La posición y se incrementa lo que defina yDirection

				if (xPosition < 0) { // Si la bola llega al borde izquierdo de la pantalla
					xDirection = -xDirection; // Cambiamos el sentido horizontal
					xPosition = radius; // Forzamos a que la bola esté justo pegada al borde
				}
				if (xPosition > getWidth() - radius) { // Si la bola llega al borde derecho
					xDirection = -xDirection; // Cambiamos el sentido horizontal
					xPosition = getWidth() - radius; // Forzamos a que la bola esté justo pegada al borde
				}
				if (yPosition < 0) { // Si la bola se sale por arriba
					yDirection = -yDirection; // Cambiamos el sentido vertical
					yPosition = radius; // Forzamos a que la bola esté justo pegada al borde superior
				}
				if (yPosition > getHeight() - radius) { // Si la bola se sale por abajo
					yDirection = -yDirection; // Cambiamos el sentido vertical
					yPosition = getHeight() - radius - 1; // Forzamos a que la bola esté justo pegada al borde inferior
				}
				Canvas c = null;
				try {
					c = surfaceHolder.lockCanvas(null); // Hacemos un lock sobre el canvas para que nadie dibuje
					synchronized (surfaceHolder) { // synchronized nos garantiza que solo este Thread esta manejando este objeto surfaceHolder (*VER NOTA)
						onDraw(c); // Forzamos a redibujar
					}
				} catch (Exception e) {
				} finally { // Independitentemente del resultado del try
					if (c != null) // Si el canvas contiene algo
						surfaceHolder.unlockCanvasAndPost(c); // Desbloqueamos el SurfaceHolder
				}
			}
		}
	}

}
// -------------------------------------------------------------------------------------------------------------------------
// *NOTA: El bloque synchronized lleva entre paréntesis la referencia a un objeto. Cada vez que un thread intenta acceder
// a un bloque sincronizado le pregunta a ese objeto si no hay algún otro thread que ya tenga el lock para ese objecto. En
// otras palabras, le pregunta si no hay otro thread ejecutando algun bloque sincronizado con ese objeto.
// -------------------------------------------------------------------------------------------------------------------------
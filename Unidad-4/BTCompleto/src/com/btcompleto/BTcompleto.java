package com.btcompleto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BTcompleto extends Activity implements OnClickListener {

	private static final int REQUEST_ENABLE_BT = 1;
	private static final String TAG = "BT-tag";
	
	private Button btnBluetooth;
	private Button btnBuscarDispositivo;
	private Button btnConectarDispositivo;
	private BluetoothAdapter bAdapter;
	
	BluetoothService servicio; // <--- Confimar si es private 
	
	BluetoothDevice ultimoDispositivo; // <-- Añado esto porque no estaba definido en ningún sitio
	
	private Button btnEnviar;
	
	private Button btnSalir;
	private EditText txtMensaje; 
	private TextView tvMensaje;
	private TextView tvConexion;
	
	
	private ListView lvDispositivos;
	
	private ArrayList<BluetoothDevice> arrayDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_btcompleto);
	
		configurarControles();
		
		registrarEventosBluetooth();
		configurarAdaptadorBluetooth();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.btcompleto, menu);
		return true;
	}


	/**
	 * Configura el BluetoothAdapter y los botones asociados
	 */
	private void configurarAdaptadorBluetooth() {

		// Obtenemos el adaptador Bluetooth. Si es NULL, significara que el
		// dispositivo no posee Bluetooth, por lo que deshabilitamos el boton
		// encargado de activar/desactivar esta caracteristica.
		bAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bAdapter == null) {
			btnBluetooth.setEnabled(false);
			return;
		}

		// Comprobamos si el Bluetooth esta activo y cambiamos el texto de los botones
		// dependiendo del estado. Tambien activamos o desactivamos los botones asociados a la conexion
		if (bAdapter.isEnabled()) {
			btnBluetooth.setText(R.string.DesactivarBluetooth);
			btnBuscarDispositivo.setEnabled(true);
			btnConectarDispositivo.setEnabled(true);
		} else {
			btnBluetooth.setText(R.string.ActivarBluetooth);
		}
	}


	// Instanciamos un BroadcastReceiver que se encargara de detectar si el
	// estado del Bluetooth del dispositivo ha cambiado mediante su handler onReceive
	private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// Filtramos por la accion. Nos interesa detectar
			// BluetoothAdapter.ACTION_STATE_CHANGED
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				
				// Solicitamos la informacion extra del intent etiquetada como
				// BluetoothAdapter.EXTRA_STATE
				// El segundo parametro indicara el valor por defecto que se
				// obtendra si el dato extra no existe
				
				final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

				switch (estado) {
				  // Apagado
				  case BluetoothAdapter.STATE_OFF: {
				    ((Button)findViewById(R.id.btnBluetooth)).setText(R.string.ActivarBluetooth);
				    break;
				  }
				 
				  // Encendido
				  case BluetoothAdapter.STATE_ON: {
				    ((Button)findViewById(R.id.btnBluetooth)).setText(R.string.DesactivarBluetooth);	    
				    // Lanzamos un Intent de solicitud de visibilidad Bluetooth,
				    // al que añadimos un par
				    // clave-valor que indicara la duracion de este estado, en
				    // este caso 120 segundos
				    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
				    startActivity(discoverableIntent);
				    break;
				  }
		 
				}
				
				
				
		        // Cada vez que se descubra un nuevo dispositivo por Bluetooth, se ejecutara
		        // este fragmento de codigo
		       
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            // Acciones a realizar al descubrir un nuevo dispositivo
		        	
		        	
		        	// Si el array no ha sido aun inicializado, lo instanciamos
		        	if (arrayDevices == null)
		        	   arrayDevices = new ArrayList<BluetoothDevice>();
		        	// Extraemos el dispositivo del intent mediante la clave
		        	// BluetoothDevice.EXTRA_DEVICE
		        	BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		        	// Añadimos el dispositivo al array
		        	arrayDevices.add(dispositivo);
		        	 
		        	// Le asignamos un nombre del estilo NombreDispositivo
		        	// [00:11:22:33:44]
		        	String descripcionDispositivo = dispositivo.getName() + " ["+ dispositivo.getAddress() + "]";
		        	 
		        	// Mostramos que hemos encontrado el dispositivo por el Toast
		        	Toast.makeText(getBaseContext(), getString(R.string.DetectadoDispositivo) + ": "
		        	             + descripcionDispositivo, Toast.LENGTH_SHORT).show();		        	
		        }
		 
		       // Codigo que se ejecutara cuando el Bluetooth finalice la busqueda
		       // de dispositivos.
				else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					// Acciones a realizar al finalizar el proceso de
					// descubrimiento
					ArrayAdapter arrayAdapter = new BluetoothDeviceArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_2, arrayDevices);
					lvDispositivos.setAdapter(arrayAdapter);
					Toast.makeText(getBaseContext(), "Fin de la búsqueda", Toast.LENGTH_SHORT).show();

				}

			}

		}

	};
	
	
	private void registrarEventosBluetooth() {
		   // Registramos el BroadcastReceiver que instanciamos previamente para
		   // detectar los distintos eventos que queremos recibir
		   IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		   filtro.addAction(BluetoothDevice.ACTION_FOUND);
		   filtro.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		   this.registerReceiver(bReceiver, filtro);
	}

	/**
	 * Handler para manejar los eventos onClick de los botones.
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// Codigo ejecutado al pulsar el Button que se va a encargar de enviar
		// los datos al otro dispositivo.
		case R.id.btnEnviar: {
			if (servicio != null) {
				servicio.enviar(txtMensaje.getText().toString().getBytes());
				txtMensaje.setText("");
			}
			break;
		}
		// Codigo ejecutado al pulsar el Button que se va a encargar de activar
		// y desactivar el Bluetooth.
		case R.id.btnBluetooth: {
			if (bAdapter.isEnabled()) {
				if (servicio != null) {
					servicio.finalizarServicio();
				}
				bAdapter.disable();
			} else {
				// Lanzamos el Intent que mostrara la interfaz de activacion del
				// Bluetooth. La respuesta de este Intent se manejara en el
				// metodo
				// onActivityResult
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			break;
		}
		// Codigo ejecutado al pulsar el Button que se va a encargar de
		// descubrir nuevos dispositivos
		case R.id.btnBuscarDispositivo: {
			if (arrayDevices != null)
				arrayDevices.clear();
			// Comprobamos si existe un descubrimiento en curso. En caso
			// afirmativo,se cancela.
			if (bAdapter.isDiscovering())
				bAdapter.cancelDiscovery();
			// Iniciamos la busqueda de dispositivos
			if (bAdapter.startDiscovery()) {
				// Mostramos el mensaje de que el proceso ha comenzado
				Toast.makeText(this, R.string.IniciandoDescubrimiento, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, R.string.ErrorIniciandoDescubrimiento, Toast.LENGTH_SHORT).show();
			}
			break;
		}

		// Codigo ejecutado al pulsar el Button que se encarga de mostrar todos
		// los dispositivos previamente enlazados al dispositivo actual.
		case R.id.btnConectarDispositivo: {
			Set<BluetoothDevice> dispositivosEnlazados = bAdapter.getBondedDevices();
			// Instanciamos un nuevo adapter para el ListView
			arrayDevices = new ArrayList<BluetoothDevice>(dispositivosEnlazados);
			ArrayAdapter arrayAdapter = new BluetoothDeviceArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, arrayDevices); // <--- Añado ArrayAdapter
			lvDispositivos.setAdapter(arrayAdapter);
			Toast.makeText(getBaseContext(), R.string.FinBusqueda, Toast.LENGTH_SHORT).show();
			break;
		}

		case R.id.btnSalir: {
			if (servicio != null)
				servicio.finalizarServicio();
			finish();
			System.exit(0);
			break;
		}

		default:
			break;
		}
	}
	
	/**
	 * Handler del evento desencadenado al retornar de una actividad. En este
	 * caso, se utiliza para comprobar el valor de retorno al lanzar la
	 * actividad que activara el Bluetooth. En caso de que el usuario acepte,
	 * resultCode sera RESULT_OK. En caso de que el usuario no acepte, resultCode
	 * valdra RESULT_CANCELED
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT: {
			Log.v(TAG, "onActivityResult: REQUEST_ENABLE_BT");
			if (resultCode == RESULT_OK) {
				btnBluetooth.setText(R.string.DesactivarBluetooth);
				
				/*  if (servicio != null) { 
					  servicio.finalizarServicio();
				      servicio.iniciarServicio(); 
				  } else {
					  servicio = new BluetoothService(this, handler, bAdapter);
				  }
				 */
			}
			break;
		}
		default:
			break;
		}
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(bReceiver);
        if(servicio != null)
            servicio.finalizarServicio();

	}
	
	@Override
	public synchronized void onResume() {
		super.onResume();
		if (servicio != null) {
			if (servicio.getEstado() == BluetoothService.ESTADO_NINGUNO) {
				servicio.iniciarServicio();
			}
		}
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
	}
	
	/**
	 * Metodo de configuracion de la actividad
	 */
	private void configurarControles() {

		// Instanciamos el array de dispositivos
		arrayDevices = new ArrayList<BluetoothDevice>();

		// Referenciamos los controles y registramos los listeners
		referenciarControles();
		registrarEventosControles();

		// Por defecto, desactivamos los botones que no puedan utilizarse
		btnEnviar.setEnabled(false);
		btnBuscarDispositivo.setEnabled(false);
		btnConectarDispositivo.setEnabled(false);

		// Configuramos el adaptador bluetooth y nos suscribimos a sus eventos
		configurarAdaptadorBluetooth();
		registrarEventosBluetooth();
	}

	/**
	 * Referencia los elementos de interfaz
	 */
	private void referenciarControles() {

		// Referenciamos los elementos de interfaz
		btnEnviar = (Button) findViewById(R.id.btnEnviar);
		btnBluetooth = (Button) findViewById(R.id.btnBluetooth);
		btnBuscarDispositivo = (Button) findViewById(R.id.btnBuscarDispositivo);
		btnConectarDispositivo = (Button) findViewById(R.id.btnConectarDispositivo);
		btnSalir = (Button) findViewById(R.id.btnSalir);
		txtMensaje = (EditText) findViewById(R.id.txtMensaje);
		tvMensaje = (TextView) findViewById(R.id.tvMensaje);
		tvConexion = (TextView) findViewById(R.id.tvConexion);
		lvDispositivos = (ListView) findViewById(R.id.lvDispositivos);
	}

	/**
	 * Registra los eventos de interfaz (eventos onClick, onItemClick, etc.)
	 */
	private void registrarEventosControles() {
		// Asignamos los handlers de los botones
		btnEnviar.setOnClickListener(this);
		btnBluetooth.setOnClickListener(this);
		btnBuscarDispositivo.setOnClickListener(this);
		btnConectarDispositivo.setOnClickListener(this);
		btnSalir.setOnClickListener(this);

		// Configuramos la lista de dispositivos para que cuando seleccionemos
		// uno de sus elementos realice la conexion al dispositivo
		configurarListaDispositivos();
	}

	
	/**
	 * Configura el ListView para que responda a los eventos de pulsacion
	 */
	private void configurarListaDispositivos() {

		lvDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView adapter, View view,
				int position, long arg) {
				// El ListView tiene un adaptador de tipo BluetoothDeviceArrayAdapter.
				// Invocamos el metodo getItem() del adaptador para
				// recibir el dispositivo bluetooth y realizar la conexion.
				BluetoothDevice dispositivo = (BluetoothDevice) lvDispositivos.getAdapter().getItem(position);

				AlertDialog dialog = crearDialogoConexion(getString(R.string.Conectar), getString(R.string.MsgConfirmarConexion) + " " + dispositivo.getName() + "?", dispositivo.getAddress());
				dialog.show();
			}
		});
	}
	
	
	private AlertDialog crearDialogoConexion(String titulo, String mensaje,
			final String direccion) {
		// Instanciamos un nuevo AlertDialog Builder y le asociamos titulo y mensaje
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(titulo);
		alertDialogBuilder.setMessage(mensaje);

		// Creamos un nuevo OnClickListener para el boton OK que realice la conexion
		DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				conectarDispositivo(direccion);
			}
		};

		// Creamos un nuevo OnClickListener para el boton Cancelar
		DialogInterface.OnClickListener listenerCancelar = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		};

		// Asignamos los botones positivo y negativo a sus respectivos listeners
		alertDialogBuilder.setPositiveButton(R.string.Conectar, listenerOk);
		alertDialogBuilder.setNegativeButton(R.string.Cancelar,	listenerCancelar);

		return alertDialogBuilder.create();
	}
	
	public void conectarDispositivo(String direccion) {
		Toast.makeText(this, "Conectando a " + direccion, Toast.LENGTH_LONG).show();
		if (servicio != null) {
			BluetoothDevice dispositivoRemoto = bAdapter.getRemoteDevice(direccion);
			servicio.solicitarConexion(dispositivoRemoto);
			this.ultimoDispositivo = dispositivoRemoto;
		}
	}

	public void enviarMensaje(String mensaje) {
		if (servicio.getEstado() != BluetoothService.ESTADO_CONECTADO) {
			Toast.makeText(this, R.string.MsgErrorConexion, Toast.LENGTH_SHORT).show();
			return;
		}

		if (mensaje.length() > 0) {
			byte[] buffer = mensaje.getBytes();
			servicio.enviar(buffer);
		}
	}
	
	

	
	public class BluetoothService {

		private static final String TAG = "com.btcompleto.BluetoothService";
		private static final boolean DEBUG_MODE = true;

		private final Handler handler;
		private final Context context;
		private final BluetoothAdapter bAdapter;
		public static final String NOMBRE_SEGURO = "BluetoothServiceSecure";
		public static final String NOMBRE_INSEGURO = "BluetoothServiceInsecure";
		public  UUID UUID_SEGURO; // =UUID.fromString("com.btcompleto.BluetoothService.Secure") // <--- Le quito el static
		public  UUID UUID_INSEGURO; // =UUID.fromString("com.btcompleto.BluetoothService.Insecure") // <--- Le quito el static

		public static final int ESTADO_NINGUNO = 0;
		public static final int ESTADO_CONECTADO = 1;
		public static final int ESTADO_REALIZANDO_CONEXION = 2;
		public static final int ESTADO_ATENDIENDO_PETICIONES = 3;

		public static final int MSG_CAMBIO_ESTADO = 10;
		public static final int MSG_LEER = 11;
		public static final int MSG_ESCRIBIR = 12;
		public static final int MSG_ATENDER_PETICIONES = 13;
		public static final int MSG_ALERTA = 14;

		private int estado;
		private HiloServidor hiloServidor = null;
		private HiloCliente hiloCliente = null;
		private HiloConexion hiloConexion = null;

		public BluetoothService(Context context, Handler handler, BluetoothAdapter adapter) {

			//debug("BluetoothService()", "Iniciando metodo");
			this.context = context;
			this.handler = handler;
			this.bAdapter = adapter;
			this.estado = ESTADO_NINGUNO;

			UUID_SEGURO = generarUUID();
			UUID_INSEGURO = generarUUID();

		}
		
		private UUID generarUUID() {

			ContentResolver appResolver = context.getApplicationContext().getContentResolver();
			String id = Secure.getString(appResolver, Secure.ANDROID_ID);
			final TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			final String deviceId = String.valueOf(tManager.getDeviceId());
			final String simSerialNumber = String.valueOf(tManager.getSimSerialNumber());
			final String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

			UUID uuid = new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32) | simSerialNumber.hashCode());
			uuid = new UUID((long) 1000, (long) 23);
			return uuid;
		
		}
		
		
		private synchronized void setEstado(int estado) {
		       this.estado = estado;
		       handler.obtainMessage(MSG_CAMBIO_ESTADO, estado, -1).sendToTarget();
		}
		      
		public synchronized int getEstado() {
		       return estado;
		}
		 
		public String getNombreDispositivo() {
		       String nombre = "";
		       if(estado == ESTADO_CONECTADO) {
		            if(hiloConexion != null)
		                    nombre = hiloConexion.getName();
		       }
		       return nombre;
		}

		
		
		//Hilo encargado de mantener la conexion y realizar las lecturas y escrituras
		//de los mensajes intercambiados entre dispositivos.
		private class HiloConexion extends Thread {
			
			private final BluetoothSocket socket;	 // Socket
			private final InputStream inputStream;	 // Flujo de entrada (lecturas)
			private final OutputStream outputStream; // Flujo de salida (escrituras)

			
			public HiloConexion(BluetoothSocket socket){
				this.socket = socket;
			  	setName(socket.getRemoteDevice().getName() + " [" + socket.getRemoteDevice().getAddress() + "]");
			  
				// Se usan variables temporales debido a que los atributos se declaran como final //<---------- NO TENGO MUY CLARO QUE ESTO VAYA DENTRO DE HILOCONEXION
				// no seria posible asignarles valor posteriormente si fallara esta llamada
				InputStream tmpInputStream = null;
				OutputStream tmpOutputStream = null;
				 
				// Obtenemos los flujos de entrada y salida del socket.       
				try {
				    tmpInputStream = socket.getInputStream();
				    tmpOutputStream = socket.getOutputStream();
				} catch(IOException e) {
				        Log.e(TAG, "HiloConexion(): Error al obtener flujos de E/S", e);
				}
				inputStream = tmpInputStream;
				outputStream = tmpOutputStream;
			}                                                                                  // --------------------------------------------------------------------

			// Metodo principal del hilo, encargado de realizar las lecturas
			public void run() {
				//debug("HiloConexion.run()", "Iniciando metodo");
				byte[] buffer = new byte[1024];
				int bytes;
				setEstado(ESTADO_CONECTADO);
				// Mientras se mantenga la conexion el hilo se mantiene en espera
				// ocupada
				// leyendo del flujo de entrada
				while (true) {
					try {
						// Leemos del flujo de entrada del socket
						bytes = inputStream.read(buffer);
						// Enviamos la informacion a la actividad a traves del handler.
						// El metodo handleMessage sera el encargado de recibir el
						// mensaje
						// y mostrar los datos recibidos en el TextView
						handler.obtainMessage(MSG_LEER, bytes, -1, buffer).sendToTarget();
						sleep(500);
					} catch (IOException e) {
						Log.e(TAG, "HiloConexion.run(): Error al realizar la lectura", e);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			public void escribir(byte[] buffer) {
				try {
					// Escribimos en el flujo de salida del socket
					outputStream.write(buffer);

					// Enviamos la informacion a la actividad a traves del handler.
					// El metodo handleMessage sera el encargado de recibir el mensaje
					// y mostrar los datos enviados en el Toast
					handler.obtainMessage(MSG_ESCRIBIR, -1, -1, buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(TAG,
							"HiloConexion.escribir(): Error al realizar la escritura", e);
				}
			}

			public void cancelarConexion() {
				//debug("HiloConexion.cancelarConexion()", "Iniciando metodo");
				try {
					// Forzamos el cierre del socket
					socket.close();
					// Cambiamos el estado del servicio
					setEstado(ESTADO_NINGUNO);
				} catch (IOException e) {
					Log.e(TAG,
							"HiloConexion.cerrarConexion(): Error al cerrar la conexion", e);
				}
			}

			// Handler que obtendrá informacion de BluetoothService
			private final Handler handler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					byte[] buffer = null;
					String mensaje = null;
					// Atendemos al tipo de mensaje
					switch (msg.what) {
					// Mensaje de lectura: se mostrara en un TextView
					case BluetoothService.MSG_LEER: {
						buffer = (byte[]) msg.obj;
						mensaje = new String(buffer, 0, msg.arg1);
						tvMensaje.setText(mensaje);
						break;
					}
					// Mensaje de escritura: se mostrara en el Toast
					case BluetoothService.MSG_ESCRIBIR: {
						buffer = (byte[]) msg.obj;
						mensaje = new String(buffer);
						mensaje = "Enviando mensaje: " + mensaje;
						Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
						break;
					}

					// Mensaje de cambio de estado
					case BluetoothService.MSG_CAMBIO_ESTADO: {
						switch (msg.arg1) {
						case BluetoothService.ESTADO_ATENDIENDO_PETICIONES:
							break;

						// CONECTADO: Se muestra el dispositivo al que se ha conectado y se activa el boton de enviar
						case BluetoothService.ESTADO_CONECTADO: {
							mensaje = getString(R.string.ConexionActual) + " " + servicio.getNombreDispositivo();
							Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
							tvConexion.setText(mensaje);
							btnEnviar.setEnabled(true);
							break;
						}

						// REALIZANDO CONEXION: Se muestra el dispositivo al que se esta conectando
						case BluetoothService.ESTADO_REALIZANDO_CONEXION: {
							mensaje = getString(R.string.ConectandoA) + " " + ultimoDispositivo.getName() + " [" + ultimoDispositivo.getAddress() + "]";
							Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
							btnEnviar.setEnabled(false);
							break;
						}
						// NINGUNO: Mensaje por defecto. Desactivacion del boton de
						// enviar
						case BluetoothService.ESTADO_NINGUNO: {
							mensaje = getString(R.string.SinConexion);
							Toast.makeText(getApplicationContext(), mensaje,
									Toast.LENGTH_SHORT).show();
							tvConexion.setText(mensaje);
							btnEnviar.setEnabled(false);
							break;
						}
						default:
							break;
						}
						break;
					}

					// Mensaje de alerta: se mostrara en el Toast
					case BluetoothService.MSG_ALERTA: {
						// mensaje = msg.getData().getString(ALERTA); // <--- Lo comento para que no de error
						Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
						break;
					}

					default:
						break;
					}
				}
			};

		}

		
		// Hilo que hace las veces de servidor, encargado de escuchar conexiones entrantes y
		// crear un hilo que maneje la conexion cuando ello ocurra.
		// La otra parte debera solicitar la conexion mediante un HiloCliente.
		private class HiloServidor extends Thread {
			private final BluetoothServerSocket serverSocket;

			public HiloServidor() {
				BluetoothServerSocket tmpServerSocket = null;
		 
				// Creamos un socket para escuchar las peticiones de conexion
				try {
					tmpServerSocket = bAdapter.listenUsingRfcommWithServiceRecord(NOMBRE_SEGURO, UUID_SEGURO);
				} catch(IOException e) {
					Log.e(TAG, "HiloServidor(): Error al abrir el socket servidor", e);
				}

				serverSocket = tmpServerSocket;
			}
			
			
			public void run() {
				// debug("HiloServidor.run()", "Iniciando metodo");
				BluetoothSocket socket = null;

				setName("HiloServidor");
				setEstado(ESTADO_ATENDIENDO_PETICIONES);
				// El hilo se mantendra en estado de espera ocupada aceptando
				// conexiones
				// entrantes siempre y cuando no exista una conexion activa.
				// En el momento en el que entre una nueva conexion,
				while (estado != ESTADO_CONECTADO) {
					try {
						// Cuando un cliente solicite la conexion se abrirá el
						// socket.
						socket = serverSocket.accept();
					} catch (IOException e) {
						Log.e(TAG,
								"HiloServidor.run(): Error al aceptar conexiones entrantes",
								e);
						break;
					}

					// Si el socket tiene valor sera porque un cliente ha solicitado
					// la conexion
					if (socket != null) {
						// Realizamos un lock del objeto
						synchronized (BluetoothService.this) {
							switch (estado) {
							case ESTADO_ATENDIENDO_PETICIONES:
							case ESTADO_REALIZANDO_CONEXION: {
								// Estado esperado, se crea el hilo de conexion que
								// recibira
								// y enviara los mensajes
								hiloConexion = new HiloConexion(socket);
								hiloConexion.start();
								break;
							}
							case ESTADO_NINGUNO:
							case ESTADO_CONECTADO: {
								// No preparado o conexion ya realizada. Se cierra
								// el nuevo socket.
								try {
									socket.close();
								} catch (IOException e) {
									Log.e(TAG,
											"HiloServidor.run(): socket.close(). Error al cerrar el socket.",
											e);
								}
								break;
							}
							default:
								break;
							}
						}
					}

				} // End while
			}

			        
			public void cancelarConexion() {
				try {
					serverSocket.close();
				} catch (IOException e) {
					Log.e(TAG,
							"HiloServidor.cancelarConexion(): Error al cerrar el socket", e);
				}
			}

		}

		

		// Hilo encargado de solicitar una conexion a un dispositivo que esté
		// corriendo un HiloServidor.
		private class HiloCliente extends Thread {
			
			private final BluetoothDevice dispositivo;
			private final BluetoothSocket socket;

			public HiloCliente(BluetoothDevice dispositivo) {
				BluetoothSocket tmpSocket = null;
				this.dispositivo = dispositivo;

				// Obtenemos un socket para el dispositivo con el que se quiere
				// conectar
				try {
					tmpSocket = dispositivo
							.createRfcommSocketToServiceRecord(UUID_SEGURO);
				} catch (IOException e) {
					Log.e(TAG,
							"HiloCliente.HiloCliente(): Error al abrir el socket", e);
				}

				socket = tmpSocket;
			}	
			
			
			public void run() {

				setName("HiloCliente");
				if (bAdapter.isDiscovering())
					bAdapter.cancelDiscovery();
				try {
					socket.connect();
					setEstado(ESTADO_REALIZANDO_CONEXION);
				} catch (IOException e) {
					Log.e(TAG,
							"HiloCliente.run(): socket.connect(): Error realizando la conexion", e);
					try {
						socket.close();
					} catch (IOException inner) {
						Log.e(TAG, "HiloCliente.run(): Error cerrando el socket", inner);
					}
					setEstado(ESTADO_NINGUNO);
				}
				// Reiniciamos el hilo cliente, ya que no lo necesitaremos mas
				synchronized (BluetoothService.this) {
					hiloCliente = null;
				}
				// Realizamos la conexion
				hiloConexion = new HiloConexion(socket);
				hiloConexion.start();
			}
			
			
			public void cancelarConexion() {

				//debug("cancelarConexion()", "Iniciando metodo");
				try {
					socket.close();
				} catch (IOException e) {
					Log.e(TAG, "HiloCliente.cancelarConexion(): Error al cerrar el socket", e);
				}
				setEstado(ESTADO_NINGUNO);

			}

		}
		
		
		// Inicia el servicio, creando un HiloServidor que se dedicara a atender las
		// peticiones de conexion.
		public synchronized void iniciarServicio() {

			//debug("iniciarServicio()", "Iniciando metodo");

			// Si se esta intentando realizar una conexion mediante un hilo cliente,
			// se cancela la conexion
			if (hiloCliente != null) {
				hiloCliente.cancelarConexion();
				hiloCliente = null;
			}
			// Si existe una conexion previa, se cancela
			if (hiloConexion != null) {
				hiloConexion.cancelarConexion();
				hiloConexion = null;
			}
			// Arrancamos el hilo servidor para que empiece a recibir peticiones
			// de conexion
			if (hiloServidor == null) {
				hiloServidor = new HiloServidor();
				hiloServidor.start();
			}
			//debug("iniciarServicio()", "Finalizando metodo");
		}
		
		
		public void finalizarServicio() {
			//debug("finalizarServicio()", "Iniciando metodo");

			if (hiloCliente != null)
				hiloCliente.cancelarConexion();
			if (hiloConexion != null)
				hiloConexion.cancelarConexion();
			if (hiloServidor != null)
				hiloServidor.cancelarConexion();

			hiloCliente = null;
			hiloConexion = null;
			hiloServidor = null;

			setEstado(ESTADO_NINGUNO);

		}
		
		
		// Instancia un hilo conector
		public synchronized void solicitarConexion(BluetoothDevice dispositivo) {

			//debug("solicitarConexion()", "Iniciando metodo");
			// Comprobamos si existia un intento de conexion en curso.
			// Si es el caso, se cancela y se vuelve a iniciar el proceso
			if (estado == ESTADO_REALIZANDO_CONEXION) {
				if (hiloCliente != null) {
					hiloCliente.cancelarConexion();
					hiloCliente = null;
				}
			}
			// Si existia una conexion abierta, se cierra y se inicia una nueva
			if (hiloConexion != null) {
				hiloConexion.cancelarConexion();
				hiloConexion = null;
			}
			// Se instancia un nuevo hilo conector, encargado de solicitar una
			// conexion
			// al servidor, que sera la otra parte.
			hiloCliente = new HiloCliente(dispositivo);
			hiloCliente.start();
			setEstado(ESTADO_REALIZANDO_CONEXION);
		}

		public synchronized void realizarConexion(BluetoothSocket socket, BluetoothDevice dispositivo) {

			//debug("realizarConexion()", "Iniciando metodo");
			hiloConexion = new HiloConexion(socket);
			hiloConexion.start();

		}
		
		
		// Sincroniza el objeto con el hilo HiloConexion e invoca a su metodo
		// escribir() para enviar el mensaje a traves del flujo de salida del socket.
		public int enviar(byte[] buffer) {

			// debug("enviar()", "Iniciando metodo");
			HiloConexion tmpConexion;

			synchronized (this) {
				if (estado != ESTADO_CONECTADO)
					return -1;
				tmpConexion = hiloConexion;
			}

			tmpConexion.escribir(buffer);
			return buffer.length;

		}
		
		
		
		

		

	}

	
}

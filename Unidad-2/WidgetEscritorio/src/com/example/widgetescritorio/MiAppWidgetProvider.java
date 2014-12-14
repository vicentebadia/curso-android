package com.example.widgetescritorio;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MiAppWidgetProvider extends AppWidgetProvider {

	// Constantes
	public static final String ACCION_INCR = "com.example.widgetescritorio.ACCION_INCR"; // Acción de incremento para identificar el tipo de intent
	public static final String EXTRA_PARAM = "com.example.widgetescritorio.EXTRA_ID"; // ID del Parámetro que pasamos en el intent

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
		for (int widgetId : widgetIds) { // Recorremos todos las instancias de nuestro widget
			actualizaWidget(context, widgetId); // Y los actualizamos
		}
	}

	public static void actualizaWidget(Context context, int widgetId) {
		int cont = incrementaContador(context, widgetId); // Aumentamos el contador y lo alamacenamos en las SharedPreferences
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget); // Obtenemos la instancia de la vista remota (el widget)
		remoteViews.setTextViewText(R.id.textView1, "Contador: " + cont); // Actualizamos el texto del widget

		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		remoteViews.setOnClickPendingIntent(R.id.analogClock1, pendingIntent); // Al pulsar sobre el RELOJ del widget lanzamos el pendingIntent asociado

		intent = new Intent(context, MiAppWidgetProvider.class);
		intent.setAction(ACCION_INCR);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		intent.putExtra(EXTRA_PARAM, "otro parámetro");
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.textView1, pendingIntent); // Al pulsar sobre el TEXTO del widget lanzamos el pendingIntent asociado

		AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews); // Actualizamos el widget
	}

	private static int incrementaContador(Context context, int widgetId) {
		SharedPreferences prefs = context.getSharedPreferences("contadores", Context.MODE_PRIVATE);
		int cont = prefs.getInt("cont_" + widgetId, 0);
		cont++;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("cont_" + widgetId, cont);
		editor.commit();
		return cont;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		AppWidgetManager mgr = AppWidgetManager.getInstance(context); // <-- PARECE QUE NO SE USA
		if (intent.getAction().equals(ACCION_INCR)) { // Si el intent corresponde a una acción de incrementar
			int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID); // Obtenemos la info de los extras
			String param = intent.getStringExtra(EXTRA_PARAM); // Pasamos a String el dato de param
			Toast.makeText(context, "Parámetro:" + param, Toast.LENGTH_SHORT).show(); // Y lo mostramos por Toast
			actualizaWidget(context, widgetId); // Refrescamos la vista del widget
		}
		super.onReceive(context, intent); // Llamada al ancestro
	}

}

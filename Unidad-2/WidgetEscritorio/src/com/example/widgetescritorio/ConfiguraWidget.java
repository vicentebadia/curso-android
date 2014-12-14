package com.example.widgetescritorio;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

// Al agregar un widget al escritorio, esta actividad se ejecutará, permitiéndonos fijar al valor
// incial del contador que se muestra sobre el reloj
public class ConfiguraWidget extends Activity {

	int widgetId;
	EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configura_widget);
		editText = (EditText) findViewById(R.id.editText1);
		setResult(RESULT_CANCELED);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			finish();
		}
		widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}
	}

	public void buttonOK(View view) {
		int cont;
		try {
			cont = Integer.parseInt(editText.getText().toString());
		} catch (Exception e) {
			Toast.makeText(this, "No es un número", Toast.LENGTH_SHORT).show();
			return;
		}
		SharedPreferences prefs = getSharedPreferences("contadores", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("cont_" + widgetId, cont);
		editor.commit();
		MiAppWidgetProvider.actualizaWidget(this, widgetId);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

}

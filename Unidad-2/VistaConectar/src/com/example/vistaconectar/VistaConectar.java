package com.example.vistaconectar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VistaConectar extends LinearLayout {
	   private EditText ip;
	   private EditText puerto;
	   private TextView estado;
	   private Button conectar;
	 
	   public VistaConectar(Context context, AttributeSet attrs) {
	       super(context, attrs);
	       
	       LayoutInflater.from(context).inflate(R.layout.conectar,this,true);  
	       
	       ip = (EditText) findViewById(R.id.ip);
	       puerto = (EditText) findViewById(R.id.puerto);
	       estado = (TextView) findViewById(R.id.estado);
	       conectar = (Button) findViewById(R.id.conectar);
	   }
	}


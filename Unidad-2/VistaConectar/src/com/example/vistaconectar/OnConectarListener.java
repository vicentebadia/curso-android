package com.example.vistaconectar;
 
public interface OnConectarListener {
   void onConectar(String ip, int puerto);
   void onConectado(String ip, int puerto);
   void onDesconectado();
   void onError(String mensage);
}


package com.example.vistaconectar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity implements OnConectarListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        VistaConectar conectar = (VistaConectar) findViewById(R.id.vistaConectar);
        conectar.setOnConectarListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onConectar(String ip, int puerto) {
		Toast.makeText(getApplicationContext(),"Conectando "+ip+":"+puerto,Toast.LENGTH_SHORT).show();	
	}


	@Override
	public void onConectado(String ip, int puerto) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDesconectado() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onError(String mensage) {
		// TODO Auto-generated method stub
		
	}
    
}

package com.excilys.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Récupération du TelephonyManager
		TelephonyManager manager = (TelephonyManager) this.getSystemService(MainActivity.TELEPHONY_SERVICE);
				 
		//Déclaration du listener
		manager.listen(new MyListener(), PhoneStateListener.LISTEN_CALL_STATE);
		
		//Le numéro à appeler: la syntaxe de l'Uri est tel:LENUMERODETEL
		Uri uri = Uri.parse("tel:+33102030405");		
		
		//Lancement de l'activité qui va déclencher l'affichage du dialer
		Intent intent = new Intent(Intent.ACTION_DIAL,uri);
		startActivity(intent);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class MyListener extends PhoneStateListener {

	    @Override
	    public void onCallStateChanged(int state, String number ) {
	
	    	super.onCallStateChanged(state, number);
	    	
	    	switch(state) {
	    	case TelephonyManager.CALL_STATE_RINGING:
	    		Log.i(MyListener.class.getName(), "Le numéro " + number + " est en train de sonner");
	    		break;
	    	case TelephonyManager.CALL_STATE_IDLE:
	    		Log.i(MyListener.class.getName(), "En standby. Rien ne se passe");
	    		break;
	    	case TelephonyManager.CALL_STATE_OFFHOOK:
	    		Log.i(MyListener.class.getName(), "Soit un appel est en communication, en train d'être composé, ou mis en attente: " + number);
	    		break;
	    	}
	        
	    }
	}
	
	
	
}

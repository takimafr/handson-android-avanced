package com.excilys.formation.parlezvous;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button animationNew;
	private Button animationOld;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		animationNew = (Button) findViewById(R.id.animationNew);
		animationOld = (Button) findViewById(R.id.animationOld);

		animationNew.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, NewAnimationActivity.class);
				startActivity(intent);
			}
		});

		animationOld.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, OldAnimationActivity.class);
				startActivity(intent);
			}
		});

	}
}

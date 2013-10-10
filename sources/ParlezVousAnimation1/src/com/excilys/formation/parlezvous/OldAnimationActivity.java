package com.excilys.formation.parlezvous;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class OldAnimationActivity extends Activity {

	private Button button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new);

		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doAnimation(v);

			}

		});

	}

	private void doAnimation(View v) {
		v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation));
	}
}

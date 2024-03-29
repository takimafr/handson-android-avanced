package com.excilys.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.excilys.android.fragments.ImageViewerFragment;
import com.excilys.android.fragments.NavigationFragment.NavigationButton;
import com.excilys.android.fragments.NavigationFragment.OnNavigationButtonClickListener;

public class MainActivity extends FragmentActivity implements OnNavigationButtonClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Va récupérer le two-pane layout
		setContentView(R.layout.activity_main);

	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onNavigationButtonClick(NavigationButton button) {
		// On récupère le contenu du fragment image_viewer
		ImageViewerFragment imageViewerFragment = (ImageViewerFragment) getSupportFragmentManager().findFragmentById(R.id.image_viewer_fragment);

		if (imageViewerFragment != null) {
			Log.i(MainActivity.class.getName(),"Clic sur le bouton " + button);
			
			//Traitements à faire ici
			switch(button) {
			case NEXT:
				imageViewerFragment.showNext();
				break;
			case PREVIOUS:
				imageViewerFragment.showPrevious();
				break;
			case PLAY:
				break;
			}
		}	
	}


}

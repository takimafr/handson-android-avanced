package com.excilys.android;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.excilys.android.audio.AudioPlayer;
import com.excilys.android.fragments.ImageViewerFragment;
import com.excilys.android.fragments.NavigationFragment;
import com.excilys.android.fragments.NavigationFragment.OnNavigationButtonClickListener;
import com.excilys.android.service.MusicService;
import com.excilys.android.service.MusicService.Callback;
import com.excilys.android.service.MusicService.LocalBinder;
import com.excilys.android.wrapper.AudioFileWrapper;

public class MainActivity extends FragmentActivity implements OnNavigationButtonClickListener, Callback {

	public static final String TAG = MainActivity.class.getSimpleName();

	private NavigationFragment navigationFragment;

	private ImageViewerFragment imageViewerFragment;

	private AudioPlayer audioPlayer;

	protected MusicService service;

	/*
	 * ACTIVITY LIFECYCLE
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Va récupérer le two-pane layout
		setContentView(R.layout.activity_main);

		// On récupère le contenu du fragment image_viewer
		imageViewerFragment = (ImageViewerFragment) getSupportFragmentManager().findFragmentById(R.id.image_viewer_fragment);
		navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_fragment);

		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioPlayer = new AudioPlayer(am);

		Intent intent = new Intent(this, MusicService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder ibinder) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) ibinder;
			service = binder.getService();
			service.setCallback(MainActivity.this);
			service.load();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
		audioPlayer.release();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onNavigationButtonClick(NavigationFragment.NavigationState button) {

		if (imageViewerFragment != null && navigationFragment != null) {
			Log.i(MainActivity.class.getName(), "Clic sur le bouton " + button);

			// Traitements à faire ici
			switch (button) {
			case NEXT:
				if (audioPlayer.next()) {
					imageViewerFragment.showNext();
				}
				break;
			case PREVIOUS:
				if (audioPlayer.previous()) {
					imageViewerFragment.showPrevious();
				}
				break;
			case PLAY:

				if (audioPlayer.isPlaying()) {
					audioPlayer.pause();
				} else {
					audioPlayer.play();
				}
				break;
			}
		}
	}

	private void updateImageViewerFragment(List<AudioFileWrapper> result) {
		if (imageViewerFragment != null) {
			imageViewerFragment.updateViewFlipper(result);
			imageViewerFragment.updateSongCount(result.size());
		}
	}

	private void toggleProgressBar(boolean show) {
		if (imageViewerFragment != null) {
			imageViewerFragment.toogleProgressBar(show);
		}
	}

	@Override
	public void onLoad(final List<AudioFileWrapper> list) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				updateImageViewerFragment(list);
				toggleProgressBar(false);
				audioPlayer.load(list);
			}
		});

	}

}

package com.excilys.android;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.excilys.android.fragments.ImageViewerFragment;
import com.excilys.android.fragments.NavigationFragment;
import com.excilys.android.fragments.NavigationFragment.NavigationButton;
import com.excilys.android.fragments.NavigationFragment.OnNavigationButtonClickListener;
import com.excilys.android.wrapper.AudioFileWrapper;

public class MainActivity extends FragmentActivity implements
		OnNavigationButtonClickListener {

	public static final String TAG = "MainActivity";
	
	private AsyncTask<Void, Void, List<AudioFileWrapper>> audioFileScannerTask;
	
	private List<AudioFileWrapper> audioFiles;
	
	private int currentPosition;
	
	/*
	 * ACTIVITY LIFECYCLE
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Va récupérer le two-pane layout
		setContentView(R.layout.activity_main);
		
		currentPosition = 0;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (audioFileScannerTask == null) {
			toogleProgressBar(true);
			audioFileScannerTask = new AudioFileScannerTask()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (audioFileScannerTask != null) {
			audioFileScannerTask.cancel(true);
			audioFileScannerTask = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onNavigationButtonClick(NavigationButton button) {
		// On récupère le contenu du fragment image_viewer
		ImageViewerFragment imageViewerFragment = (ImageViewerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.image_viewer_fragment);
		NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_fragment);

		if (imageViewerFragment != null && navigationFragment != null) {
			Log.i(MainActivity.class.getName(), "Clic sur le bouton " + button);

			// Traitements à faire ici
			switch (button) {
			case NEXT:
				imageViewerFragment.showNext();
				currentPosition++;
				if(currentPosition == audioFiles.size())
					currentPosition = 0;
				if(navigationFragment.getMediaPlayer().isPlaying())
					navigationFragment.playPauseSong(true,audioFiles.get(currentPosition).getAudioFile().getAbsolutePath());
				break;
			case PREVIOUS:
				imageViewerFragment.showPrevious();
				currentPosition--;
				if(currentPosition == -1)
					currentPosition = audioFiles.size()-1;
				if(navigationFragment.getMediaPlayer().isPlaying())
					navigationFragment.playPauseSong(true,audioFiles.get(currentPosition).getAudioFile().getAbsolutePath());
				break;
			case PLAY:
				navigationFragment.playPauseSong(false,audioFiles.get(currentPosition).getAudioFile().getAbsolutePath());
				break;
			}
		}
	}
	
	private void updateImageViewerFragment(List<AudioFileWrapper> result) {
		ImageViewerFragment imageViewerFragment = (ImageViewerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.image_viewer_fragment);

		if (imageViewerFragment != null) {
			imageViewerFragment.updateViewFlipper(result);
			imageViewerFragment.updateSongCount(result.size());
		}
	}
		
	private void toogleProgressBar(boolean show) {
		ImageViewerFragment imageViewerFragment = (ImageViewerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.image_viewer_fragment);

		if (imageViewerFragment != null) {
			imageViewerFragment.toogleProgressBar(show);
		}
	}

	public class AudioFileScannerTask extends AsyncTask<Void, Void, List<AudioFileWrapper>> {

		@Override
		protected List<AudioFileWrapper> doInBackground(Void... params) {
			return loadAudioFilesFromSDCard();
		}

		@Override
		protected void onPostExecute(List<AudioFileWrapper> result) {
			super.onPostExecute(result);
			updateImageViewerFragment(result);
			toogleProgressBar(false);
			audioFiles = result;
		}
		
		private List<AudioFileWrapper> loadAudioFilesFromSDCard() {

			Map<String,Bitmap> albumCovers = new HashMap<String, Bitmap>();
			
			String storageState = Environment.getExternalStorageState();
			if (!(Environment.MEDIA_MOUNTED.equals(storageState) || Environment.MEDIA_MOUNTED_READ_ONLY
					.equals(storageState)))
				return new ArrayList<AudioFileWrapper>();

			// On récupère le dossier /mnt/sdcard/Music
			File musicDirectory = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
			if (!musicDirectory.exists()) {
				throw new RuntimeException("Aucun répertoire Music détecté");
			}

			return readAudioFiles(albumCovers, musicDirectory);
		}
		
		private List<AudioFileWrapper> readAudioFiles(Map<String,Bitmap> albumCovers, File parent) {

			List<AudioFileWrapper> audioFiles = new ArrayList<AudioFileWrapper>();
			
			Log.i(TAG, "Lecture des fichiers audio: " + parent.getAbsolutePath());

			try {
				for (File file : parent.listFiles()) {
					if (file.isDirectory())
						audioFiles.addAll(readAudioFiles(albumCovers, file));

					else if (isAudioFile(file.getName())) {
						String parentPath = file.getParentFile().getPath();
						Log.i(TAG, "Nouvelle musique: " + file.getName());
						
						//Si la pochette de l'album a déjà été trouvée, pas la peine de recommencer.
						if(!albumCovers.containsKey(parentPath)) {
							Bitmap albumCover = readAlbumCover(file.getParentFile());
							if(albumCover != null)
							albumCovers.put(parentPath, albumCover);
						}
						
						AudioFileWrapper afw = new AudioFileWrapper();
						afw.setAlbumCoverBitmap(albumCovers.get(parentPath));
						afw.setAudioFile(file);
						
						audioFiles.add(afw);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "Une exception s'est produite");
			}
			return audioFiles;
		}

		
		private Bitmap readAlbumCover(File parent) {

			Log.i(TAG, "Lecture des pochettes: " + parent.getAbsolutePath());
			Bitmap albumCover = null;
			
			try {
				for (File file : parent.listFiles()) {
					if (file.isDirectory()) {
						if((albumCover = readAlbumCover(file)) != null) {
							return albumCover;
						}
					}
					else if (isImageFile(file.getName())) {
						Log.i(TAG, "Nouvelle pochette: " + file.getName());
						albumCover = BitmapFactory.decodeFile(file.getAbsolutePath());
						return albumCover;
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "Une exception s'est produite");
			}
			Log.i(TAG,"Aucune pochette trouvée dans cet album");
			return null;
		}

		private boolean isImageFile(String fileName) {
			int i = fileName.lastIndexOf('.');
			if (i <= 0)
				return false;
			else {
				String ext = fileName.substring(i + 1);
				if ("png".equals(ext) || "jpg".equals(ext) || "bmp".equals(ext)
						|| "gif".equals(ext))
					return true;
				return false;
			}
		}
		
		private boolean isAudioFile(String fileName) {
			int i = fileName.lastIndexOf('.');
			if (i <= 0)
				return false;
			else {
				String ext = fileName.substring(i + 1);
				if ("mp3".equals(ext) || "mp4a".equals(ext) || "mp4".equals(ext)	|| "m4a".equals(ext))
					return true;
				return false;
			}
		}
	}

}

package com.excilys.android.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.excilys.android.R;

public class ImageViewerFragment extends Fragment {

	private static final String TAG = "ImageViewerFragment";
	
	private RelativeLayout mLayout;
	private ViewFlipper viewFlipper;

	private AsyncTask<Void, Void, List<Bitmap>> audioFileScannerTask;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Chargement du layout
		mLayout = (RelativeLayout) inflater.inflate(
				R.layout.fragment_image_viewer, container, false);

		// On récupère le viewFlipper
		viewFlipper = (ViewFlipper) mLayout.findViewById(R.id.image_viewer);

		return mLayout;
	}
	
	
	private void toogleProgressBar(boolean show) {
		if(show)
			mLayout.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
		else
			mLayout.findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
	}
	
	private void updateViewFlipper(List<Bitmap> albumCovers) {
		ImageView albumView = null;

		for(Bitmap coverBitmap : albumCovers) {
			albumView = new ImageView(getActivity());
			
			if(coverBitmap != null) {
				albumView.setImageBitmap(coverBitmap);
				viewFlipper.addView(albumView);
			}			
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		showNext();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(audioFileScannerTask == null) {
			toogleProgressBar(true);
			audioFileScannerTask = new AudioFileScannerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(audioFileScannerTask != null) {
			audioFileScannerTask.cancel(true);
			audioFileScannerTask = null;
		}
	}

	public void showPrevious() {
		viewFlipper.showPrevious();
	}

	public void showNext() {
		viewFlipper.showNext();
	}

	private class AudioFileScannerTask extends AsyncTask<Void, Void, List<Bitmap>> {

		@Override
		protected List<Bitmap> doInBackground(Void... params) {
			return loadAlbumCoversFromSDCard();
		}
		
		@Override
		protected void onPostExecute(List<Bitmap> result) {
			super.onPostExecute(result);
			updateViewFlipper(result);
			toogleProgressBar(false);
		}
		
		private List<Bitmap> loadAlbumCoversFromSDCard() {

			// On récupère le dossier /mnt/sdcard
			File sdcardDirectory = Environment.getExternalStorageDirectory();

			if (!sdcardDirectory.exists()) {
				throw new RuntimeException("Aucune sdcard détectée");
			}

			File musicDirectory = new File(sdcardDirectory.getAbsolutePath()
					+ "/Music");

			if (!musicDirectory.exists()) {
				throw new RuntimeException("Aucun répertoire Music détecté");
			}

			return readAlbumCovers(musicDirectory);
		}

		private List<Bitmap> readAlbumCovers(File parent) {

			Log.i(TAG, "Lecture des pochettes: " + parent.getAbsolutePath());
			List<Bitmap> albumCovers = new ArrayList<Bitmap>();

			try {
				for (File file : parent.listFiles()) {
					if (file.isDirectory())
						albumCovers.addAll(readAlbumCovers(file));
					else if (isImageFile(file.getName())) {
						Log.i(TAG, "Nouvelle pochette: " + file.getName());
						albumCovers.add(BitmapFactory.decodeFile(file.getAbsolutePath()));
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "Une exception s'est produite");
			}
			return albumCovers;
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
	}
	
}

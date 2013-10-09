package com.excilys.android.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

	private RelativeLayout mLayout;
	private ViewFlipper viewFlipper;

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
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ImageView albumView = null;

		// On récupère les images du dossier Music (de façon récursive)
		List<String> albumCovers= readAlbumCoversFromSDCard();

		// On parcourt la liste précédente et on instancie les vues correspondantes, avant de les ajouter au viewFlipper
		for(String cover : albumCovers) {
			Log.i(getTag(), "Nouvelle pochette: " + cover);
			albumView = new ImageView(getActivity());
			
			if(cover != null) {
				Bitmap bitmap = BitmapFactory.decodeFile(cover);
				albumView.setImageBitmap(bitmap);
				viewFlipper.addView(albumView);
			}
			
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		showNext();
	}

	public void showPrevious() {
		viewFlipper.showPrevious();
	}

	public void showNext() {
		viewFlipper.showNext();
	}

	private List<String> readAlbumCoversFromSDCard() {

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

	private List<String> readAlbumCovers(File parent) {

		Log.i(getTag(), "Lecture des pochettes: " + parent.getAbsolutePath());
		List<String> albumCovers = new ArrayList<String>();

		try {
			for (File file : parent.listFiles()) {
				if (file.isDirectory())
					albumCovers.addAll(readAlbumCovers(file));
				else if (isImageFile(file.getName())) {
					albumCovers.add(file.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			Log.e(getTag(), "Une exception s'est produite");
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

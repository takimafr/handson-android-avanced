package com.excilys.android.fragments;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.excilys.android.R;
import com.excilys.android.wrapper.AudioFileWrapper;

public class ImageViewerFragment extends Fragment {

	private static final String TAG = "ImageViewerFragment";

	private RelativeLayout mLayout;
	private ViewFlipper viewFlipper;

	
	/*
	 * FRAGMENT LIFECYCLE METHODS
	 */
	
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

		
		//On définit les animations à exécuter lors du changement de vue du viewflipper
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_flipper_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_flipper_out));

		return mLayout;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		showNext();
	}

	/*
	 * METHODS
	 */
	
	public void toogleProgressBar(boolean show) {
		if (show)
			mLayout.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
		else
			mLayout.findViewById(R.id.progress_bar).setVisibility(
					View.INVISIBLE);
	}

	public void updateViewFlipper(List<AudioFileWrapper> audioFiles) {
		
		viewFlipper.removeAllViews();
		
		ImageView albumView = null;

		for (AudioFileWrapper audioFile : audioFiles) {
			albumView = new ImageView(getActivity());

			if (audioFile.getAlbumCoverBitmap() != null) {
				albumView.setImageBitmap(audioFile.getAlbumCoverBitmap());

			}
			else {
				albumView.setImageDrawable(getResources().getDrawable(R.drawable.default_cover));
			}
			viewFlipper.addView(albumView);
		}
	}

	public void updateSongCount(int count) {
		TextView songCountView = (TextView) mLayout.findViewById(R.id.song_count);
		if(songCountView != null) {
			songCountView.setText(getString(R.string.song_count,count));
			songCountView.setVisibility(View.VISIBLE);
		}
	}	

	public void showPrevious() {
		viewFlipper.showPrevious();
	}

	public void showNext() {
		viewFlipper.showNext();
	}

	

}

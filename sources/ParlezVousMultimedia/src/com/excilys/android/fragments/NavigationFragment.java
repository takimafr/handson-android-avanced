package com.excilys.android.fragments;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.excilys.android.R;

public class NavigationFragment extends Fragment implements OnClickListener, OnAudioFocusChangeListener {

	private static final String TAG = "NavigationFragment";

	private OnNavigationButtonClickListener mCallback;

	private MediaPlayer mediaPlayer;

	private AudioManager am;

	public enum NavigationButton {
		PLAY, NEXT, PREVIOUS;
	}

	public interface OnNavigationButtonClickListener {
		void onNavigationButtonClick(NavigationButton button);
	}

	
	/*
	 * FRAGMENT LIFECYCLE METHODS
	 */
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.mCallback = (OnNavigationButtonClickListener) activity;
		} catch (ClassCastException e) {
			Log.w(getTag(),
					"Warning: activity must implement OnNavigationButtonClickListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Chargement du layout
		View v = inflater.inflate(R.layout.fragment_navigation, container,
				false);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		am = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);
		setMediaPlayer(new MediaPlayer());
	}

	@Override
	public void onPause() {
		super.onPause();
		stopSong();
		am = null;
		mediaPlayer = null;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		((ImageButton) (getView().findViewById(R.id.btn_previous)))
				.setOnClickListener(this);
		((ImageButton) (getView().findViewById(R.id.btn_play)))
				.setOnClickListener(this);
		((ImageButton) (getView().findViewById(R.id.btn_next)))
				.setOnClickListener(this);

	}

		
	
	
	/*
	 * METHODS
	 */
	
	public void playPauseSong(boolean change, String filePath) {
		if (change) {
			stopSong();
		}

		if (mediaPlayer.isPlaying()) {
			pauseSong();
		} else {
			if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()) {
				resetSong(filePath);
			}

			playSong();
		}
	}
	
	private void stopSong() {
		mediaPlayer.stop();
		mediaPlayer.reset();
		// On lache le focus
		am.abandonAudioFocus(this);
		// Affichage du bouton "Play"
		((ImageButton) getView().findViewById(R.id.btn_play))
				.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_action_9_av_play));
	}

	private void pauseSong() {
		mediaPlayer.pause();
		// On lache le focus
		am.abandonAudioFocus(this);
		// Affichage du bouton "Play"
		((ImageButton) getView().findViewById(R.id.btn_play))
				.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_action_9_av_play));
	}

	private void resetSong(String filePath) {
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			Log.i(TAG,e.getMessage());
		} catch (SecurityException e) {
			Log.i(TAG,e.getMessage());
		} catch (IllegalStateException e) {
			Log.i(TAG,e.getMessage());
		} catch (IOException e) {
			Log.i(TAG,e.getMessage());
		}
	}

	private void playSong() {

		// On demande le focus audio pour commencer la lecture
		int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			((ImageButton) getView().findViewById(R.id.btn_play))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_action_9_av_pause));
			mediaPlayer.start();
		}
	}

	

	/*
	 * LISTENERS
	 */
	
	@Override
	public void onClick(View v) {

		// On les ajoute dans un animation set
		Animation buttonClickAnimation = AnimationUtils.loadAnimation(
				getActivity(), R.anim.anim_button);
		// On lance l'animation
		v.startAnimation(buttonClickAnimation);

		switch (v.getId()) {
		case R.id.btn_previous:
			mCallback.onNavigationButtonClick(NavigationButton.PREVIOUS);
			break;
		case R.id.btn_play:
			mCallback.onNavigationButtonClick(NavigationButton.PLAY);
			break;
		case R.id.btn_next:
			mCallback.onNavigationButtonClick(NavigationButton.NEXT);
			break;
		default:
			break;
		}
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
			float newVolume = 0.1f * am
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			mediaPlayer.setVolume(newVolume, newVolume);
		} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
			float newVolume = 2f * am
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			mediaPlayer.setVolume(newVolume, newVolume);
		} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			mediaPlayer.start();
		} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS
				|| focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
			am.abandonAudioFocus(this);
			mediaPlayer.stop();
		}

	}
	

	/*
	 * GETTER/SETTER
	 */
	
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

}

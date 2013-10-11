package com.excilys.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.excilys.android.R;
import com.excilys.android.view.PlayButton;

public class NavigationFragment extends Fragment implements OnClickListener {

    private static final String TAG = "NavigationFragment";

    private OnNavigationButtonClickListener mCallback;

    private AudioManager am;

    private PlayButton playButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mCallback = (OnNavigationButtonClickListener) activity;
        } catch (ClassCastException e) {
            Log.w(getTag(), "Warning: activity must implement OnNavigationButtonClickListener");
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

	/*
     * FRAGMENT LIFECYCLE METHODS
	 */

    @Override
    public void onResume() {
        super.onResume();
        am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*stopSong();
		am = null;
		mediaPlayer = null;*/
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.btn_previous).setOnClickListener(this);
        playButton = (PlayButton) getView().findViewById(R.id.btn_play);
        playButton.setOnClickListener(this);
        getView().findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // On les ajoute dans un animation set
        Animation buttonClickAnimation = AnimationUtils.loadAnimation(
                getActivity(), R.anim.anim_button);
        // On lance l'animation
        v.startAnimation(buttonClickAnimation);

        switch (v.getId()) {
            case R.id.btn_previous:
                mCallback.onNavigationButtonClick(NavigationState.PREVIOUS);
                break;
            case R.id.btn_play:
                playButton.toggle();
                mCallback.onNavigationButtonClick(NavigationState.PLAY);
                break;
            case R.id.btn_next:
                mCallback.onNavigationButtonClick(NavigationState.NEXT);
                break;
            default:
                break;
        }
    }

    public enum NavigationState {
        PLAY, NEXT, PREVIOUS;
    }

	/*
	 * LISTENERS
	 */

    public interface OnNavigationButtonClickListener {

        void onNavigationButtonClick(NavigationState button);
    }

}

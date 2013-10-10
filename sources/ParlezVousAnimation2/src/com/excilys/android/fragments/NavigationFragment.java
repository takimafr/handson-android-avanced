package com.excilys.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import com.excilys.android.R;

public class NavigationFragment extends Fragment implements OnClickListener, AnimationListener {

	private OnNavigationButtonClickListener mCallback;
	
	public enum NavigationButton {
		PLAY,NEXT,PREVIOUS;
	}
	
	public interface OnNavigationButtonClickListener {
		void onNavigationButtonClick(NavigationButton button);
	}
	
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
		View v = inflater.inflate(R.layout.fragment_navigation, container, false);
		
		return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        
       
        
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
    	((ImageButton)(getView().findViewById(R.id.btn_previous))).setOnClickListener(this);
 		((ImageButton)(getView().findViewById(R.id.btn_play))).setOnClickListener(this);
 		((ImageButton)(getView().findViewById(R.id.btn_next))).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		
		//On définit deux animations à effectuer successivement
		
		Animation anim1 = new ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim1.setDuration(300);
		anim1.setInterpolator(new DecelerateInterpolator());
		
		Animation anim2 = new ScaleAnimation(1f, 1f/0.8f, 1f, 1f/0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim2.setDuration(300);
		anim2.setStartOffset(300);
		anim2.setInterpolator(new OvershootInterpolator(1.5f));
		
		//On les ajoute dans un animation set
		Animation buttonClickAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_button);
		//On lance l'animation
		v.startAnimation(buttonClickAnimation);
		
		switch(v.getId()) {
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
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
}

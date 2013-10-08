package com.excilys.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.excilys.android.R;

public class NavigationFragment extends Fragment implements OnClickListener {

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
	
}

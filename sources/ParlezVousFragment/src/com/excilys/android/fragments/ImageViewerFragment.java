package com.excilys.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.excilys.android.R;

public class ImageViewerFragment extends Fragment {

	private RelativeLayout mLayout;
	private ViewSwitcher viewSwitcher;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		
		// Chargement du layout
        mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_image_viewer, container, false);
        
        viewSwitcher = (ViewSwitcher)mLayout.findViewById(R.id.image_viewer);
        
        ImageView view = null;
        
        view = new ImageView(getActivity());
        view.setImageDrawable(getResources().getDrawable(R.drawable.album_rhcp));
        viewSwitcher.addView(view);
        
        view = new ImageView(getActivity());
        view.setImageDrawable(getResources().getDrawable(R.drawable.album_to));
        viewSwitcher.addView(view);
        
        return mLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        showNext();
    }
    
    public void showPrevious() {
    	viewSwitcher.showPrevious();
    }
    
    public void showNext() {
    	viewSwitcher.showNext();
    }

	
}

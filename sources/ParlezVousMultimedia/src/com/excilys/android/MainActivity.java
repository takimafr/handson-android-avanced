package com.excilys.android;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.excilys.android.audio.AudioFileManager;
import com.excilys.android.audio.AudioPlayer;
import com.excilys.android.fragments.ImageViewerFragment;
import com.excilys.android.fragments.NavigationFragment;
import com.excilys.android.fragments.NavigationFragment.OnNavigationButtonClickListener;
import com.excilys.android.wrapper.AudioFileWrapper;

import java.util.List;

public class MainActivity extends FragmentActivity implements OnNavigationButtonClickListener {

    @SuppressWarnings("unused")
    public static final String TAG = MainActivity.class.getSimpleName();

    private NavigationFragment navigationFragment;

    private ImageViewerFragment imageViewerFragment;

    private AudioFileScannerTask audioFileScannerTask;

    private List<AudioFileWrapper> audioFiles;

    private AudioPlayer audioPlayer;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (audioFileScannerTask == null) {
            toggleProgressBar(true);
            audioFileScannerTask = new AudioFileScannerTask();
            audioFileScannerTask.execute();
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
    protected void onDestroy() {
        super.onDestroy();
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

    public class AudioFileScannerTask extends AsyncTask<Void, Void, List<AudioFileWrapper>> {

        @Override
        protected List<AudioFileWrapper> doInBackground(Void... params) {
            AudioFileManager audioFileManager = new AudioFileManager();
            return audioFileManager.loadAudioFilesFromSDCard();
        }

        @Override
        protected void onPostExecute(List<AudioFileWrapper> result) {
            super.onPostExecute(result);
            updateImageViewerFragment(result);
            toggleProgressBar(false);
            audioFiles = result;
            audioPlayer.load(audioFiles);
        }
    }

}

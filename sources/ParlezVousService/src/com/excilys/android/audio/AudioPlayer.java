package com.excilys.android.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.excilys.android.wrapper.AudioFileWrapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AudioPlayer implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = AudioPlayer.class.getSimpleName();

    private final AudioManager am;

    private MediaPlayer mediaPlayer;

    private List<AudioFileWrapper> trackFiles;

    private int currentPosition;

    private boolean isPrepared;

    private boolean forceNotPlayingAfterLoading;

    public AudioPlayer(AudioManager audioManager) {
        this.am = audioManager;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
    }

    public void load(List<AudioFileWrapper> tracks) {
        this.trackFiles = tracks;
    }

    public void play() {
        if (isPrepared) {
            mediaPlayer.start();
            return;
        }

        // load the song and play it
        load(currentPosition);
    }

    private void playTrack() {
        am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mediaPlayer.start();
    }

    private void load(int position) {
        mediaPlayer.reset();

        // récupérer fichier audio
        AudioFileWrapper track = trackFiles.get(position);
        File audioFile = track.getAudioFile();
        String path = audioFile.getAbsolutePath();
        try {
            mediaPlayer.setDataSource(path);
            // charger le fichier audio de façon asynchrone
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, "an error ocurred during audio file preparing", e);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // le fichier est chargé et prêt à être joué
        isPrepared = true;

        // force to not play the track after loading
        if (forceNotPlayingAfterLoading) {
            forceNotPlayingAfterLoading = false;
            return;
        }

        playTrack();
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            am.abandonAudioFocus(this);
        }
    }

    public boolean next() {
        currentPosition++;

        if (currentPosition >= trackFiles.size()) {
            currentPosition = trackFiles.size();
            return false;
        }

        changeTrack();
        return true;
    }

    public boolean previous() {

        currentPosition--;

        if (currentPosition < 0) {
            currentPosition = 0;
            return false;
        }

        changeTrack();
        return true;
    }

    /**
     * Used in {@link #previous()} and {@link #next()}
     */
    private void changeTrack() {

        forceNotPlayingAfterLoading = !mediaPlayer.isPlaying();

        isPrepared = false;
        load(currentPosition);
    }

    public void stop() {
        // On lache le focus
        am.abandonAudioFocus(this);
        mediaPlayer.stop();
        mediaPlayer.reset();
        isPrepared = false;
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            float newVolume = 0.1f * am.getStreamVolume(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(newVolume, newVolume);
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
            float newVolume = 2f * am.getStreamVolume(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(newVolume, newVolume);
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            mediaPlayer.start();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            stop();
        }

    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
}

package com.excilys.android.wrapper;

import java.io.File;

import android.graphics.Bitmap;

public class AudioFileWrapper {

	private Bitmap albumCoverBitmap;
	
	private File audioFile;

	public Bitmap getAlbumCoverBitmap() {
		return albumCoverBitmap;
	}

	public void setAlbumCoverBitmap(Bitmap albumCoverBitmap) {
		this.albumCoverBitmap = albumCoverBitmap;
	}
	
	public File getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(File audioFile) {
		this.audioFile = audioFile;
	}
	
	
}

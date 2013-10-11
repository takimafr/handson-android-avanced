package com.excilys.android.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.excilys.android.audio.AudioFileManager;
import com.excilys.android.wrapper.AudioFileWrapper;

public class MusicService extends Service {

	private LocalBinder binder = new LocalBinder();
	private Handler handler;
	private HandlerThread handlerThread;

	private Callback callback;

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void onCreate() {
		super.onCreate();
		handlerThread = new HandlerThread(MusicService.class.getSimpleName());
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
	}

	public void load() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				AudioFileManager audioFileManager = new AudioFileManager();
				List<AudioFileWrapper> list = audioFileManager.loadAudioFilesFromSDCard();
				if (callback != null) {
					callback.onLoad(list);
				}
			}
		});
	}

	public class LocalBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	public interface Callback {
		public void onLoad(List<AudioFileWrapper> list);
	}

}

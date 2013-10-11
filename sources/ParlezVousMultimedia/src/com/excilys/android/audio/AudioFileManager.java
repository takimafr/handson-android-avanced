package com.excilys.android.audio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.excilys.android.wrapper.AudioFileWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioFileManager {

    private static final String TAG = AudioFileManager.class.getSimpleName();

    public List<AudioFileWrapper> loadAudioFilesFromSDCard() {
        Map<String, Bitmap> albumCovers = new HashMap<String, Bitmap>();

        // Load from external storage
        // On récupère le dossier /mnt/sdcard/Music
        File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        String storageState = Environment.getExternalStorageState();
        if (!hasExternalStorage(storageState) || !musicDirectory.exists()) {
            throw new IllegalStateException("External storage indisponible ou aucun répertoire Music détecté");
        }

        return readAudioFiles(albumCovers, musicDirectory);
    }

    private boolean hasExternalStorage(String storageState) {
        return Environment.MEDIA_MOUNTED.equals(storageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState);
    }

    private List<AudioFileWrapper> readAudioFiles(Map<String, Bitmap> albumCovers, File parent) {

        List<AudioFileWrapper> audioFiles = new ArrayList<AudioFileWrapper>();

        Log.i(TAG, "Lecture des fichiers audio: " + parent.getAbsolutePath());

        for (File file : parent.listFiles()) {
            if (file.isDirectory()) {
                audioFiles.addAll(readAudioFiles(albumCovers, file));
            } else if (isAudioFile(file.getName())) {
                String parentPath = file.getParentFile().getPath();
                Log.i(TAG, "Nouvelle musique: " + file.getName());

                //Si la pochette de l'album a déjà été trouvée, pas la peine de recommencer.
                if (!albumCovers.containsKey(parentPath)) {
                    Bitmap albumCover = readAlbumCover(file.getParentFile());
                    if (albumCover != null) {
                        albumCovers.put(parentPath, albumCover);
                    }
                }

                AudioFileWrapper afw = new AudioFileWrapper();
                afw.setAlbumCoverBitmap(albumCovers.get(parentPath));
                afw.setAudioFile(file);

                audioFiles.add(afw);
            }
        }
        return audioFiles;
    }

    private Bitmap readAlbumCover(File parent) {

        Log.i(TAG, "Lecture des pochettes: " + parent.getAbsolutePath());
        Bitmap albumCover = null;

        for (File file : parent.listFiles()) {
            if (file.isDirectory()) {
                if ((albumCover = readAlbumCover(file)) != null) {
                    return albumCover;
                }
            } else if (isImageFile(file.getName())) {
                Log.i(TAG, "Nouvelle pochette: " + file.getName());
                albumCover = BitmapFactory.decodeFile(file.getAbsolutePath());
                return albumCover;
            }
        }
        Log.i(TAG, "Aucune pochette trouvée dans cet album");
        return null;
    }

    private boolean isImageFile(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i <= 0)
            return false;
        else {
            String ext = fileName.substring(i + 1);
            if ("png".equals(ext) || "jpg".equals(ext) || "bmp".equals(ext)
                    || "gif".equals(ext))
                return true;
            return false;
        }
    }

    private boolean isAudioFile(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i <= 0)
            return false;
        else {
            String ext = fileName.substring(i + 1);
            if ("mp3".equals(ext) || "mp4a".equals(ext) || "mp4".equals(ext) || "m4a".equals(ext))
                return true;
            return false;
        }
    }

}

package za.skablerpro.beatmusic.repositories;


import static za.skablerpro.beatmusic.MainActivity.songs;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import za.skablerpro.beatmusic.models.AudioFile;

/*
 *Singleton pattern
 *
 */
public class AudioFileRepository {
    Context mContext;

    private static AudioFileRepository repositoryInstance;
    private ArrayList<AudioFile> allAudioFiles = new ArrayList<>();

    public static AudioFileRepository getRepositoryInstance() {
        if (repositoryInstance == null) {
            repositoryInstance = new AudioFileRepository();
        }
        return repositoryInstance;
    }

    //    get audio from storage
    public MutableLiveData<ArrayList<AudioFile>> getAllAudioFiles() {
        MutableLiveData<ArrayList<AudioFile>> audios = new MutableLiveData<>();
        audios.setValue(allAudioFiles);
        return audios;
    }

    public void updateAudioRepo(ArrayList<AudioFile> newList){
        setAudioFiles(newList);
    }
    private void setAudioFiles(ArrayList<AudioFile> audioList) {
        allAudioFiles = audioList;
    }

}

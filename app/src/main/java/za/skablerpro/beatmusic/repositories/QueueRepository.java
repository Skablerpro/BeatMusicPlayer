package za.skablerpro.beatmusic.repositories;

import static za.skablerpro.beatmusic.MainActivity.queuedSongs;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import za.skablerpro.beatmusic.models.AudioFile;

public class QueueRepository {
    private static QueueRepository repositoryInstance;
    private ArrayList<AudioFile> queuedFiles = new ArrayList<>();

    public static QueueRepository getRepositoryInstance() {
        if (repositoryInstance == null) {
            repositoryInstance = new QueueRepository();
        }
        return repositoryInstance;
    }

    //    get audio from storage
    public MutableLiveData<ArrayList<AudioFile>> getQueuedFiles() {
        setAudioFiles();
        MutableLiveData<ArrayList<AudioFile>> queueSongs = new MutableLiveData<>();
        queueSongs.setValue(queuedFiles);
        return queueSongs;
    }

    private void setAudioFiles() {

        if (queuedSongs != null){
            queuedFiles = queuedSongs;
        }
    }

    public void addToQueue(AudioFile newSong){
        queuedFiles.add(newSong);
    }

    public void updateQueue(ArrayList<AudioFile> newQueue){
        queuedFiles.clear();
        queuedFiles.addAll(newQueue);
    }
}

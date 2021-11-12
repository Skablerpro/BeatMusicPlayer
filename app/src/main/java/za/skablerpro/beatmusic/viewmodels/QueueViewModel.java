package za.skablerpro.beatmusic.viewmodels;

import static za.skablerpro.beatmusic.MainActivity.songs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import za.skablerpro.beatmusic.models.AudioFile;
import za.skablerpro.beatmusic.repositories.QueueRepository;

public class QueueViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<AudioFile>> mQueueFiles;
    private QueueRepository mQueueRepo;

    public QueueViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    public void init(){
        if (mQueueFiles != null){
            return;
        }

        mQueueRepo = QueueRepository.getRepositoryInstance();
        mQueueFiles = mQueueRepo.getQueuedFiles();
    }

    public LiveData<ArrayList<AudioFile>> getQueuedFiles(){
        return mQueueFiles;
    }

//    public void addAudioFiles(final ArrayList<AudioFile> audios){
//        mAudioFiles.postValue(audios);
//    }

    public void updateQueue(ArrayList<AudioFile> newQueue){
            mQueueRepo.updateQueue(newQueue);
    }

    public void addToQueue(AudioFile newSong){
        mQueueRepo.addToQueue(newSong);
    }
}

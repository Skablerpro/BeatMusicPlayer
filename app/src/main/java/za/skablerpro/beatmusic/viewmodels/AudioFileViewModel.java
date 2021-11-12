package za.skablerpro.beatmusic.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import za.skablerpro.beatmusic.models.AudioFile;
import za.skablerpro.beatmusic.repositories.AudioFileRepository;

public class AudioFileViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<AudioFile>> mAudioFiles;
    private AudioFileRepository mAudioRepo;

    public AudioFileViewModel(@NonNull Application application) {
        super(application);

        init();

    }

    public void init(){
        if (mAudioFiles != null){
            return;
        }

        mAudioRepo = AudioFileRepository.getRepositoryInstance();
        mAudioFiles = mAudioRepo.getAllAudioFiles();
    }

    public LiveData<ArrayList<AudioFile>> getAudioFiles(){
        return mAudioFiles;
    }

//    public void addAudioFiles(final ArrayList<AudioFile> audios){
//        mAudioFiles.postValue(audios);
//    }

    public void updateSongs(ArrayList<AudioFile> newList){
        mAudioRepo.updateAudioRepo(newList);
    }
}

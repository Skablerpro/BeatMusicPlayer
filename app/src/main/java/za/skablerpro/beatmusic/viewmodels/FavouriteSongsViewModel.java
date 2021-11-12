package za.skablerpro.beatmusic.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import za.skablerpro.beatmusic.models.AudioFile;
import za.skablerpro.beatmusic.repositories.FavouriteSongsRepository;

public class FavouriteSongsViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<AudioFile>> mFavouriteAudioFiles;
    private FavouriteSongsRepository mFavouriteAudioRepo;

    public FavouriteSongsViewModel(@NonNull Application application) {
        super(application);

        init();

    }

    public void init(){
        if (mFavouriteAudioFiles != null){
            return;
        }

        mFavouriteAudioRepo = FavouriteSongsRepository.getRepositoryInstance();
        mFavouriteAudioFiles = mFavouriteAudioRepo.getFavouriteFiles();
    }

    public LiveData<ArrayList<AudioFile>> getFavouriteFiles(){
        return mFavouriteAudioFiles;
    }

//    public void addAudioFiles(final ArrayList<AudioFile> audios){
//        mAudioFiles.postValue(audios);
//    }

    public void updateSongs(){
        mFavouriteAudioRepo.getFavouriteFiles();
    }
    public void addToFavourites(AudioFile newFave){
        mFavouriteAudioRepo.addToFavourites(newFave);
    }

}

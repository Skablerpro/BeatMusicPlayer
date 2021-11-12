package za.skablerpro.beatmusic.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import za.skablerpro.beatmusic.repositories.AlbumsRepository;
import za.skablerpro.beatmusic.models.AlbumFile;
import za.skablerpro.beatmusic.repositories.AlbumsRepository;

public class AlbumViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<AlbumFile>> mAlbumFiles;
    private AlbumsRepository mAlbumRepo;

    public AlbumViewModel(@NonNull Application application) {
        super(application);

        init();

    }

    public void init(){
        if (mAlbumFiles != null){
            return;
        }

        mAlbumRepo = AlbumsRepository.getRepositoryInstance();
        mAlbumFiles = mAlbumRepo.getAllAlbumFiles();
    }

    public LiveData<ArrayList<AlbumFile>> getAlbumFiles(){
        return mAlbumFiles;
    }

//    public void addAudioFiles(final ArrayList<AudioFile> audios){
//        mAudioFiles.postValue(audios);
//    }

    public void updateAlbums(ArrayList<AlbumFile> newAlbums){
        mAlbumRepo.updateAlbums(newAlbums);
    }
}

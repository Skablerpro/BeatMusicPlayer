package za.skablerpro.beatmusic.repositories;

import static za.skablerpro.beatmusic.MainActivity.favouriteSongs;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import za.skablerpro.beatmusic.models.AudioFile;

public class FavouriteSongsRepository {
    private static FavouriteSongsRepository repositoryInstance;
    private ArrayList<AudioFile> favouriteAudioFiles = new ArrayList<>();

    public static FavouriteSongsRepository getRepositoryInstance() {
        if (repositoryInstance == null) {
            repositoryInstance = new FavouriteSongsRepository();
        }
        return repositoryInstance;
    }

    //    get audio from storage
    public MutableLiveData<ArrayList<AudioFile>> getFavouriteFiles() {
        setAudioFiles();
        MutableLiveData<ArrayList<AudioFile>> favourites = new MutableLiveData<>();
        favourites.setValue(favouriteAudioFiles);
        return favourites;
    }

    private void setAudioFiles() {

        if (favouriteSongs != null){
            favouriteAudioFiles = favouriteSongs;
        }
    }

    public void addToFavourites(AudioFile newFavourite){
        favouriteAudioFiles.add(newFavourite);
    }
}

package za.skablerpro.beatmusic.repositories;

import static za.skablerpro.beatmusic.MainActivity.albums;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import za.skablerpro.beatmusic.models.AlbumFile;

public class AlbumsRepository {
    private static AlbumsRepository repositoryInstance;
    private ArrayList<AlbumFile> allAlbumFiles = new ArrayList<>();

    public static AlbumsRepository getRepositoryInstance() {
        if (repositoryInstance == null) {
            repositoryInstance = new AlbumsRepository();
        }
        return repositoryInstance;
    }

    //    get audio from storage
    public MutableLiveData<ArrayList<AlbumFile>> getAllAlbumFiles() {
//        setAlbumFiles();
        MutableLiveData<ArrayList<AlbumFile>> albums = new MutableLiveData<>();
        albums.setValue(allAlbumFiles);
        return albums;
    }
    public void updateAlbums(ArrayList<AlbumFile> newAlbums){
        setAlbumFiles(newAlbums);
    }

    private void setAlbumFiles(ArrayList<AlbumFile> newAlbumList) {
        allAlbumFiles = newAlbumList;
    }
}

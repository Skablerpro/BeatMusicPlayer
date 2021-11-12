package za.skablerpro.beatmusic.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class AlbumFile {
    private String albumName;
    private String albumArtist;
    private String albumArtPath;
    private ArrayList<AudioFile> albumSongs;
    private String numOfSongs;
    private String albumDuration;
    private String albumYear;
    private Long albumId;



    public AlbumFile(String albumName, String albumArtist, String path, String numOfSongs, ArrayList<AudioFile> albumSongs, String albumDuration, String albumYear, Long albumId) {
        this.albumName = albumName;
        this.albumArtist = albumArtist;
        this.albumSongs = albumSongs;
        this.albumArtPath = path;
        this.numOfSongs = numOfSongs;
        this.albumDuration = albumDuration;
        this.albumYear = albumYear;
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getAlbumArtPath(String index) {
        if (albumSongs.size() > 0) {
            albumArtPath = albumSongs.get(0).getPath();
        }
        return albumArtPath;
    }
    public String getAlbumArtPath() {
        if (albumSongs.size() > 0) {
            albumArtPath = albumSongs.get(0).getPath();
        }
        return albumArtPath;
    }

    public String getNumOfSongs() {
        return numOfSongs;
    }

    public void setNumOfSongs(String numOfSongs) {
        this.numOfSongs = numOfSongs;
    }

    public void setAlbumArtPath(String albumArtPath) {
        albumArtPath = albumSongs.get(0).getPath();
        this.albumArtPath = albumArtPath;
    }

    public Long getAlbumId() {
        return albumId;
    }


    public ArrayList<AudioFile> getAlbumSongs() {
        return albumSongs;
    }

    public void setAlbumSongs(ArrayList<AudioFile> albumSongs) {
        this.albumSongs = albumSongs;
    }

    public String getAlbumDuration() {
        return albumDuration;
    }

    public void setAlbumDuration(String albumDuration) {
        this.albumDuration = albumDuration;
    }

    public String getAlbumYear() {
        return albumYear;
    }

    public void setAlbumYear(String albumYear) {
        this.albumYear = albumYear;
    }

    @NonNull
    @Override
    public String toString() {
        return albumName + " | Album Art path: " + getAlbumArtPath();
    }
}

package za.skablerpro.beatmusic.models;

import android.media.MediaMetadataRetriever;
import android.os.Parcelable;

public class AudioFile {
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String numOfPlays;
    private String playLength;
    private String id;
    private String albumArtist;
    private String albumYear;
    private String dateAdded;
    private Long album_id;

    public AudioFile(String path, String title, String artist, String album, String duration, String numOfPlays, String playLength, String id, String albumArtist, String albumYear, String dateAdded, Long album_id) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.numOfPlays = numOfPlays;
        this.playLength = playLength;
        this.id = id;
        this.albumArtist = albumArtist;
        this.albumYear = albumYear;
        this.dateAdded = dateAdded;
        this.album_id = album_id;

    }

    AudioFile() {
    }

    public Long getAlbum_id() {
        return album_id;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNumOfPlays() {
        return numOfPlays;
    }

    public void setNumOfPlays(String numOfPlays) {
        this.numOfPlays = numOfPlays;
    }

    public String getPlayLength() {
        return playLength;
    }

    public void setPlayLength(String playLength) {
        this.playLength = playLength;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getAlbumYear() {
        return albumYear;
    }
}

package za.skablerpro.beatmusic;


import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static za.skablerpro.beatmusic.ApplicationClass.ACTION_CLOSE;
import static za.skablerpro.beatmusic.ApplicationClass.ACTION_NEXT;
import static za.skablerpro.beatmusic.ApplicationClass.ACTION_PLAY;
import static za.skablerpro.beatmusic.ApplicationClass.ACTION_PREVIOUS;
import static za.skablerpro.beatmusic.ApplicationClass.ACTION_STOP;
import static za.skablerpro.beatmusic.ApplicationClass.CHANNEL_ID2;
import static za.skablerpro.beatmusic.MainActivity.TAG;
import static za.skablerpro.beatmusic.MainActivity.musicService;
import static za.skablerpro.beatmusic.MainActivity.repeatBoolean;
import static za.skablerpro.beatmusic.MainActivity.shuffleBoolean;
//import static za.skablerpro.beatmusic.MainActivity.repeatBoolean;
//import static za.skablerpro.beatmusic.MainActivity.shuffleBoolean;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import za.skablerpro.beatmusic.models.AudioFile;
import za.skablerpro.beatmusic.repositories.QueueRepository;
import za.skablerpro.beatmusic.viewmodels.QueueViewModel;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<AudioFile> playingQueue = new ArrayList<>();
    QueueViewModel mQueueViewModel;
    QueueRepository queueRepository;
    Uri uri;
    int position = -1;
    boolean randomBoolean = false;

    private static final int NOTIFICATION_ID = 2295;
    PlayingInterface playingInterface;
    MediaSessionCompat mediaSession;
    PlaybackStateCompat mPlaybackState;

    NotificationManager notificationManager;
    Notification notification;

    ArrayList<AudioFile> queueSongs;

    public static final String ACTION_SEEK_TO = "ActionSeekTo";
    MediaControllerCompat mController;
    //    MediaSessionManager mediaSessionManager;
    MediaControllerCompat.TransportControls transportControls;

    public static final String SONG_LAST_PLAYED = "SONG_LAST_PLAYED";
    public static final String MUSIC_FILE_OBJ = "MUSIC_FILE_OBJ";
    public static final String LAST_PROGRESS = "LAST_PROGRESS";
    public static final String MUSIC_FILE = "STORED_SONG";
    public static final String SONG_ARTIST = "ARTIST_NAME";
    public static final String SONG_TITLE = "SONG_TITLE";

    Gson gson;

//    Threads

    Thread nextThread, prevThread, play_pauseThread;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(getBaseContext(), "Beat");


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind ", "Method");
        return mBinder;
    }


    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        boolean start_playing = intent.getBooleanExtra("start_playing", false);
        String actionName = intent.getStringExtra("ActionName");
        String queueJson = intent.getStringExtra("queue_songs");

        Gson arrGson = new Gson();
        Type queueType = new TypeToken<ArrayList<AudioFile>>() {
        }.getType();
        queueSongs = arrGson.fromJson(queueJson, queueType);

//        Log.e(TAG, String.valueOf(queueSongs.size()));

        if (myPosition != -1) {
            playMedia(queueSongs, myPosition);
//            if (!start_playing) {
//                mediaPlayer.pause();
//            }
        }

        if (actionName != null) {
            switch (actionName) {
                case "playPause":
                    Toast.makeText(this, "playPause", Toast.LENGTH_SHORT).show();
                    if (playingInterface != null) {
                        playingInterface.playPauseBtnClicked();
                    }
                    break;
                case "previous":
                    Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
                    if (playingInterface != null) {
                        playingInterface.prevBtnClicked();
                    }
                    break;
                case "next":
                    Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
                    if (playingInterface != null) {
                        playingInterface.nextBtnClicked();
                    }
                    break;
//                case "seekTo":
//                    Toast.makeText(this, "Seek", Toast.LENGTH_SHORT).show();
//                    if (playingInterface != null) {
//                        playingInterface.seekTo(0);
//                    }
//                    break;
                case "stop":
                    Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
//                    if (playingInterface != null) {
//                    }
                    break;
            }
        }
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }


    public void playMedia(ArrayList<AudioFile> list, int startPosition) {
//        queueSongs.clear();
//        queueSongs.addAll(list);
        queueSongs = list;

        SharedPreferences preferences = getSharedPreferences(LAST_PROGRESS, MODE_PRIVATE);
        int progress = preferences.getInt(LAST_PROGRESS, 0);

        try {
            if (startPosition != -1 && queueSongs.size() > 0) {
                if (randomBoolean) {
                    position = getRandom(queueSongs.size() - 1);
                } else {
                    position = startPosition;
                }
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();

                }
                if (queueSongs != null) {
                    createMediaPlayer(position);
                    mediaPlayer.start();
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }


    }

    public void playRandom() {
        randomBoolean = true;
        playMedia(queueSongs, 0);
    }

    void start() {
        mediaPlayer.start();
    }

    boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    void pause() {
        mediaPlayer.pause();
    }

    void stop() {
        mediaPlayer.stop();
    }

    void release() {
        mediaPlayer.release();
    }

    int getDuration() {
        return mediaPlayer.getDuration();
    }

    int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    void seekTo(int position) {
        mediaPlayer.seekTo(position);

        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(mPlaybackState.STATE_PLAYING,
                        Long.parseLong(mediaPlayer.getCurrentPosition() + ""),
                        1.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SEEK_TO)
                .build();
        showNotification(R.drawable.ic_pause);
    }

    void createMediaPlayer(int positionInner) {
        position = positionInner;
        uri = Uri.parse(queueSongs.get(position).getPath());
        SharedPreferences.Editor editor = getSharedPreferences(SONG_LAST_PLAYED, MODE_PRIVATE).edit();
//        editor.putString(MUSIC_FILE, uri.toString());
//        editor.putString(SONG_ARTIST, musicFilesList.get(position).getArtist());
//        editor.putString(SONG_TITLE, musicFilesList.get(position).getTitle());

        gson = new Gson();
        String song_object = gson.toJson(queueSongs.get(position));
        editor.putString(MUSIC_FILE_OBJ, song_object);
        editor.apply();

        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    void onCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        //TODO: null safety for songsList
        if (playingInterface != null) {
//            shuffle and repeat check
            if (!shuffleBoolean && !repeatBoolean) {
                playingInterface.nextBtnClicked();
            } else if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(queueSongs.size() - 1);
                playingInterface.nextBtnClicked();
            } else if (!shuffleBoolean) {
                playingInterface.playPauseBtnClicked();
            } else {
                playingInterface.playPauseBtnClicked();
            }

//            play after song finishes
            if (mediaPlayer == null) {
                createMediaPlayer(position);
                mediaPlayer.start();
                onCompleted();
            }
        }

    }

    void setCallBack(PlayingInterface actionPlaying) {
        this.playingInterface = actionPlaying;
    }

    void showNotification(int playPauseBtn) {
        Intent contentIntent = new Intent(this, MainActivity.class);
        contentIntent.putExtra("position", queueSongs.indexOf(queueSongs.get(position)));
        contentIntent.putExtra("progress", mediaPlayer.getCurrentPosition());
        contentIntent.putExtra("notificationPlay", true);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, contentIntent, 0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getBroadcast(this, 0, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_CLOSE);
        PendingIntent deletePending = PendingIntent.getBroadcast(this, 0, deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

//        Intent seekIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_SEEK_TO);
//        PendingIntent seekPending = PendingIntent.getBroadcast(this, 0, seekIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] art;
        art = getAlbumArt(queueSongs.get(position).getPath());
        Bitmap album_art;
        if (art != null) {
            album_art = BitmapFactory.decodeByteArray(art, 0, art.length);
        } else {
            album_art = BitmapFactory.decodeResource(getResources(), R.drawable.skp);
        }

//        Palette p = createPaletteSync(album_art);
//        Palette.Swatch dominantSwatch = p.getDominantSwatch();
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, album_art)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, queueSongs.get(position).getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, queueSongs.get(position).getArtist())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(queueSongs.get(position).getDuration())).build());


        MediaSessionCompat.Callback mediaSessionCallbacks = new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                playingInterface.playPauseBtnClicked();
            }

            @Override
            public void onPause() {
                super.onPause();
                playingInterface.playPauseBtnClicked();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                playingInterface.nextBtnClicked();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                playingInterface.prevBtnClicked();
            }

            @Override
            public void onFastForward() {
                super.onFastForward();
            }

            @Override
            public void onRewind() {
                super.onRewind();
            }

            @Override
            public void onStop() {
                super.onStop();
                mediaPlayer.stop();
                mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(mPlaybackState.STATE_STOPPED,
                                Long.parseLong(mediaPlayer.getCurrentPosition() + ""),
                                1.0f)
                        .setActions(PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SEEK_TO)
                        .build();
                showNotification(R.drawable.ic_play);
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                mediaPlayer.seekTo((int) pos);

                if (mediaPlayer.isPlaying()) {
                    mPlaybackState = new PlaybackStateCompat.Builder()
                            .setState(mPlaybackState.STATE_PLAYING,
                                    Long.parseLong(mediaPlayer.getCurrentPosition() + ""),
                                    1.0f)
                            .setActions(PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PAUSE |
                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                    PlaybackStateCompat.ACTION_SEEK_TO)
                            .build();
                    showNotification(R.drawable.ic_pause);
                } else {
                    mPlaybackState = new PlaybackStateCompat.Builder()
                            .setState(mPlaybackState.STATE_PAUSED,
                                    Long.parseLong(mediaPlayer.getCurrentPosition() + ""),
                                    1.0f)
                            .setActions(PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PAUSE |
                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                    PlaybackStateCompat.ACTION_SEEK_TO)
                            .build();
                    showNotification(R.drawable.ic_play);
                }

            }

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                //on default, KEYCODE_MEDIA_PLAY calls onPlay
                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        };

        transportControls = mediaSession.getController().getTransportControls();

//        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mController = new MediaControllerCompat(this, mediaSession.getSessionToken());

        mediaSession.setCallback(mediaSessionCallbacks);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(mPlaybackState);
        mediaSession.setActive(true);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID2);
        notificationBuilder.setSmallIcon(playPauseBtn)
                .setLargeIcon(album_art)
                .setContentTitle(queueSongs.get(position).getTitle())
                .setContentText(queueSongs.get(position).getArtist())
                .addAction(R.drawable.ic_previous, "previous", prevPending)
                .addAction(playPauseBtn, "play", pausePending)
                .addAction(R.drawable.ic_next, "next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2)
                        .setShowCancelButton(true))
                .setContentIntent(contentPendingIntent)
                .setDeleteIntent(deletePending)
                .setColorized(true)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        notification = notificationBuilder.build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(NOTIFICATION_ID, notification);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        return retriever.getEmbeddedPicture();
    }

    private int getRandom(int i) {

        Random random = new Random();

        return random.nextInt(i + 1);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
    }


    //    private int getMusicState() {
//        if (mediaPlayer.isPlaying()) {
//            return PlaybackStateCompat.STATE_PLAYING;
//        } else {
//            return PlaybackStateCompat.STATE_PAUSED;
//        }
//    }
}



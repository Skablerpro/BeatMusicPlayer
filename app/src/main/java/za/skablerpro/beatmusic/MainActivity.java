package za.skablerpro.beatmusic;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

import static java.util.Collections.reverseOrder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import za.skablerpro.beatmusic.adapters.QueueAdapter;
import za.skablerpro.beatmusic.fragments.AlbumsFragment;
import za.skablerpro.beatmusic.fragments.ArtistsFragment;
import za.skablerpro.beatmusic.fragments.DetailsFragment;
import za.skablerpro.beatmusic.fragments.FavouritesFragment;
import za.skablerpro.beatmusic.fragments.FoldersFragment;
import za.skablerpro.beatmusic.fragments.PlaylistsFragment;
import za.skablerpro.beatmusic.fragments.SongsFragment;
import za.skablerpro.beatmusic.models.AlbumFile;
import za.skablerpro.beatmusic.models.AudioFile;
import za.skablerpro.beatmusic.viewmodels.AlbumViewModel;
import za.skablerpro.beatmusic.viewmodels.AudioFileViewModel;
import za.skablerpro.beatmusic.viewmodels.QueueViewModel;

public class MainActivity extends AppCompatActivity implements ServiceConnection, PlayingInterface {
    public static final String TAG = "MainActivity";
    public static final String SONG_LAST_PLAYED = "SONG_LAST_PLAYED";
    public static final String MUSIC_FILE_OBJ = "MUSIC_FILE_OBJ";
    public static final String LAST_PROGRESS = "LAST_PROGRESS";
    public static final String MUSIC_FILE = "STORED_SONG";
    public static final String SONG_ARTIST = "ARTIST_NAME";
    public static final String SONG_TITLE = "SONG_TITLE";
    Context mContext = this;

    public static final int REQUEST_CODE = 1;
    private AudioFileViewModel mAudioFileViewModel;
    private QueueViewModel mQueueViewModel;
    private AlbumViewModel mAlbumViewModel;


    public static ArrayList<AudioFile> songs;
    public static ArrayList<AudioFile> favouriteSongs;
    public static ArrayList<AudioFile> queuedSongs;
    public static ArrayList<AlbumFile> albums;


    public static boolean listBoolean = false, shuffleBoolean = false, repeatBoolean = false,
            favourites_play = false;

    //    From PlayerActivity
    static int position = -1;
    static ArrayList<AudioFile> playingQueue = new ArrayList<>();
    //    ArrayList<AudioFile> queueList = new ArrayList<>();
    ArrayList<AudioFile> allSongsList = new ArrayList<>();
    ArrayList<AudioFile> albumSongs = new ArrayList<>();

    private boolean album = false;
    private boolean listplay = false;
    static Uri uri;

    private Handler handler = new Handler();

    private Thread play_pauseThread, prevThread, nextThread;

    static MusicService musicService;
    NotificationManagerCompat notificationManager;
    private boolean notification_play = false;

    Gson gson;

    TextView tv_duration, tv_progress, tv_title, tv_artist;
    ImageView album_cover, prev_btn, play_btn, next_btn, shuffle_btn, repeat_btn;
    SeekBar seekBar;
    ProgressBar progressBar;
    RecyclerView recyclerView_queue;
    QueueAdapter mQueueAdapter;
    PlayingInterface playingInterface;
    MotionLayout motionLayout;

    String sortType = "name";
    boolean first_start = true;
    int progress_position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_title = findViewById(R.id.tv_title);
        tv_artist = findViewById(R.id.tv_artist);
        tv_duration = findViewById(R.id.tv_duration);
        tv_progress = findViewById(R.id.tv_progress);
        album_cover = findViewById(R.id.album_cover);
        seekBar = findViewById(R.id.seek_bar);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView_queue = findViewById(R.id.recyclerview_queue);
        album_cover.setClipToOutline(true);
        motionLayout = findViewById(R.id.parent_layout);

        prev_btn = findViewById(R.id.iv_previous);
        play_btn = findViewById(R.id.iv_play);
        next_btn = findViewById(R.id.iv_next);
        shuffle_btn = findViewById(R.id.iv_shuffle);
        repeat_btn = findViewById(R.id.iv_repeat);

        setSongsList();
        setQueue();
        setAlbums();
        initSongsList();
        initViews();

//        Intent intent = new Intent(this, MusicService.class);
//        bindService(intent, this, BIND_AUTO_CREATE);


        notificationManager = NotificationManagerCompat.from(this);
        initViewPager();

//        if (songs != null && songs.size() > 0) {
//            position = 0;
//            playingQueue = songs;
//            metaData(Uri.parse(playingQueue.get(position).getPath()));
//        }


//        playingQueue = mAudioFileViewModel.getAudioFiles().getValue();

        handler = new Handler();
//        audioObserver = new ExplorerActivity.AudioObserver(handler);
//        this.getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                true, audioObserver);

        Intent seekIntent = new Intent(getApplicationContext(), MusicService.class);
        seekIntent.setAction(MusicService.ACTION_SEEK_TO);
        startService(seekIntent);

        //        play Thread
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        progressBar.setProgress(mCurrentPosition);

                        tv_progress.setText(formattedTime(mCurrentPosition));

                        if (!musicService.isPlaying()) {
                            play_btn.setImageResource(R.drawable.ic_play);
                        } else {
                            play_btn.setImageResource(R.drawable.ic_pause);
                        }
                    }
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException e) {
                    Log.e(TAG, "state Change");
                }

            }
        });

        prev_btn.setOnClickListener(v -> {
            prevBtnClicked();
        });
        play_btn.setOnClickListener(v -> {
            playPauseBtnClicked();
        });
        next_btn.setOnClickListener(v -> {
            nextBtnClicked();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setAlbums() {
        //        Playing songs list view model
        mAlbumViewModel = ViewModelProviders.of(this).get(AlbumViewModel.class);
//        mAudioFileViewModel.init();
        mAlbumViewModel.updateAlbums(getAllAlbumFiles());
        mAlbumViewModel.getAlbumFiles().observe(this, new Observer<List<AlbumFile>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<AlbumFile> albumFiles) {
                Log.d(TAG + "->setAlbums", "AlbumList Changed!!");
//                mAudioAdapter.notifyDataSetChanged();
//                mQueueViewModel.updateQueue();
            }
        });
    }

    private void setQueue() {
        //        Playing songs list view model
        mQueueViewModel = ViewModelProviders.of(this).get(QueueViewModel.class);
//        mAudioFileViewModel.init();
        mQueueViewModel.updateQueue(getAllAudioFiles());
        mQueueViewModel.getQueuedFiles().observe(this, new Observer<List<AudioFile>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<AudioFile> audioFiles) {
                Log.d(TAG + "->setQueue", "Queue Changed!!");
//                mAudioAdapter.notifyDataSetChanged();
//                mQueueViewModel.updateQueue();
            }
        });

    }

    private void setSongsList() {

//        Playing songs list view model
        mAudioFileViewModel = ViewModelProviders.of(this).get(AudioFileViewModel.class);
//        mAudioFileViewModel.init();
        mAudioFileViewModel.updateSongs(getAllAudioFiles());
        mAudioFileViewModel.getAudioFiles().observe(this, new Observer<List<AudioFile>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<AudioFile> audioFiles) {
                Log.d(TAG + "->setSongsList", "SongsList Changed!!");
//                mAudioAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initViews() {
    }

    public void initSongsList() {
        try {
            if (position == -1) {
                position = 0;
            }


            if (mQueueViewModel.getQueuedFiles().getValue() != null
                    && (mQueueViewModel.getQueuedFiles().getValue().size() > 0)) {

                play_btn.setImageResource(R.drawable.ic_pause);
                uri = Uri.parse(mQueueViewModel.getQueuedFiles().getValue().get(position).getPath());
            }


            Intent intent = new Intent(this, MusicService.class);

            gson = new Gson();
            SharedPreferences preferences = getSharedPreferences(SONG_LAST_PLAYED, MODE_PRIVATE);
            String song_json = preferences.getString(MUSIC_FILE_OBJ, null);
            Log.e("Song Json", "" + song_json);
            int last_position = -1;
            if (song_json != null) {
                AudioFile song_object = gson.fromJson(song_json, AudioFile.class);
                for (int i = 0; i < mQueueViewModel.getQueuedFiles().getValue().size(); i++) {
                    if (mQueueViewModel.getQueuedFiles().getValue().get(i).getPath().equals(song_object.getPath())) {
                        last_position = i;
                    }
                }

//            last_position = songsList.indexOf(song_object);
                Log.e("last positon: ", String.valueOf(last_position));
            }


            if (mQueueViewModel.getQueuedFiles().getValue().size() > 0 && last_position != -1) {
                position = last_position;
                uri = Uri.parse(mQueueViewModel.getQueuedFiles().getValue().get(position).getPath());
            }
            intent.putExtra("servicePosition", position);
            Gson arrGson = new Gson();
            String queueJson = arrGson.toJson(mQueueViewModel.getQueuedFiles().getValue());
            intent.putExtra("queue_songs", queueJson);


            if (musicService != null) {

                musicService.stop();
                musicService.release();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e(TAG + "Foreground Service", " about to start" + position + " last position" + last_position);
                startForegroundService(intent);
            } else {
                startService(intent);
            }

//            startService(intent);

//        musicService.createMediaPlayer(position);
//        musicService.start();

            if (mQueueViewModel.getQueuedFiles().getValue().size() > 0) {
                mQueueAdapter = new QueueAdapter(this, mQueueViewModel.getQueuedFiles().getValue());

                recyclerView_queue.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,
                        false));
                recyclerView_queue.setAdapter(mQueueAdapter);
                metaData(uri);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void songClick(ArrayList<AudioFile> list, int newPosition) {
        position = newPosition;
        mQueueViewModel.updateQueue(list);
        musicService.playMedia(mQueueViewModel.getQueuedFiles().getValue(), position);
        metaData(Uri.parse(mQueueViewModel.getQueuedFiles().getValue().get(position).getPath()));

    }

    public void albumClick(String albumName) {
//        Intent albumIntent = new Intent(this, AlbumActivity.class);
//        albumIntent.putExtra("album_name", albumName);
//        startActivityForResult(albumIntent,2);

        //album details
        DetailsFragment detailsFragment = new DetailsFragment();

        Bundle data = new Bundle();
        data.putString("album_name", albumName);
        detailsFragment.setArguments(data);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fl_album_frag, detailsFragment)
                .commit();
        Log.e(TAG, "album clicked");

    }

    //    Player Controls

    //Next
    public void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (mQueueViewModel.getQueuedFiles().getValue().size() > 0) {

                    next_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (shuffleBoolean && !repeatBoolean) {
                                position = getRandom(mQueueViewModel.getQueuedFiles().getValue().size() - 1);
                            }
                            nextBtnClicked();
                        }
                    });
                }

            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            position = ((position + 1) % mQueueViewModel.getQueuedFiles().getValue().size());
            uri = Uri.parse(mQueueViewModel.getQueuedFiles().getValue().get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);

            tv_title.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getTitle());
            tv_artist.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            progressBar.setMax(musicService.getDuration() / 1000);

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;

                        seekBar.setProgress(mCurrentPosition);
                        progressBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });

            musicService.onCompleted();
            musicService.mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(musicService.mPlaybackState.STATE_PLAYING,
                            Long.parseLong(position + ""),
                            1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                            PlaybackStateCompat.ACTION_SEEK_TO)
                    .build();
            musicService.showNotification(R.drawable.ic_pause);

            musicService.start();

//            int audioSessionId = musicService.mediaPlayer.getAudioSessionId();
//            if (audioSessionId != -1) {
//                visualizer.setAudioSessionId(audioSessionId);
//            }
            play_btn.setImageResource(R.drawable.ic_pause);

        } else {
            musicService.stop();
            musicService.release();
            position = ((position + 1) % mQueueViewModel.getQueuedFiles().getValue().size());
            uri = Uri.parse(mQueueViewModel.getQueuedFiles().getValue().get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);


            tv_title.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getTitle());
            tv_artist.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getArtist());

            seekBar.setMax(musicService.getDuration() / 1000);
            progressBar.setMax(musicService.getDuration() / 1000);

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;

                        seekBar.setProgress(mCurrentPosition);
                        progressBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });

            musicService.onCompleted();

            if (musicService.isPlaying()) {
                musicService.mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(musicService.mPlaybackState.STATE_PLAYING,
                                Long.parseLong(position + ""),
                                1.0f)
                        .setActions(PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SEEK_TO)
                        .build();
                musicService.showNotification(R.drawable.ic_play);

                play_btn.setImageResource(R.drawable.ic_play);

            } else {
                musicService.mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(musicService.mPlaybackState.STATE_PLAYING,
                                Long.parseLong(position + ""),
                                1.0f)
                        .setActions(PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SEEK_TO)
                        .build();
                musicService.showNotification(R.drawable.ic_pause);
                play_btn.setImageResource(R.drawable.ic_pause);
            }
            musicService.start();
        }


//        int audioSessionId = musicService.mediaPlayer.getAudioSessionId();
//        if (audioSessionId != -1) {
//            visualizer.setAudioSessionId(audioSessionId);
//        }
    }

    //  Previous
    public void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();

                if (mQueueViewModel.getQueuedFiles().getValue().size() > 0) {
                    prev_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (shuffleBoolean && !repeatBoolean) {
                                position = getRandom(playingQueue.size() - 1);
                            }
                            prevBtnClicked();
                        }
                    });
                }
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            position = ((position - 1) < 0 ? (mQueueViewModel.getQueuedFiles().getValue().size() - 1) : (position - 1));
            uri = Uri.parse(mQueueViewModel.getQueuedFiles().getValue().get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);

            tv_title.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getTitle());
            tv_artist.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getArtist());

            seekBar.setMax(musicService.getDuration() / 1000);
            progressBar.setMax(musicService.getDuration() / 1000);


            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;

                        seekBar.setProgress(mCurrentPosition);
                        progressBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();

            play_btn.setImageResource(R.drawable.ic_pause);

            musicService.mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(musicService.mPlaybackState.STATE_PLAYING,
                            Long.parseLong(position + ""),
                            1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                            PlaybackStateCompat.ACTION_SEEK_TO)
                    .build();
            musicService.showNotification(R.drawable.ic_pause);
            musicService.start();

        } else {
            musicService.stop();
            musicService.release();
            position = ((position - 1) < 0 ? (mQueueViewModel.getQueuedFiles().getValue().size() - 1) : (position - 1));
            uri = Uri.parse(mQueueViewModel.getQueuedFiles().getValue().get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);

            tv_title.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getTitle());
            tv_artist.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getArtist());

            seekBar.setMax(musicService.getDuration() / 1000);
            progressBar.setMax(musicService.getDuration() / 1000);

            try {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (musicService != null) {
                            int mCurrentPosition = musicService.getCurrentPosition() / 1000;

                            seekBar.setProgress(mCurrentPosition);
                            progressBar.setProgress(mCurrentPosition);
                        }
                        handler.postDelayed(this, 1000);
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            musicService.onCompleted();


            if (musicService.isPlaying()) {
                musicService.mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(musicService.mPlaybackState.STATE_PLAYING,
                                Long.parseLong(position + ""),
                                1.0f)
                        .setActions(PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SEEK_TO)
                        .build();
                musicService.showNotification(R.drawable.ic_play);

                play_btn.setImageResource(R.drawable.ic_play);

            } else {
                musicService.mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(musicService.mPlaybackState.STATE_PLAYING,
                                Long.parseLong(position + ""),
                                1.0f)
                        .setActions(PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SEEK_TO)
                        .build();
                musicService.showNotification(R.drawable.ic_pause);

                play_btn.setImageResource(R.drawable.ic_pause);
            }
            musicService.start();

        }

//        int audioSessionId = musicService.mediaPlayer.getAudioSessionId();
//        if (audioSessionId != -1) {
//            visualizer.setAudioSessionId(audioSessionId);
//        }

    }

    //    Play
    private void playThreadBtn() {
        play_pauseThread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (mQueueViewModel.getQueuedFiles().getValue().size() > 0) {
                    play_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            playPauseBtnClicked();
                        }
                    });
                }
            }
        };
        play_pauseThread.start();
    }

    public void playPauseBtnClicked() {
        if (musicService.isPlaying()) {
            play_btn.setImageResource(R.drawable.ic_play);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);
            progressBar.setMax(musicService.getDuration() / 1000);

            musicService.mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(musicService.mPlaybackState.STATE_PAUSED,
                            Long.parseLong(musicService.getCurrentPosition() + ""),
                            1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                            PlaybackStateCompat.ACTION_SEEK_TO)
                    .build();

            musicService.showNotification(R.drawable.ic_play);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        progressBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            musicService.mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(musicService.mPlaybackState.STATE_PLAYING,
                            Long.parseLong(musicService.getCurrentPosition() + ""),
                            1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                            PlaybackStateCompat.ACTION_SEEK_TO)
                    .build();
            musicService.showNotification(R.drawable.ic_pause);

            play_btn.setImageResource(R.drawable.ic_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);
            progressBar.setMax(musicService.getDuration() / 1000);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        progressBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    //  ViewPager
    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_BOTTOM);


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        SongsFragment songsFragment = new SongsFragment();
        AlbumsFragment albumsFragment = new AlbumsFragment();
        FavouritesFragment favouritesFragment = new FavouritesFragment();
        PlaylistsFragment playlistsFragment = new PlaylistsFragment();
        ArtistsFragment artistsFragment = new ArtistsFragment();
        FoldersFragment foldersFragment = new FoldersFragment();

        viewPagerAdapter.addFragments(songsFragment, "Tracks");
        viewPagerAdapter.addFragments(albumsFragment, "Albums");
        viewPagerAdapter.addFragments(favouritesFragment, "Favorites");
        viewPagerAdapter.addFragments(playlistsFragment, "Playlists");
        viewPagerAdapter.addFragments(artistsFragment, "Artists");
        viewPagerAdapter.addFragments(foldersFragment, "Folders");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(
//                tabLayout));

    }

    //    viewPager for fragments
    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);

            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

    }

    //    Get Songs
    public ArrayList<AudioFile> getAllAudioFiles() {
        ArrayList<AudioFile> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            projection = new String[]{
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,        //for path
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ALBUM_ARTIST,
                    MediaStore.Audio.Media.YEAR,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.ALBUM_ID

            };
        } else {
            projection = new String[]{
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,        //for path
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.YEAR,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.ALBUM_ID
            };
        }

        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);
                String albumArtist = cursor.getString(5);
                String albumYear = cursor.getString(7);
                String dateAdded = cursor.getString(8);
                Long albumId = cursor.getLong(9);

                //checking paths
                Log.e(TAG, "Album: " + album + "\nPath: " + path);
                AudioFile musicFile = new AudioFile(path, title, artist, album, duration, "0", "0", id, albumArtist, albumYear, dateAdded, albumId);
                tempAudioList.add(musicFile);
            }
            cursor.close();
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tempAudioList.sort(new Comparator<AudioFile>() {
//                @Override
//                public int compare(AudioFile o1, AudioFile o2) {
//                    if (sortType.equals("name")) {
//                        return o1.getTitle().compareTo(o2.getTitle());
//                    } else if (sortType.equals("album")) {
//                        return o1.getAlbum().compareTo(o2.getAlbum());
//                    } else if (sortType.equals("artist")) {
//                        return o1.getArtist().compareTo(o2.getArtist());
//                    } else if (sortType.equals("date")) {
//                        return o1.getDateAdded().compareTo(o2.getDateAdded());
//                    } else {
//                        return 0;
//                    }
//                }
//            });
//        }

        return tempAudioList;
    }

    public ArrayList<AlbumFile> getAllAlbumFiles() {
        ArrayList<AlbumFile> tempAlbumList = new ArrayList<>();

        ArrayList<AudioFile> tMusicList = getAllAudioFiles();
        ArrayList<AudioFile> tempAlbumSongs = new ArrayList<>();

        ArrayList<String> albumNames = getAllAlbumNames();

        String tAlbum;
        String tArtist = "";
        String tsArtist;
        String tPath = "";
        AlbumFile albumFile;


//        System.out.println("# of Albums" + albumNames.size());


        for (int i = 0; i < albumNames.size(); i++) {

            int j = 0;
            for (int k = 0; k < tMusicList.size(); k++) {
                if (albumNames.get(i).equals(songs.get(k).getAlbum())) {
                    tempAlbumSongs.add(j, songs.get(k));
                    tArtist = tMusicList.get(i).getArtist();

                    System.out.println("\ni: " + i + " album size: " + tempAlbumSongs.size() +
                            " Song Name: " + tempAlbumSongs.get(j).getTitle());
                    j++;
                }

//                tempAlbumList.get(i).getAlbumArtPath(musicFiles.get(j).getPath());

            }
            if (tempAlbumList.size() > 0) {
                if (tempAlbumSongs.size() > 0) {
                    tPath = tempAlbumList.get(0).getAlbumArtPath(tempAlbumList.get(0).getAlbumSongs().get(0).getPath());
                    System.out.println("path: " + tPath);
                }
            }

            int aDuration = 0;
            int counter = 0;
            for (AudioFile audioFile : tempAlbumSongs) {
                aDuration += Integer.parseInt(tempAlbumSongs.get(counter).getDuration());
                counter++;
            }
            String album_year = tempAlbumSongs.get(0).getAlbumYear();
            String albumDuration = String.valueOf(aDuration);
            Long album_id = tempAlbumSongs.get(0).getAlbum_id();
            albumFile = new AlbumFile(albumNames.get(i), tArtist, tPath, "" + tempAlbumSongs.size(), tempAlbumSongs, albumDuration, album_year, album_id);
            tempAlbumList.add(albumFile);
            tempAlbumSongs = new ArrayList<>();
        }

        return tempAlbumList;
    }

    private ArrayList<String> getAllAlbumNames() {

        ArrayList<String> albumNames = new ArrayList<>();

        if (mAlbumViewModel.getAlbumFiles().getValue().size() > 0) {
            for (AudioFile audioFile : mAudioFileViewModel.getAudioFiles().getValue()) {
                if (!albumNames.contains(audioFile.getAlbum())) {
                    albumNames.add(audioFile.getAlbum());
                }
            }
        }

        return albumNames;
    }


    public static String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);

        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }

    private int getRandom(int i) {

        Random random = new Random();

        return random.nextInt(i + 1);
    }

    public void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
//        int totalDuration = Integer.parseInt(playingQueue.get(position).getDuration()) / 1000;

        int totalDuration = Integer.parseInt(
                mQueueViewModel.getQueuedFiles().getValue().get(position).getDuration()) / 1000;
        RoundedImageView cover;

        tv_duration.setText(formattedTime(totalDuration));
        tv_title.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getTitle());
        tv_artist.setText(mQueueViewModel.getQueuedFiles().getValue().get(position).getArtist());

        byte[] art = retriever.getEmbeddedPicture();
//            Bitmap bitmap;

        if (art != null) {
            Glide.with(this)
                    .asBitmap()
                    .placeholder(R.drawable.ic_beat_logo)
                    .load(art)
                    .apply(new RequestOptions().override(1080, 1080).fitCenter())
                    .into(album_cover);

//                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);

//                imageAnimation(this, maxFragment.cover_art, bitmap);
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.ic_beat_logo)
                    .into(album_cover);
        }

    }

    public void restartMetaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
//        int totalDuration = Integer.parseInt(playingQueue.get(position).getDuration()) / 1000;
        int totalDuration = musicService.getDuration() / 1000;
//        RoundedImageView cover;
        position = musicService.position;

        tv_duration.setText(formattedTime(totalDuration));
        tv_title.setText(playingQueue.get(position).getTitle());
        tv_artist.setText(playingQueue.get(position).getArtist());

        byte[] art = retriever.getEmbeddedPicture();
//            Bitmap bitmap;

        if (art != null) {
            Glide.with(this)
                    .asBitmap()
                    .placeholder(R.drawable.skp)
                    .load(art)
                    .apply(new RequestOptions().override(1080, 1080).fitCenter())
                    .into(album_cover);

//                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);

//                imageAnimation(this, maxFragment.cover_art, bitmap);
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.skp)
                    .into(album_cover);
        }
    }


    public void imageAnimation(Context context, ImageView imageview, Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Glide.with(context).load(bitmap).into(imageview);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageview.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageview.startAnimation(animOut);

    }



    @Override
    public void onBackPressed() {
        if (motionLayout != null) {
            if (motionLayout.getCurrentState() == motionLayout.getEndState()) {
                super.onBackPressed();
            } else {
                motionLayout.transitionToEnd();
            }
        }
    }


    @Override
    protected void onResume() {
        Log.e("On Resume", " Called");
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();


        super.onResume();
    }

    @Override
    protected void onDestroy() {
//        this.getContentResolver().unregisterContentObserver(audioObserver);
//        musicService.stop();
//        musicService.release();
//        musicService = null;
//        unbindService(this);

        super.onDestroy();
    }

    //    Service

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.e(TAG + "QueueSize: ", "" + mQueueViewModel.getQueuedFiles().getValue().size());
        if (mQueueViewModel.getQueuedFiles().getValue().size() > 0) {
            MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
            musicService = myBinder.getService();

            musicService.setCallBack(this);
            Toast.makeText(this, "Connected" + musicService, Toast.LENGTH_SHORT).show();


            if (musicService != null) {
//                setPlayerValues();
            }
        }
    }

    private void setPlayerValues() {
        seekBar.setMax(musicService.getDuration() / 1000);
        progressBar.setMax(musicService.getDuration() / 1000);
        restartMetaData(musicService.uri);

        musicService.onCompleted();

//            int audioSessionId = musicService.mediaPlayer.getAudioSessionId();
//            if (audioSessionId != -1) {
//                visualizer.setAudioSessionId(audioSessionId);
//            }

        musicService.mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(musicService.mPlaybackState.STATE_PLAYING,
                        Long.parseLong(position + ""),
                        1.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SEEK_TO)
                .build();
        musicService.showNotification(R.drawable.ic_pause);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }


//    class MyMotionLayout extends MotionLayout {
//
//        @Override
//        public boolean onInterceptTouchEvent(MotionEvent event) {
//            //Check if we have MotionEvent.ACTION_UP while MotionEvent.ACTION_MOVE<0.5
//            // i.e ..this was a click
//            if(event.getAction() ==MotionEvent.ACTION_UP){
//                return true;
//            }
//            return super.onInterceptTouchEvent(event);
//        }
//
//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            // Here we actually handle the touch event
//            // This method will only be called if the touch event was intercepted in
//            // onInterceptTouchEvent
//            callOnClick();
//            return super.onTouchEvent(event);
//        }
//
//        public MyMotionLayout(@NonNull Context context) {
//            super(context);
//        }
//    }
}
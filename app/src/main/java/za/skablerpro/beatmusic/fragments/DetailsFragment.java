package za.skablerpro.beatmusic.fragments;

import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import za.skablerpro.beatmusic.R;
import za.skablerpro.beatmusic.adapters.AudioAdapter;
import za.skablerpro.beatmusic.models.AlbumFile;
import za.skablerpro.beatmusic.viewmodels.AlbumViewModel;


public class DetailsFragment extends Fragment {

    RoundedImageView album_cover;
    TextView tv_name, tv_artist, tv_year_duration;
    RecyclerView recyclerView;
    AudioAdapter songsAdapter;
    View view;
    AlbumViewModel mAlbumViewModel;
    AlbumFile album;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        Playing songs list view model
        mAlbumViewModel = ViewModelProviders.of(this).get(AlbumViewModel.class);
//        mAudioFileViewModel.init();
        mAlbumViewModel.getAlbumFiles().observe(this, new Observer<List<AlbumFile>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<AlbumFile> albumFiles) {
                Log.d("AlbumDetailFrag", "Album Changed!!");
                songsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_details, container, false);
        album_cover = view.findViewById(R.id.album_cover);
        tv_name = view.findViewById(R.id.album_name);
        tv_artist = view.findViewById(R.id.album_artist);
        tv_year_duration = view.findViewById(R.id.album_detail);
        recyclerView = view.findViewById(R.id.album_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(30);

        initRecyclerView();

        recyclerView.setAdapter(songsAdapter);

        return view;
    }

    private void initRecyclerView(){
        int pos = -1;
        String albumName = null;

        Bundle bundle = this.getArguments();

        if (bundle != null){
            albumName = bundle.getString("album_name", null);
        }

        if (albumName != null){
            for (AlbumFile albumFile : mAlbumViewModel.getAlbumFiles().getValue()){

                if (albumFile.getAlbumName().equals(albumName)){
                    album = albumFile;
                }
            }
        }

        if (album != null){
            songsAdapter = new AudioAdapter(getContext(), album.getAlbumSongs());
            recyclerView.setAdapter(songsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
                    false));

            String duration = formattedTime(Integer.parseInt(album.getAlbumDuration()) / 1000);
            String year_duration = album.getAlbumYear() + " | " + duration;
            tv_year_duration.setText(year_duration);
            tv_name.setText(album.getAlbumName());
            tv_artist.setText(album.getAlbumSongs().get(0).getArtist());

            byte[] img_art = getAlbumArt(album.getAlbumArtPath(album.getAlbumSongs().get(0).getPath()));

            if (img_art != null) {
                Glide.with(this).asBitmap()
                        .load(img_art)
                        .into(album_cover);
            } else {
                Glide.with(this)
                        .load(R.drawable.skp)
                        .into(album_cover);
            }
        }

    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();

        return art;
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
}
package za.skablerpro.beatmusic.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import za.skablerpro.beatmusic.R;
import za.skablerpro.beatmusic.adapters.AlbumAdapter;
import za.skablerpro.beatmusic.models.AlbumFile;
import za.skablerpro.beatmusic.viewmodels.AlbumViewModel;

public class AlbumsFragment extends Fragment {

    private AlbumViewModel mAlbumsViewModel;
    private Context mContext = getContext();

    RecyclerView recyclerView;
    AlbumAdapter mAlbumAdapter;
    ImageView sort_btn;

    public AlbumsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlbumsViewModel = ViewModelProviders.of(this).get(AlbumViewModel.class);
//        mAudioFileViewModel.init();
        mAlbumsViewModel.getAlbumFiles().observe(this, new Observer<ArrayList<AlbumFile>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(ArrayList<AlbumFile> albumFiles) {

                mAlbumAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(30);

        ArrayList<AlbumFile> albums = mAlbumsViewModel.getAlbumFiles().getValue();
        int listSize = 0;
        if (albums != null){
            listSize = albums.size();
        }
        initRecyclerView();

        if (view.findViewById(R.id.album_frag_land) != null){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }

        if (listSize > 0){

//            sort_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
//                    popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());
//                    popupMenu.show();
////                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
////                        @Override
////                        public boolean onMenuItemClick(MenuItem item) {
////                            switch (item.getItemId()) {
////                                case R.id.sort_by_name:
////                                    sortAllSongs("name");
////                                    break;
////                                case R.id.sort_by_album:
////                                    sortAllSongs("album");
////                                    break;
////                                case R.id.sort_by_artist:
////                                    sortAllSongs("artist");
////                                    break;
////                                case R.id.sort_by_date:
////                                    sortAllSongs("date");
////                                    break;
////                            }
////                            return true;
////                        }
////                    });
//                }
//            });
        }
        return view;
    }
    private void initRecyclerView(){
        mAlbumAdapter = new AlbumAdapter(getContext(), mAlbumsViewModel.getAlbumFiles().getValue());
        recyclerView.setAdapter(mAlbumAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }
}
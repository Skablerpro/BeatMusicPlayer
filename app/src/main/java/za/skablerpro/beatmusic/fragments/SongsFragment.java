package za.skablerpro.beatmusic.fragments;

import static java.util.Collections.reverseOrder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import za.skablerpro.beatmusic.R;
import za.skablerpro.beatmusic.adapters.AudioAdapter;
import za.skablerpro.beatmusic.models.AudioFile;
import za.skablerpro.beatmusic.viewmodels.AudioFileViewModel;


public class SongsFragment extends Fragment {

    private AudioFileViewModel mAudioFileViewModel;
    private Context mContext = getContext();
    RecyclerView recyclerView;
    AudioAdapter mAudioAdapter;
    ImageView sort_btn, play_shuffle, play_list, sort_order;
    private String sort_type = "name";
    boolean reverse_order = false;


    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAudioFileViewModel = ViewModelProviders.of(this).get(AudioFileViewModel.class);
//        mAudioFileViewModel.init();
        mAudioFileViewModel.getAudioFiles().observe(this, new Observer<List<AudioFile>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<AudioFile> audioFiles) {
//                mAudioAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        sort_btn = view.findViewById(R.id.song_list_sort);
        sort_order = view.findViewById(R.id.sort_order);
        play_list = view.findViewById(R.id.play_list);
        play_shuffle = view.findViewById(R.id.play_list_shuffle);
        recyclerView = view.findViewById(R.id.recyclerView);
//        fastScroller = view.findViewById(R.id.rv_fastscroll);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(30);


        ArrayList<AudioFile> audioList = mAudioFileViewModel.getAudioFiles().getValue();
        int listSize = 0;
        if (audioList != null) {
            listSize = audioList.size();
        }
        initRecyclerView();

        if (listSize > 0) {

            sortAllSongs(sort_type);
            sort_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.sort_by_name:

                                    reverse_order = !reverse_order;
                                    sortAllSongs("name");
                                    sort_type = "name";
                                    break;
                                case R.id.sort_by_album:
                                    reverse_order = !reverse_order;
                                    sortAllSongs("album");
                                    sort_type = "album";
                                    break;
                                case R.id.sort_by_artist:
                                    reverse_order = !reverse_order;
                                    sortAllSongs("artist");
                                    sort_type = "artist";
                                    break;
                                case R.id.sort_by_date:
                                    reverse_order = !reverse_order;
                                    sortAllSongs("date");
                                    sort_type = "date";
                                    break;
                            }
                            return true;
                        }
                    });
                }
            });
            sort_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sort_type.equals("name")) {
                        sortAllSongs(sort_type);
                    } else if (sort_type.equals("album")) {
                        sortAllSongs(sort_type);
                    } else if (sort_type.equals("artist")) {
                        sortAllSongs(sort_type);
                    } else if (sort_type.equals("date")) {
                        sortAllSongs(sort_type);
                    }
                }
            });

            play_shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getContext(), ExplorerActivity.class);
//                    intent.putExtra("shuffle", true);
//                    getContext().startActivity(intent);

//                    shuffleBoolean = true;
//                    int shuffle_position = getRandom(mAudioFileViewModel.getAudioFiles().getValue().size() - 1);
//                    ((MainActivity) getActivity()).playShuffle(shuffle_position);
                }
            });

            play_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getContext(), ExplorerActivity.class);
//                    intent.putExtra("listPlay", true);
//                    getContext().startActivity(intent);

//                    listBoolean = true;
//                    songsList = musicFiles;
////                    ExplorerActivity.musicService.playMedia(0);
//
//                    ((ExplorerActivity) getActivity()).playShuffle(0);
//                    bottomSheetBehavior.setState(STATE_EXPANDED);

                }
            });

        }
        return view;
    }

    private void initRecyclerView() {
        mAudioAdapter = new AudioAdapter(getContext(), mAudioFileViewModel.getAudioFiles().getValue());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
                false));
        recyclerView.setAdapter(mAudioAdapter);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(50);

        //has to be called AFTER RecyclerView.setAdapter()
//        fastScroller.setRecyclerView(recyclerView);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortAllSongs(String sortType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (reverse_order) {
                mAudioFileViewModel.getAudioFiles().getValue().sort(reverseOrder(new Comparator<AudioFile>() {
                    @Override
                    public int compare(AudioFile o1, AudioFile o2) {
                        if (sortType.equals("name")) {
                            return o1.getTitle().compareTo(o2.getTitle());
                        } else if (sortType.equals("album")) {
                            return o1.getAlbum().compareTo(o2.getAlbum());
                        } else if (sortType.equals("artist")) {
                            return o1.getArtist().compareTo(o2.getArtist());
                        } else if (sortType.equals("date")) {
                            return o1.getDateAdded().compareTo(o2.getDateAdded());
                        } else {
                            return 0;
                        }
                    }
                }));
            } else {
                mAudioFileViewModel.getAudioFiles().getValue().sort(new Comparator<AudioFile>() {
                    @Override
                    public int compare(AudioFile o1, AudioFile o2) {
                        if (sortType.equals("name")) {
                            return o1.getTitle().compareTo(o2.getTitle());
                        } else if (sortType.equals("album")) {
                            return o1.getAlbum().compareTo(o2.getAlbum());
                        } else if (sortType.equals("artist")) {
                            return o1.getArtist().compareTo(o2.getArtist());
                        } else if (sortType.equals("date")) {
                            return o1.getDateAdded().compareTo(o2.getDateAdded());
                        } else {
                            return 0;
                        }
                    }
                });
            }
        } else {
            if (reverse_order) {
                Collections.sort(Objects.requireNonNull(mAudioFileViewModel.getAudioFiles().getValue()), reverseOrder(new Comparator<AudioFile>() {
                    @Override
                    public int compare(AudioFile o1, AudioFile o2) {
                        if (sortType.equals("name")) {
                            return o1.getTitle().compareTo(o2.getTitle());
                        } else if (sortType.equals("album")) {
                            return o1.getAlbum().compareTo(o2.getAlbum());
                        } else if (sortType.equals("artist")) {
                            return o1.getArtist().compareTo(o2.getArtist());
                        } else if (sortType.equals("date")) {
                            return o1.getDateAdded().compareTo(o2.getDateAdded());
                        } else {
                            return 0;
                        }
                    }
                }));
            } else {
                Collections.sort(Objects.requireNonNull(mAudioFileViewModel.getAudioFiles().getValue()), new Comparator<AudioFile>() {
                    @Override
                    public int compare(AudioFile o1, AudioFile o2) {

                        if (sortType.equals("name")) {
                            return o1.getTitle().compareTo(o2.getTitle());
                        } else if (sortType.equals("album")) {
                            return o1.getAlbum().compareTo(o2.getAlbum());
                        } else if (sortType.equals("artist")) {
                            return o1.getArtist().compareTo(o2.getArtist());
                        } else if (sortType.equals("date")) {
                            return o1.getDateAdded().compareTo(o2.getDateAdded());
                        } else {
                            return 0;
                        }
                    }

                });
            }
        }

        if (reverse_order) {
            sort_order.setImageResource(R.drawable.ic_arrow_upward);
        } else {
            sort_order.setImageResource(R.drawable.ic_arrow_downward);
        }
        reverse_order = !reverse_order;

        mAudioAdapter.notifyDataSetChanged();
    }

    private int getRandom(int i) {

        Random random = new Random();

        return random.nextInt(i + 1);
    }
}
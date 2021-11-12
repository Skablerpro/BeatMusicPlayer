package za.skablerpro.beatmusic.adapters;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Comparator;

import za.skablerpro.beatmusic.MainActivity;
import za.skablerpro.beatmusic.R;
import za.skablerpro.beatmusic.models.AlbumFile;
import za.skablerpro.beatmusic.models.AudioFile;
import za.skablerpro.beatmusic.viewmodels.FavouriteSongsViewModel;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder>  {
    public static final String TAG = "AudioAdapter";
    private Context mContext;
    private ArrayList<AudioFile> mAudioFiles;
    private FavouriteSongsViewModel favouriteSongsViewModel;

    Uri uri;

    public AudioAdapter(Context context, ArrayList<AudioFile> audioFiles) {
        this.mContext = context;
        this.mAudioFiles = audioFiles;

//        sortAll();
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "" + mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.audio_item, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, @SuppressLint("RecyclerView") int position) {

//        holder.song_title.setText(mAudioFiles.get(position).getTitle());
        holder.song_title.setText(getSongTitle(position));
        holder.song_artist.setText(mAudioFiles.get(position).getArtist());
        String duration = formattedTime(Integer.parseInt(mAudioFiles.get(position).getDuration()) / 1000);
        holder.song_duration.setText(duration);
//        holder.album_art.setImageURI(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(AudioFile.getId())));


//        byte[] img_art = getAlbumArt(mAudioFiles.get(position).getPath());
        Bitmap img_art = getAlbumArt(mAudioFiles.get(position).getAlbum_id());

        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);

        if (img_art != null)
        {
            Glide.with(mContext).asBitmap()
                    .placeholder(R.drawable.skp)
                    .load(img_art)
                    .apply(requestOptions)
                    .into(holder.album_art);
        }
        else
        {
            Glide.with(mContext)
                    .load(R.drawable.skp)
                    .apply(requestOptions)
                    .into(holder.album_art);
        }

        img_art = null;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent open
//                Intent intent = new Intent(mContext, ExplorerActivity.class);
//                intent.putExtra("position", position);
//                mContext.startActivity(intent);

                uri = Uri.parse(mAudioFiles.get(position).getPath());
//                BottomSheet Open
//                songClick(position, uri);
                ((MainActivity)mContext).songClick(mAudioFiles, position);
//                MainActivity.musicService.playMedia(position);
//                bottomSheetBehavior.setState(STATE_EXPANDED);

            }
        });
        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.song_item_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.share:
                                Toast.makeText(mContext, "Share Clicked", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.add_to_favorite:
                                favouriteSongsViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(FavouriteSongsViewModel.class);
                                favouriteSongsViewModel.addToFavourites(mAudioFiles.get(position));
//                                addToFavourites(mAudioFiles.get(position));
                                Toast.makeText(mContext, "Added to Favourites", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.add_to_playlist:
                                Toast.makeText(mContext, "Add to Playlist Clicked", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.track_details:
                                Toast.makeText(mContext, "Track Details Clicked", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.track_album:
                                Toast.makeText(mContext, "Album Clicked", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.track_artist:
                                Toast.makeText(mContext, "Artist Clicked", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.track_delete:
                                //Toast.makeText(mContext, "Delete from Device Clicked"+ position, Toast.LENGTH_SHORT).show();
                                deleteSong(position, v);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAudioFiles.size();
    }

//    @Override
//    public String getSectionTitle(int position) {
//        //this String will be shown in a bubble for specified position
//        return getSongTitle(position).substring(0, 1);
////        return "A";
//    }

    public class AudioViewHolder extends RecyclerView.ViewHolder{

        TextView song_title, song_artist, song_duration;
        ImageView album_art, menu_more;


        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            song_title = itemView.findViewById(R.id.song_title);
            song_title.setSelected(true); //for marquee

            song_artist = itemView.findViewById(R.id.song_artist);
            song_duration = itemView.findViewById(R.id.song_duration);
            album_art = itemView.findViewById(R.id.album_art);

            menu_more = itemView.findViewById(R.id.menu_more);
        }
    }

    private String getSongTitle(int position){
        return mAudioFiles.get(position).getTitle();
    }

    public Bitmap getAlbumArt(Long album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = mContext.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();

        return art;
    }
    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);

        if (minutes.length() == 1){
            totalOut = "0" + minutes + ":" + seconds;
            totalNew = "0" + minutes + ":" + "0" + seconds;
        }else {
            totalOut = minutes + ":" + seconds;
            totalNew = minutes + ":" + "0" + seconds;
        }

        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }

    public void addToFavourites(AudioFile song){
        if (song != null){
//            favouriteSongs.add(song);
        }
    }

    private void sortAll(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mAudioFiles.sort(new Comparator<AudioFile>() {
                @Override
                public int compare(AudioFile o1, AudioFile o2) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
            });
        }
    }

    private void deleteSong(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mAudioFiles.get(position).getId()));  //content://
    }



}


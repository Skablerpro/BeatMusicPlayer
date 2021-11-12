package za.skablerpro.beatmusic.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import za.skablerpro.beatmusic.MainActivity;
import za.skablerpro.beatmusic.R;
import za.skablerpro.beatmusic.models.AudioFile;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder> {

    private Context mContext;
    private ArrayList<AudioFile> favouriteSongs;

    public FavouritesAdapter(Context mContext, ArrayList<AudioFile> favouriteSongs)
    {
        this.favouriteSongs = favouriteSongs;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.audio_item, parent, false);
        return new FavouritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.song_title.setText(favouriteSongs.get(position).getTitle());

        String artistAlbum = favouriteSongs.get(position).getArtist() + "\t\t|\t" + favouriteSongs.get(position).getAlbum();
        holder.song_artist.setText(artistAlbum);

        String duration = formattedTime(Integer.parseInt(favouriteSongs.get(position).getDuration()) / 1000);
        holder.song_duration.setText(duration);

        byte[] img_art = getAlbumArt(favouriteSongs.get(position).getPath());
        if (img_art != null)
        {
            Glide.with(mContext).asBitmap()
                    .load(img_art)
                    .into(holder.album_art);
        }
        else
        {
            Glide.with(mContext)
                    .load(R.drawable.skp)
                    .into(holder.album_art);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, PlayerActivity.class);
//                intent.putExtra("position", position);
//                intent.putExtra("favourites", true);
//                mContext.startActivity(intent);

//                MainActivity.queuedSongs = favouriteSongs;
                ((MainActivity)mContext).songClick(favouriteSongs, position);
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
                        }
                        return true;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return favouriteSongs.size();
    }

    public class FavouritesViewHolder extends RecyclerView.ViewHolder
    {
        TextView song_title, song_artist, song_duration;
        ImageView album_art, menu_more;


        public FavouritesViewHolder(@NonNull View itemView) {
            super(itemView);

            song_title = itemView.findViewById(R.id.song_title);
            song_title.setSelected(true); //for marquee

            song_artist = itemView.findViewById(R.id.song_artist);
            song_duration = itemView.findViewById(R.id.song_duration);
            album_art = itemView.findViewById(R.id.album_art);

            menu_more = itemView.findViewById(R.id.menu_more);
        }
    }

    private byte[] getAlbumArt(String uri)
    {
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

        if (mCurrentPosition >= 3600){
            String hours = String.valueOf(mCurrentPosition / 3600);

            if (minutes.length() == 1){
                totalOut = hours + ":" + "0" + minutes + ":" + seconds;
                totalNew =  hours + ":" + "0" + minutes + ":" + "0" + seconds;
            }else {
                totalOut =  hours + ":" + minutes + ":" + seconds;
                totalNew =  hours + ":" + minutes + ":" + "0" + seconds;
            }

        }else{
            if (minutes.length() == 1){
                totalOut = "0" + minutes + ":" + seconds;
                totalNew = "0" + minutes + ":" + "0" + seconds;
            }else {
                totalOut = minutes + ":" + seconds;
                totalNew = minutes + ":" + "0" + seconds;
            }
        }

        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }

}

package za.skablerpro.beatmusic.adapters;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;

import za.skablerpro.beatmusic.MainActivity;
import za.skablerpro.beatmusic.R;
import za.skablerpro.beatmusic.models.AlbumFile;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Context mContext;
    private ArrayList<AlbumFile> mAlbumFiles;
    View view;
//    AlbumDetailFragment albumDetailFragment;

    public AlbumAdapter(Context context, ArrayList<AlbumFile> albums) {
        this.mContext = context;
        this.mAlbumFiles = albums;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);

        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.album_name.setText(mAlbumFiles.get(position).getAlbumName());
        holder.album_artist.setText(mAlbumFiles.get(position).getAlbumSongs().get(0).getArtist());
        String duration = formattedTime(Integer.parseInt(mAlbumFiles.get(position).getAlbumDuration()) / 1000);
        holder.album_length.setText(duration);


//        byte[] img_art = getAlbumArt(mAlbumFiles.get(position).getAlbumArtPath(mAlbumFiles.get(position).getAlbumSongs().get(0).getPath()));

        Bitmap img_art = getAlbumArt(mAlbumFiles.get(position).getAlbumId());

        if (img_art != null) {
            Glide.with(mContext).asBitmap()
                    .placeholder(R.drawable.skp)
                    .load(img_art)
                    .into(holder.album_cover);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.skp)
                    .into(holder.album_cover);
        }

        img_art = null;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)mContext).albumClick(mAlbumFiles.get(position).getAlbumName());

//                albumDetailFragment = new AlbumDetailFragment();
//                FragmentManager fm = albumDetailFragment.getParentFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                Bundle data = new Bundle();
//                data.putString("album", mAlbumFiles.get(position).getAlbumName());
//                albumDetailFragment.setArguments(data);
//                ft.add(R.id.album_detail, albumDetailFragment);
//                ft.addToBackStack(null);
//                ft.commit();
            }
        });


        holder.album_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.song_item_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.share:
                                Toast.makeText(mContext, "Share Clicked", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.add_to_favorite:
                                Toast.makeText(mContext, "Add to Favourites Clicked", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(mContext, "Delete Album Clicked", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(mContext, "Delete from Device Clicked"+ position, Toast.LENGTH_SHORT).show();
//                                deleteAlbum(position, v);
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
        return mAlbumFiles.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView album_cover;
        ImageView album_more;
        TextView album_name, album_artist, album_length;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            album_cover = itemView.findViewById(R.id.card_album_cover);
            album_more = itemView.findViewById(R.id.album_card_more);

            album_name = itemView.findViewById(R.id.card_album_name);
            album_artist = itemView.findViewById(R.id.card_album_artist);
            album_length = itemView.findViewById(R.id.card_album_length);

        }
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();

        return art;
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

//    private void deleteAlbum(int position, View v) {
//        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                Long.parseLong(mAlbumFiles.get(position).getAlbumId()));  //content://
//
//        File file = new File(mAlbumFiles.get(position).getAlbumArtPath(mAlbumFiles.get(position).getAlbumSongs().get(0).getPath()));
//        boolean deleted = file.delete();
//        if (deleted) {
//            mAlbumFiles.remove(position);
//            mContext.getContentResolver().delete(contentUri, null, null);
//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, mAlbumFiles.size());
//            Snackbar.make(v, "Album Deleted!", Snackbar.LENGTH_LONG).show();
//        } else {
//            //if file is in sdcard and api level is <19
//            Snackbar.make(v, "Album Could not be Deleted!", Snackbar.LENGTH_LONG).show();
//        }
//    }

}

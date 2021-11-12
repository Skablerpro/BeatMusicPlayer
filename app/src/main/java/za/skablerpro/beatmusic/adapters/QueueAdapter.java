package za.skablerpro.beatmusic.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import za.skablerpro.beatmusic.R;
import za.skablerpro.beatmusic.models.AudioFile;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder> {
    public static final String TAG = "AudioAdapter";
    private Context mContext;
    private ArrayList<AudioFile> mAudioFiles;

    public QueueAdapter(Context context, ArrayList<AudioFile> audioFiles) {
        this.mContext = context;
        this.mAudioFiles = audioFiles;

//        sortAll();
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "" + mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.queue_item, parent, false);
        return new QueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
        holder.song_title.setText(getSongTitle(position));
        holder.song_artist.setText(mAudioFiles.get(position).getArtist());

    }

    @Override
    public int getItemCount() {
        return mAudioFiles.size();
    }

    public class QueueViewHolder extends RecyclerView.ViewHolder{

        TextView song_title, song_artist, song_duration;


        public QueueViewHolder(@NonNull View itemView) {
            super(itemView);

            song_title = itemView.findViewById(R.id.tvq_title);
            song_title.setSelected(true); //for marquee

            song_artist = itemView.findViewById(R.id.tvq_artist);
//            song_duration = itemView.findViewById(R.id.song_duration);
        }
    }

    private String getSongTitle(int position){
        return mAudioFiles.get(position).getTitle();
    }
}

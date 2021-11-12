package za.skablerpro.beatmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import za.skablerpro.beatmusic.models.AlbumFile;
import za.skablerpro.beatmusic.models.AudioFile;
import za.skablerpro.beatmusic.viewmodels.AlbumViewModel;
import za.skablerpro.beatmusic.viewmodels.AudioFileViewModel;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String TAG = "Splash Activity";
    public static final int REQUEST_CODE = 1;

    AudioFileViewModel audioFileViewModel;
    AlbumViewModel albumViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        checkStoragePermission();
    }


    private void nextActivity(){
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }




    //    Permission Requests
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);

        }else {
            Log.d(TAG, "Storage Permission Granted!!");
            nextActivity();
//            songs = getAllAudioFiles();
//            albums = getAllAlbumFiles();

//            Log.e(TAG + " # of Songs ", " " + audioFileViewModel.getAudioFiles().getValue().size());
//            Log.e(TAG + " # of Albums ", " " + albumViewModel.getAlbumFiles().getValue().size());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TODO: what the permission is for

                Log.d(TAG, "Storage Permission Granted!!");
                nextActivity();
//                songs = getAllAudioFiles();
//                albums = getAllAlbumFiles();

//                Log.e(TAG + " # of Songs ", " " + songs.size());
//                Log.e(TAG + " # of Albums ", " " + albums.size());

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
            }
        }
    }
}
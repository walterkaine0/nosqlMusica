package com.example.optimusprime;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class AlbumDetalActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albumdetail_activity);

        String albumId = getIntent().getStringExtra("albumId");
        if (albumId == null) {
            Toast.makeText(this, "Ошибка: ID альбома не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("albums").child(albumId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Album album = snapshot.getValue(Album.class);
                if (album == null) return;

                ((TextView) findViewById(R.id.idTVAlbumName)).setText(album.title);
                ((TextView) findViewById(R.id.idTVArtistName)).setText(album.artist);

                Glide.with(AlbumDetalActivity.this)
                        .load(album.coverUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into((ImageView) findViewById(R.id.idIVAlbum));

                findViewById(R.id.playButton).setOnClickListener(v -> {
                    if (album.tracks != null && !album.tracks.isEmpty()) {
                        String url = album.tracks.values().iterator().next().url;
                        playMusic(url);
                    } else {
                        Toast.makeText(AlbumDetalActivity.this, "Треки не найдены", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void playMusic(String url) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());

            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Toast.makeText(AlbumDetalActivity.this, "Воспроизведение...", Toast.LENGTH_SHORT).show();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MusicError", "Ошибка MediaPlayer: " + what + ", доп: " + extra);
                mp.reset();
                return false;
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(AlbumDetalActivity.this, "Трек завершен", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            Log.e("MusicError", "Ошибка при установке источника: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
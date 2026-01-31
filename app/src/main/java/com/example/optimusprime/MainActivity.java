package com.example.optimusprime;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvAlbums;
    private AlbumAdapter adapter;
    private List<DataSnapshot> albumSnapshots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvAlbums = findViewById(R.id.idRVAlbums);
        rvAlbums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("albums");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                albumSnapshots.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    albumSnapshots.add(ds);
                }
                adapter = new AlbumAdapter(albumSnapshots, id -> {
                    Intent intent = new Intent(MainActivity.this, AlbumDetalActivity.class);
                    intent.putExtra("albumId", id);
                    startActivity(intent);
                });
                rvAlbums.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
package com.techone.keeper;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.techone.keeper.activity.About_us;
import com.techone.keeper.adapter.DashboardAdapter;
import com.techone.keeper.model.SingleNote;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArchiveActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.archive_recycler_view)
    RecyclerView archiveRecyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.archive_image)
    ImageView archiveImage;
    @BindView(R.id.warning_container)
    RelativeLayout warningContainer;
    @BindView(R.id.navmenu)
    NavigationView navmenu;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    DashboardAdapter archiveAdapter;
    ArrayList<SingleNote> archiveNotes = new ArrayList<>();
    FirebaseUser user;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {
            //getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorwhite)); //status bar or the time bar at the top
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        toggle.syncState();
        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        navmenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.notes:
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.archieved:
                        drawer.closeDrawer(GravityCompat.START);
                        break;


                    case R.id.delete:
                        Intent intent2 = new Intent(getApplicationContext(), RecycleBin.class);
                        startActivity(intent2);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.aboutus:
                        Intent intent3=new Intent(getApplicationContext(), About_us.class);
                        startActivity(intent3);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });
        StaggeredGridLayoutManager binStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        archiveRecyclerView.setLayoutManager(binStaggeredGridLayoutManager);
        archiveAdapter = new DashboardAdapter(this, archiveNotes);
        archiveRecyclerView.setAdapter(archiveAdapter);
        fetchNotesFromDataBase();
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNotesFromDataBase();
            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchNotesFromDataBase();
    }
    private void fetchNotesFromDataBase() {

        archiveRecyclerView.setVisibility(View.GONE);
        archiveNotes.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    archiveNotes.clear();
                    for (DocumentSnapshot x : value.getDocuments()) {
                        SingleNote message = x.toObject(SingleNote.class);
                        message.setId(x.getId());

                        if (message.getArchived() && !message.getInTrash()) {
                            archiveNotes.add(message);

                        }

                    }

                    loader.setVisibility(View.GONE);
                    if (archiveNotes.size() == 0) {

                        archiveRecyclerView.setVisibility(View.GONE);

                    }else{
                        archiveRecyclerView.setVisibility(View.VISIBLE);
                    }

                    archiveAdapter.notifyDataSetChanged();
                    refresh.setRefreshing(false);
                    if (archiveNotes.size() == 0) {
                        warningContainer.setVisibility(View.VISIBLE);
                        refresh.setRefreshing(false);
                        return;
                    } else {
                        warningContainer.setVisibility(View.GONE);

                    }
                }
            }
        });


    }



}
package com.techone.keeper;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.techone.keeper.adapter.RecycleAdapter;
import com.techone.keeper.model.SingleNote;

import java.util.ArrayList;

public class RecycleBin extends AppCompatActivity {

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    RecyclerView binrecyclerView;
    RecycleAdapter recycleBinAdapter;
    ArrayList<SingleNote> binNotes = new ArrayList<>();
    RelativeLayout warningContainer;
    SwipeRefreshLayout refresh;
    SingleNote singleNote;
    ProgressBar loader;
    FirebaseUser user;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);

        if (Build.VERSION.SDK_INT >= 21) {
            //getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorwhite)); //status bar or the time bar at the top
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        nav = (NavigationView) findViewById(R.id.navmenu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        warningContainer = (RelativeLayout) findViewById(R.id.warning_container);
        loader = findViewById(R.id.loader);
        refresh = findViewById(R.id.refresh);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.notes:
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.archieved:
                        Intent intent3 = new Intent(getApplicationContext(), ArchiveActivity.class);
                        startActivity(intent3);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;


                    case R.id.delete:

                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.aboutus:
                        Intent intent1=new Intent(getApplicationContext(), About_us.class);
                        startActivity(intent1);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                }

                return true;
            }
        });
        binrecyclerView = (RecyclerView) findViewById(R.id.bin_recycler_view);


        StaggeredGridLayoutManager binStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binrecyclerView.setLayoutManager(binStaggeredGridLayoutManager);
        recycleBinAdapter = new RecycleAdapter(this, binNotes);
        binrecyclerView.setAdapter(recycleBinAdapter);
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    binNotes.clear();
                    for (DocumentSnapshot x : value.getDocuments()) {
                        SingleNote message = x.toObject(SingleNote.class);
                        message.setId(x.getId());
                        if (message.getInTrash()) {
                            binNotes.add(message);
                        }
                    }


                    loader.setVisibility(View.GONE);
                    if (binNotes.size() == 0) {

                        binrecyclerView.setVisibility(View.GONE);

                    } else {
                        binrecyclerView.setVisibility(View.VISIBLE);
                    }

                    recycleBinAdapter.notifyDataSetChanged();
                    refresh.setRefreshing(false);
                    if (binNotes.size() == 0) {
                        warningContainer.setVisibility(View.VISIBLE);
                        refresh.setRefreshing(false);
                        return;
                    } else {
                        warningContainer.setVisibility(View.GONE);

                    }
                }
            }
        });

//

    }
}

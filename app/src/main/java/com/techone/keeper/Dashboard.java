package com.techone.keeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.techone.keeper.activity.About_us;
import com.techone.keeper.activity.LogIn;
import com.techone.keeper.adapter.DashboardAdapter;
import com.techone.keeper.model.SingleNote;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Dashboard extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.pinned_title)
    TextView pinnedTitle;
    @BindView(R.id.pinned_recycler_view)
    RecyclerView pinnedRecyclerView;
    @BindView(R.id.others_title)
    TextView othersTitle;
    @BindView(R.id.other_recycler_view)
    RecyclerView otherRecyclerView;
    DrawerLayout drawer;
    NavigationView navmenu;
    ArrayList<SingleNote> pinnedNotes = new ArrayList<>();
    ArrayList<SingleNote> otherNotes = new ArrayList<>();
    ArrayList<SingleNote> pinnedfilteredList =new ArrayList<>();
    ArrayList<SingleNote> otherfilteredList =new ArrayList<>();
    DashboardAdapter pinnedNotesAdapter;
    DashboardAdapter otherNotesAdapter;
    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.no_data_image)
    ImageView noDataImage;
    @BindView(R.id.warning_container)
    RelativeLayout warningContainer;
    @BindView(R.id.account)
    ImageView account;
    FirebaseUser user;
    String uid;
    @BindView(R.id.search)
    EditText search;
    private SingleNote singleNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 21) {
            //getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorwhite)); //status bar or the time bar at the top
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(account);
            uid = user.getUid();
        } else {
            uid = "XYZ";
        }
        registerForContextMenu(pinnedRecyclerView);




        navmenu = (NavigationView) findViewById(R.id.navmenu);
        drawer = (DrawerLayout) findViewById(R.id.drawer);

        final CustomDrawerButton customDrawerButton = (CustomDrawerButton)findViewById(R.id.btnOpenDrawer);
        customDrawerButton.setDrawerLayout( drawer );
        customDrawerButton.getDrawerLayout().addDrawerListener( customDrawerButton );
        customDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDrawerButton.changeState();
            }
        });
        navmenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.notes:

                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.archieved:
                        Intent intent1 = new Intent(getApplicationContext(), ArchiveActivity.class);
                        startActivity(intent1);
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

    search.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

    if(editable.toString().isEmpty()) {

        pinnedRecyclerView.setAdapter(pinnedNotesAdapter);
        otherRecyclerView.setAdapter(otherNotesAdapter);

}
else{
    pinnedfilteredList.clear();
    otherfilteredList.clear();
    filter(editable.toString());


}
        }
    });


        StaggeredGridLayoutManager pinnedStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        StaggeredGridLayoutManager otherStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myalert = new AlertDialog.Builder(Dashboard.this, R.style.AlertDialogTheme);
                myalert.setTitle("Logout");
                myalert.setMessage("Do you want to Logout?");
                myalert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(getApplicationContext(), LogIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        startActivity(intent);
                        finish();
                    }
                });
                myalert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                myalert.show();


            }
        });

        pinnedRecyclerView.setLayoutManager(pinnedStaggeredGridLayoutManager);
        pinnedNotesAdapter = new DashboardAdapter(this, pinnedNotes);
        pinnedRecyclerView.setAdapter(pinnedNotesAdapter);

        otherRecyclerView.setLayoutManager(otherStaggeredGridLayoutManager);
        otherNotesAdapter = new DashboardAdapter(this, otherNotes);
        otherRecyclerView.setAdapter(otherNotesAdapter);
        fetchNotesFromDataBase();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNotesFromDataBase();
            }
        });
    }




    private void filter(String text) {

        for (SingleNote p : pinnedNotes) {
            {

                if (p.getTitle().toLowerCase().contains(text.toLowerCase()) || p.getBody().toLowerCase().contains(text.toLowerCase())) {
                    pinnedfilteredList.add(p);
                }
            }
        }


        for (SingleNote p : otherNotes) {
            {
                    if(p.getTitle()!=null && p.getBody()!=null) {
                        if (p.getTitle().toLowerCase().contains(text.toLowerCase()) || p.getBody().toLowerCase().contains(text.toLowerCase())) {
                            otherfilteredList.add(p);
                        }
                    }else if(p.getTitle()!=null && p.getBody()==null){
                        if (p.getTitle().toLowerCase().contains(text.toLowerCase())) {
                            otherfilteredList.add(p);
                        }
                    }
                    else if(p.getTitle()==null && p.getBody()!=null){
                        if (p.getBody().toLowerCase().contains(text.toLowerCase())) {
                            otherfilteredList.add(p);
                        }
                    }


            }
        }

        pinnedRecyclerView.setAdapter(new DashboardAdapter(getApplicationContext(), pinnedfilteredList));

        pinnedNotesAdapter.notifyDataSetChanged();
        otherRecyclerView.setAdapter(new DashboardAdapter(getApplicationContext(),otherfilteredList));
        otherNotesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchNotesFromDataBase();
    }


    @OnClick(R.id.fab)
    public void onViewClicked() {
        Intent intent = new Intent(Dashboard.this, NoteMaker.class);
        startActivity(intent);
    }

    private void fetchNotesFromDataBase() {
        pinnedTitle.setVisibility(View.GONE);
        othersTitle.setVisibility(View.GONE);
        pinnedRecyclerView.setVisibility(View.GONE);
        otherRecyclerView.setVisibility(View.GONE);
        pinnedNotes.clear();
        otherNotes.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error==null){
                    pinnedNotes.clear();
                    otherNotes.clear();
                    for(DocumentSnapshot x: value.getDocuments()){
                        SingleNote message = x.toObject(SingleNote.class);
                        message.setId(x.getId());
                        if (!message.getArchived() && !message.getInTrash()) {
                            if (message.getPinned()) {
                                pinnedNotes.add(message);
                            } else {
                                otherNotes.add(message);
                            }
                        }
                    }


                    loader.setVisibility(View.GONE);
                    if (pinnedNotes.size() == 0) {
                        pinnedTitle.setVisibility(View.GONE);
                        othersTitle.setVisibility(View.GONE);
                        pinnedRecyclerView.setVisibility(View.GONE);
                        otherRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        if (otherNotes.size() == 0) {
                            pinnedTitle.setVisibility(View.VISIBLE);
                            othersTitle.setVisibility(View.GONE);
                            pinnedRecyclerView.setVisibility(View.VISIBLE);
                            otherRecyclerView.setVisibility(View.GONE);
                        } else {
                            pinnedTitle.setVisibility(View.VISIBLE);
                            othersTitle.setVisibility(View.VISIBLE);
                            pinnedRecyclerView.setVisibility(View.VISIBLE);
                            otherRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                    pinnedNotesAdapter.notifyDataSetChanged();
                    otherNotesAdapter.notifyDataSetChanged();
                    refresh.setRefreshing(false);
                    if (pinnedNotes.size() == 0 && otherNotes.size() == 0) {
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
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 101:
                Toast.makeText(this, "deletdde", Toast.LENGTH_SHORT).show();
                break;
            case 102:

                String shareBody = "XYZ";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                shareBody=singleNote.getBody();
                if(singleNote.getBody()!=null && singleNote.getTitle()!=null) {
                    shareBody = singleNote.getTitle() + "\n\n" + singleNote.getBody();
                }else if (singleNote.getTitle()!=null && singleNote.getBody()==null){
                    shareBody = singleNote.getTitle();
                }
                else if(singleNote.getTitle()==null && singleNote.getBody()!=null){
                    shareBody=singleNote.getBody();
                }
                String shareSubject = "My subject";
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "Share via"));
                break;
        }
        return super.onContextItemSelected(item);
    }

}
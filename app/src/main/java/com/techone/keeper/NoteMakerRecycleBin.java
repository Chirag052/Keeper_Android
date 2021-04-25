package com.techone.keeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.techone.keeper.model.SingleNote;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteMakerRecycleBin extends AppCompatActivity {
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.titleh1)
    TextView titleh1;
    @BindView(R.id.notes)
    TextView notes;
    @BindView(R.id.extra_options)
    ImageView extraOptions;
    @BindView(R.id.options)
    ImageView options;

    ScrollView noteDeleted1;
    FirebaseFirestore db;
    @BindView(R.id.time)
    TextView time;
    private SingleNote singleNote;
    FirebaseUser user;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_maker_deleted);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        noteDeleted1 = findViewById(R.id.note_deleted1);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorwhite)); //status bar or the time bar at the top
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        noteDeleted1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NoteMakerRecycleBin.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (getIntent().getStringExtra("notedeleted") != null) {
            Gson gson = new Gson();
            singleNote = gson.fromJson(getIntent().getStringExtra("notedeleted"), SingleNote.class);
        } else {
            singleNote = new SingleNote();
        }

        updateUi();
        titleh1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                singleNote.setTitle(titleh1.getText().toString());
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                String datetime = simpleDateFormat.format(calendar.getTime());
                singleNote.setEditedOn("Edited " + datetime);
                time.setText(singleNote.getEditedOn());

            }
        });
        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                singleNote.setBody(notes.getText().toString());
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                String datetime = simpleDateFormat.format(calendar.getTime());
                singleNote.setEditedOn("Edited " + datetime);
                time.setText(singleNote.getEditedOn());
            }
        });

        time.setText(singleNote.getEditedOn());
    }

    private void updateUi() {
        if (singleNote != null) {

            titleh1.setText(singleNote.getTitle());
            notes.setText(singleNote.getBody());

        } else {
            Toast.makeText(this, "Note Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.options)
    public void onViewClicked() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(NoteMakerRecycleBin.this, R.style.BottomSheetDialogTheme);

        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.bin_bottom_sheet, (ViewGroup) findViewById(R.id.bin_bottom_sheet_container)
        );
        sheetView.findViewById(R.id.restorebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NoteMakerRecycleBin.this, "Note restored", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();

                singleNote.setInTrash(false);
                if (singleNote.getId() == null || singleNote.getId().isEmpty()) {
//


                    DocumentReference documentReference = db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).document();
                    String noteId = documentReference.getId();

                    db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).document(noteId).set(singleNote).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                        }
                    });

                } else {

                    db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).document(singleNote.getId()).set(singleNote).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                        }
                    });

                }
            }
        });
        sheetView.findViewById(R.id.deleteforeverbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myalert = new AlertDialog.Builder(NoteMakerRecycleBin.this, R.style.AlertDialogTheme);
                myalert.setMessage("Delete this note forever?");
                myalert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).document(singleNote.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                            }
                        });
                        Intent intent = new Intent(getApplicationContext(), RecycleBin.class);
                        startActivity(intent);

                    }
                });
                myalert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                myalert.show();


            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();

    }


}

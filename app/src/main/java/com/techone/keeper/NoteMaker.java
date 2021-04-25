package com.techone.keeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.techone.keeper.adapter.ColorAdapter;
import com.techone.keeper.model.SingleNote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import androidx.annotation.Nullable;

public class NoteMaker extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.archieve)
    ImageView archieve;
    @BindView(R.id.pin)
    ImageView pin;
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.titleh1)
    EditText titleh1;
    @BindView(R.id.notes)
    EditText notes;
    @BindView(R.id.extra_options)
    ImageView extraOptions;
    @BindView(R.id.options)
    ImageView options;
    @BindView(R.id.titleandbody)
    LinearLayout titleandbody;
    @BindView(R.id.notemakerscrollview)
    ScrollView notemakerscrollview;
    @BindView(R.id.contentarea)
    RelativeLayout contentarea;
    @BindView(R.id.edited_time)
    TextView time;
    LinearLayout bottomsheetContainer;


    LinearLayout linearLayout;
    View Vcolor;
    TextView tvcode, tvvalue;
    SeekBar sbred, sbgreen, sbblue;
    int red = 0, green = 0, blue = 0;

    private SingleNote singleNote;
    FirebaseUser user;
    RecyclerView bottomRecyclerView;

    int defaultvalue;
    String hexcode;
    FirebaseFirestore db;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_maker);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
       if(user!=null) {
           uid = user.getUid();
       }
        db= FirebaseFirestore.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            //getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorwhite)); //status bar or the time bar at the top
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        bottomRecyclerView = findViewById(R.id.color_recycler_view);
        defaultvalue = ContextCompat.getColor(NoteMaker.this, R.color.colorPrimary);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (getIntent().getStringExtra("note") != null) {
            Gson gson = new Gson();
            singleNote = gson.fromJson(getIntent().getStringExtra("note"), SingleNote.class);
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
            if (singleNote.getPinned()) {
                pin.setImageResource(R.drawable.icon_push_pin);

            } else {
                pin.setImageResource(R.drawable.icon_not_pinned);
            }

            if (singleNote.getArchived()) {
                archieve.setImageResource(R.drawable.ic_baseline_unarchive_24);

            } else {
                archieve.setImageResource(R.drawable.ic_baseline_archive_24);


            }

            titleh1.setText(singleNote.getTitle());
            notes.setText(singleNote.getBody());
            if (singleNote.getColorPallete() != null || singleNote.getColorPallete() != "") {
                contentarea.setBackgroundColor(Color.parseColor(singleNote.getColorPallete()));
                titleandbody.setBackgroundColor(Color.parseColor(singleNote.getColorPallete()));
                toolbar.setBackgroundColor(Color.parseColor(singleNote.getColorPallete()));
            }
        } else {
            Toast.makeText(this, "Note Not Found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (singleNote.getId() == null || singleNote.getId().isEmpty()) {

            DocumentReference documentReference =db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).document();
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

    @OnClick(R.id.archieve)
    public void onArchieveClicked() {
        singleNote.setArchived(!singleNote.getArchived());
        updateUi();
    }

    @OnClick(R.id.pin)
    public void onPinClicked() {
        singleNote.setPinned(!singleNote.getPinned());
        updateUi();
    }

    @OnClick(R.id.extra_options)
    public void onExtraOptionsClicked() {
        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.options)
    public void onOptionsClicked() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                NoteMaker.this, R.style.BottomSheetDialogTheme
        );


        View sheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.option_bottom_sheet,
                        (ViewGroup) findViewById(R.id.bottomSheetConatiner)
                );
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#ffffff");
        colors.add("#e3b6b3");
        colors.add("#b8c9ff");
        colors.add("#fff2a8");
        colors.add("#fcc7ff");
        colors.add("#e5ffa8");
        colors.add("#E5FFED");
        colors.add("#DBEC9A");
        colors.add("#EEE5DE");
        colors.add("#B4CCA8");
        colors.add("#a87d48");
        colors.add("#A9A9A9");

        RecyclerView color_recycler_view = sheetView.findViewById(R.id.color_recycler_view);
        LinearLayout bottomsheet = sheetView.findViewById(R.id.bottomSheetConatiner);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        color_recycler_view.setLayoutManager(linearLayoutManager);
        ColorAdapter colorAdapter = new ColorAdapter(this, colors, bottomsheet, contentarea, titleandbody, toolbar);

        color_recycler_view.setAdapter(colorAdapter);
        bottomsheet.setBackgroundColor(Color.parseColor(singleNote.getColorPallete()));
        sheetView.findViewById(R.id.sendbutton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String shareBody = "";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
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
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(colorAdapter.currentlySelected != -1) {
                    singleNote.setColorPallete(colorAdapter.getCurrentSelectedColor());
                    singleNote.setCustomColor(false);
                }
            }
        });

        sheetView.findViewById(R.id.deletebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleNote.setInTrash(true);

                FirebaseFirestore db= FirebaseFirestore.getInstance();
                db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).document(singleNote.getId()).set(singleNote).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                    }
                });
                Toast.makeText(NoteMaker.this, "deleted", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
                Intent intent =new Intent(getApplicationContext(),Dashboard.class);
                startActivity(intent);
            }

        }

        );
        sheetView.findViewById(R.id.colorpicker).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(NoteMaker.this, R.style.AlertDialogTheme);
                LayoutInflater inflater = (NoteMaker.this).getLayoutInflater();
                View view1 = inflater.inflate(R.layout.color_picker_layout, null);

                tvcode = view1.findViewById(R.id.tv_code);
                tvvalue = view1.findViewById(R.id.tv_value);
                sbred = view1.findViewById(R.id.sb_red);
                sbgreen = view1.findViewById(R.id.sb_green);
                sbblue = view1.findViewById(R.id.sb_blue);
                Vcolor = view1.findViewById(R.id.v_color);

                sbred.setOnSeekBarChangeListener(NoteMaker.this);
                sbgreen.setOnSeekBarChangeListener(NoteMaker.this);
                sbblue.setOnSeekBarChangeListener(NoteMaker.this);

                builder.setView(view1);
                builder.setTitle("Color Picker");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        singleNote.setCustomColor(true);
                        singleNote.setColorPallete(hexcode);

                        bottomsheet.setBackgroundColor(Color.parseColor(singleNote.getColorPallete()));
                        contentarea.setBackgroundColor(Color.parseColor(singleNote.getColorPallete()));
                        titleandbody.setBackgroundColor(Color.parseColor(singleNote.getColorPallete()));
                        toolbar.setBackgroundColor(Color.parseColor(singleNote.getColorPallete()));

                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setCancelable(true);
                builder.show();
            }


        });




        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId())
        {
            case R.id.sb_red:
                red = i;
                break;
            case R.id.sb_green:
                green = i;
                break;
            case R.id.sb_blue:
                blue = i;
                break;
        }

        Vcolor.setBackgroundColor(Color.rgb(red,green,blue));

        hexcode = HexCode(red,green,blue);

        tvcode.setText(hexcode.toUpperCase());
        tvvalue.setText(String.format("RGB (%d, %d, %d)", red,green,blue));

    }

    public String HexCode(int red, int green, int blue) {
        String code;
        code = "#";
        if (red != 0 && green == 0 && blue == 0) {
            code += Integer.toHexString(red);
            code += "0000";
        } else if (red == 0 && green != 0 && blue == 0) {
            code += "00";
            code += Integer.toHexString(green);
            code += "00";

        } else if (red == 0 && green == 0 && blue != 0) {
//            code+="0000";
            code += "0000";
            code += Integer.toHexString(blue);
//            code+="00";

        } else if (red != 0 && green == 0 && blue != 0) {
            code += Integer.toHexString(red);
            code += "00";
            code += Integer.toHexString(blue);
        }
        else if (red != 0 && green != 0 && blue == 0)
        {
            code += Integer.toHexString(red);

            code += Integer.toHexString(green);
            code += "00";
        }
            else if(red == 0 && green != 0 && blue != 0)
        {
            code+="00";
            code+=Integer.toHexString(green);
            code+=Integer.toHexString(blue);
        }
        else if(red == 0 && green == 0 && blue == 0)
        {
            code="#000000";
        }
        else
        {
            code += Integer.toHexString(red);
            code += Integer.toHexString(green);
            code += Integer.toHexString(blue);
        }

        return code;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}




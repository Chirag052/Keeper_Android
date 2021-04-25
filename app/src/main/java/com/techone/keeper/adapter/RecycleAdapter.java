package com.techone.keeper.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.techone.keeper.Constants;
import com.techone.keeper.NoteMakerRecycleBin;
import com.techone.keeper.R;
import com.techone.keeper.model.SingleNote;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.DataViewHolder> {
    Context context;
    ArrayList<SingleNote> data=new ArrayList<>();
    FirebaseUser user;
    String uid;
    public RecycleAdapter(Context context, ArrayList<SingleNote> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecycleAdapter.DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflater=LayoutInflater.from(context);
        View myownview =myInflater.inflate(R.layout.layout_view,parent,false);
        return new RecycleAdapter.DataViewHolder(myownview);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.DataViewHolder holder, int position) {
        holder.title.setText(data.get(position).getTitle());
        holder.body.setText(data.get(position).getBody());
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteMakerRecycleBin.class);
                Gson gson = new Gson();
                String dataToPass = gson.toJson(data.get(position));
                intent.putExtra("notedeleted", dataToPass);
                context.startActivity(intent);
            }
        });
        holder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //creating a popup menu

                PopupMenu popup = new PopupMenu(context, holder.title);
                //inflating menu from xml resource
                popup.inflate(R.menu.restore_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.restore:
                                FirebaseFirestore db= FirebaseFirestore.getInstance();
                                Toast.makeText(context, "Note restored", Toast.LENGTH_SHORT).show();
                                uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                data.get(position).setInTrash(false);
                                db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).document(data.get(position).getId()).set(data.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                    }
                                });
                                notifyDataSetChanged();
                                break;
                            case R.id.delete:
                                uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                AlertDialog.Builder myalert = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                                myalert.setMessage("Delete this note forever?");
                                myalert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseFirestore db= FirebaseFirestore.getInstance();
                                        db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).document(data.get(position).getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                            }
                                        });
                                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                myalert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                myalert.show();
                                notifyDataSetChanged();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
                return true;
            }
        });
        if (data.get(position).getColorPallete() != null && data.get(position).getColorPallete() != "") {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(Color.parseColor(data.get(position).getColorPallete()));
            shape.setStroke(1, Color.GRAY);
            shape.setCornerRadius(16);
            holder.mainView.setBackground(shape);
        } else {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(Color.parseColor("#FFFFFF"));
            shape.setStroke(1, Color.GRAY);
            shape.setCornerRadius(16);
            holder.mainView.setBackground(shape);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView body;
        RelativeLayout mainView;
        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            body=(itemView.findViewById(R.id.body));
            mainView = itemView.findViewById(R.id.main_view);
        }
    }
}

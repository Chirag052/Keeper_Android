package com.techone.keeper.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.techone.keeper.R;
import com.techone.keeper.model.SingleNote;

import java.util.ArrayList;


public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {


    LinearLayout bottomsheetContainer, titleandbody;
    RelativeLayout contentarea;
    SingleNote singleNote;
    MaterialToolbar materialToolbar;
    Context context;
    ArrayList<String> colors=new ArrayList<>();
    public int currentlySelected = -1;
    public ColorAdapter(Context context, ArrayList<String> colors, LinearLayout bottomsheetContainer, RelativeLayout contentarea, LinearLayout titleandbody
    , MaterialToolbar materialToolbar) {
        this.context = context;
        this.colors = colors;
        this.bottomsheetContainer = bottomsheetContainer;
        this.contentarea = contentarea;
        this.titleandbody = titleandbody;
        this.materialToolbar = materialToolbar;
    }



    @NonNull
    @Override
    public ColorAdapter.ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflater=LayoutInflater.from(context);
        View myownview =myInflater.inflate(R.layout.color_dot,parent,false);
        return new ColorViewHolder(myownview);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorAdapter.ColorViewHolder holder, int position) {
        if(currentlySelected==position)
        {

            holder.check.setVisibility(View.VISIBLE);

        }
        else
        {

            holder.check.setVisibility(View.GONE);

        }

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor(colors.get(position)));
        shape.setStroke(1,Color.GRAY);
        holder.color_view.setBackground(shape);

        holder.color_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(currentlySelected==-1)
                {
                    holder.check.setVisibility(View.VISIBLE);
                    currentlySelected = position;
                    bottomsheetContainer.setBackgroundColor(Color.parseColor(colors.get(position)));
                    contentarea.setBackgroundColor(Color.parseColor(colors.get(position)));
                    titleandbody.setBackgroundColor(Color.parseColor(colors.get(position)));
                    materialToolbar.setBackgroundColor(Color.parseColor(colors.get(position)));
                }
                else {

                    if(currentlySelected==position)
                    {
                            holder.check.setVisibility(View.GONE);
                            currentlySelected=-1;
                            bottomsheetContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            contentarea.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            titleandbody.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            materialToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    }
                    else
                    {
                        holder.check.setVisibility(View.VISIBLE);
                        currentlySelected = position;
                        bottomsheetContainer.setBackgroundColor(Color.parseColor(colors.get(position)));
                        contentarea.setBackgroundColor(Color.parseColor(colors.get(position)));
                        titleandbody.setBackgroundColor(Color.parseColor(colors.get(position)));
                        materialToolbar.setBackgroundColor(Color.parseColor(colors.get(position)));
                        Color.parseColor(colors.get(position));
                    }

                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public String getCurrentSelectedColor() {
        if(currentlySelected == -1) {
            return "#FFFFFF";
        } else {
            return colors.get(currentlySelected);
        }

    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout color_view;
        ImageView check;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            color_view = itemView.findViewById(R.id.color_view);
            check = itemView.findViewById(R.id.checked);



        }
    }
}

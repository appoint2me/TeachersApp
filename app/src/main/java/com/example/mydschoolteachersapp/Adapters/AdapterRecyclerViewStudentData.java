package com.example.mydschoolteachersapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydschoolteachersapp.Model.StudentDataModel;
import com.example.mydschoolteachersapp.R;
import com.example.mydschoolteachersapp.ViewStudentsListActivity;

import java.util.ArrayList;

public class AdapterRecyclerViewStudentData extends RecyclerView.Adapter<AdapterRecyclerViewStudentData.ViewHolder> {
    Context mContext=null;
    ArrayList<StudentDataModel> mListStudentData=null;
    public AdapterRecyclerViewStudentData(Context context, ArrayList<StudentDataModel> mListStudentData) {
    this.mContext=context;
    this.mListStudentData=mListStudentData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_student_data,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final StudentDataModel studentDataModel=mListStudentData.get(i);
            viewHolder.textViewFirstName.setText(studentDataModel.getFirstName());
            viewHolder.textViewRollNo.setText(studentDataModel.getRollNo());
            viewHolder.textViewAttendance.setText(studentDataModel.getAttendance());
            if (studentDataModel.getAttendance().equalsIgnoreCase("P"))
            {
                viewHolder.fabAttendance.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGreen)));
            }
            else
            {
                viewHolder.fabAttendance.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorRed)));
            }
            viewHolder.fabAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String Section_idll = String.valueOf(studentDataModel.getSectionId());
                    Log.d("Status", Section_idll);
                    String Classid = String.valueOf(studentDataModel.getClassId());
                    Log.d("Status", Classid);
                    String Itemstatus = String.valueOf(studentDataModel.getAttendance());
                    Log.d("Status", Itemstatus);
                    String Students_id = String.valueOf(studentDataModel.getApplicationId());
                    Log.d("Status", Students_id);
                    Intent intent = new Intent(v.getContext(), ViewStudentsListActivity.class);
                    intent.putExtra("Item_status", Itemstatus);
                    intent.putExtra("CLASS_ID", Classid);
                    intent.putExtra("Application_id", Students_id);
                    intent.putExtra("SECTION_ID",Section_idll);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mListStudentData.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFirstName,textViewRollNo,textViewAttendance;
        FloatingActionButton fabAttendance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFirstName= itemView.findViewById(R.id.textViewFirstName);
            textViewRollNo= itemView.findViewById(R.id.textViewRollNo);
            textViewAttendance= itemView.findViewById(R.id.textViewAttendance);
            fabAttendance= itemView.findViewById(R.id.fab);
        }
    }
}

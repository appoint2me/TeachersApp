package com.example.mydschoolteachersapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mydschoolteachersapp.Adapters.AdapterRecyclerViewStudentData;
import com.example.mydschoolteachersapp.Classes.CheckInternetConnection;
import com.example.mydschoolteachersapp.Classes.Config;
import com.example.mydschoolteachersapp.Classes.Singelton;
import com.example.mydschoolteachersapp.Model.StudentDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewStudentsListActivity extends AppCompatActivity {
    String mClassId,mSectionId;
    ArrayList<StudentDataModel> mListStudentData=new ArrayList<>();
    RecyclerView recyclerViewStudentData;
    String current_status;
    String Statusofstd = "P";
    String item_status=null;
    String application_id=null;
    ProgressDialog progressDialog;
    String Status=null;
    TextView textViewPresentCount,textViewAbsentCount;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class_list);
        Intent intent=getIntent();
        android.support.v7.widget.Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Students List");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        textViewPresentCount= findViewById(R.id.textViewPresentCount);
        textViewAbsentCount= findViewById(R.id.textViewAbsentCount);
        mClassId= intent.getExtras().getString("CLASS_ID");
        mSectionId= intent.getExtras().getString("SECTION_ID");
        item_status=getIntent().getStringExtra("Item_status");
        application_id=getIntent().getStringExtra("Application_id");
        recyclerViewStudentData= findViewById(R.id.recyclerViewStudentData);
        if (item_status != null) {
            if (item_status.equalsIgnoreCase(Statusofstd)) {
                //o for delet from db
                Status = "0";
                changeAttendanceStatus();
            }
            else
            {
                //1 for insert in db
                Status = "1";
                changeAttendanceStatus();
            }
        }
        else
        {
            if (CheckInternetConnection.checkConnection(this)) {
                getClassStudentsList();
            } else {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.container),
                        "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Go to Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(
                                Settings.ACTION_WIFI_SETTINGS));

                    }
                });
                snackbar.show();

            }

        }
        textViewPresentCount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog=new ProgressDialog(ViewStudentsListActivity.this);
                progressDialog.setTitle("Students Details");
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                StringRequest stringRequestGetClassList=new StringRequest(Request.Method.POST, Config.URL_GET_CLASS_STUDENTS_LIST, new Response.Listener<String>() {
                    int presentCount=0;
                    int absentCount=0;
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        mListStudentData.clear();
                        try{
                            System.out.println("View Class List Response:"+response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("studentsname");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String attendance=jsonObject1.getString("Attidends");
                                String firstName=jsonObject1.getString("first_name");
                                String classId=jsonObject1.getString("class_id");
                                String applicationId=jsonObject1.getString("application_id");
                                String schoolId=jsonObject1.getString("school_id");
                                String rollNo=jsonObject1.getString("roll_no");
                                String sectionId=jsonObject1.getString("section_id");
                                StudentDataModel studentDataModel=new StudentDataModel(attendance,firstName,
                                        classId,sectionId,rollNo,applicationId,schoolId);
                                if(attendance.equalsIgnoreCase("P"))
                                {

                                    mListStudentData.add(studentDataModel);
                                }
//                                else if(attendance.equalsIgnoreCase("A"))
//                                {
//                                    mListStudentData.add(studentDataModel);
//                                }

                                // Toast.makeText(ViewStudentsListActivity.this, attendance, Toast.LENGTH_SHORT).show();
                                if(studentDataModel.getAttendance().equalsIgnoreCase("P"))
                                {
                                    presentCount=presentCount+1;
                                    textViewPresentCount.setText("Present-"+presentCount+"");
                                    //Toast.makeText(ViewStudentsListActivity.this, presentCount, Toast.LENGTH_SHORT).show();
                                }
                                else if(studentDataModel.getAttendance().equalsIgnoreCase("A"))
                                {
                                    absentCount=absentCount+1;
                                    textViewAbsentCount.setText("Absent-"+absentCount);
                                }
                            }

                            AdapterRecyclerViewStudentData adapterRecyclerViewStudentData=new AdapterRecyclerViewStudentData(ViewStudentsListActivity.this,mListStudentData);
                            LinearLayoutManager layoutManager=new LinearLayoutManager(ViewStudentsListActivity.this);
                            layoutManager.setReverseLayout(true);
                            layoutManager.setStackFromEnd(true);
                            recyclerViewStudentData.setLayoutManager(layoutManager);
                            recyclerViewStudentData.setAdapter(adapterRecyclerViewStudentData);
                            recyclerViewStudentData.setHasFixedSize(true);
                            adapterRecyclerViewStudentData.notifyDataSetChanged();


                        }
                        catch(Exception e)
                        {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            // System.out.println("View Class List Exception:"+e.getMessage());
                            Toast.makeText(ViewStudentsListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        // System.out.println("View Class List Error:"+error.getMessage());
                        Toast.makeText(ViewStudentsListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> params=new HashMap<>();
                        params.put("class_id",mClassId);
                        params.put("section_id",mSectionId);
                        return params;
                    }
                };
                stringRequestGetClassList.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Singelton.getInstance(getApplicationContext()).addToRequestQue(stringRequestGetClassList);

            }
        });
        textViewAbsentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog=new ProgressDialog(ViewStudentsListActivity.this);
                progressDialog.setTitle("Students Details");
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                StringRequest stringRequestGetClassList=new StringRequest(Request.Method.POST, Config.URL_GET_CLASS_STUDENTS_LIST, new Response.Listener<String>() {
                    int presentCount=0;
                    int absentCount=0;
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        mListStudentData.clear();
                        try{
                            System.out.println("View Class List Response:"+response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("studentsname");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String attendance=jsonObject1.getString("Attidends");
                                String firstName=jsonObject1.getString("first_name");
                                String classId=jsonObject1.getString("class_id");
                                String applicationId=jsonObject1.getString("application_id");
                                String schoolId=jsonObject1.getString("school_id");
                                String rollNo=jsonObject1.getString("roll_no");
                                String sectionId=jsonObject1.getString("section_id");
                                StudentDataModel studentDataModel=new StudentDataModel(attendance,firstName,
                                        classId,sectionId,rollNo,applicationId,schoolId);
                                if(attendance.equalsIgnoreCase("A"))
                                {

                                    mListStudentData.add(studentDataModel);
                                }
//                                else if(attendance.equalsIgnoreCase("A"))
//                                {
//                                    mListStudentData.add(studentDataModel);
//                                }

                                // Toast.makeText(ViewStudentsListActivity.this, attendance, Toast.LENGTH_SHORT).show();
                                if(studentDataModel.getAttendance().equalsIgnoreCase("P"))
                                {
                                    presentCount=presentCount+1;
                                    textViewPresentCount.setText("Present-"+presentCount+"");
                                    //Toast.makeText(ViewStudentsListActivity.this, presentCount, Toast.LENGTH_SHORT).show();
                                }
                                else if(studentDataModel.getAttendance().equalsIgnoreCase("A"))
                                {
                                    absentCount=absentCount+1;
                                    textViewAbsentCount.setText("Absent-"+absentCount);
                                }
                            }

                            AdapterRecyclerViewStudentData adapterRecyclerViewStudentData=new AdapterRecyclerViewStudentData(ViewStudentsListActivity.this,mListStudentData);
                            LinearLayoutManager layoutManager=new LinearLayoutManager(ViewStudentsListActivity.this);
                            layoutManager.setReverseLayout(true);
                            layoutManager.setStackFromEnd(true);
                            recyclerViewStudentData.setLayoutManager(layoutManager);
                            recyclerViewStudentData.setAdapter(adapterRecyclerViewStudentData);
                            recyclerViewStudentData.setHasFixedSize(true);
                            adapterRecyclerViewStudentData.notifyDataSetChanged();


                        }
                        catch(Exception e)
                        {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            // System.out.println("View Class List Exception:"+e.getMessage());
                            Toast.makeText(ViewStudentsListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        // System.out.println("View Class List Error:"+error.getMessage());
                        Toast.makeText(ViewStudentsListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> params=new HashMap<>();
                        params.put("class_id",mClassId);
                        params.put("section_id",mSectionId);
                        return params;
                    }
                };
                stringRequestGetClassList.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Singelton.getInstance(getApplicationContext()).addToRequestQue(stringRequestGetClassList);

            }
        });
    }
    public void getClassStudentsList()
    {

        final ProgressDialog progressDialog=new ProgressDialog(ViewStudentsListActivity.this);
        progressDialog.setTitle("Students Details");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        StringRequest stringRequestGetClassList=new StringRequest(Request.Method.POST, Config.URL_GET_CLASS_STUDENTS_LIST, new Response.Listener<String>() {
        int presentCount=0;
        int absentCount=0;
        @Override
        public void onResponse(String response) {
            progressDialog.dismiss();
            mListStudentData.clear();
            try{
                System.out.println("View Class List Response:"+response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("studentsname");
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    String attendance=jsonObject1.getString("Attidends");
                    String firstName=jsonObject1.getString("first_name");
                    String classId=jsonObject1.getString("class_id");
                    String applicationId=jsonObject1.getString("application_id");
                    String schoolId=jsonObject1.getString("school_id");
                    String rollNo=jsonObject1.getString("roll_no");
                    String sectionId=jsonObject1.getString("section_id");
                    StudentDataModel studentDataModel=new StudentDataModel(attendance,firstName,
                            classId,sectionId,rollNo,applicationId,schoolId);
                    mListStudentData.add(studentDataModel);
                    // Toast.makeText(ViewStudentsListActivity.this, attendance, Toast.LENGTH_SHORT).show();
                    if(studentDataModel.getAttendance().equalsIgnoreCase("P"))
                    {
                        presentCount=presentCount+1;
                        textViewPresentCount.setText("Present-"+presentCount+"");
                        //Toast.makeText(ViewStudentsListActivity.this, presentCount, Toast.LENGTH_SHORT).show();
                    }
                    else if(studentDataModel.getAttendance().equalsIgnoreCase("A"))
                    {
                        absentCount=absentCount+1;
                        textViewAbsentCount.setText("Absent-"+absentCount);
                    }
                }

                AdapterRecyclerViewStudentData adapterRecyclerViewStudentData=new AdapterRecyclerViewStudentData(ViewStudentsListActivity.this,mListStudentData);
                LinearLayoutManager layoutManager=new LinearLayoutManager(ViewStudentsListActivity.this);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                recyclerViewStudentData.setLayoutManager(layoutManager);
                recyclerViewStudentData.setAdapter(adapterRecyclerViewStudentData);
                recyclerViewStudentData.setHasFixedSize(true);
                adapterRecyclerViewStudentData.notifyDataSetChanged();


            }
            catch(Exception e)
            {
                progressDialog.dismiss();
                e.printStackTrace();
                // System.out.println("View Class List Exception:"+e.getMessage());
                Toast.makeText(ViewStudentsListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            // System.out.println("View Class List Error:"+error.getMessage());
            Toast.makeText(ViewStudentsListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }){
        @Override
        protected Map<String, String> getParams() {
            Map<String,String> params=new HashMap<>();
            params.put("class_id",mClassId);
            params.put("section_id",mSectionId);
            return params;
        }
    };
        stringRequestGetClassList
                .setRetryPolicy(new DefaultRetryPolicy(50000
                        ,DefaultRetryPolicy
                        .DEFAULT_MAX_RETRIES
                        ,DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        Singelton.getInstance(getApplicationContext())
                .addToRequestQue(stringRequestGetClassList);
    }
    public void changeAttendanceStatus() {
        //Toast.makeText(ViewStudentsListActivity.this, "STUDENT ID: "+application_id+"STATUS:  "+Status, Toast.LENGTH_SHORT).show();
        final ProgressDialog progressDialog1=new ProgressDialog(ViewStudentsListActivity.this);
        progressDialog1.setTitle("Change Students List");
        progressDialog1.setMessage("Please Wait...");
        progressDialog1.show();
        StringRequest stringRequestChangeAttendanceStatus = new StringRequest(Request.Method.POST, Config.URL_CHANGE_ATTENDANCE_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog1.dismiss();
                try {

                   // Toast.makeText(ViewStudentsListActivity.this, response, Toast.LENGTH_SHORT).show();
                   getClassStudentsList();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog1.dismiss();
                    Toast.makeText(ViewStudentsListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog1.dismiss();
                Toast.makeText(ViewStudentsListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //send data to server and get respons from its in respons

                params.put("student_id", application_id);
                params.put("status", Status);
                return params;
            }
        };

        stringRequestChangeAttendanceStatus.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Singelton.getInstance(ViewStudentsListActivity.this).addToRequestQue(stringRequestChangeAttendanceStatus);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ViewStudentsListActivity.this, NavigationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
                // todo: goto back activity from here
            case R.id.action_settings:
                final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
                alertBuilder.setTitle("Logout");
                alertBuilder.setMessage("Do you Really Want to Exit");
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor=getSharedPreferences(Config.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();
                        Intent intent=new Intent(ViewStudentsListActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertBuilder.create();
                alertBuilder.show();

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation,menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewStudentsListActivity.this, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

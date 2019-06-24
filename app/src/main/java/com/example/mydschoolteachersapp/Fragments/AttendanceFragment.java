package com.example.mydschoolteachersapp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.mydschoolteachersapp.Adapters.AdapterRecyclerViewClass;
import com.example.mydschoolteachersapp.Classes.CheckInternetConnection;
import com.example.mydschoolteachersapp.Classes.Config;
import com.example.mydschoolteachersapp.Classes.Singelton;
import com.example.mydschoolteachersapp.Model.ClassModel;
import com.example.mydschoolteachersapp.NavigationActivity;
import com.example.mydschoolteachersapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment {
    RecyclerView recyclerViewClass;
    public ArrayList<ClassModel> mListClass;
    Context mContext;
    ProgressBar progressBar;



    public AttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class, container, false);
        mContext=view.getContext();
        ((NavigationActivity) getActivity()).getSupportActionBar().setTitle("Class List");
        recyclerViewClass = view.findViewById(R.id.recyclerViewClass);

        progressBar = view.findViewById(R.id.progressBar);
        mListClass = new ArrayList<>();
        if (CheckInternetConnection.checkConnection(view.getContext())) {
//            Snackbar snackbar = Snackbar.make(view.findViewById(R.id.container),
//                    " Connected to Internet", Snackbar.LENGTH_SHORT);
//            snackbar.show();
            //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            getClassDataList();
        } else {
            Snackbar snackbar = Snackbar.make(view.findViewById(R.id.container),
                    "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Go to Settings", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(
                            Settings.ACTION_WIFI_SETTINGS));

                }
            });
            snackbar.show();
            // Toast.makeText(view.getContext(), "Not Connected", Toast.LENGTH_SHORT).show();

        }

        return view;
    }


    public void getClassData() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Config.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
        final String  school_id = sharedPreferences.getString(Config.SCHOOL_ID, null);
        StringRequest stringRequestGetClass=new StringRequest(Request.Method.POST, Config.URL_GET_CLASS_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    progressBar.setVisibility(View.GONE);
                    recyclerViewClass.setVisibility(View.VISIBLE);

                    //Log.d("RESPONSE",response);
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("SClasslist");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        ClassModel classModel =new ClassModel(jsonObject1.getString("class_name")
                                ,jsonObject1.getString("class_id"));
                        mListClass.add(classModel);
                       // System.out.print(mListClass.size()+"Json");
                    }
                   // System.out.println(mListClass.size()+"Fragment");
                    AdapterRecyclerViewClass adapterRecyclerViewClass=new AdapterRecyclerViewClass(mContext, mListClass);
                    GridLayoutManager layoutManager = new GridLayoutManager(mContext,2);
                    recyclerViewClass.setLayoutManager(layoutManager);
                    recyclerViewClass.setAdapter(adapterRecyclerViewClass);
                    recyclerViewClass.setHasFixedSize(true);
                    adapterRecyclerViewClass.notifyDataSetChanged();
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                  //  Log.e("JSON_EXCEPTION",e.getMessage(),e);

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params=new HashMap<>();

                params.put("school_id",school_id);
               // Log.d(Config.TAG,school_id);

                return params;

            }
        };
        Singelton.getInstance(getContext()).addToRequestQue(stringRequestGetClass);
    }
    private void getClassDataList(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Config.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
        final String school_id = sharedPreferences.getString(Config.SCHOOL_ID, null);
        final String staff_id = sharedPreferences.getString(Config.USER_ID, null);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Config.URL_GET_ASSINGMENT_LIST+staff_id, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                recyclerViewClass.setVisibility(View.VISIBLE);

                try {
                    String status=response.getString("status");
                    if(status.equalsIgnoreCase("1")){
                        JSONObject jsonObject=response.getJSONObject("data");
                        JSONArray jsonArray=jsonObject.getJSONArray("Class_List");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            String class_id=  jsonObject1.getString("class_id");
                            String class_name=jsonObject1.getString("class_name");
                          //  Toast.makeText(mContext, class_id+class_name, Toast.LENGTH_SHORT).show();

                            ClassModel classModel = new ClassModel(class_name,class_id);
                            mListClass.add(classModel);
                        }
                        AdapterRecyclerViewClass adapterRecyclerViewClass = new AdapterRecyclerViewClass(mContext, mListClass);
                        GridLayoutManager layoutManager = new GridLayoutManager(mContext,2);
                        recyclerViewClass.setLayoutManager(layoutManager);
                        recyclerViewClass.setAdapter(adapterRecyclerViewClass);
                        recyclerViewClass.setHasFixedSize(true);
                        adapterRecyclerViewClass.notifyDataSetChanged();
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewClass.getContext(),
                                layoutManager.getOrientation());
                        recyclerViewClass.addItemDecoration(dividerItemDecoration);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        Singelton.getInstance(mContext).addToRequestQue(jsonObjectRequest);
    }
}
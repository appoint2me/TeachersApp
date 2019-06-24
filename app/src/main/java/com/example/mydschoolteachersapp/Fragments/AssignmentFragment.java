package com.example.mydschoolteachersapp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.example.mydschoolteachersapp.Adapters.AdapterRecyclerViewAssignment;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignmentFragment extends Fragment {
    RecyclerView recyclerViewAssignment;
    public ArrayList<ClassModel> assignmentModelList;
    Context mContext;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    public AssignmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assingment, container, false);
        mContext = view.getContext();
        assignmentModelList = new ArrayList<>();
        ((NavigationActivity) getActivity()).getSupportActionBar().setTitle("Class List");
        recyclerViewAssignment = view.findViewById(R.id.recylerViewAssignment);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerViewAssignment.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if (CheckInternetConnection.checkConnection(view.getContext())) {

            getAssignmentList();
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

    private void getAssignmentList(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(Config.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
        final String school_id = sharedPreferences.getString(Config.SCHOOL_ID, null);
        final String staff_id = sharedPreferences.getString(Config.USER_ID, null);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Config.URL_GET_ASSINGMENT_LIST+staff_id, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              //  Toast.makeText(mContext, response.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                recyclerViewAssignment.setVisibility(View.VISIBLE);
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
                            assignmentModelList.add(classModel);
                        }
                        AdapterRecyclerViewAssignment adapterRecyclerViewAssignment = new AdapterRecyclerViewAssignment(mContext, assignmentModelList);
                        GridLayoutManager layoutManager = new GridLayoutManager(mContext,2);
                        recyclerViewAssignment.setLayoutManager(layoutManager);
                        recyclerViewAssignment.setAdapter(adapterRecyclerViewAssignment);
                        recyclerViewAssignment.setHasFixedSize(true);
                        adapterRecyclerViewAssignment.notifyDataSetChanged();
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewAssignment.getContext(),
                                layoutManager.getOrientation());
                        recyclerViewAssignment.addItemDecoration(dividerItemDecoration);
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


package com.example.mydschoolteachersapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydschoolteachersapp.Classes.Config;
import com.example.mydschoolteachersapp.Fragments.AssignmentFragment;
import com.example.mydschoolteachersapp.Fragments.AttendanceFragment;
import com.example.mydschoolteachersapp.Fragments.CalendarFragment;
import com.example.mydschoolteachersapp.Fragments.HomeFragment;

import java.util.Objects;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
      int fragmentId;
      String fragmentName;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("DASHBOARD");
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment=new HomeFragment();
        fragmentTransaction.replace(R.id.content_navigation,homeFragment).commit();
        fragmentTransaction.addToBackStack("");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        SharedPreferences sharedPreferences=getSharedPreferences(Config.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
        String user_name= sharedPreferences.getString(Config.USERNAME,"");
        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView textViewUserName=navigationView.getHeaderView(0).findViewById(R.id.textViewUsername);
        textViewUserName.setText(user_name);
        navigationView.setNavigationItemSelectedListener(this);
    }
    int doubleBackToExitPressed = 1;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Toast.makeText(this, "Press again to exit the app", Toast.LENGTH_SHORT).show();
            if (doubleBackToExitPressed == 2) {
                finishAffinity();
                System.exit(0);
            }
            else {
                doubleBackToExitPressed++;
                Toast.makeText(this, "Please press Back again to exit", Toast.LENGTH_SHORT).show();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressed=1;
                }
            }, 2000);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
            alertBuilder.setTitle("Logout");
            alertBuilder.setMessage("Do you Really Want to Exit");

            alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor=getSharedPreferences(Config.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    Intent intent=new Intent(NavigationActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertBuilder.create();
            alertBuilder.show();


        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();


        int id = item.getItemId();
        if(id == R.id.nav_Home)
        {
                    HomeFragment homeFragment1=new HomeFragment();
                    fragmentTransaction.replace(R.id.content_navigation,homeFragment1).commit();
                    fragmentTransaction.addToBackStack("Home Fragment");
        }
       else if (id == R.id.nav_assingment) {
            // Handle the camera action
            AssignmentFragment assingmentFragment=new AssignmentFragment();
            fragmentTransaction.replace(R.id.content_navigation,assingmentFragment).commit();
            fragmentTransaction.addToBackStack("Assingment Fragment");
        } else if (id == R.id.nav_attendance) {
            AttendanceFragment attendanceFragment =new AttendanceFragment();
            fragmentTransaction.replace(R.id.content_navigation, attendanceFragment).commit();
            fragmentTransaction.addToBackStack("Class Fragment");
        }
        else if (id == R.id.nav_calender) {
            CalendarFragment calendarFragment=new CalendarFragment();
            fragmentTransaction.replace(R.id.content_navigation,calendarFragment).commit();
            fragmentTransaction.addToBackStack(null);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}

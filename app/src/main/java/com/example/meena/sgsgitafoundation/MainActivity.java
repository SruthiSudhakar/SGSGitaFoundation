package com.example.meena.sgsgitafoundation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.vision) {
            Intent intent = new Intent(getBaseContext(), VisionActivity.class);
            intent.putExtra("Tab", "Vision");
            startActivity(intent);
        } else if (id == R.id.swamiji) {
            Intent intent = new Intent(getBaseContext(), VisionActivity.class);
            intent.putExtra("Tab", "Swamiji");
            startActivity(intent);
        } else if (id == R.id.program) {
            Intent intent = new Intent(getBaseContext(), VisionActivity.class);
            intent.putExtra("Tab", "Program");
            startActivity(intent);
        } else if (id == R.id.journey) {
            Intent intent = new Intent(getBaseContext(), VisionActivity.class);
            intent.putExtra("Tab", "Journey");
            startActivity(intent);
        } else if (id == R.id.timeline) {
            startActivity(new Intent(getBaseContext(), TimeLine.class));
        } else if (id == R.id.upcoming) {
            Intent intent = new Intent(getBaseContext(), VisionActivity.class);
            intent.putExtra("Tab", "Upcoming");
            startActivity(intent);
//        } else if (id == R.id.centers) {
//            Intent intent = new Intent(getBaseContext(), VisionActivity.class);
//            intent.putExtra("Tab", "Centers");
//            startActivity(intent);
        } else if (id == R.id.tutor) {
            startActivity(new Intent(getBaseContext(), TutorApp.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

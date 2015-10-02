package com.test.arvindiyer.econvoy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.test.arvindiyer.econvoy.Helper.SQLiteHandler;
import com.test.arvindiyer.econvoy.Helper.SessionManager;


import java.util.HashMap;

public class Dashboard extends AppCompatActivity  {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private SQLiteHandler db;
    private SessionManager session;
    private TextView txtName;
    private TextView txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        txtEmail = (TextView) findViewById(R.id.user_mail);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
             logoutUser();
        }
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();


        final  String name = user.get("name");
        final String email = user.get("email");
        final  String type=user.get("type");

        txtEmail.setText(email);


       // txtName.setText(name);
        // txtEmail.setText(email);




        /**
         *Setup the DrawerLayout and NavigationView
         */
        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the FirTabFragment as the first Fragment
         */
        Bundle bundle=new Bundle();
        final WelcomeFragment w = new WelcomeFragment();

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        bundle.putString("Name",name);
        bundle.putString("Email",email);
        w.setArguments(bundle);
        mFragmentTransaction.replace(R.id.containerView,w).commit();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav) ;

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.nav_item_welcome) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,w).commit();

                }


                if (menuItem.getItemId() == R.id.nav_item_profile) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new ProfileFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_fir) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView,new FirTabFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_lost) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new LostFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_uvch) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new VechileFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_ub) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new BodiesFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_uc) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new ChildrenFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_law) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new LawyerFragment()).commit();

                }
                if (menuItem.getItemId() == R.id.nav_item_ipc) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new IPCFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_court) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new CourtFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_status) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new StatFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_feedback) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new FeedbackFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    logoutUser();
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(Dashboard.this, Login.class);
        startActivity(intent);
        finish();
    }

}
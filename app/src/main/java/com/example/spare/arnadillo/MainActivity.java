package com.example.spare.arnadillo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import java.util.Locale;

public class MainActivity extends Activity implements FelineAdapter.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mFelineKind;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        mTitle = mDrawerTitle = getTitle();
        mFelineKind = getResources().getStringArray(R.array.feline_kinds);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        // mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new FelineAdapter(mFelineKind, this));
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Default starting state upon startup
        if (savedInstanceState == null) {
            selectItem(0);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("", "Started Flurry Session -------------------------- >");
        FlurryAgent.init(this, AppConstants.FLURRY_API_KEY);
        FlurryAgent.setLogEnabled(true);
        FlurryAgent.setLogEvents(true);
        FlurryAgent.onStartSession(this, AppConstants.FLURRY_API_KEY);
        FlurryAgent.setLogLevel(Log.VERBOSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = FelineFragment.newInstance(position);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        // update selected item title, then close the drawer
        setTitle(mFelineKind[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public static class FelineFragment extends Fragment {
        public static final String ARG_FELINE_NUMBER = "feline_number";

        public FelineFragment() {
            // Empty constructor required for fragment subclasses
        }

        public static Fragment newInstance(int position) {
            Fragment fragment = new FelineFragment();
            Bundle args = new Bundle();
            args.putInt(FelineFragment.ARG_FELINE_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_feline, container, false);
            int i = getArguments().getInt(ARG_FELINE_NUMBER);

            String feline = getResources().getStringArray(R.array.feline_kinds)[i];

            int imageId = getResources().getIdentifier(feline.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            final ImageView iv = ((ImageView) rootView.findViewById(R.id.image));
            iv.setImageResource(imageId);

            /*  Pulse Animation on the image
                TODO: Separate the ImageResource dependency on the ImageView
                (i.e. Apply the animation on the image resource, not the entire iv)
             */
            final Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);

            // Soundboard Implementation!
            final int soundID = getResources().getIdentifier(feline.toLowerCase(Locale.getDefault()), "raw", getActivity().getPackageName());

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   iv.startAnimation(pulse);
                   MediaPlayer mp = MediaPlayer.create(getActivity(), soundID);
                   mp.start();

                    FlurryAgent.logEvent("Main Button Pressed");
                    getActivity().finish();

                }
            });


            getActivity().setTitle(feline);
            return rootView;
        }
    }
}

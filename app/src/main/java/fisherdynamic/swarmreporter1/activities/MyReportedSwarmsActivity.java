package fisherdynamic.swarmreporter1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import fisherdynamic.swarmreporter1.R;
import fisherdynamic.swarmreporter1.models.SwarmReport;
import fisherdynamic.swarmreporter1.utilityClasses.Utilities;
import fisherdynamic.swarmreporter1.viewHolders.FirebaseClaimViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyReportedSwarmsActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private Query swarmReportQuery;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private String userName;
    private String userId;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String TAG = MyReportedSwarmsActivity.class.getSimpleName();

    private View hView;
    private NavigationView navigationView;

    @Bind(R.id.myReportedSwarmsRecyclerView) RecyclerView myReportedSwarmsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reported_drawer);
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        userName = mSharedPreferences.getString("userName", null);
        userId = mSharedPreferences.getString("userId", null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);

        ArrayList<String> children = new ArrayList<>();
        children.add("users");
        children.add(userId);
        children.add("reportedSwarms"); //TODO ???

        setUpFirebaseAdapter(children);
    }

    private void setUpFirebaseAdapter(final ArrayList<String> children) {
        swarmReportQuery = FirebaseDatabase.getInstance().getReference(children.get(0));
        for(int i =1; i<children.size(); i++){
            swarmReportQuery = swarmReportQuery.getRef().child(children.get(i));
        }
        mFirebaseAdapter = new FirebaseRecyclerAdapter<SwarmReport, FirebaseClaimViewHolder>
                (SwarmReport.class, R.layout.my_reported_claim_item, FirebaseClaimViewHolder.class,
                        swarmReportQuery) {

            @Override
            protected void populateViewHolder(FirebaseClaimViewHolder viewHolder,
                                              SwarmReport model, int position) {
                viewHolder.bindCurrentUserNameAndId(userName, userId);
                viewHolder.bindSwarmReportForMyReportedSwarms(model);
                myReportedSwarmsRecyclerView.setVisibility(View.VISIBLE); //TODO maybe move this back to setUpBlankAdapter?
            }
        };
        setUpBlankAdapter();
        Log.d("personal", "got past setUpBlankAdapter");
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myReportedSwarmsRecyclerView.getContext(),
                new LinearLayoutManager(MyReportedSwarmsActivity.this).getOrientation());
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.recycler_view_divider));
        myReportedSwarmsRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setUpBlankAdapter(){
        Log.d("personal", "got here setUpBlankAdapater");
        myReportedSwarmsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyReportedSwarmsActivity.this);
        myReportedSwarmsRecyclerView.setLayoutManager(linearLayoutManager);
        myReportedSwarmsRecyclerView.setAdapter(mFirebaseAdapter);
        Log.d("personal", "setUpBlankAdapter done with function");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Utilities.logoutWithContextAndSharedPreferences(this, mEditor);
            finish();
            return true;
        }

        if (id == R.id.action_viewAvailableReports){ //&& !TAG.equals("MainActivity")
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_newReport) { //  && !TAG.equals("NewSwarmReportActivity")
            Intent intent = new Intent(this, NewSwarmReportActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_myClaims) { //  && !TAG.equals("MyClaimedSwarmsActivity")
            Intent intent = new Intent(this, MyClaimedSwarmsActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_myReportedSwarms) { //  && !TAG.equals("MyReportedSwarmsActivity")
            Intent intent = new Intent(this, MyReportedSwarmsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

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
import android.widget.Toast;

import fisherdynamic.swarmreporter1.R;
import fisherdynamic.swarmreporter1.models.SwarmReport;
import fisherdynamic.swarmreporter1.viewHolders.FirebaseClaimViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyClaimedSwarmsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Query swarmReportQuery;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String userName;
    private String userId;
    private String TAG = MyClaimedSwarmsActivity.class.getSimpleName();
    private View hView;
    private NavigationView navigationView;

    @Bind(R.id.myClaimedSwarmsRecyclerView) RecyclerView myClaimedSwarmsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, ">>>>onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_claim_drawer);
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
        children.add("claimedSwarms"); //TODO ??
        setUpFirebaseAdapter(children);
    }

    private void setUpFirebaseAdapter(final ArrayList<String> children) {
                swarmReportQuery = FirebaseDatabase.getInstance().getReference(children.get(0));
                for(int i =1; i<children.size(); i++){
                    swarmReportQuery = swarmReportQuery.getRef().child(children.get(i));
                }
                swarmReportQuery = swarmReportQuery
                        .orderByChild("claimed")
                        .equalTo(true);
                mFirebaseAdapter = new FirebaseRecyclerAdapter<SwarmReport, FirebaseClaimViewHolder>
                        (SwarmReport.class, R.layout.my_claim_item, FirebaseClaimViewHolder.class,
                                swarmReportQuery) {

                    @Override
                    protected void populateViewHolder(FirebaseClaimViewHolder viewHolder,
                                                      SwarmReport model, int position) {
                        viewHolder.bindCurrentUserNameAndId(userName, userId);
                        viewHolder.bindSwarmReportForMyClaims(model);
                    }
                };
                setUpBlankAdapter();
                Log.d(TAG, "got past setUpBlankAdapter myClaimed");
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myClaimedSwarmsRecyclerView.getContext(),
                        new LinearLayoutManager(MyClaimedSwarmsActivity.this).getOrientation());
                dividerItemDecoration.setDrawable(getDrawable(R.drawable.recycler_view_divider));
                myClaimedSwarmsRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setUpBlankAdapter(){
        Log.d(TAG, "got here setUpBlankAdapater");
        myClaimedSwarmsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyClaimedSwarmsActivity.this);
        myClaimedSwarmsRecyclerView.setLayoutManager(linearLayoutManager);
        myClaimedSwarmsRecyclerView.setAdapter(mFirebaseAdapter);
        myClaimedSwarmsRecyclerView.setVisibility(View.VISIBLE);
        Log.d(TAG, "setUpBlankAdapter done with function");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        if (id == R.id.action_newReport) {
            Intent intent = new Intent(MyClaimedSwarmsActivity.this, NewSwarmReportActivity.class);
            if (userName != null && userId != null) {
                intent.putExtra("userName", userName);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unable to retrieve username and id", Toast.LENGTH_SHORT).show();
            }

        }

        if (id == R.id.action_myClaims) {
            Intent intent = new Intent(MyClaimedSwarmsActivity.this, MyClaimedSwarmsActivity.class);
            if (userName != null && userId != null) {
                intent.putExtra("userName", userName);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unable to retrieve username and id", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.action_myReportedSwarms) {
            Intent intent = new Intent(MyClaimedSwarmsActivity.this, MyReportedSwarmsActivity.class);
            if (userName != null && userId != null) {
                intent.putExtra("userName", userName);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unable to retrieve username and id", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        addToSharedPreferences("userName", "");
        addToSharedPreferences("userId", "");
        addToSharedPreferences("photoUrl", "");
        Intent intent = new Intent(fisherdynamic.swarmreporter1.activities.MyClaimedSwarmsActivity.this, LoginGateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void addToSharedPreferences(String key, String passedUserName) {
        mEditor.putString(key, passedUserName).apply();
    }

}

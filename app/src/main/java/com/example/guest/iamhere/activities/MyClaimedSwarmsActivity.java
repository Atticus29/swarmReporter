package com.example.guest.iamhere.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.models.SwarmReport;
import com.example.guest.iamhere.viewHolders.FirebaseClaimViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyClaimedSwarmsActivity extends AppCompatActivity {
    private Query swarmReportQuery;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String userName;
    private String userId;

    @Bind(R.id.myClaimedSwarmsRecyclerView) RecyclerView myClaimedSwarmsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_claimed_swarms);
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        userName = mSharedPreferences.getString("userName", null);
        userId = mSharedPreferences.getString("userId", null);

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
                Log.d("personal", "got past setUpBlankAdapter myClaimed");
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myClaimedSwarmsRecyclerView.getContext(),
                        new LinearLayoutManager(MyClaimedSwarmsActivity.this).getOrientation());
                dividerItemDecoration.setDrawable(getDrawable(R.drawable.recycler_view_divider));
                myClaimedSwarmsRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setUpBlankAdapter(){
        Log.d("personal", "got here setUpBlankAdapater");
        myClaimedSwarmsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyClaimedSwarmsActivity.this);
        myClaimedSwarmsRecyclerView.setLayoutManager(linearLayoutManager);
        myClaimedSwarmsRecyclerView.setAdapter(mFirebaseAdapter);
        myClaimedSwarmsRecyclerView.setVisibility(View.VISIBLE);
        Log.d("personal", "setUpBlankAdapter done with function");
    }

}

package com.example.guest.iamhere.activities;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyReportedSwarmsActivity extends AppCompatActivity {
    private Query swarmReportQuery;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    @Bind(R.id.myReportedSwarmsRecyclerView) RecyclerView myReportedSwarmsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reported_swarms);
        ButterKnife.bind(this);

        String Uid = getIntent().getStringExtra("userId");

        ArrayList<String> children = new ArrayList<>();
        children.add("users");
        children.add(Uid);
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
                viewHolder.bindSwarmReportForMyClaims(model);
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

}

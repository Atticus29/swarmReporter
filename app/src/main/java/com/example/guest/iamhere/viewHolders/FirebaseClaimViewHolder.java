package com.example.guest.iamhere.viewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class FirebaseClaimViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    View mView;
    Context mContext;

    public FirebaseClaimViewHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mContext = itemView.getContext();
    }


}

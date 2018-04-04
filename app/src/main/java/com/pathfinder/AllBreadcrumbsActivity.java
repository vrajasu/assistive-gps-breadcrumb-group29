package com.pathfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.pathfinder.Adapters.ListBreadCrumbAdapter;
import com.pathfinder.Adapters.ListRouteAdapter;
import com.pathfinder.Models.BreadCrumb;
import com.pathfinder.Models.Route;

import java.util.ArrayList;
import java.util.List;

//contains the Recycler view that lists all the breadcrumbs for a particular route

public class AllBreadcrumbsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    TextView tv_no_info;
    List<BreadCrumb> allBreadCrumbs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_all_routes);
        tv_no_info = (TextView) findViewById(R.id.tv_no_routes);


        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // initializing the adapter and then passing the Breadcrumb list
        mAdapter = new ListBreadCrumbAdapter(AllBreadcrumbsActivity.this, (List<BreadCrumb>) getIntent().getSerializableExtra("ALL_BREADCRUMBS"));

        mRecyclerView.setVisibility(View.VISIBLE);
        tv_no_info.setVisibility(View.GONE);

        mRecyclerView.setAdapter(mAdapter);

    }
}

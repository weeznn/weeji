package com.weeznn.weeji.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weeznn.weeji.MyApplication;
import com.weeznn.weeji.R;
import com.weeznn.weeji.adpater.DiaryAdapter;
import com.weeznn.weeji.adpater.MettingAdapter;
import com.weeznn.weeji.util.db.DiaryDao;
import com.weeznn.weeji.util.db.MeetingDao;
import com.weeznn.weeji.util.db.entry.Diary;
import com.weeznn.weeji.util.db.entry.Meeting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weeznn on 2018/4/2.
 */

public class DiaryFragment extends Fragment {
    private static final String TAG= DiaryFragment.class.getSimpleName();

    //view
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private android.support.v7.app.ActionBar actionBar;
    private FloatingActionButton fab;

    //逻辑
    private List<Diary> data=new ArrayList<>();
    private DiaryAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter=new DiaryAdapter(getContext(),data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_metting, container, false);
        recyclerView=view.findViewById(R.id.recyclerView);
        refreshLayout=view.findViewById(R.id.freshLayout);
        toolbar=view.findViewById(R.id.toolbar);
        toolbarTitle=view.findViewById(R.id.text);
        fab=view.findViewById(R.id.fab);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //freshlayout
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MyApplication.getInstant().runInTx(new Runnable() {
                    @Override
                    public void run() {
                        DiaryDao dao=MyApplication.getInstant().getDiaryDao();
                        data=dao.queryBuilder()
                                .limit(10)
                                .list();
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });


        //recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //toolbar
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
       toolbarTitle.setText(R.string.nav_skill_diary);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWright));

        //fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/4/4 添加
            }
        });
    }

}

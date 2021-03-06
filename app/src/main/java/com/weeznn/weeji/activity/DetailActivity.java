package com.weeznn.weeji.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.weeznn.mylibrary.utils.Constant;
import com.weeznn.weeji.MyApplication;
import com.weeznn.weeji.R;
import com.weeznn.weeji.adpater.DetailAdapter;
import com.weeznn.weeji.fragment.DairyDetailFragment;
import com.weeznn.weeji.fragment.MeetingDetailFragment;
import com.weeznn.weeji.fragment.NoteDetailFragment;
import com.weeznn.weeji.fragment.PeopleDetailFragment;
import com.weeznn.weeji.interfaces.ItemClickListener;
import com.weeznn.weeji.interfaces.UpdataFragmentDetailListener;
import com.weeznn.weeji.util.db.DiaryDao;
import com.weeznn.weeji.util.db.MeetingDao;
import com.weeznn.weeji.util.db.NoteDao;
import com.weeznn.weeji.util.db.PeopleDao;
import com.weeznn.weeji.util.db.entry.Diary;
import com.weeznn.weeji.util.db.entry.Meeting;
import com.weeznn.weeji.util.db.entry.Note;
import com.weeznn.weeji.util.db.entry.People;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements
        ItemClickListener,
        Constant,
        MeetingDetailFragment.OnFragmentInteractionListener {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private long code;
    private int type;

    private Context context;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private List<Object> list = new ArrayList<>();
    private DetailAdapter adapter;
    private static long DEFAULT = Long.MIN_VALUE;

    private UpdataFragmentDetailListener updataFragmentDetailListener;

    //view
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_detail);
        context = this;

        Intent intent = getIntent();
        type = intent.getIntExtra(getResources().getString(R.string.LEFT_TYPE), CODE_MRT);
        code = intent.getLongExtra(getString(R.string.LEFT_CODE), DEFAULT);

        Log.i(TAG, "onCreate type:" + type + "   code:" + code);

        fragmentManager = getSupportFragmentManager();
        adapter = new DetailAdapter(context, type, list);
        adapter.setItemClickListener(this);

        initLeftData();
        initView();

    }


    /**
     * 侧边栏的数据
     */
    private void initLeftData() {
        MyApplication.getInstant().runInTx(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case CODE_DAI:
                        DiaryDao diaryDao = MyApplication.getInstant().getDiaryDao();
                        List<Diary> resultD = diaryDao.queryBuilder()
                                .list();
                        list.addAll(resultD);
                        adapter.notifyDataSetChanged();
                    case CODE_NOT:
                        NoteDao noteDao = MyApplication.getInstant().getNoteDao();
                        List<Note> resultN = noteDao.queryBuilder()
                                .list();
                        list.addAll(resultN);
                        adapter.notifyDataSetChanged();
                    case CODE_MRT:
                        MeetingDao meetingDao = MyApplication.getInstant().getMeetingDao();
                        List<Meeting> result = meetingDao.queryBuilder()
                                .list();
                        list.addAll(result);
                        adapter.notifyDataSetChanged();
                        break;
                    case CODE_PEO:
                        PeopleDao peopleDao=MyApplication.getInstant().getPeopleDao();
                        List<People> resultP=peopleDao.queryBuilder()
                                .list();
                        list.addAll(resultP);
                        adapter.notifyDataSetChanged();

                }
                Log.i(TAG, "updata size " + list.size());
            }
        });
    }

    private void initView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        recyclerView = findViewById(R.id.recyclerView);
        imageView = findViewById(R.id.image);

        switch (type) {
            case CODE_DAI:
                fragment = DairyDetailFragment.newInstance(code);
                imageView.setBackground(getResources().getDrawable(R.drawable.ic_diary));
            case CODE_NOT:
                fragment = NoteDetailFragment.newInstance(code);
                imageView.setBackground(getResources().getDrawable(R.drawable.ic_note));
            case CODE_MRT:
                fragment = MeetingDetailFragment.newInstance(code);
                imageView.setBackground(getResources().getDrawable(R.drawable.ic_meeting));
                break;
            case CODE_PEO:
               fragment=new PeopleDetailFragment().newInstance(code);
               imageView.setBackground(getResources().getDrawable(R.drawable.ic_user_black));
                break;
        }
        fragmentManager.beginTransaction().add(R.id.frameLayout, fragment).commit();

        //侧边栏
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void onItemClick(int position) {
        switch (type) {
            case CODE_DAI:
                code = ((Diary) list.get(position)).get_DAIID();
                break;
            case CODE_NOT:
                code = ((Note) list.get(position)).get_noteID();
            case CODE_MRT:
                code = (((Meeting) list.get(position)).get_metID());
                break;
        }
        updataFragmentDetailListener.updata(code);
        Log.i(TAG, "item position: " + position + "  code:" + code);
    }

    @Override
    public void onItemLongClick(int position) {

    }

    /**
     * 所有的Detail Fragment 通过该方法更新fragment内容
     *
     * @param listener
     */
    public void setFragmentDetailListener(UpdataFragmentDetailListener listener) {
        this.updataFragmentDetailListener = listener;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode,resultCode,data);
    }
}

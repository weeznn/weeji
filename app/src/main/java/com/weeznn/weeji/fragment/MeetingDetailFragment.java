package com.weeznn.weeji.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weeznn.mylibrary.utils.Constant;
import com.weeznn.mylibrary.utils.FileUtil;
import com.weeznn.weeji.MyApplication;
import com.weeznn.weeji.R;
import com.weeznn.weeji.activity.DetailActivity;
import com.weeznn.weeji.activity.MarkDownActivity;
import com.weeznn.weeji.interfaces.UpdataFragmentDetailListener;
import com.weeznn.weeji.service.AudioIntentService;
import com.weeznn.weeji.service.AudioService;
import com.weeznn.weeji.util.db.MeetingDao;
import com.weeznn.weeji.util.db.entry.Meeting;

import java.lang.reflect.Method;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MeetingDetailFragment extends Fragment implements Constant{
    private static final String TAG = "MeetingDetailFragment";

    private static final String ARG_PARAM1 = "CODE";
    private static final int MSG_CODE_METTING_DB = 1;
    private static final int MSG_CODE_METTING_UPDATA = 2;
    private static final int MSG_CODE_METTING_FILE = 3;

    private static final int PLAYER_START = 1;
    private static final int PLAYER_PAUSE = 2;


    //view
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;

    private TextView titleView;
    private TextView timeView;
    private TextView addrView;
    private TextView keyWord1View;
    private TextView keyWord2View;
    private TextView keyWord3View;
    private EditText textView;
    private Toolbar toolbar;


    //逻辑
    private long code;
    private Meeting meeting;
    private String txt;//正文
    private int playerState = PLAYER_PAUSE;
    private boolean isEdit=false;
    private String title="";

    private OnFragmentInteractionListener mListener;

    private LocalBroadcastManager localBroadcastManager;
    private MyAudioProgressResiver resiver;
    private AudioIntentService audioIntentService;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            onHandleMessage(msg);
            return true;
        }
    });

    public static MeetingDetailFragment newInstance(long code) {
        MeetingDetailFragment fragment = new MeetingDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, code);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            code = getArguments().getLong(ARG_PARAM1);
            initInfo();
        }
        //广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioIntentService.ACTION_AUDIO_PRO);
        resiver = new MyAudioProgressResiver();
        localBroadcastManager.registerReceiver(resiver, filter);

        ((DetailActivity) getActivity()).setFragmentDetailListener(new UpdataFragmentDetailListener() {
            @Override
            public void updata(long code) {
                Message message=new Message();
                message.what=MSG_CODE_METTING_UPDATA;
                message.obj=code;
               handler.sendMessage(message);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_detail, container, false);
        appBarLayout = view.findViewById(R.id.appbar);
        progressBar = view.findViewById(R.id.progressbar);
        toolbar=view.findViewById(R.id.toolbar);

        titleView = view.findViewById(R.id.title);
        timeView = view.findViewById(R.id.time);
        addrView = view.findViewById(R.id.address);
        keyWord1View = view.findViewById(R.id.key_word_1);
        keyWord2View = view.findViewById(R.id.key_word_2);
        keyWord3View = view.findViewById(R.id.key_word_3);
        textView = view.findViewById(R.id.text);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWright));

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleMenuItemClick(item);
                return true;
            }
        });
    }

    private AudioService.MyBinder myBinder;
    private void handleMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_player:
                if (playerState == PLAYER_PAUSE &&myBinder==null) {
                    Log.i(TAG, "开始播放");
                    playerState = PLAYER_START;

                    Intent intent=new Intent(getContext(), AudioService.class);
                    getActivity().bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
                    item.setIcon(R.drawable.ic_pause_black_24dp);
                } else if (playerState == PLAYER_PAUSE&&myBinder!=null){
                    playerState = PLAYER_START;
                    Log.i(TAG, "继续播放");
                    myBinder.start();
                    item.setIcon(R.drawable.ic_pause_black_24dp);
                }else if (playerState == PLAYER_START&&myBinder!=null){
                    playerState = PLAYER_PAUSE;
                    Log.i(TAG, "暂停播放");
                    myBinder.pause();
                    item.setIcon(R.drawable.ic_play_arrow_black_24dp);
                }
                break;
            case R.id.edit:
                Intent intent=new Intent(getContext(), MarkDownActivity.class);
                intent.putExtra(MarkDownActivity.INTENT_FILE_NAME,title);
                intent.putExtra(MarkDownActivity.INTENT_FILE_TYPE,FileUtil.FILE_TYPE_MEETING);
                intent.putExtra(MarkDownActivity.INTENT_FILE_DATA,txt);
                startActivityForResult(intent,REQUEST_CODE_MET_EDIT);
                break;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG,"onCreateOptionsMenu");
        inflater.inflate(R.menu.meeting_detail_menu,menu);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        localBroadcastManager.unregisterReceiver(resiver);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void onHandleMessage(Message msg) {
        switch (msg.what) {
            case MSG_CODE_METTING_DB:
                //读文件
                readText(meeting.getTitle());
                toolbar.setTitle(meeting.getTitle());
                timeView.setText(meeting.getTime());
                addrView.setText(meeting.getAddress());
                keyWord1View.setText(meeting.getKeyword1());
                keyWord2View.setText(meeting.getKeyword2());
                keyWord3View.setText(meeting.getKeyword3());
                break;
            case MSG_CODE_METTING_FILE:
                textView.setText(txt);
                break;
            case MSG_CODE_METTING_UPDATA:
                code= (long) msg.obj;
                initInfo();
        }
    }

    /**
     * 从数据库中读取该meeting 的信息
     */
    private void initInfo() {
        MyApplication.getInstant().runInTx(new Runnable() {
            @Override
            public void run() {
                List<Meeting> resule = MyApplication.getInstant().getMeetingDao().queryBuilder()
                        .where(MeetingDao.Properties._metID.eq(code))
                        .list();
                if (resule != null && resule.size() > 0) {
                    meeting = resule.get(0);
                    handler.sendEmptyMessage(MSG_CODE_METTING_DB);
                }
            }
        });
    }

    /**
     * 从本地读取文本文件
     *
     * @param fileName
     */
    private void readText(final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                txt = FileUtil.ReadText(FileUtil.FILE_TYPE_MEETING, fileName);
                handler.sendEmptyMessage(MSG_CODE_METTING_FILE);
            }
        }).start();

    }

    /**
     * 滑动折叠监听器
     */
//    private class MyOffsetChangeListener implements AppBarLayout.OnOffsetChangedListener {
//        private final int EXPAND = 1;
//        private final int CLOOSE = 2;
//        private final int IDEO = 3;
//        private int currontState = IDEO;
//
//        @Override
//        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//            if (verticalOffset == 0) {
//                if (currontState != EXPAND) {
//                    currontState = EXPAND;
//                    //展开状态  toolbar 不显示 fab显示
//                }
//            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
//                if (currontState != CLOOSE) {
//                    currontState = CLOOSE;
//                }
//                //闭合状态  toolbar显示  其他的不显示
//            } else {
//                currontState = IDEO;
//            }
//        }
//    }

    /**
     * 广播接收器  接收音频播放的进度条消息
     */
    private class MyAudioProgressResiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //填写进度条
            Log.i(TAG,intent.getIntExtra(AudioIntentService.AUDIO_PROGRESS, 0)+"");
            progressBar.setProgress(intent.getIntExtra(AudioIntentService.AUDIO_PROGRESS, 0));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            Snackbar.make(textView,"MarkDown文件已保存到本地！",Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder=((AudioService.MyBinder)service);
            myBinder.prepare(getContext(),meeting.getTitle(),FileUtil.FILE_TYPE_MEETING);
            myBinder.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Snackbar snackbar = Snackbar.make(textView, "音频无法播放", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            snackbar.show();
        }
    };
}

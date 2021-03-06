package com.weeznn.weeji.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.request.RequestOptions;
import com.weeznn.mylibrary.utils.FileUtil;
import com.weeznn.weeji.MyApplication;
import com.weeznn.weeji.R;
import com.weeznn.weeji.util.db.PeopleDao;
import com.weeznn.weeji.util.db.entry.People;

public class PeopleDetailFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = PeopleDetailFragment.class.getSimpleName();
    public static final String FLAG_BACK = "PeopleDetail";
    public static final String FLAG_ARG_PEOPLE_INFO = "info";
    public static final String FLAG_ARG_PEOPLE_CODE = "code";

    public static final int FAB_EDIT = 1;
    public static final int FAB_DOWN = 2;
    public static final int INTENT_IMAGE_PICK=3;

    private String name;
    private String job;
    private String number;
    private String email;
    private String photo;
    private String company;
    private long code;
    private People people;

    private int fabState = FAB_EDIT;
    private boolean isSelf = true;//是不是本人的信息


    //view
    private ImageView photoView;
    private TextInputEditText nameView;
    private KeyListener namelistener;
    private TextInputEditText jobView;
    private KeyListener joblistener;
    private TextInputEditText numberView;
    private KeyListener numberlistener;
    private TextInputEditText emailView;
    private KeyListener emaillistener;
    private TextInputEditText companyView;
    private KeyListener companylistener;
    private FloatingActionButton fab;

    private ImageView imageCall;
    private ImageView imageEmail;

    public static PeopleDetailFragment newInstance(long code){
        PeopleDetailFragment fragment=new PeopleDetailFragment();
        Bundle bundle=new Bundle();
        bundle.putLong(FLAG_ARG_PEOPLE_CODE,code);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        if (getArguments() == null) {
            isSelf = true;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.SharedPreferences_name), 0);
            name = sharedPreferences.getString(getString(R.string.pref_self_name), getString(R.string.pref_self_def_name));
            photo = sharedPreferences.getString(getString(R.string.pref_self_photo), getString(R.string.pref_self_def_photo));
            job = sharedPreferences.getString(getString(R.string.pref_self_job), getString(R.string.pref_self_def_job));
            company = sharedPreferences.getString(getString(R.string.pref_self_company), getString(R.string.pref_self_def_company));
            number = sharedPreferences.getString(getString(R.string.pref_self_number), getString(R.string.pref_self_def_number));
            email = sharedPreferences.getString(getString(R.string.pref_self_email), getString(R.string.pref_self_def_email));
        } else {
            isSelf = false;
            Bundle bundle = getArguments().getBundle(FLAG_ARG_PEOPLE_INFO);
            code=bundle.getLong(FLAG_ARG_PEOPLE_CODE);
            MyApplication.getInstant().runInTx(new Runnable() {
                @Override
                public void run() {
                    people=MyApplication.getInstant().getPeopleDao().queryBuilder()
                            .where(PeopleDao.Properties.Phone.eq(code))
                            .list().get(0);

                    name = people.getName();
                    photo = people.getPhoto();
                    job = people.getJob();
                    company =people.getCompany();
                    number = people.getPhone()+"";
                    email = people.getEmail();
                }
            });
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_detail, container, false);
        photoView = view.findViewById(R.id.photo);
        photoView.setClickable(false);

        nameView = view.findViewById(R.id.name);
        namelistener = nameView.getKeyListener();
        nameView.setKeyListener(null);

        jobView = view.findViewById(R.id.job);
        joblistener = jobView.getKeyListener();
        jobView.setKeyListener(null);

        companyView = view.findViewById(R.id.company);
        companylistener = companyView.getKeyListener();
        companyView.setKeyListener(null);

        emailView = view.findViewById(R.id.email);
        emaillistener = emailView.getKeyListener();
        emailView.setKeyListener(null);
        imageEmail = view.findViewById(R.id.imageViewEmail);

        numberView = view.findViewById(R.id.number);
        numberlistener = numberView.getKeyListener();
        numberView.setKeyListener(null);
        imageCall = view.findViewById(R.id.imageViewCall);

        fab = view.findViewById(R.id.fab);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Glide.with(photoView)
                .load(photo)
                .into(photoView);

        nameView.setText(name);
        numberView.setText(number);
        emailView.setText(email);
        companyView.setText(company);
        jobView.setText(job);

        fab.setOnClickListener(this);
        imageCall.setOnClickListener(this);
        imageEmail.setOnClickListener(this);

    }

    private void down() {
        // 完成编辑，校验，传送
        name = nameView.getText().toString();
        number = numberView.getText().toString();
        email = emailView.getText().toString();
        job = jobView.getText().toString();
        company = companyView.getText().toString();
        // TODO: 2018/4/4 photo 的地址待获取
        if (isSelf) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.SharedPreferences_name), 0);
            String json = "{" +
                    "\"people\":" +
                    "[{" +
                    "\"name\":" + "\"" + name + "\"," +
                    "\"photo\":" + "\"" + photo + "\"," +
                    "\"job\":" + "\"" + job + "\"," +
                    "\"company\":" + "\"" + company + "\"" +
                    "}]" +
                    "}";
            sharedPreferences.edit().putString(getString(R.string.pref_self_name), name)
                    .putString(getString(R.string.pref_self_number), number)
                    .putString(getString(R.string.pref_self_email), email)
                    .putString(getString(R.string.pref_self_company), company)
                    .putString(getString(R.string.pref_self_job), job)
                    .putString(getString(R.string.pref_sim_self_json), json)
                    .apply();
        } else {
            People people = new People(Long.getLong(number), name, email, photo, company, job);
            PeopleDao dao = MyApplication.getInstant().getPeopleDao();
            if (dao.queryBuilder().where(PeopleDao.Properties.Phone.eq(number)) == null) {
                dao.insert(people);
            } else {
                dao.update(people);
            }
        }
        fabState = FAB_EDIT;

        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(this);
    }

    private void edit() {
        fabState = FAB_DOWN;

        nameView.setKeyListener(namelistener);
        numberView.setKeyListener(numberlistener);
        emailView.setKeyListener(emaillistener);
        companyView.setKeyListener(companylistener);
        jobView.setKeyListener(joblistener);
        photoView.setClickable(true);
        photoView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (FAB_EDIT == fabState) {
                    //编辑
                    fab.setImageResource(R.drawable.ic_yes);
                    edit();
                } else {
                    //编辑完毕
                    down();
                    fab.setImageResource(R.drawable.ic_edit);
                }
                break;
            case R.id.imageViewCall:
                if (!isSelf) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL_BUTTON);
                    intent.setData(Uri.parse("tel:" + number));
                    startActivity(intent);
                }
                break;
            case R.id.imageViewEmail:
                if (!isSelf) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, email);
                    startActivity(intent);
                }
                break;
            case R.id.photo:
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,INTENT_IMAGE_PICK);
                break;
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==INTENT_IMAGE_PICK&& data!=null){
            Uri image=data.getData();//获取图片URI
            String[] imagepath={MediaStore.Images.Media.DATA};
            //获取URI所对应的图片
            Cursor cursor=getActivity().getContentResolver().query(image,imagepath,null,null,null);
            cursor.moveToFirst();
            int index=cursor.getColumnIndex(imagepath[0]);
            String path=cursor.getString(index);//获取图片路径
            cursor.close();//guanbi
            FileUtil.copyImage(path,name,FileUtil.FILE_TYPE_HEAD);//将图片压缩保存
            photo=FileUtil.HEAD_PATH+name+".jpg";//重新命名图片路径

            RequestOptions options=new RequestOptions();
            Glide.with(photoView).load(path)
                    .into(photoView);
        }
    }
}

package com.biaoke.bklive.user.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.base.BaseActivity;
import com.biaoke.bklive.common.HeaderImageUtils;
import com.biaoke.bklive.message.Api;
import com.biaoke.bklive.message.AppConsts;
import com.biaoke.bklive.user.eventbus.Event_mywork;
import com.biaoke.bklive.user.eventbus.Event_nickname;
import com.biaoke.bklive.user.eventbus.Event_signture;
import com.biaoke.bklive.utils.GlideUtis;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;
import okhttp3.MediaType;

import static android.support.v4.content.FileProvider.getUriForFile;


public class EditUserActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.edit_head)
    RelativeLayout editHead;
    @BindView(R.id.edit_nickname)
    RelativeLayout editNickname;
    @BindView(R.id.edit_bkid)
    TextView editBkid;
    @BindView(R.id.edit_sex)
    RelativeLayout editSex;
    @BindView(R.id.edit_signture)
    RelativeLayout editSignture;
    @BindView(R.id.edit_impression)
    RelativeLayout editImpression;
    @BindView(R.id.edit_age)
    RelativeLayout editAge;
    @BindView(R.id.edit_emotion)
    RelativeLayout editEmotion;
    @BindView(R.id.edit_hometown)
    RelativeLayout editHometown;
    @BindView(R.id.edit_work)
    RelativeLayout editWork;
    @BindView(R.id.selectemotion)
    TextView selectemotion;
    @BindView(R.id.my_signture)
    TextView mySignture;
    @BindView(R.id.iv_sex)
    ImageView ivSex;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_nickName)
    TextView tvNickName;
    @BindView(R.id.btn_home)
    TextView btnHome;
    @BindView(R.id.btn_work)
    TextView btnWork;
    @BindView(R.id.edit_user_header)
    ImageView editUserHeader;

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private String userId;
    private String mNickName;
    private String mLevel;
    private String mExperience;
    private String mCharm;
    private String mDiamond;
    private String mLiveNum;
    private String mVideoNum;
    private String mHeadimageUrl;
    private String mSex;
    private String mAge;
    private String mEmotion;
    private String mHometown;
    private String mWork;
    private String mFollow;
    private String mFans;
    private String mSignture;
    private SharedPreferences sharedPreferences_user;
    private String accessKey;
    private UploadManager uploadManager;
    public String uptoken = null;
    public String sendFileName;
    private GlideUtis glideUtis_header;
    private File file;
    private String currentTime = System.currentTimeMillis() + "";//用于更换头像地址

    public EditUserActivity() {
        Configuration config = new Configuration.Builder()
                // recorder 分片上传时，已上传片记录器
                // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
//            .recorder(recorder, keyGen)
                .connectTimeout(10) // 链接超时。默认10秒
                .zone(Zone.httpsAutoZone)
                .build();
        // 实例化一个上传的实例
        uploadManager = new UploadManager(config);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//注册
        glideUtis_header = new GlideUtis(this);
        //获取本地保存的用户信息
        sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);//首先获取用户ID
        userId = sharedPreferences_user.getString("userId", "");
        //获取秘钥，以后基本都要用
        accessKey = sharedPreferences_user.getString("AccessKey", "");
        mSex = sharedPreferences_user.getString("mSex", "");
//        getUserInfo();
        JSONObject jsonObject_user = new JSONObject();
        try {
            jsonObject_user.put("Protocol", "UserInfo");
            jsonObject_user.put("Cmd", "GetAll");
            jsonObject_user.put("UserId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("主页面获取用户ID", jsonObject_user.toString());
        UserInfoHttp(Api.ENCRYPT64, jsonObject_user.toString());
    }

    private Handler myHandler = new Handler() {

        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    SharedPreferences.Editor editor_userinfo = sharedPreferences_user.edit();
                    editor_userinfo.putString("mNickName", mNickName);
                    editor_userinfo.putString("mLevel", mLevel);
                    editor_userinfo.putString("mCharm", mCharm);
                    editor_userinfo.putString("mHeadimageUrl", mHeadimageUrl);
                    editor_userinfo.putString("mExperience", mExperience);
                    editor_userinfo.putString("mDiamond", mDiamond);
                    editor_userinfo.putString("mLiveNum", mLiveNum);
                    editor_userinfo.putString("mVideoNum", mVideoNum);
                    editor_userinfo.putString("mSex", mSex);
                    editor_userinfo.putString("mAge", mAge);
                    editor_userinfo.putString("mEmotion", mEmotion);
                    editor_userinfo.putString("mHometown", mHometown);
                    editor_userinfo.putString("mWork", mWork);
                    editor_userinfo.putString("mFollow", mFollow);
                    editor_userinfo.putString("mFans", mFans);
                    editor_userinfo.putString("mSignture", mSignture);
                    editor_userinfo.commit();
                    setUserInfo();
                    break;
                case 1:
                    int user_age = yearnow - (int) message.obj;
                    tvAge.setText(user_age + "");
                    break;
                case 2:
                    String birthday = (String) message.obj;
                    sendBirthday(birthday);
                    break;
            }
        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();
        JSONObject jsonObject_user = new JSONObject();
        try {
            jsonObject_user.put("Protocol", "UserInfo");
            jsonObject_user.put("Cmd", "GetAll");
            jsonObject_user.put("UserId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("onrestart获取用户ID", jsonObject_user.toString());
        UserInfoHttp(Api.ENCRYPT64, jsonObject_user.toString());
    }

    //获取用户本地信息
//    private void getUserInfo() {
//        sharedPreferences_user = getSharedPreferences("isLogin", Context.MODE_PRIVATE);//首先获取用户ID
//        userId = sharedPreferences_user.getString("userId", "");
//        mNickName = sharedPreferences_user.getString("mNickName", "");
//        mHeadimageUrl = sharedPreferences_user.getString("mHeadimageUrl", "");
//        mLevel = sharedPreferences_user.getString("mLevel", "");
//        mCharm = sharedPreferences_user.getString("mCharm", "");
//        mExperience = sharedPreferences_user.getString("mExperience", "");
//        mDiamond = sharedPreferences_user.getString("mDiamond", "");
//        mLiveNum = sharedPreferences_user.getString("mLiveNum", "");
//        mVideoNum = sharedPreferences_user.getString("mVideoNum", "");
//        mSex = sharedPreferences_user.getString("mSex", "");
//        mAge = sharedPreferences_user.getString("mAge", "");
//        mEmotion = sharedPreferences_user.getString("mEmotion", "");
//        mHometown = sharedPreferences_user.getString("mHometown", "");
//        mWork = sharedPreferences_user.getString("mWork", "");
//        mFollow = sharedPreferences_user.getString("mFollow", "");
//        mFans = sharedPreferences_user.getString("mFans", "");
//        mSignture = sharedPreferences_user.getString("mSignture", "");
//        //获取秘钥，以后基本都要用
//        accessKey = sharedPreferences_user.getString("AccessKey", "");
//    }

    //修改后从服务器获取用户信息
    public void UserInfoHttp(String url, String path) {
        OkHttpUtils
                .postString()
                .url(url)
                .content(path)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.USERINFO_USER)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.d("失败的返回", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.d("失败的返回", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        Log.d("用户编辑页面", response);
                                                        try {
                                                            JSONObject jsonobject = new JSONObject(response);
                                                            mNickName = jsonobject.getString("NickName");
                                                            mLevel = jsonobject.getString("Level");
                                                            mExperience = jsonobject.getString("经验");
                                                            mCharm = jsonobject.getString("魅力");
                                                            mDiamond = jsonobject.getString("钻石");
                                                            mLiveNum = jsonobject.getString("直播");
                                                            mVideoNum = jsonobject.getString("点播");
                                                            mHeadimageUrl = jsonobject.getString("IconUrl") + "?" + currentTime;
                                                            mSex = jsonobject.getString("性别");
                                                            mAge = jsonobject.getString("生日");
                                                            mEmotion = jsonobject.getString("情感");
                                                            mHometown = jsonobject.getString("家乡");
                                                            mWork = jsonobject.getString("职业");
                                                            mFollow = jsonobject.getString("关注" + "");
                                                            mFans = jsonobject.getString("粉丝" + "");
                                                            mSignture = jsonobject.getString("签名" + "");
                                                            Message message_userinfo = new Message();
                                                            message_userinfo.what = 0;
                                                            myHandler.sendMessage(message_userinfo);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    //设置用户信息
    private void setUserInfo() {
        glideUtis_header.glideCircle(mHeadimageUrl, editUserHeader, true);
        tvNickName.setText(mNickName);
        editBkid.setText(userId);
        tvSex.setText(mSex + "");
        if (mSex.equals("男")) {
            ivSex.setImageResource(R.drawable.man);
        } else {
            ivSex.setImageResource(R.drawable.female);
        }
        mySignture.setText(mSignture);
        selectemotion.setText(mEmotion);
        btnHome.setText(mHometown);
        btnWork.setText(mWork);
        tvAge.setText(mAge);
    }
    @Override
    protected String getPowerBarColors() {
        return AppConsts.POWER_BAR_BACKGROUND;
    }

    Calendar c = Calendar.getInstance();
    int yearnow = c.get(Calendar.YEAR);


    @OnClick({R.id.back, R.id.edit_head, R.id.edit_nickname, R.id.edit_sex, R.id.edit_signture, R.id.edit_impression, R.id.edit_age, R.id.edit_emotion, R.id.edit_hometown, R.id.edit_work})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit_head:
                headPopupWindow(view);
                //获取uptoken
                getUptoken();
                break;
            case R.id.edit_nickname:
                Intent intent_nickname = new Intent(this, NicknameActivity.class);
                startActivity(intent_nickname);
                break;
            case R.id.edit_sex:
                sexPop();
                sexPopw.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.edit_signture:
                Intent intent_signture = new Intent(this, SigntureActivity.class);
                startActivity(intent_signture);
                break;
            case R.id.edit_impression:
                break;
            case R.id.edit_age:

                new DatePickerDialog(EditUserActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        tvAge.setText(String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth));
                        Log.d("出生日期", String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth));
                        String date = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                        Message msg = Message.obtain();
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = year;
                        msg.what = 2;
                        msg.obj = date;   //从这里把你想传递的数据放进去就行了
                        myHandler.sendMessage(msg);
                        myHandler.sendMessage(message);
                    }
                }, 2000, 1, 2).show();

                break;
            case R.id.edit_emotion:
                emotionPop();
                emotionPopw.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.edit_hometown:
                selectHome();
                break;
            case R.id.edit_work:
                Intent intent_work = new Intent(this, MyworkActivity.class);
                startActivity(intent_work);
                break;
        }
    }

    //地区选择器
    private void selectHome() {
        CityPicker cityPicker = new CityPicker.Builder(EditUserActivity.this).textSize(20)
                .titleTextColor("#000000")
                .backgroundPop(0xa0000000)
                .province("江苏省")
                .city("徐州市")
                .district("云龙区")
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .build();

        cityPicker.show();
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                btnHome.setText(citySelected[0] + citySelected[1]);
                //家乡上传服务器
                sendHometown(citySelected[0] + citySelected[1]);
            }
//            tvResult.setText("选择结果：\n省：" + citySelected[0] + "\n市：" + citySelected[1] + "\n区："
//                    + citySelected[2] + "\n邮编：" + citySelected[3]);

            @Override
            public void onCancel() {
                Toast.makeText(EditUserActivity.this, "已取消", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendHometown(String home) {
        JSONObject jsonObject_sendNickname = new JSONObject();
        try {
            jsonObject_sendNickname.put("Protocol", "UserInfo");
            jsonObject_sendNickname.put("Cmd", "Set");
            jsonObject_sendNickname.put("UserId", userId);
            jsonObject_sendNickname.put("AccessKey", accessKey);
            jsonObject_sendNickname.put("Name", "家乡");
            jsonObject_sendNickname.put("Data", home);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("修改昵称", jsonObject_sendNickname.toString());
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_sendNickname.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.USERINFO_USER)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.e("失败的返回", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.e("失败的返回", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_nickname = new JSONObject(response);
                                                            String msg = object_nickname.getString("Msg");
//                                                            Toast.makeText(NicknameActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void sendBirthday(String home) {
        JSONObject jsonObject_sendNickname = new JSONObject();
        try {
            jsonObject_sendNickname.put("Protocol", "UserInfo");
            jsonObject_sendNickname.put("Cmd", "Set");
            jsonObject_sendNickname.put("UserId", userId);
            jsonObject_sendNickname.put("AccessKey", accessKey);
            jsonObject_sendNickname.put("Name", "年龄");
            jsonObject_sendNickname.put("Data", home);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("修改昵称", jsonObject_sendNickname.toString());
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_sendNickname.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.USERINFO_USER)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.e("失败的返回", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.e("失败的返回", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_nickname = new JSONObject(response);
                                                            String msg = object_nickname.getString("Msg");
//                                                            Toast.makeText(NicknameActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void headPopupWindow(View v) {
        View headView = LayoutInflater.from(this).inflate(R.layout.item_pop_selectpic, null);
        LinearLayout button_sel = (LinearLayout) headView.findViewById(R.id.photo_sel);
        button_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headPopw.dismiss();
                selectPhoto();
            }
        });
        LinearLayout button_take = (LinearLayout) headView.findViewById(R.id.photo_take);
        button_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headPopw.dismiss();
                takePhoto();
            }
        });
        headPopw = new PopupWindow(headView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        headPopw.setTouchable(true);

        headPopw.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return false;
            }
        });
        headPopw.setBackgroundDrawable(getResources().getDrawable(R.drawable.selectmenu_bg_downward));
        headPopw.showAsDropDown(v);
    }

    private void takePhoto() {


//        File cameraPhoto = new File(Environment.getExternalStorageDirectory(), "bkimage.jpg");
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempUri = getUriForFile(this, getPackageName() + ".fileprovider", new File(Environment.getExternalStorageDirectory(), "image.jpg"));
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(takePhotoIntent, TAKE_PICTURE);


//        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File sdfile=new File(Environment.getExternalStorageDirectory(), "bkimage.jpg");
////        tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
//        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
//        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
//        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    ;

    private void selectPhoto() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("返回码", "requestCode"+requestCode + "resultCode"+resultCode);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     * @param data
     */
    protected void setImageToView(Intent data) {
        file = new File("/mnt/sdcard/pic/01.jpg");
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
//            photo = HeaderImageUtils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            editUserHeader.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了
//        String imagePath = HeaderImageUtils.savePhoto(bitmap,
//                Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
        String imagePath = HeaderImageUtils.savePhoto(bitmap,
                Environment.getExternalStorageDirectory().getAbsolutePath(), sendFileName);
        Log.e("imagePath", imagePath + "");

        //(七牛)设定需要添加的自定义变量为Map<String, String>类型 并且放到UploadOptions第一个参数里面
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("x:phone", userId);//（qiniu）
//        String mykey = "x:phone";
        if (imagePath != null) {
            // 拿着imagePath上传了
            //
            uploadManager.put(imagePath, sendFileName, uptoken,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject jsonObject) {
                            Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + jsonObject);
                            if (info.isOK()) {
//                                Toast.makeText(EditUserActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                                Log.i("qiniu", "头像上传成功");
                            }
                        }
                    }, new UploadOptions(null, null, false,
                            new UpProgressHandler() {
                                @Override
                                public void progress(String s, double v) {

                                }
                            }, new UpCancellationSignal() {
                        @Override
                        public boolean isCancelled() {
                            return false;
                        }
                    }));
        }
    }

    //获取上传凭证uptoken
    private void getUptoken() {
        JSONObject jsonObject_uptoken = new JSONObject();
        try {
            jsonObject_uptoken.put("Protocol", "Upload");
            jsonObject_uptoken.put("Cmd", "icon");
            jsonObject_uptoken.put("Ext", "jpg");
            jsonObject_uptoken.put("UserId", userId);
            jsonObject_uptoken.put("AccessKey", accessKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_uptoken.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.UPLOAD)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.e("失败的返回", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.e("失败的返回", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_uptoken = new JSONObject(response);
                                                            uptoken = object_uptoken.getString("Token");
                                                            sendFileName = object_uptoken.getString("FileName");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void sexPop() {
        final View sex_View = LayoutInflater.from(this).inflate(R.layout.sex_select, null);
        sexPopw = new PopupWindow(sex_View, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ColorDrawable cd = new ColorDrawable(this.getResources().getColor(R.color.white));
        sexPopw.setBackgroundDrawable(cd);
        sexPopw.setOutsideTouchable(true);
        RelativeLayout rl_man = (RelativeLayout) sex_View.findViewById(R.id.rl_man);
        rl_man.setOnClickListener(sex);
        RelativeLayout rl_female = (RelativeLayout) sex_View.findViewById(R.id.rl_female);
        rl_female.setOnClickListener(sex);
        Button btn_cancel = (Button) sex_View.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(sex);
    }

    private View.OnClickListener sex = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_man:
                    ivSex.setImageResource(R.drawable.man);
                    tvSex.setText("男");
                    sendSex("男");
                    sexPopw.dismiss();
                    break;
                case R.id.rl_female:
                    ivSex.setImageResource(R.drawable.female);
                    tvSex.setText("女");
                    sendSex("女");
                    sexPopw.dismiss();
                    break;
                case R.id.btn_cancel:
                    sexPopw.dismiss();
                    break;
            }
        }
    };

    private void sendSex(String sex) {
        JSONObject jsonObject_sendSex = new JSONObject();
        try {
            jsonObject_sendSex.put("Protocol", "UserInfo");
            jsonObject_sendSex.put("Cmd", "Set");
            jsonObject_sendSex.put("UserId", userId);
            jsonObject_sendSex.put("AccessKey", accessKey);
            jsonObject_sendSex.put("Name", "性别");
            jsonObject_sendSex.put("Data", sex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_sendSex.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.USERINFO_USER)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.e("失败的返回", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.e("失败的返回", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_nickname = new JSONObject(response);
                                                            String msg = object_nickname.getString("Msg");
//                                                            Toast.makeText(NicknameActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
//事件的处理会在UI线程中执行。事件处理时间不能太长，这个不用说的，长了会ANR的，对应的函数名是onEventMainThread。
    public void onEvent_signture(Event_signture evt) {
        String signture = evt.getMsg();
//        Toast.makeText(this, evt.getMsg(), Toast.LENGTH_SHORT).show();
        mySignture.setText(signture);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent_nickname(Event_nickname evt_nickname) {
        String nickname = evt_nickname.getNickname();
        tvNickName.setText(nickname);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent_mywork(Event_mywork evt_mywork) {
        String mywork = evt_mywork.getMyWork();
        btnWork.setText(mywork);
    }


    private PopupWindow emotionPopw, sexPopw, headPopw;

    private void emotionPop() {
        final View emotion_View = LayoutInflater.from(this).inflate(R.layout.emotion, null);
        emotionPopw = new PopupWindow(emotion_View, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ColorDrawable cd = new ColorDrawable(this.getResources().getColor(R.color.white));
        emotionPopw.setBackgroundDrawable(cd);
        emotionPopw.setOutsideTouchable(true);

        Button btn_secret = (Button) emotion_View.findViewById(R.id.secret);
        Button btn_single = (Button) emotion_View.findViewById(R.id.single);
        Button btn_loving = (Button) emotion_View.findViewById(R.id.loving);
        Button btn_married = (Button) emotion_View.findViewById(R.id.married);
        Button btn_canael = (Button) emotion_View.findViewById(R.id.canael);
        btn_secret.setOnClickListener(emo);
        btn_single.setOnClickListener(emo);
        btn_loving.setOnClickListener(emo);
        btn_married.setOnClickListener(emo);
        btn_canael.setOnClickListener(emo);

    }

    private View.OnClickListener emo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.secret:
                    selectemotion.setText("保密");
                    sendEmotion("保密");
                    emotionPopw.dismiss();
                    break;
                case R.id.single:
                    selectemotion.setText("单身");
                    sendEmotion("单身");
                    emotionPopw.dismiss();
                    break;
                case R.id.loving:
                    selectemotion.setText("恋爱中");
                    sendEmotion("恋爱中");
                    emotionPopw.dismiss();
                    break;
                case R.id.married:
                    selectemotion.setText("已婚");
                    sendEmotion("已婚");
                    emotionPopw.dismiss();
                    break;
                case R.id.canael:
                    emotionPopw.dismiss();
                    break;
            }
        }
    };

    private void sendEmotion(String emotion) {
        JSONObject jsonObject_sendEmotion = new JSONObject();
        try {
            jsonObject_sendEmotion.put("Protocol", "UserInfo");
            jsonObject_sendEmotion.put("Cmd", "Set");
            jsonObject_sendEmotion.put("UserId", userId);
            jsonObject_sendEmotion.put("AccessKey", accessKey);
            jsonObject_sendEmotion.put("Name", "情感");
            jsonObject_sendEmotion.put("Data", emotion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url(Api.ENCRYPT64)
                .content(jsonObject_sendEmotion.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("失败的返回", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        OkHttpUtils.postString()
                                .url(Api.USERINFO_USER)
                                .content(response)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.e("失败的返回", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        OkHttpUtils.postString()
                                                .url(Api.UNENCRYPT64)
                                                .content(response)
                                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                                .build()
                                                .execute(new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.e("失败的返回", e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        try {
                                                            JSONObject object_nickname = new JSONObject(response);
                                                            String msg = object_nickname.getString("Msg");
//                                                            Toast.makeText(NicknameActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//释放
    }
}

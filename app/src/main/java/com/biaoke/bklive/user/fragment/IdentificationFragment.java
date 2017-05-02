package com.biaoke.bklive.user.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.biaoke.bklive.R;
import com.biaoke.bklive.common.HeaderImageUtils;
import com.biaoke.bklive.message.Api;
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
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.MediaType;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hasee on 2017/4/14.
 */

public class IdentificationFragment extends Fragment {
    @BindView(R.id.et_putName)
    EditText etPutName;
    @BindView(R.id.iv_certification_photo_one)
    ImageView ivCertificationPhotoOne;
    @BindView(R.id.iv_certification_photo_two)
    ImageView ivCertificationPhotoTwo;
    @BindView(R.id.iv_certification_photo_three)
    ImageView ivCertificationPhotoThree;
    @BindView(R.id.cb_agree_bk)
    CheckBox cbAgreeBk;
    @BindView(R.id.btn_put_identification)
    Button btnPutIdentification;
    Unbinder unbinder;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    protected static Uri tempUri;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private UploadManager uploadManager;
    public String uptoken = null;
    private ArrayList<Bitmap> list = new ArrayList<>();//用于存储上传照片地址
    private PopupWindow headPopw;
    private SharedPreferences sharedPreferences_user;
    private String userId;
    private String accessKey;
    private String sendFileName;
    private String cmd;

    // Fragment管理对象
    private FragmentManager manager;
    private FragmentTransaction ft;

    public IdentificationFragment() {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identification, container, false);
        unbinder = ButterKnife.bind(this, view);
        sharedPreferences_user = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);//首先获取用户ID
        userId = sharedPreferences_user.getString("userId", "");
        //获取秘钥，以后基本都要用
        accessKey = sharedPreferences_user.getString("AccessKey", "");
        return view;
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    IdentificationingFragment identificationingFragment = new IdentificationingFragment();
                    ft = manager.beginTransaction();
                    ft.replace(R.id.replace_fragment, identificationingFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    break;

            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.cb_agree_bk, R.id.iv_certification_photo_one, R.id.iv_certification_photo_two, R.id.iv_certification_photo_three, R.id.btn_put_identification})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_certification_photo_one:
                headPopupWindow(view);
                //获取uptoken
                cmd = "cardId_1";
                getUptoken(cmd);
                break;
            case R.id.iv_certification_photo_two:
                headPopupWindow(view);
                //获取uptoken
                cmd = "cardId_2";
                getUptoken(cmd);
                break;
            case R.id.iv_certification_photo_three:
                headPopupWindow(view);
                //获取uptoken
                cmd = "cardId_3";
                getUptoken(cmd);
                break;
            case R.id.btn_put_identification:
                String name = etPutName.getText().toString().trim();
//                Log.e("上传状态", name + cbAgreeBk.isSelected());
                if ((!name.isEmpty()) && cbAgreeBk.isSelected()) {
                    Toast.makeText(getActivity(), "可以上传", Toast.LENGTH_SHORT).show();
                    //未完待续
                } else {
                    Toast.makeText(getActivity(), "协议或姓名为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cb_agree_bk:
                if (!cbAgreeBk.isSelected()) {
                    cbAgreeBk.setSelected(true);
                } else {
                    cbAgreeBk.setSelected(false);
                }
                break;
        }
    }

    private void headPopupWindow(View v) {
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.item_pop_selectpic, null);
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
        Intent openCameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    ;

    private void selectPhoto() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
     * 保存裁剪之后的图片数据
     *
     * @param
     * @param data
     */
    protected void setImageToView(Intent data) {
        File file = new File("/mnt/sdcard/pic/02.jpg");
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
//            photo = HeaderImageUtils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            if (cmd.equals("cardId_1")) {
                ivCertificationPhotoOne.setImageBitmap(photo);
            } else if (cmd.equals("cardId_2")) {
                ivCertificationPhotoTwo.setImageBitmap(photo);
            } else {
                ivCertificationPhotoThree.setImageBitmap(photo);
            }
            uploadPic(photo);
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
        intent.putExtra("aspectY", 1.6);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 160);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
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
                                Log.i("qiniu", "实名照片上传成功");
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
    private void getUptoken(String cmd) {
        JSONObject jsonObject_uptoken = new JSONObject();
        try {
            jsonObject_uptoken.put("Protocol", "Upload");
            jsonObject_uptoken.put("Cmd", cmd);
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
}

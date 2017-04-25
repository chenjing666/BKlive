package com.biaoke.bklive.user.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.message.Api;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by hasee on 2017/4/14.
 */

public class AnchorLivedataFragment extends Fragment {
    @BindView(R.id.livenum)
    TextView livenum;
    Unbinder unbinder;
    private String chatroomId;
    private int page = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anchor_livedata, container, false);
        unbinder = ButterKnife.bind(this, view);
        SharedPreferences sharedPreferences_chatroomId = getActivity().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        chatroomId = sharedPreferences_chatroomId.getString("chatroomId", "");
        JSONObject jsonObject_content = new JSONObject();
        try {
            jsonObject_content.put("Protocol", "GetVideoList");
            jsonObject_content.put("UserId", chatroomId);
            jsonObject_content.put("Page", page + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getVideo(jsonObject_content.toString());
        return view;
    }

    //获取视频用户信息
    private void getVideo(String content) {
        OkHttpUtils
                .postString()
                .url(Api.ENCRYPT64)
                .content(content)
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
                                .url(Api.VIDEOLIST)
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
                                                        Log.d("成功的返回", response);
                                                        try {
                                                            JSONObject object = new JSONObject(response);
                                                            JSONArray jsonArray = new JSONArray(object.getString("Data"));
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                                                String UserId_video = jsonobject.getString("UserId");
                                                                String Tag = jsonobject.getString("Tag");
                                                                String Exp = jsonobject.getString("Exp");
                                                                String HV = jsonobject.getString("HV");
                                                                String type = jsonobject.getString("Type");
                                                                String Title = jsonobject.getString("Title");
                                                                String SnapshotUrl = jsonobject.getString("SnapshotUrl");
                                                                String videoUrl = jsonobject.getString("VideoUrl");
                                                                String Format = jsonobject.getString("Format");
                                                                String PubTime = jsonobject.getString("PubTime");
//                                                                live_item liveItem = new live_item(UserId_video, NickName, IconUrl, Exp, Title, SnapshotUrl, videoUrl, Format, HV, type);
//                                                                recyclerDataList.add(liveItem);
                                                            }

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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

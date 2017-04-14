package com.biaoke.bklive.user.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.biaoke.bklive.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identification, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_certification_photo_one, R.id.iv_certification_photo_two, R.id.iv_certification_photo_three, R.id.btn_put_identification})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_certification_photo_one:
                break;
            case R.id.iv_certification_photo_two:
                break;
            case R.id.iv_certification_photo_three:
                break;
            case R.id.btn_put_identification:
                break;
        }
    }
}

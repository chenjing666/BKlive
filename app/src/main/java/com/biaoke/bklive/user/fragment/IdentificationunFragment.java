package com.biaoke.bklive.user.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.biaoke.bklive.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by hasee on 2017/4/14.
 */

public class IdentificationunFragment extends Fragment {


    @BindView(R.id.btn_certification_apply)
    Button btnCertificationApply;
    Unbinder unbinder;
    // Fragment管理对象
    private FragmentManager manager;
    private FragmentTransaction ft;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identification_un, container, false);
        unbinder = ButterKnife.bind(this, view);
        manager = getFragmentManager();
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_certification_apply)
    public void onClick() {
        IdentificationFragment identificationFragment = new IdentificationFragment();
        ft = manager.beginTransaction();
        ft.replace(R.id.replace_fragment, identificationFragment);
//        ft.addToBackStack(null);//是否销毁的控制
        ft.commit();
    }
}

package com.bber.company.android.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.viewmodel.HeaderBarViewModel;


public abstract class BaseFragment extends Fragment {
    public HeaderBarViewModel headerBarViewModel;
    protected NetWork netWork;
    protected JsonUtil jsonUtil;
    protected TextView title;
    protected Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headerBarViewModel = new HeaderBarViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

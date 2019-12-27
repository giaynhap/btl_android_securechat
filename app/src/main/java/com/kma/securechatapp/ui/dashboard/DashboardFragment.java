package com.kma.securechatapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.DashboardAdapter;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.ui.control.suggestview.SuggestView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    SuggestView suggestView;
    EventBus.EvenBusAction evenBus;

    @BindView(R.id.dashboard_recyclerview)
    RecyclerView recyclerView;

    DashboardAdapter dashboardAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this,root);
        suggestView = new SuggestView(this.getActivity(),root.findViewById(R.id.suggest_view));

        dashboardAdapter = new DashboardAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setAdapter(dashboardAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);



        dashboardViewModel.getSuggest().observe(this,userInfos -> {
            suggestView.upDateList(userInfos);
        });



        if (AppData.getInstance().currentUser != null){
            dashboardViewModel.triggerLoadSuggest();
        }

        evenBus = new EventBus.EvenBusAction() {
            @Override
            public void onLogin(UserInfo u){
                dashboardViewModel.triggerLoadSuggest();
            }
        };

        EventBus.getInstance().addEvent(evenBus);

        return root;
    }
    @Override
    public void onDetach() {
        if (evenBus != null);
        EventBus.getInstance().removeEvent(evenBus);

        super.onDetach();
    }
}
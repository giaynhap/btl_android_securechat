package com.kma.securechatapp.ui.conversation.Inbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.MessageAdapter;
import com.kma.securechatapp.adapter.StickerItemAdapter;
import com.kma.securechatapp.adapter.StickerListAdapter;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.Sticker;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.ui.contact.ContactAddActivity;
import com.kma.securechatapp.ui.conversation.InboxActivity;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.misc.RecyclerItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StickerFragment extends Fragment  {

    ApiInterface api = ApiUtil.getChatApi();
    @BindView(R.id.sticker_item_list)
    RecyclerView recyclerView;
    @BindView(R.id.sticker_item_view)
    RecyclerView recyclerView2;

    StickerListAdapter listAdapter  = new StickerListAdapter();
    StickerItemAdapter itemAdapter  = new StickerItemAdapter();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sticker, container, false);
        ButterKnife.bind(this,root);


        recyclerView.setAdapter(listAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        recyclerView2.setAdapter(itemAdapter);

        recyclerView2.setLayoutManager(new GridLayoutManager(this.getContext(), 4));
        recyclerView2.setHasFixedSize(true);

        api.getStikers().enqueue(new Callback<ApiResponse<List<Sticker>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Sticker>>> call, Response<ApiResponse<List<Sticker>>> response) {
                if (response.body() == null){
                    return;
                }
                listAdapter.setStikers(response.body().data);
                listAdapter.notifyDataSetChanged();
                if (listAdapter.getStikers().size() > 0) {
                    Sticker sticker = listAdapter.getStikers().get(0);
                    itemAdapter.setSticker(sticker.model, sticker.num);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Sticker>>> call, Throwable t) {

            }
        });
        recyclerView2.addOnItemTouchListener( new RecyclerItemClickListener(this.getContext(), recyclerView2 ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        sendSticker(itemAdapter.model,position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this.getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                      Sticker sticker =   listAdapter.getStikers().get(position);
                      itemAdapter.setSticker(sticker.model,sticker.num);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void sendSticker(String model,int index){
        ( (InboxActivity) this.getActivity()).sendSticker( model, index);
    }


}

package com.kma.securechatapp.ui.contact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.UserInfoAdapter;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.misc.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

public class ContactAddActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {

    @BindView(R.id.add_contact_toolbar)
    Toolbar toolbar;
    @BindView(R.id.contact_search_input)
    TextInputEditText searchInput;


    @BindView(R.id.contact_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.contact_swipcontainer)
    SwipeRefreshLayout swipeRefreshLayout;

    private ContactAddViewModel contractAddViewModel;
    private UserInfoAdapter userInfoAdapter = new UserInfoAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        contractAddViewModel =  ViewModelProviders.of(this).get(ContactAddViewModel.class);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle("Add new contact");



        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setAdapter(userInfoAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        contractAddViewModel.getSearchUser().observe(this,userInfos -> {
            userInfoAdapter.setUsers(userInfos);
            userInfoAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ContactAddActivity.this, UserProfileActivity.class);
                        UserInfo userInfo =  userInfoAdapter.getUsers().get(position);
                        intent.putExtra("uuid",userInfo.uuid);
                        intent.setAction("view_user");
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }
    @OnEditorAction(R.id.contact_search_input)
    public boolean onSearchAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            contractAddViewModel.trigerSearch(searchInput.getText().toString());
            hideKeyboard(this);

            return true;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        contractAddViewModel.trigerSearch(searchInput.getText().toString());
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

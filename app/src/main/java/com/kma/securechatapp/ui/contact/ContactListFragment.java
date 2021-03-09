package com.kma.securechatapp.ui.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kma.securechatapp.MainActivity;
import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.ContactAdapter;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.misc.EndlessRecyclerOnScrollListener;
import com.kma.securechatapp.utils.misc.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class ContactListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {

    private ContactViewModel contactViewModel;
    private ContactAdapter contactAdapter = new ContactAdapter();
    private  EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    @BindView(R.id.float_add_button)
    FloatingActionButton floatAddButton;

    @BindView(R.id.contact_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.contact_swipcontainer)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.contact_search_input)
    TextInputEditText editTextSearch;

    EventBus.EvenBusAction evenBus;

    @OnEditorAction(R.id.contact_search_input)
    public boolean onSearchAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            contactAdapter.search(editTextSearch.getText().toString());
            hideKeyboard(this.getActivity());
            return true;
        }
        return false;
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactViewModel =
                ViewModelProviders.of(this).get(ContactViewModel.class);
        View root = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this,root);



       // recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setAdapter(contactAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (contactViewModel.numPage > currentPage){
                    swipeRefreshLayout.setRefreshing(true);
                    loadMore(currentPage);
                }
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadData();
            }
        });


        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this.getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ContactListFragment.this.getActivity(), UserProfileActivity.class);
                        Contact contact =  contactAdapter.getContacts().get(position);
                        intent.putExtra("uuid",contact.contactUuid);
                        intent.setAction("view_user");
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
        registerEvent();
        return root;
    }
    void registerEvent(){
        evenBus = new EventBus.EvenBusAction() {
            @Override
            public void onRefreshContact() {
                contactViewModel.trigerLoadData(0);
            }
        };
        EventBus.getInstance().addEvent(evenBus);

    }
    @Override
    public void onRefresh() {
        endlessRecyclerOnScrollListener.reset();
        contactViewModel.trigerLoadData(0);
    }

    @OnClick(R.id.float_add_button)
    public void onClickAdd(View view){
        Intent intent = new Intent(MainActivity.instance,ContactAddActivity.class);
        startActivity(intent);
    }


    void loadData(){
        swipeRefreshLayout.setRefreshing(true);
        contactViewModel.getContact(0).observe(this,contacts -> {
            contactAdapter.setContacts(contacts);
            contactAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }
    void loadMore(int page){
        swipeRefreshLayout.setRefreshing(true);
        contactViewModel.trigerLoadData(page);
    }
    @Override
    public void onDetach() {
        if (evenBus != null);
        EventBus.getInstance().removeEvent(evenBus);

        super.onDetach();
    }
}
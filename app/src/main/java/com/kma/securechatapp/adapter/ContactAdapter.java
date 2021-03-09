package com.kma.securechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.ContactViewHolder;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.utils.common.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends   RecyclerView.Adapter {
    private List<Contact> contacts;
    private List<Contact> display;
    private  boolean isSearch = false;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContactViewHolder contactHolder = (ContactViewHolder)holder;
        Contact contact = display.get(position);
        contactHolder.setContactName(contact.contactName);
        contactHolder.setContactAvatar(ImageLoader.getUserAvatarUrl(contact.contactUuid,80,80));
        contactHolder.setSubName(contact.contactUuid);
        contactHolder.setOnline(contact.online);

    }

    @Override
    public int getItemCount() {
        if (display == null)
            return 0;
        return display.size();
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void search(String name){
        if (contacts == null || contacts.size() < 1){
            isSearch = false;
            return;
        }
        if (name == null || name == ""){
            isSearch = false;
        } else {
            isSearch = true;
        }
        if (isSearch ){
            List<Contact> searchList= new ArrayList<>();
            for (Contact x :contacts){
                if ( x.contactName == null || x.contactName == ""){
                    continue;
                }
                if (x.contactName.toLowerCase().indexOf(name.toLowerCase()) >= 0){
                    searchList.add(x);
                }
            }
            display = searchList;
        } else {
            display  = contacts;
        }
        this.notifyDataSetChanged();
    }
    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        display = contacts;
    }
    public void addContacts(List<Contact> contacts){
        this.contacts.addAll(contacts);
    }
}

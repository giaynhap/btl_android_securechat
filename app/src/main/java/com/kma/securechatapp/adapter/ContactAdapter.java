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

import java.util.List;

public class ContactAdapter extends   RecyclerView.Adapter {
    private List<Contact> contacts;

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
        Contact contact = contacts.get(position);
        contactHolder.setContactName(contact.contactName);
        contactHolder.setContactAvatar(ImageLoader.getUserAvatarUrl(contact.contactUuid,80,80));
        contactHolder.setSubName(contact.contactUuid);
        contactHolder.setOnline(contact.online);

    }

    @Override
    public int getItemCount() {
        if (contacts == null)
            return 0;
        return contacts.size();
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
    public void addContacts(List<Contact> contacts){
        this.contacts.addAll(contacts);
    }
}

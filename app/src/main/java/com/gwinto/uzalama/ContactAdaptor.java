package com.gwinto.uzalama;

import android.content.Context;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContactAdaptor extends BaseAdapter {
    private Context context;
    private ArrayList<ContactItem> contacts;

    public ContactAdaptor(Context context, ArrayList<ContactItem> items){
        this.contacts = items;
        this.context = context;
    }
    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public ContactItem getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_item,parent,false);

        TextView name_txt = convertView.findViewById(R.id.item_fullname);
        TextView email_txt = convertView.findViewById(R.id.item_email);
        TextView phone_txt = convertView.findViewById(R.id.item_phone);

        final ContactItem contact = getItem(position);
        String full_name = contact.getFname()+ " "+ contact.getOnames();
        name_txt.setText(full_name);
        email_txt.setText(contact.getEmail());
        phone_txt.setText(contact.getPhone());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, String> note = new HashMap<>();
                Date currentTime = Calendar.getInstance().getTime();
                note.put("fname",contact.getFname());
                note.put("onames",contact.getOnames());
                note.put("email",contact.getEmail());
                note.put("phone",contact.getPhone());
                note.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                note.put("message", "HELP! I am in a critical emergency!");
                note.put("timestamp", currentTime.toString());
                FirebaseFirestore.getInstance().collection("history")
                        .add(note).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(contact.getPhone(), null, note.get("message"), null, null);
                            Toast.makeText(context, "Emergency Sent!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Failed to send emergency message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return  convertView;
    }
}

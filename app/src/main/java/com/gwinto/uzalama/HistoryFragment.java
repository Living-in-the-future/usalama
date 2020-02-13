package com.gwinto.uzalama;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    ListView historyList;
    ArrayList<HistoryItem> historyItems;
    HistoryAdaptor historyAdaptor;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);

        historyList = view.findViewById(R.id.user_emergency_list);
        historyItems = new ArrayList<>();

        historyAdaptor = new HistoryAdaptor(getActivity(),historyItems);

        historyList.setAdapter(historyAdaptor);

        FirebaseFirestore.getInstance().collection("history")
                .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                HistoryItem histItem = doc.getDocument().toObject(HistoryItem.class);
                                historyItems.add(histItem);
                                historyAdaptor.notifyDataSetChanged();
                            }
                        }
                    }
                });


        return view;
    }
}

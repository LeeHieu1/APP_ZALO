package hcmute.edu.vn.zalo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Phaser;

import hcmute.edu.vn.zalo.adapter.fragmentChatsAdapter;
import hcmute.edu.vn.zalo.model.Chat;
import hcmute.edu.vn.zalo.model.User;


public class fragment_Chats extends Fragment {

    private fragmentChatsAdapter adapter;
    private ListView lsvChats;
    private List<String> lsIdUser;
    private List<User> lsUser;
    private List<Chat> lsChat;
    private String uPhone, pass;
    private User user;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference ref = db.child("Chats");
    private DatabaseReference ref_2 = db.child("User");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__chats, container, false);


        uPhone = getActivity().getIntent().getExtras().getString("phone");
        pass = getActivity().getIntent().getExtras().getString("pass");


        lsvChats = view.findViewById(R.id.lsv_fragmentChat_ListChats);
        lsIdUser = new ArrayList<>();
        lsUser = new ArrayList<>();
        lsChat = new ArrayList<>();


        lsvChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), Activity_Chatting.class);
                User user = (User) parent.getAdapter().getItem(position);
                intent.putExtra("opponet_phone", user.getUserPhone());
                intent.putExtra("opponent_name", user.getUserName());
                intent.putExtra("phone", uPhone);
                intent.putExtra("pass", pass);
                startActivity(intent);
            }
        });


        ValueEventListener valueEventListener_2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                user = u;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref_2.child(uPhone).addListenerForSingleValueEvent(valueEventListener_2);
        ref_2.child(uPhone).addValueEventListener(valueEventListener_2);


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lsIdUser.clear();
                int check = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getSender().equals(uPhone)) {
                        int count_1 = 0;
                        if (lsIdUser.size() != 0) {
                            for (String id : lsIdUser) {
                                if (chat.getReceiver().equals(id)) {
                                    count_1++;
                                }
                            }
                            if (count_1 == 0) {
                                lsIdUser.add(chat.getReceiver());
                            }
                        } else {
                            lsIdUser.add(chat.getReceiver());
                        }
                        check = 1;
                    }
                    if (chat.getReceiver().equals(uPhone)) {
                        int count_2 = 0;
                        for (String id : lsIdUser) {
                            if (chat.getSender().equals(id)) {
                                count_2++;
                            }
                        }
                        if (count_2 == 0) {
                            lsIdUser.add(chat.getSender());
                        }
                        check = 1;
                    }
                }
                if (check == 1) {
                    readChats();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
        ref.addValueEventListener(valueEventListener);

        return view;
    }

    private void readChats() {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lsUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for (String id : lsIdUser) {
                        if (user.getUserPhone().equals(id)) {
                            lsUser.add(new User(user.getUserPhone(), pass, user.getUserName(),
                                    user.getUserImage(), user.getUserCover(), user.getUserAge(),
                                    user.getUserDateofBirth(), user.getUserSex(), user.getStatus()));
                        }
                    }
                }
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lsChat.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            lsChat.add(chat);
                        }
                        adapter = new fragmentChatsAdapter(getContext(), R.layout.dong_chat_userchatwith, lsUser, lsChat, user);
                        lsvChats.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref_2.addListenerForSingleValueEvent(valueEventListener);
        ref_2.addValueEventListener(valueEventListener);
    }


}
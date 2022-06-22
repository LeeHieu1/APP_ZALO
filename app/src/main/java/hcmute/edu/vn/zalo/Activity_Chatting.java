package hcmute.edu.vn.zalo;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo.adapter.MessageAdapter;
import hcmute.edu.vn.zalo.model.Chat;
import hcmute.edu.vn.zalo.model.User;

public class Activity_Chatting extends AppCompatActivity {
    private TextView txtOpponentPhone, isRecording, notRecord, txtHello;
    private CircleImageView imgOpponet;
    private ImageButton btnSend, btnGallery, btnCamera, btnAudio;
    private ImageView btnRecord, btnBack, btnRecordSend, btnRecordCancle;
    private EditText edtMess;
    private MessageAdapter adapter;
    private RecyclerView rcvChats;
    private List<Chat> lsChat;

    private User uUser, opponent;
    private Dialog dialog, dialog_2;
    private int check_image_message = 0;
    private String img_message, uPhone, recordFile, oName, oPhone, uPass, Status;
    private boolean isRecord = false;
    private MediaRecorder recorder;
    private Chronometer chronometer;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference ref = db.child("Chats");
    private DatabaseReference ref_2 = db.child("User");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_chat);

        rcvChats = findViewById(R.id.rcv_PageChat_ListMessages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rcvChats.setLayoutManager(linearLayoutManager);

        btnGallery = findViewById(R.id.img_PageChat_Gallery);
        txtOpponentPhone = findViewById(R.id.textview_PageChat_OpponentName);
        btnSend = findViewById(R.id.imageButton_PageChat_Send);
        edtMess = findViewById(R.id.edittext_PageChat_TypeChats);
        btnBack = findViewById(R.id.img_PageChat_BackIcon);
        btnAudio = findViewById(R.id.img_PageChat_Record);
        imgOpponet = findViewById(R.id.circleimg_PageChat_imgOpponent);
        txtHello = findViewById(R.id.textview_PageChat_Hello);

        oPhone = getIntent().getExtras().getString("opponet_phone");
        oName = getIntent().getExtras().getString("opponent_name");
        uPhone = getIntent().getExtras().getString("phone");
        uPass = getIntent().getExtras().getString("pass");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getUserImage() != null) {
                    byte[] arr = Base64.decode(user.getUserImage(), 0);
                    Bitmap bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                    imgOpponet.setImageBitmap(bmp);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref_2.child(oPhone).addListenerForSingleValueEvent(valueEventListener);
        ref_2.child(oPhone).addValueEventListener(valueEventListener);
        ref_2.child(oPhone).removeEventListener(valueEventListener);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogGalleryOption();
            }
        });

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRecordPermission()) {
                    requestRecordPermission();
                }
                DialogRecordAudio();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Chatting.this, Activity_Home.class);
                intent.putExtra("phone", uPhone);
                intent.putExtra("pass", uPass);
                startActivity(intent);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edtMess.getText().toString();
                if (!mess.equals("") && check_image_message == 0) {
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String time = format.format(date);
                    SendMessage(uPhone, oPhone, mess, time);
                    ReadMessage(uPhone, oPhone);
                } else if (check_image_message == 1) {
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String time = format.format(date);
                    SendMessage(uPhone, oPhone, img_message, time);
                    ReadMessage(uPhone, oPhone);
                } else {
                    Toast.makeText(Activity_Chatting.this, "Vui lòng nhập tin nhắn muốn gửi !", Toast.LENGTH_SHORT).show();
                }
                edtMess.setText("");
                adapter.notifyDataSetChanged();
            }
        });


        txtOpponentPhone.setText(oName);
        ReadMessage(uPhone, oPhone);
    }


    private void SendMessage(String sender, String receiver, String message, String messtime) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("time", messtime);
        if (check_image_message == 1) {
            hashMap.put("isImageMessage", 1);
            ref.push().setValue(hashMap);
        } else if (check_image_message == 0) {
            hashMap.put("isImageMessage", 0);
            ref.push().setValue(hashMap);
        } else {
            hashMap.put("isImageMessage", 2);
            ref.push().setValue(hashMap);
        }
        check_image_message = 0;
        ReadMessage(sender, receiver);
    }

    private void ReadMessage(String myid, String user) {
        lsChat = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lsChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(user) ||
                            chat.getReceiver().equals(user) && chat.getSender().equals(myid)) {
                        lsChat.add(chat);
                    }
                }
                if (lsChat.size() == 0) {
                    txtHello.setVisibility(View.VISIBLE);
                } else {
                    txtHello.setVisibility(View.GONE);
                }
                adapter = new MessageAdapter(Activity_Chatting.this, lsChat, uUser, opponent);
                rcvChats.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ValueEventListener valueEventListener_2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getUserPhone().equals(uPhone)) {
                        uUser = user;
                    }
                    if (user.getUserPhone().equals(oPhone)) {
                        opponent = user;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref_2.addListenerForSingleValueEvent(valueEventListener_2);
        ref_2.addValueEventListener(valueEventListener_2);
        ref.addListenerForSingleValueEvent(valueEventListener);
        ref.addValueEventListener(valueEventListener);
    }

    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private void requestRecordPermission() {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

    private boolean checkStoragePermission() {
        boolean res = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res;
    }

    private boolean checkRecordPermission() {
        boolean res = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        return res && res2;
    }

    private void openGalerry() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 101);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 102);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                openGalerry();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            img_message = android.util.Base64.encodeToString(byteArray, 0, byteArray.length, 0);
            check_image_message = 1;
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String time = format.format(date);
            SendMessage(uPhone, oPhone, img_message, time);
            dialog.cancel();
        } else if (requestCode == 101 && resultCode == RESULT_OK) {
            Uri link = data.getData();
            if (link != null) {
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(link);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    img_message = android.util.Base64.encodeToString(byteArray, 0, byteArray.length, 0);
                    check_image_message = 1;
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String time = format.format(date);
                    SendMessage(uPhone, oPhone, img_message, time);
                    dialog.cancel();
                } catch (Exception er) {
                    Toast.makeText(this, er.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            dialog.cancel();
        }
    }

    private void DialogGalleryOption() {
        dialog = new Dialog(Activity_Chatting.this);
        dialog.setContentView(R.layout.dialog_gallery_option);

        btnCamera = dialog.findViewById(R.id.img_Camera);
        btnGallery = dialog.findViewById(R.id.img_Gallery);


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    openCamera();
                    check_image_message = 1;
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    openGalerry();
                    check_image_message = 1;
                }
            }
        });
        dialog.show();
    }

    public void DialogRecordAudio() {
        dialog_2 = new Dialog(Activity_Chatting.this);
        dialog_2.setContentView(R.layout.dialog_record);

        btnRecordCancle = dialog_2.findViewById(R.id.img_Record_Cancle);
        btnRecordSend = dialog_2.findViewById(R.id.img_Record_Send);
        isRecording = dialog_2.findViewById(R.id.textview_StatusRecording);
        notRecord = dialog_2.findViewById(R.id.textview_StatusRecord);
        btnRecord = dialog_2.findViewById(R.id.img_ButtonRecrod);
        chronometer = dialog_2.findViewById(R.id.chronometer_RecordLength);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecord == true) {
                    isRecord = false;
                    stopRecording();
                } else {
                    isRecord = true;
                    startRecording();
                }
            }
        });
        btnRecordCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecord = false;
                dialog_2.cancel();
            }
        });
        btnRecordSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().getPath() + "/Download/record.mp3";
                String mess = "";
                try {
                    byte[] arr = Files.readAllBytes(Paths.get(path));
                    mess = Base64.encodeToString(arr, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                check_image_message = 2;
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String time = format.format(date);
                SendMessage(uPhone, oPhone, mess, time);
                ReadMessage(uPhone, oPhone);
                dialog_2.cancel();
            }
        });
        dialog_2.show();
    }

    private void startRecording() {
        chronometer.start();
        btnRecordSend.setVisibility(View.VISIBLE);
        btnRecordCancle.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        notRecord.setVisibility(View.GONE);
        isRecording.setVisibility(View.VISIBLE);
        btnRecord.setImageResource(R.drawable.microphone_icon_2);
        try {
            String path = Environment.getExternalStorageDirectory().getPath() +
                    "/Download";
            recordFile = "record.mp3";
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(path + "/" + recordFile);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        chronometer.stop();
        isRecording.setVisibility(View.GONE);
        notRecord.setVisibility(View.VISIBLE);
        btnRecord.setImageResource(R.drawable.microphone_icon);
        recorder.stop();
        recorder.release();
        recorder = null;
    }


    private void updateStatus(String status){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        uPhone = getIntent().getExtras().getString("phone");
        ref_2.child(uPhone).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status = "online";
        updateStatus(Status);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Status="offline";
        updateStatus(Status);
    }

}

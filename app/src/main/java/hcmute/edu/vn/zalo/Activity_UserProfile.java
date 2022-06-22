package hcmute.edu.vn.zalo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo.model.User;

public class Activity_UserProfile extends AppCompatActivity {
    private EditText edtUsername, edtUserPhone, edtUserAge, edtUserSex, edtUserDoB;
    private ImageView imgUserCover;
    private CircleImageView imgAvatar;
    private ImageButton imgCalendar, imgBack;
    private Button btnUpdate;
    private String ImageUser, ImageCover, uPhone, pass, Status;
    private AlertDialog dialog;


    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference ref = db.child("User");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_userinfor);


        uPhone = getIntent().getExtras().getString("phone");
        pass = getIntent().getExtras().getString("pass");


        edtUsername = findViewById(R.id.edittext_PageUserInfor_Username);
        edtUserAge = findViewById(R.id.edittext_PageUserInfor_Age);
        edtUserPhone = findViewById(R.id.edittext_PageUserInfor_Phone);
        edtUserSex = findViewById(R.id.edittext_PageUserInfor_Sex);
        edtUserDoB = findViewById(R.id.edittext_PageUserInfor_DoB);
        imgUserCover = findViewById(R.id.img_PageUserInfor_CoverImage);
        imgAvatar = findViewById(R.id.circleimg_PageUserInfor_Avatar);
        imgCalendar = findViewById(R.id.imgbutton_PageUserInfor_Calendar);
        imgBack = findViewById(R.id.imgbutton_PageUserInfor_Back);
        btnUpdate = findViewById(R.id.button_PageUserInfor_Update);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        setData();

        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_UserProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        edtUserDoB.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        Activity_UserProfile.this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Activity_UserProfile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    selectAvatar();
                }
            }
        });

        imgUserCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        Activity_UserProfile.this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Activity_UserProfile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                } else {
                    selectCover();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check = 0;
                String phone = edtUserPhone.getText().toString();
                String name = edtUsername.getText().toString();
                String img = ImageUser;
                String cover = ImageCover;
                String age = edtUserAge.getText().toString();
                String DoB = edtUserDoB.getText().toString();
                String sex = edtUserSex.getText().toString();
                String status = "online";

                if (phone.isEmpty()) {
                    edtUserPhone.setError("Vui lòng nhập số điện thoại !");
                    edtUserPhone.requestFocus();
                    check = 1;
                }

                if (name.isEmpty()) {
                    edtUsername.setError("Vui lòng nhập tên !");
                    edtUsername.requestFocus();
                    check = 1;
                }

                if (age.isEmpty()) {
                    edtUserAge.setError("Vui lòng điền tuổi !");
                    edtUserAge.requestFocus();
                    check = 1;
                }

                if (Integer.parseInt(age) <= 0) {
                    edtUserAge.setError("Tuổi không hợp lệ !");
                    edtUserAge.requestFocus();
                    check = 1;
                }
                if (DoB.isEmpty()) {
                    edtUserDoB.setError("Vui lòng nhập năm sinh !");
                    edtUserDoB.requestFocus();
                    check = 1;
                }

                if (sex.isEmpty()) {
                    edtUserSex.setError("Vui lòng điền giới tính !");
                    edtUserSex.requestFocus();
                    check = 1;
                }

                if (phone.length() < 10 || phone.length() > 10) {
                    edtUserPhone.setError("Số điện thoại không chính xác !");
                    edtUserPhone.requestFocus();
                    check = 1;
                }

                if (check == 0) {
                    DialogProcessing();
                    User user = new User(phone, pass, name, img, cover, Integer.parseInt(age), DoB, sex, status);

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ref.child(uPhone).setValue(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    ref.child(uPhone).addListenerForSingleValueEvent(valueEventListener);
                    ref.child(uPhone).addValueEventListener(valueEventListener);
                    ref.child(uPhone).removeEventListener(valueEventListener);
                    dialog.cancel();
                    Toast.makeText(Activity_UserProfile.this, "Update thông tin thành công !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void selectAvatar() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(Activity_UserProfile.this.getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }

    private void selectCover() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(Activity_UserProfile.this.getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectAvatar();
            } else {
                Toast.makeText(Activity_UserProfile.this, "Permission Denied !", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectCover();
            } else {
                Toast.makeText(Activity_UserProfile.this, "Permission Denied !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    try {
                        InputStream inputStream = Activity_UserProfile.this.getContentResolver().openInputStream(selectedImage);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        ImageUser = Base64.encodeToString(byteArray, 0, byteArray.length, 0);
                        imgAvatar.setImageBitmap(bitmap);
                    } catch (Exception er) {
                        Toast.makeText(Activity_UserProfile.this, er.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    try {
                        InputStream inputStream = Activity_UserProfile.this.getContentResolver().openInputStream(selectedImage);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        ImageCover = Base64.encodeToString(byteArray, 0, byteArray.length, 0);
                        imgUserCover.setImageBitmap(bitmap);
                    } catch (Exception er) {
                        Toast.makeText(Activity_UserProfile.this, er.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void setData() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    edtUserPhone.setText(user.getUserPhone());
                    edtUsername.setText(user.getUserName());
                    edtUserAge.setText(String.valueOf(user.getUserAge()));
                    edtUserSex.setText(user.getUserSex());
                    edtUserDoB.setText(user.getUserDateofBirth());
                    String img = user.getUserImage();
                    String cover = user.getUserCover();
                    if (img != null) {
                        byte[] arr = Base64.decode(img, 0);
                        Bitmap bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                        imgAvatar.setImageBitmap(bmp);
                        ImageUser = img;
                    } else {
                        imgAvatar.setImageResource(R.drawable.upload);
                    }

                    if (cover != null) {
                        byte[] arrcover = Base64.decode(cover, 0);
                        Bitmap bmpcover = BitmapFactory.decodeByteArray(arrcover, 0, arrcover.length);
                        imgUserCover.setImageBitmap(bmpcover);
                        ImageCover = cover;
                    } else {
                        imgUserCover.setImageResource(R.drawable.upload);
                    }

                } else {
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        ref.child(uPhone).addListenerForSingleValueEvent(valueEventListener);
        ref.child(uPhone).removeEventListener(valueEventListener);
    }

    //hàm DialogProcessing để hiển thị dialog khi đang chạy kiểm tra tài khoản có trong db hay không
    private void DialogProcessing() {
        //khởi tạo builder và xác định context nơi sẽ builder dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //khởi tạo inflater
        LayoutInflater inflater = this.getLayoutInflater();
        //dùng method inflate để get layout muốn hiển thị trong dialog, root: để là null vì ta chỉ get layout ra chứ không hiển thị ngay lập tức
        builder.setView(inflater.inflate(R.layout.dialog_processing, null));
        // method setCancleable dùng để tắt dialog bằng phím back trên điện thoại
        builder.setCancelable(true);

        //tạo dialog bằng lệnh create
        dialog = builder.create();
        //hiển thị dialog
        dialog.show();
    }


    private void updateStatus(String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        uPhone = getIntent().getExtras().getString("phone");
        ref.child(uPhone).updateChildren(hashMap);
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
        Status = "offline";
        updateStatus(Status);
    }
}

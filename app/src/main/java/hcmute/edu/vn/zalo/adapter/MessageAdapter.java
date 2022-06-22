package hcmute.edu.vn.zalo.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import hcmute.edu.vn.zalo.R;
import hcmute.edu.vn.zalo.model.Chat;
import hcmute.edu.vn.zalo.model.User;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public boolean check = false;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler;
    private Runnable runnable;


    private Context context;
    private List<Chat> lsChat;
    private User user, opponent;
    private int check_message_image;


    public MessageAdapter(Context context, List<Chat> lsChat, User user, User opponent) {
        this.context = context;
        this.lsChat = lsChat;
        this.user = user;
        this.opponent = opponent;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtShowChat;
        private ImageView imgChats, imgPlayRecord;
        public SeekBar seekbar;
        public Chronometer chronometer;
        public ConstraintLayout layoutRecord;


        public ViewHolder(View itemView) {
            super(itemView);
            txtShowChat = itemView.findViewById(R.id.message);
            imgChats = itemView.findViewById(R.id.img_Chats_Image);
            seekbar = itemView.findViewById(R.id.seekbar_chat);
            chronometer = itemView.findViewById(R.id.chronometer_chat);
            imgPlayRecord = itemView.findViewById(R.id.img_playrecord);
            layoutRecord = itemView.findViewById(R.id.layout_chat);
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.dong_chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.dong_chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = lsChat.get(position);
        check_message_image = chat.getIsImageMessage();
        if (check_message_image == 0) {

            holder.imgChats.setVisibility(View.GONE);
            holder.txtShowChat.setVisibility(View.VISIBLE);
            holder.txtShowChat.setText(chat.getMessage());
        } else if (check_message_image == 1) {

            holder.imgChats.setVisibility(View.VISIBLE);
            holder.txtShowChat.setVisibility(View.GONE);
            String img = chat.getMessage();
            byte[] arr = Base64.decode(img, 0);
            Bitmap bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            holder.imgChats.setImageBitmap(bmp);
        } else {
            String mess = chat.getMessage();
            holder.layoutRecord.setVisibility(View.VISIBLE);
            holder.txtShowChat.setVisibility(View.GONE);
            holder.imgChats.setVisibility(View.GONE);
            holder.imgPlayRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.imgPlayRecord.setImageResource(R.drawable.pause_icon);
                    try {
                        byte[] retrive = Base64.decode(mess, 0);
                        File tempMp3 = File.createTempFile("temp", "mp3");
                        tempMp3.deleteOnExit();
                        FileOutputStream fos = new FileOutputStream(tempMp3);
                        fos.write(retrive);
                        fos.close();

                        FileInputStream fis = new FileInputStream(tempMp3);
                        mediaPlayer.setDataSource(fis.getFD());

                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        holder.chronometer.setBase(SystemClock.elapsedRealtime());
                        holder.chronometer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                holder.chronometer.stop();
                                holder.imgPlayRecord.setImageResource(R.drawable.play_icon);
                                holder.seekbar.setMax(0);
                                holder.chronometer.setBase(SystemClock.elapsedRealtime());
                            }
                        });

                        holder.seekbar.setMax(mediaPlayer.getDuration());
                        handler = new Handler();
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                holder.seekbar.setProgress(mediaPlayer.getCurrentPosition());
                                handler.postDelayed(runnable, 0);
                            }
                        };
                        handler.postDelayed(runnable, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return lsChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (lsChat.get(position).getSender().equals(user.getUserPhone())) {
            return MSG_TYPE_RIGHT;
        } else {

            return MSG_TYPE_LEFT;
        }
    }
}

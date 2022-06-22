package hcmute.edu.vn.zalo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo.R;
import hcmute.edu.vn.zalo.model.Chat;
import hcmute.edu.vn.zalo.model.User;

public class fragmentChatsAdapter extends BaseAdapter {

    public Context context;
    public int layout;
    public List<User> lsUser;
    public List<Chat> lsChat;
    public User user;

    public fragmentChatsAdapter(Context context, int layout, List<User> lsUser, List<Chat> lsChat, User user) {
        this.context = context;
        this.layout = layout;
        this.lsUser = lsUser;
        this.lsChat = lsChat;
        this.user = user;
    }

    @Override
    public int getCount() {
        return lsUser.size();
    }

    @Override
    public Object getItem(int position) {
        return lsUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder {
        private CircleImageView imgFriend;
        private ImageView imgStatus;
        private TextView txtFriendName, txtLastMessage;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.imgFriend = convertView.findViewById(R.id.img_dongChatFriends_ImageFriend);
            holder.txtLastMessage = convertView.findViewById(R.id.textview_dongChatFriends_LastMessage);
            holder.txtFriendName = convertView.findViewById(R.id.textview_dongChatFriends_FriendName);
            holder.imgStatus = convertView.findViewById(R.id.img_dongChatFriends_Status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User u = lsUser.get(position);
        Chat lastmess = new Chat();

        for (Chat chat : lsChat) {
            if (user.getUserPhone().equals(chat.getReceiver()) && u.getUserPhone().equals(chat.getSender())
                    || user.getUserPhone().equals(chat.getSender()) && u.getUserPhone().equals(chat.getReceiver())) {
                lastmess = chat;
            }
        }

        holder.txtFriendName.setText(u.getUserName());
        if (lastmess.getIsImageMessage() == 0) {
            if (lastmess.getSender().equals(user.getUserPhone())) {
                holder.txtLastMessage.setText("Bạn: " + lastmess.getMessage());
            } else {
                String[] arr = u.getUserName().split(" ");
                int index = arr.length;
                holder.txtLastMessage.setText(arr[index - 1] + ": " + lastmess.getMessage());
            }

        } else if (lastmess.getIsImageMessage() == 1) {
            if (lastmess.getSender().equals(user.getUserPhone())) {
                holder.txtLastMessage.setText("Bạn đã gửi 1 file hình ảnh");
            } else {
                String[] arr = u.getUserName().split(" ");
                int index = arr.length;
                holder.txtLastMessage.setText(arr[index - 1] + " đã gửi 1 file hình ảnh");
            }
        } else {
            if (lastmess.getSender().equals(user.getUserPhone())) {
                holder.txtLastMessage.setText("Bạn đã gửi 1 file âm thanh");
            } else {
                String[] arr = u.getUserName().split(" ");
                int index = arr.length;
                holder.txtLastMessage.setText(arr[index - 1] + " đã gửi 1 file âm thanh");
            }
        }
        String img = u.getUserImage();
        if (img != null) {
            byte[] arr = Base64.decode(img, 0);
            Bitmap bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            holder.imgFriend.setImageBitmap(bmp);
        } else {
            holder.imgFriend.setImageResource(R.drawable.anh_dai_dien);
        }

        for (User u_2 : lsUser) {
            if (u_2.getStatus().equals("online")) {
                holder.imgStatus.setImageResource(R.drawable.status_icon_2);
            } else {
                holder.imgStatus.setImageResource(R.drawable.status_icon);
            }
        }

        return convertView;
    }

}

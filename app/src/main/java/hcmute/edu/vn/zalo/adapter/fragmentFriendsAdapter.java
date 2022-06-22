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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import hcmute.edu.vn.zalo.R;
import hcmute.edu.vn.zalo.model.User;


public class fragmentFriendsAdapter extends BaseAdapter {
    public Context context;     //Khởi tạo biến context để nhận Activity cần
    public int layout;
    public List<User> lsAccount;

    public fragmentFriendsAdapter(Context context, int layout, List<User> lsAccount) {
        this.context = context;
        this.layout = layout;
        this.lsAccount = lsAccount;
    }

    @Override
    public int getCount() {
        return lsAccount.size();
    }

    @Override
    public Object getItem(int position) {
        return lsAccount.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder {
        private ImageView imgFriend;
        private TextView txtFriendName, txtFriendPhone;
        private ListView lsvFriends;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.imgFriend = convertView.findViewById(R.id.img_dongFriends);
            holder.txtFriendName = convertView.findViewById(R.id.textview_FriendName_dongFriends);
            holder.txtFriendPhone = convertView.findViewById(R.id.textview_FriendPhone_dongFriends);
            holder.lsvFriends = convertView.findViewById(R.id.lsv_fragmentFriend_ListFriends);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User account = lsAccount.get(position);

        holder.txtFriendPhone.setText(account.getUserPhone());
        holder.txtFriendName.setText(account.getUserName());

        if(account.getUserImage()!=null)
        {
            byte[] arr = Base64.decode(account.getUserImage(),0);
            Bitmap bmp = BitmapFactory.decodeByteArray(arr,0,arr.length);
            holder.imgFriend.setImageBitmap(bmp);
        }else{
            holder.imgFriend.setImageResource(R.drawable.anh_dai_dien);
        }


        return convertView;
    }
}

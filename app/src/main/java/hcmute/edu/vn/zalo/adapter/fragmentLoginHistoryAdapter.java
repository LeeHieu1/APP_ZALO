package hcmute.edu.vn.zalo.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hcmute.edu.vn.zalo.R;
import hcmute.edu.vn.zalo.model.LoginHistory;

public class fragmentLoginHistoryAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<LoginHistory> lsLoginHistory;

    public fragmentLoginHistoryAdapter(Context context, int layout, List<LoginHistory> lsLoginHistory) {
        this.context = context;
        this.layout = layout;
        this.lsLoginHistory = lsLoginHistory;
    }

    @Override
    public int getCount() {
        return lsLoginHistory.size();
    }

    @Override
    public Object getItem(int position) {
        return lsLoginHistory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder {
        private TextView txtDeviceName, txtTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.txtDeviceName = convertView.findViewById(R.id.textview_LoginHistory_DeviceName);
            holder.txtTime = convertView.findViewById(R.id.textview_LoginHistory_Time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LoginHistory loginHistory = lsLoginHistory.get(position);

        holder.txtDeviceName.setText(loginHistory.getDeviceName());
        holder.txtTime.setText(loginHistory.getTime());

        return convertView;
    }
}

package com.example.shalhan4.sqmint.ui.user;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shalhan4.sqmint.R;
import com.example.shalhan4.sqmint.ui.job.Job;

import java.util.List;

/**
 * Created by shalhan4 on 6/18/2017.
 */

public class UserListAdapter extends BaseAdapter {
    private Context mContext;
    private List<User> mUserList;

    public UserListAdapter(Context mContext, List<User> mUserList) {
        this.mContext = mContext;
        this.mUserList = mUserList;
    }

    @Override
    public int getCount() {
        return this.mUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(this.mContext, R.layout.user_list, null);

        TextView tvId = (TextView) v.findViewById(R.id.user_id);
        TextView tvUserName = (TextView) v.findViewById(R.id.user_name);
        TextView tvLastLoginDate = (TextView) v.findViewById(R.id.user_last_login_date);
        TextView tvLastLoginTime = (TextView) v.findViewById(R.id.user_last_login_time);

        tvId.setText("" + this.mUserList.get(position).getId());
        tvUserName.setText(this.mUserList.get(position).getName());
        tvLastLoginDate.setText(this.mUserList.get(position).getLastLoginDate());
        tvLastLoginTime.setText(this.mUserList.get(position).getLastLoginTime());

        v.setTag(mUserList.get(position).getId());
        return v;
    }
}
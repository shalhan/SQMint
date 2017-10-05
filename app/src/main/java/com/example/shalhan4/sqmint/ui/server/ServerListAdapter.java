package com.example.shalhan4.sqmint.ui.server;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.shalhan4.sqmint.R;

import java.util.List;

/**
 * Created by shalhan4 on 9/27/2017.
 */

public class ServerListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Server> mServerList;
    private ServerView mServerView;

    public ServerListAdapter(Context context, List<Server> serverList, ServerView serverView)
    {
        this.mContext = context;
        this.mServerList = serverList;
        this.mServerView = serverView;
    }

    @Override
    public int getCount() {
        return this.mServerList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mServerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(this.mContext, R.layout.server_list, null);

        TextView tvServerIp = (TextView) v.findViewById(R.id.tv_server_ip);
        TextView tvServerAdmin = (TextView) v.findViewById(R.id.tv_server_admin);
        TextView tvServerUsername = (TextView) v.findViewById(R.id.tv_server_username);

        ImageButton ibServerDelete = (ImageButton) v.findViewById(R.id.ib_server_delete);

        tvServerIp.setText(this.mServerList.get(position).getIpAddress());
        tvServerAdmin.setText(this.mServerList.get(position).getAdmin());
        tvServerUsername.setText(this.mServerList.get(position).getUsername());

//        ibServerDelete.setTag(mServerList.get(position).getId());
        ibServerDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServerView.deleteServer(mServerList.get(position).getId());
            }
        });

        v.setTag(mServerList.get(position).getId());
        return v;
    }
}
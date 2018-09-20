package team.developer.official.dpkt.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import team.developer.official.dpkt.R;

/**
 * Created by Haydar Dzaky S on 8/14/2018.
 */

public class CustomAdapter extends ArrayAdapter<List> {

    int groupid;

    ArrayList<List> records;

    Context context;


    public CustomAdapter(Context context, int vg, int id, ArrayList<List> records) {
        super(context, vg, id, records);

        this.context = context;

        groupid = vg;

        this.records = records;

    }

    public View getView(int position, View convertView, ViewGroup parent) {



        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(groupid, parent, false);

        TextView textDay = (TextView) itemView.findViewById(R.id.day);

        textDay.setText(records.get(position).getDay());

        TextView textRole = (TextView) itemView.findViewById(R.id.role);

        textRole.setText(records.get(position).getRole());



        return itemView;

    }

}

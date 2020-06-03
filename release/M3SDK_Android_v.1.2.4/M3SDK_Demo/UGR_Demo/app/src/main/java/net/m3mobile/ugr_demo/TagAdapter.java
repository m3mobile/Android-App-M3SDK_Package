package net.m3mobile.ugr_demo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by M3 on 2017-12-11.
 */

public class TagAdapter extends SimpleAdapter{
    Context context;
    private ArrayList<HashMap<String, UhfTag>> arrayList;
    private int resource; // Layout ID


    public TagAdapter(Context context, ArrayList<HashMap<String, UhfTag>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);

        this.context = context;
        this.arrayList = data;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(resource, null);
        }

        HashMap<String, UhfTag> hm = arrayList.get(position);
        UhfTag epc = (UhfTag)hm.values().toArray()[0];

        TextView title = (TextView)convertView.findViewById(R.id.txtEPC);
        TextView reads = (TextView)convertView.findViewById(R.id.txtCount);

        String epcData = epc.EPC;

        title.setText(epcData);
        reads.setText(Integer.toString(epc.Reads));

        return convertView;
    }
}

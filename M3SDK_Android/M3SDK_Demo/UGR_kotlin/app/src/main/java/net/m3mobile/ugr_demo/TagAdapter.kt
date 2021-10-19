package net.m3mobile.ugr_demo

import net.m3mobile.ugr_demo.UHFTag
import android.widget.SimpleAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import net.m3mobile.ugr_demo.R
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by M3 on 2017-12-11.
 */
class TagAdapter(
    var context: Context, private val arrayList: ArrayList<HashMap<String, UHFTag>>, // Layout ID
    private val resource: Int, from: Array<String?>?, to: IntArray?
) : SimpleAdapter(
    context, arrayList, resource, from, to
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (context as Activity).layoutInflater
            convertView = inflater.inflate(resource, null)
        }
        val hm = arrayList[position]
        val epc = hm.values.toTypedArray()[0]
        val title = convertView?.findViewById<View>(R.id.txtEPC) as TextView
        val reads = convertView.findViewById<View>(R.id.txtCount) as TextView
        val epcData = epc.EPC
        title.text = epcData
        reads.text = Integer.toString(epc.Reads)
        return convertView
    }
}
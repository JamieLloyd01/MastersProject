package com.example.mastersproject

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import android.graphics.Color
import android.graphics.Color.rgb
import android.widget.LinearLayout
import androidx.cardview.widget.CardView

class MyAdapter(private val activityList : ArrayList<Item>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {

        if (activityList.isEmpty()) {
            return  // Handle empty list gracefully
        }

        val actualPosition = position % activityList.size
        val activity: Item = activityList[actualPosition]
        holder.name.text = activity.name
        holder.filter1.text = activity.filter1
        holder.filter2.text = activity.filter2
        holder.description.text = activity.description
        holder.priceBracket.text = activity.priceBracket
        Picasso.get().load(activity.imageurl).into(holder.imageurl)

        var backgroundColor = Color.parseColor("#FF5733")
        when (activity.filter1) {
            "Sports & Exercise" -> backgroundColor = Color.argb(255, 255, 100, 100)
            "Games" -> backgroundColor = Color.argb( 255, 128, 0, 128)
            "Outdoors" -> backgroundColor = Color.argb(255, 143, 143, 86)
            "Nature & Wildlife" -> backgroundColor = Color.argb(255, 54, 130, 64)
            "Historic" -> backgroundColor = Color.argb(255, 205, 155, 85)
            "Arts" -> backgroundColor = Color.argb(255, 0, 206, 209)
            else -> Color.parseColor("#000000")
        }


        holder.linearLayout.setBackgroundColor(backgroundColor)

        if (backgroundColor == Color.argb(255, 54, 130, 64)) {
            holder.name.setTextColor(Color.WHITE) // Set text color to white
            holder.description.setTextColor(Color.WHITE)
            holder.priceBracket.setTextColor(Color.WHITE)
        } else  if (backgroundColor == Color.argb(255, 128, 0, 128)) {
            holder.name.setTextColor(Color.WHITE) // Set text color to white
            holder.description.setTextColor(Color.WHITE)
            holder.priceBracket.setTextColor(Color.WHITE)
        } else {
            // Set text colors for other background colors
            // Adjust the text colors as needed for each case
            holder.name.setTextColor(Color.BLACK)
            holder.filter1.setTextColor(Color.BLACK)
            holder.filter2.setTextColor(Color.BLACK)
            holder.description.setTextColor(Color.BLACK)
            holder.priceBracket.setTextColor(Color.BLACK)
        }



        holder.link.tag = activity.link

        holder.link.setOnClickListener {
            val link = holder.link.tag as? String

            if (!link.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                holder.itemView.context.startActivity(intent)
            }
        }


    }


    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }


    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout: LinearLayout = itemView.findViewById(R.id.activityCard)
        val name: TextView = itemView.findViewById(R.id.activityName)
        val filter1: TextView = itemView.findViewById(R.id.filter1)
        val filter2: TextView = itemView.findViewById(R.id.filter2)
        val description: TextView = itemView.findViewById(R.id.description)
        val priceBracket: TextView = itemView.findViewById(R.id.priceBracket)
        val imageurl: ImageView = itemView.findViewById(R.id.image)
        val link: TextView = itemView.findViewById(R.id.visitWebsite)

    }
}
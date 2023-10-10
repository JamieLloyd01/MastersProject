package com.example.mastersproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyAdapter(private val activityList : ArrayList<Item>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val activity : Item = activityList[position]
        holder.name.text = activity.name
        holder.filter1.text = activity.filter1
        holder.filter2.text = activity.filter2
        holder.description.text = activity.description
        holder.priceBracket.text = activity.priceBracket
        Picasso.get().load(activity.imageurl).into(holder.imageurl)
    }

    override fun getItemCount(): Int {
        return activityList.size
    }


    public class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val name : TextView = itemView.findViewById(R.id.activityName)
        val filter1 : TextView = itemView.findViewById(R.id.filter1)
        val filter2: TextView = itemView.findViewById(R.id.filter2)
        val description : TextView = itemView.findViewById(R.id.description)
        val priceBracket : TextView = itemView.findViewById(R.id.priceBracket)
        val imageurl : ImageView = itemView.findViewById(R.id.image)
    }
}
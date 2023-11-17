package com.example.mastersproject

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter(private val activityList : ArrayList<Item>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    fun View.bringToBack() {
        val parent = this.parent as ViewGroup
        val index = parent.indexOfChild(this)
        if (index > 0) {
            parent.removeView(this)
            parent.addView(this, 0)
        }
    }

    // Define an interface for the item click listener
    interface OnItemClickListener {
        fun onItemClick(name: String)
    }



    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: HomeActivity) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Ensure the user is logged in


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
        if(activity.imageurl != null && !activity.imageurl!!.isEmpty()) {
            Picasso.get().load(activity.imageurl).into(holder.imageurl)
        }


        val backgroundImage: ImageView = holder.itemView.findViewById(R.id.backgroundImage)
        backgroundImage.bringToBack()

        val myTextView = holder.name // The TextView you want to change the font for



        // Set the background image for "Nature & Wildlife"
        if (activity.filter1 == "Nature & Wildlife") {
            backgroundImage.setImageResource(R.drawable.nature2)
            myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.amaticboldfont)
            holder.name.setTextSize(40.0F)
        } else  if (activity.filter1 == "Games") {
            backgroundImage.setImageResource(R.drawable.games)
            myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.bangersfont)
            holder.name.setTextSize(30.0F)
        } else  if (activity.filter1 == "Outdoors") {
            backgroundImage.setImageResource(R.drawable.outdoors)
            myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.rustlemonfont)
            holder.name.setTextSize(26.5F)
        } else  if (activity.filter1 == "Sports & Exercise") {
            backgroundImage.setImageResource(R.drawable.sports)
            myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.graduatefont)
            holder.name.setTextSize(24.5F)
        } else  if (activity.filter1 == "Historic") {
            backgroundImage.setImageResource(R.drawable.history2)
            myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.trajanproboldfont)
            holder.name.setTextSize(25.0F)
        } else  if (activity.filter1 == "Arts") {
            backgroundImage.setImageResource(R.drawable.art)
            myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.anandablackfont)
            holder.name.setTextSize(29.0F)
        }

        var backgroundColor = Color.parseColor("#FF5733")
        when (activity.filter1) {
            "Sports & Exercise" -> backgroundColor = Color.argb(155, 255, 100, 100)
            "Games" -> backgroundColor = Color.argb( 220, 128, 0, 128)
            "Outdoors" -> backgroundColor = Color.argb(155, 135, 206, 250)
            "Nature & Wildlife" -> backgroundColor = Color.argb(120, 54, 150, 64)
            "Historic" -> backgroundColor = Color.argb(155, 205, 155, 85)
            "Arts" -> backgroundColor = Color.argb(155, 0, 206, 209)
            else -> Color.parseColor("#000000")
        }


        holder.linearLayout.setBackgroundColor(backgroundColor)

        if (backgroundColor == Color.argb(220, 128, 0, 128)) {
            holder.name.setTextColor(Color.WHITE) // Set text color to white
            holder.description.setTextColor(Color.WHITE)
            holder.priceBracket.setTextColor(Color.WHITE)
        } else {
            // Set text colors for other background colors
            // Adjust the text colors as needed for each case
            holder.name.setTextColor(Color.BLACK)
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

        // Set a click listener for the entire item view
        holder.itemView.setOnClickListener {
            val actualPosition = position % activityList.size
            val activity: Item = activityList[actualPosition]
            activity.name?.let { it1 -> onItemClickListener?.onItemClick(it1) }
        }


        holder.itemView.setOnLongClickListener {
            val actualPosition = position % activityList.size
            val activity: Item = activityList[actualPosition]

            userId?.let { uid ->
                val userDoc = db.collection("users").document(uid)
                userDoc.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val currentValue = activity.name?.let { it1 -> document.getBoolean(it1) } ?: false
                        val newValue = !currentValue
                        val newCompletionNumber = if (currentValue) {
                            (document.getLong("completionNumber") ?: 0) - 1
                        } else {
                            (document.getLong("completionNumber") ?: 0) + 1
                        }

                        activity.name?.let { activityName ->
                            val updates = hashMapOf<String, Any>(
                                activityName to newValue,
                                "completionNumber" to newCompletionNumber
                            )

                            userDoc.update(updates)
                                .addOnSuccessListener {
                                    val toastMessage = if (newValue) "Marked as Complete" else "Marked as Uncomplete"
                                    Toast.makeText(holder.itemView.context, toastMessage, Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    // Handle the failure here, e.g., show an error message
                                }
                        }
                    }
                }
            }
            // Get the Vibrator service
            val vibrator = holder.itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            // Check if the device has a vibrator
            if (vibrator.hasVibrator()) {
                // Vibrate for a specified length of time (e.g., 500 milliseconds)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }

            true // Return true to indicate that the callback consumed the long click
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
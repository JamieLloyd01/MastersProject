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
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter(private val activityList: ArrayList<Item>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: HomeActivity) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (activityList.isEmpty()) {
            return
        }

        val actualPosition = position % activityList.size
        val currentActivity = activityList[actualPosition]

        holder.apply {
            name.text = currentActivity.name
            filter1.text = currentActivity.filter1
            filter2.text = currentActivity.filter2
            description.text = currentActivity.description
            priceBracket.text = currentActivity.priceBracket

            if (currentActivity.imageurl != null && currentActivity.imageurl!!.isNotEmpty()) {
                Picasso.get().load(currentActivity.imageurl).into(imageurl)
            }

            val backgroundImage: ImageView = itemView.findViewById(R.id.backgroundImage)
            backgroundImage.bringToBack()
            setLayoutDetails(currentActivity, name, description, priceBracket, backgroundImage, linearLayout)

            //Handling "visit website" button
            link.tag = currentActivity.link

            link.setOnClickListener {
                val linkUrl = it.tag as? String
                if (!linkUrl.isNullOrBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
                    itemView.context.startActivity(intent)
                }
            }

            // When card clicked
            itemView.setOnClickListener {
                currentActivity.name?.let { it1 -> onItemClickListener?.onItemClick(it1) }
            }

            //When card held mark that activity as complete/uncomplete and update completion number
            itemView.setOnLongClickListener {
                userId?.let { uid ->
                    val userDoc = db.collection("users").document(uid)
                    userDoc.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val currentValue = currentActivity.name?.let { it1 ->
                                document.getBoolean(
                                    it1
                                )
                            }
                                ?: false
                            val newValue = !currentValue
                            val newCompletionNumber = if (currentValue) {
                                (document.getLong("completionNumber") ?: 0) - 1
                            } else {
                                (document.getLong("completionNumber") ?: 0) + 1
                            }

                            val activityName = currentActivity.name ?: return@addOnSuccessListener //if activityName is null return (don't think it is possible to be null but safety safety safety)

                            val updates = hashMapOf<String, Any>(
                                activityName to newValue,
                                "completionNumber" to newCompletionNumber
                            )

                            userDoc.update(updates)
                                .addOnSuccessListener {
                                    val toastMessage = if (newValue) "Marked as Complete" else "Marked as Uncomplete"
                                    Toast.makeText(itemView.context, toastMessage, Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    val toastMessageFail = "Error"
                                    Toast.makeText(itemView.context, toastMessageFail, Toast.LENGTH_SHORT).show()
                                }

                        }
                    }
                }
                // Make device vibrate when card held
                val vibrator = itemView.context.getSystemService(Vibrator::class.java)
                if (vibrator?.hasVibrator() == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(200)
                    }
                }
                true
            }
        }
    }


// List of methods to configure the cards depending on their filter
    private fun setLayoutDetails(activity: Item, name: TextView, description: TextView, priceBracket: TextView, backgroundImage: ImageView, linearLayout: LinearLayout) {
        when (activity.filter1) {
            "Nature & Wildlife" -> configureLayoutForNature(name, backgroundImage)
            "Games" -> configureLayoutForGames(name, backgroundImage)
            "Outdoors" -> configureLayoutForOutdoors(name, backgroundImage)
            "Sports & Exercise" -> configureLayoutForSports(name, backgroundImage)
            "Historic" -> configureLayoutForHistoric(name, backgroundImage)
            "Arts" -> configureLayoutForArts(name, backgroundImage)

        }

        // Set the background colour
        val backgroundColour = when (activity.filter1) {
            "Sports & Exercise" -> Color.argb(155, 255, 100, 100)
            "Games" -> Color.argb( 220, 128, 0, 128)
            "Outdoors" -> Color.argb(155, 135, 206, 250)
            "Nature & Wildlife" -> Color.argb(120, 54, 150, 64)
            "Historic" -> Color.argb(155, 205, 155, 85)
            "Arts" -> Color.argb(155, 0, 206, 209)
            else -> Color.parseColor("#000000")
        }
        linearLayout.setBackgroundColor(backgroundColour)

        // Set text colours
        when (backgroundColour) {
            Color.argb(220, 128, 0, 128) -> setColourForViews(name, description, priceBracket, Color.WHITE)
            Color.argb(255, 255, 100, 100),
            Color.argb(255, 143, 143, 86),
            Color.argb(255, 0, 206, 209) -> {
                name.setTextColor(Color.argb(255, 0, 0, 0))
                description.setTextColor(Color.argb(255, 0, 0, 0))
                priceBracket.setTextColor(Color.argb(255, 0, 0, 0))
            }
            else -> setColourForViews(name, description, priceBracket, Color.BLACK)
        }
    }
    

    private fun configureLayoutForNature(name: TextView, backgroundImage: ImageView) {
        backgroundImage.setImageResource(R.drawable.nature2)
        name.typeface = ResourcesCompat.getFont(name.context, R.font.amaticboldfont)
        name.textSize = 40.0F
    }
    private fun configureLayoutForGames(name: TextView, backgroundImage: ImageView) {
        backgroundImage.setImageResource(R.drawable.games)
        name.typeface = ResourcesCompat.getFont(name.context, R.font.bangersfont)
        name.textSize = 30.0F
    }
    private fun configureLayoutForOutdoors(name: TextView, backgroundImage: ImageView) {
        backgroundImage.setImageResource(R.drawable.outdoors)
        name.typeface = ResourcesCompat.getFont(name.context, R.font.rustlemonfont)
        name.textSize = 26.5F
    }
    private fun configureLayoutForSports(name: TextView, backgroundImage: ImageView) {
        backgroundImage.setImageResource(R.drawable.sports)
        name.typeface = ResourcesCompat.getFont(name.context, R.font.graduatefont)
        name.textSize = 24.5F
    }
    private fun configureLayoutForHistoric(name: TextView, backgroundImage: ImageView) {
        backgroundImage.setImageResource(R.drawable.history2)
        name.typeface = ResourcesCompat.getFont(name.context, R.font.trajanproboldfont)
        name.textSize = 25.0F
    }
    private fun configureLayoutForArts(name: TextView, backgroundImage: ImageView) {
        backgroundImage.setImageResource(R.drawable.art)
        name.typeface = ResourcesCompat.getFont(name.context, R.font.anandablackfont)
        name.textSize = 29.0F
    }

    private fun setColourForViews(view: TextView, view2: TextView, view3: TextView, color: Int) {
        view.setTextColor(color)
        view2.setTextColor(color)
        view3.setTextColor(color)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout: LinearLayout = itemView.findViewById(R.id.activityCard)
        val name: TextView = itemView.findViewById(R.id.activityName)
        val filter1: TextView = itemView.findViewById(R.id.filter1)
        val filter2: TextView = itemView.findViewById(R.id.filter2)
        val description: TextView = itemView.findViewById(R.id.description)
        val priceBracket: TextView = itemView.findViewById(R.id.priceBracket)
        val imageurl: ImageView = itemView.findViewById(R.id.image)
        val link: TextView = itemView.findViewById(R.id.visitWebsite)
    }

    // interface for the card click listener
    interface OnItemClickListener {
        fun onItemClick(name: String)
    }

    private fun View.bringToBack() {
        val parent = this.parent as ViewGroup
        val index = parent.indexOfChild(this)
        if (index > 0) {
            parent.removeView(this)
            parent.addView(this, 0)
        }
    }
}

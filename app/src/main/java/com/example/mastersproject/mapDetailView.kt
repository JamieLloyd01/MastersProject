package com.example.mastersproject

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso



/**
 * A simple [Fragment] subclass.
 * Use the [mapDetailView.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapDetailView : Fragment() {

    fun View.bringToBack() {
        val parent = this.parent as ViewGroup
        val index = parent.indexOfChild(this)
        if (index > 0) {
            parent.removeView(this)
            parent.addView(this, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.item_view, container, false)


        // Retrieve the selected item from the arguments bundle
        val item = arguments?.getParcelable<Item>("selectedItem")

        // Find views within the inflated layout
        val name: TextView = rootView.findViewById(R.id.activityName)
        val filter1: TextView = rootView.findViewById(R.id.filter1)
        val filter2: TextView = rootView.findViewById(R.id.filter2)
        val description: TextView = rootView.findViewById(R.id.description)
        val priceBracket: TextView = rootView.findViewById(R.id.priceBracket)
        val image: ImageView = rootView.findViewById(R.id.image)
        val link: TextView = rootView.findViewById(R.id.visitWebsite)

        // Populate the views with item details
        item?.let {
            name.text = it.name
            filter1.text = it.filter1
            filter2.text = it.filter2
            description.text = it.description
            priceBracket.text = it.priceBracket

            // Load an image using a library like Picasso or Glide
            Picasso.get().load(it.imageurl).into(image)

            val linearLayout: LinearLayout = rootView.findViewById(R.id.activityCard)
            val backgroundImage: ImageView = rootView.findViewById(R.id.backgroundImage)
            backgroundImage.bringToBack()
            val myTextView: TextView = rootView.findViewById(R.id.activityName)


            // Set the background image for "Nature & Wildlife"
            if (it.filter1 == "Nature & Wildlife") {
                backgroundImage.setImageResource(R.drawable.nature2)
                myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.amaticboldfont)
                name.setTextSize(40.0F)
            } else  if (it.filter1 == "Games") {
                backgroundImage.setImageResource(R.drawable.games)
                myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.bangersfont)
                name.setTextSize(30.0F)
            } else  if (it.filter1 == "Outdoors") {
                backgroundImage.setImageResource(R.drawable.outdoors)
                myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.rustlemonfont)
                name.setTextSize(26.5F)
            } else  if (it.filter1 == "Sports & Exercise") {
                backgroundImage.setImageResource(R.drawable.sports)
                myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.graduatefont)
                name.setTextSize(26.5F)
            } else  if (it.filter1 == "Historic") {
                backgroundImage.setImageResource(R.drawable.history2)
                myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.trajanproboldfont)
                name.setTextSize(25.0F)
            } else  if (it.filter1 == "Arts") {
                backgroundImage.setImageResource(R.drawable.art)
                myTextView.typeface = ResourcesCompat.getFont(myTextView.context, R.font.anandablackfont)
                name.setTextSize(29.0F)
            }
            // Set the background color based on your conditions
            var backgroundColor = Color.parseColor("#FF5733")
            when (it.filter1) {
                "Sports & Exercise" -> backgroundColor = Color.argb(155, 255, 100, 100)
                "Games" -> backgroundColor = Color.argb( 220, 128, 0, 128)
                "Outdoors" -> backgroundColor = Color.argb(155, 135, 206, 250)
                "Nature & Wildlife" -> backgroundColor = Color.argb(120, 54, 150, 64)
                "Historic" -> backgroundColor = Color.argb(155, 205, 155, 85)
                "Arts" -> backgroundColor = Color.argb(155, 0, 206, 209)
                else -> Color.parseColor("#000000")
            }
            linearLayout.setBackgroundColor(backgroundColor)

            // Set text colors based on the background color
            if (backgroundColor == Color.argb(220, 128, 0, 128)) {
                name.setTextColor(Color.WHITE) // Set text color to white
                description.setTextColor(Color.WHITE)
                priceBracket.setTextColor(Color.WHITE)
            } else if (backgroundColor == Color.argb(255, 255, 100, 100)) {
                name.setTextColor(Color.argb(255, 0, 0, 0))
                description.setTextColor(Color.argb(255, 0, 0, 0))
                priceBracket.setTextColor(Color.argb(255, 0, 0, 0))
            } else if (backgroundColor == Color.argb(255, 143, 143, 86)) {
                name.setTextColor(Color.argb(255, 0, 0, 0))
                description.setTextColor(Color.argb(255, 0, 0, 0))
                priceBracket.setTextColor(Color.argb(255, 0, 0, 0))

            } else if (backgroundColor == Color.argb(255, 0, 206, 209)) {
                name.setTextColor(Color.argb(255, 0, 0, 0))
                description.setTextColor(Color.argb(255, 0, 0, 0))
                priceBracket.setTextColor(Color.argb(255, 0, 0, 0))
            } else {
                // Set text colors for other background colors
                // Adjust the text colors as needed for each case
                name.setTextColor(Color.BLACK)
                description.setTextColor(Color.BLACK)
                priceBracket.setTextColor(Color.BLACK)
            }
        }


        // Set an OnClickListener for the "Visit Website" TextView if needed
        link.setOnClickListener {
            val itemLink = item?.link
            if (!itemLink.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemLink))
                startActivity(intent)
            }
        }


        return rootView
    }
}


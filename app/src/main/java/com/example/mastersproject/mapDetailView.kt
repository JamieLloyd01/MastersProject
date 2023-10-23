package com.example.mastersproject

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso



/**
 * A simple [Fragment] subclass.
 * Use the [mapDetailView.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapDetailView : Fragment() {
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

            // Set the background color based on your conditions
            var backgroundColor = Color.parseColor("#FF5733")
            when (it.filter1) {
                "Sports & Exercise" -> backgroundColor = Color.argb(255, 255, 100, 100)
                "Games" -> backgroundColor = Color.argb( 255, 128, 0, 128)
                "Outdoors" -> backgroundColor = Color.argb(255, 135, 206, 250)
                "Nature & Wildlife" -> backgroundColor = Color.argb(255, 54, 130, 64)
                "Historic" -> backgroundColor = Color.argb(255, 205, 155, 85)
                "Arts" -> backgroundColor = Color.argb(255, 0, 206, 209)
                else -> Color.parseColor("#000000")
            }
            linearLayout.setBackgroundColor(backgroundColor)

            // Set text colors based on the background color
            if (backgroundColor == Color.argb(255, 54, 130, 64)) {
                name.setTextColor(Color.WHITE) // Set text color to white
                description.setTextColor(Color.WHITE)
                priceBracket.setTextColor(Color.WHITE)
            } else  if (backgroundColor == Color.argb(255, 128, 0, 128)) {
                name.setTextColor(Color.WHITE) // Set text color to white
                description.setTextColor(Color.WHITE)
                priceBracket.setTextColor(Color.WHITE)
            }else if (backgroundColor == Color.argb(255, 255, 100, 100)) {
                name.setTextColor(Color.argb(255, 0, 0, 0))
                description.setTextColor(Color.argb(255, 0, 0, 0))
                priceBracket.setTextColor(Color.argb(255, 0, 0, 0))
            } else if (backgroundColor == Color.argb(255, 143, 143, 86)) {
                name.setTextColor(Color.argb(255, 0, 0, 0))
                description.setTextColor(Color.argb(255, 0, 0, 0))
                priceBracket.setTextColor(Color.argb(255, 0, 0, 0))

            } else {
                // Set text colors for other background colors
                // Adjust the text colors as needed for each case
                name.setTextColor(Color.BLACK)
                filter1.setTextColor(Color.BLACK)
                filter2.setTextColor(Color.BLACK)
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


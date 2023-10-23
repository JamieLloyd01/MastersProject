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

            var backgroundColor = Color.parseColor("#FF5733")
            when (it.filter1) {
                "Sports & Exercise" -> backgroundColor = Color.argb(255, 255, 0, 0)
                "Games" -> backgroundColor = Color.argb(255, 128, 0, 128)
                "Outdoors" -> backgroundColor = Color.argb(255, 139, 69, 19)
                "Nature & Wildlife" -> backgroundColor = Color.argb(255, 29, 185, 84)
                "Historic" -> backgroundColor = Color.argb(255,246, 188, 91)
                "Arts" -> backgroundColor = Color.argb(255, 0, 206, 209)
                else -> Color.parseColor("#000000")
            }
            linearLayout.setBackgroundColor(backgroundColor)

            // Set an OnClickListener for the "Visit Website" TextView if needed
            link.setOnClickListener {
                val itemLink = item.link
                if (!itemLink.isNullOrBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemLink))
                    startActivity(intent)
                }
            }
        }

        return rootView
    }
}


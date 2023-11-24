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
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso


class MapDetailView : Fragment() {

    private fun View.bringToBack() {
        val parent = this.parent as ViewGroup
        val index = parent.indexOfChild(this)
        if (index > 0) {
            parent.removeView(this)
            parent.addView(this, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.item_view, container, false)

        val item = arguments?.getParcelable<Item>("selectedItem")

        val name: TextView = rootView.findViewById(R.id.activityName)
        val filter1: TextView = rootView.findViewById(R.id.filter1)
        val filter2: TextView = rootView.findViewById(R.id.filter2)
        val description: TextView = rootView.findViewById(R.id.description)
        val priceBracket: TextView = rootView.findViewById(R.id.priceBracket)
        val image: ImageView = rootView.findViewById(R.id.image)
        val link: TextView = rootView.findViewById(R.id.visitWebsite)

        item?.let {
            name.text = it.name
            filter1.text = it.filter1
            filter2.text = it.filter2
            description.text = it.description
            priceBracket.text = it.priceBracket

            if (it.imageurl != null) {
                Picasso.get().load(it.imageurl).into(image)
            }

            val linearLayout: LinearLayout = rootView.findViewById(R.id.activityCard)
            val backgroundImage: ImageView = rootView.findViewById(R.id.backgroundImage)
            backgroundImage.bringToBack()

            setLayoutDetails(it, name, description, priceBracket, backgroundImage, linearLayout)
        }

        link.setOnClickListener {
            item?.link?.let { itemLink ->
                if (itemLink.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemLink))
                    startActivity(intent)
                }
            }
        }

        return rootView
    }

    private fun setLayoutDetails(
        item: Item,
        name: TextView,
        description: TextView,
        priceBracket: TextView,
        backgroundImage: ImageView,
        linearLayout: LinearLayout
    ) {
        when (item.filter1) {
            "Nature & Wildlife" -> configureLayoutForNature(name, backgroundImage)
            "Games" -> configureLayoutForGames(name, backgroundImage)
            "Outdoors" -> configureLayoutForOutdoors(name, backgroundImage)
            "Sports & Exercise" -> configureLayoutForSports(name, backgroundImage)
            "Historic" -> configureLayoutForHistoric(name, backgroundImage)
            "Arts" -> configureLayoutForArts(name, backgroundImage)

        }

        // Set the background color
        val backgroundColor = when (item.filter1) {
            "Sports & Exercise" -> Color.argb(155, 255, 100, 100)
            "Games" -> Color.argb( 220, 128, 0, 128)
            "Outdoors" -> Color.argb(155, 135, 206, 250)
            "Nature & Wildlife" -> Color.argb(120, 54, 150, 64)
            "Historic" -> Color.argb(155, 205, 155, 85)
            "Arts" -> Color.argb(155, 0, 206, 209)
            else -> Color.parseColor("#000000")
        }
        linearLayout.setBackgroundColor(backgroundColor)

        // Set text colors
        when (backgroundColor) {
            Color.argb(220, 128, 0, 128) -> setColorForViews(name, description, priceBracket, Color.WHITE)
            Color.argb(255, 255, 100, 100),
            Color.argb(255, 143, 143, 86),
            Color.argb(255, 0, 206, 209) -> {
                name.setTextColor(Color.argb(255, 0, 0, 0))
                description.setTextColor(Color.argb(255, 0, 0, 0))
                priceBracket.setTextColor(Color.argb(255, 0, 0, 0))
            }
            else -> setColorForViews(name, description, priceBracket, Color.BLACK)
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


    private fun setColorForViews(view: TextView, view2: TextView, view3: TextView, color: Int) {
        view.setTextColor(color)
        view2.setTextColor(color)
        view3.setTextColor(color)
    }
}


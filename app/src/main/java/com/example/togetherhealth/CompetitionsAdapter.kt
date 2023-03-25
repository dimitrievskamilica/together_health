package com.example.togetherhealth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CompetitionsAdapter(private val data: MutableList<Competition>, private val onClickObject:CompetitionsAdapter.MyOnClick) :
    RecyclerView.Adapter<CompetitionsAdapter.ViewHolder>() {
    interface MyOnClick {
        fun onLongClick(p0: View?, position:Int)
        fun onClick(p0: View?, position:Int)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //val imageView: ImageView = itemView.findViewById(R.id.imageViewMainActivity)
        val CompetitionName: TextView = itemView.findViewById(R.id.textViewName)
        val CompetitionDate: TextView = itemView.findViewById(R.id.textViewDate)
        val CompetitionTime: TextView = itemView.findViewById(R.id.textViewTime)
        //val ActivityAchievment: TextView = itemView.findViewById(R.id.textViewAchievment)
        //val roomType: TextView = itemView.findViewById(R.id.textView13)
        val line: CardView = itemView.findViewById(R.id.competitionCvLine)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_competition, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = data[position]
        //Picasso.get().load("https://iconarchive.com/show/business-economic-icons-by-inipagi/store-icon.html").placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(holder.imageView);
        // sets the text to the textview from our itemHolder class
        //Timber.d("MM onBindViewHolder ${data.size}")
        holder.CompetitionName.text = ItemsViewModel.name
        holder.CompetitionDate.text = ItemsViewModel.date
        holder.CompetitionTime.text = ItemsViewModel.time
        //holder.ActivityAchievment.text=ItemsViewModel.goal.toString()
        //holder.roomType.text = ItemsViewModel.roomInfo.roomType

        holder.line.setOnLongClickListener(object: View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                println("Here code comes Click on ${holder.adapterPosition}")

                onClickObject.onLongClick(v, holder.adapterPosition)//delegacija klica na lasten objekt-sledi razlaga

                return true
            }
        });

        holder.line.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                //Timber.d("Here code comes Click on ${holder.adapterPosition}")
                onClickObject.onClick(p0,holder.adapterPosition) //Action from Activity
            }
        })

    }
}
package id.trydev.kupompong.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import id.trydev.kupompong.R
import id.trydev.kupompong.model.Fase
import id.trydev.kupompong.utils.GlideApp
import id.trydev.kupompong.utils.GlideModule

class FaseAdapter(private val context: Context, val onClick: (Fase)->Unit): RecyclerView.Adapter<FaseAdapter.ViewHolder>() {

    private val listFase = mutableListOf<Fase>()
    
    fun setData(listFase: List<Fase>) {
        this.listFase.clear()
        this.listFase.addAll(listFase)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fase_level, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listFase[position])
    }

    override fun getItemCount(): Int = listFase.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val itemBody = view.findViewById<CardView>(R.id.item_body)
        private val tvName = view.findViewById<TextView>(R.id.tv_name)

        fun bindItem(item: Fase) {
            tvName.text = item.title

            itemBody.setOnClickListener { onClick(item) }
        }

    }

}
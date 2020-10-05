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
import id.trydev.kupompong.model.HasilTerapi
import id.trydev.kupompong.utils.GlideApp
import java.text.SimpleDateFormat
import java.util.*

class ProgressTerapiAdapter(private val context: Context, val onClick: (HasilTerapi)->Unit): RecyclerView.Adapter<ProgressTerapiAdapter.ViewHolder>() {

    private val listHasilTerapi = mutableListOf<HasilTerapi>()
    
    fun setData(listHasilTerapi: List<HasilTerapi>) {
        this.listHasilTerapi.clear()
        this.listHasilTerapi.addAll(listHasilTerapi)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_terapi, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listHasilTerapi[position])
    }

    override fun getItemCount(): Int = listHasilTerapi.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val itemBody = view.findViewById<CardView>(R.id.item_body)
        private val tvName = view.findViewById<TextView>(R.id.tv_name)
        private val tvTopik = view.findViewById<TextView>(R.id.tv_topik)
        private val tvDate = view.findViewById<TextView>(R.id.tv_date)

        fun bindItem(item: HasilTerapi) {
            tvName.text = item.namaAnak
            tvTopik.text = item.topik
            tvDate.text = SimpleDateFormat("E,dd MM yyyy", Locale("id", "ID")).format(item.dateTerapi)

            itemBody.setOnClickListener { onClick(item) }
        }

    }

}
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
import id.trydev.kupompong.model.Anak
import id.trydev.kupompong.utils.GlideApp
import id.trydev.kupompong.utils.GlideModule

class DaftarAnakAdapterLite(private val context: Context, val onClick: (Anak)->Unit): RecyclerView.Adapter<DaftarAnakAdapterLite.ViewHolder>() {

    private val listAnak = mutableListOf<Anak>()
    
    fun setData(listAnak: List<Anak>) {
        this.listAnak.clear()
        this.listAnak.addAll(listAnak)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_anak_lite, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listAnak[position])
    }

    override fun getItemCount(): Int = listAnak.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val itemBody = view.findViewById<CardView>(R.id.item_body)
        private val imgAnak = view.findViewById<ImageView>(R.id.img_anak)
        private val tvName = view.findViewById<TextView>(R.id.tv_name)

        fun bindItem(item: Anak) {
            tvName.text = item.fullName

            GlideApp.with(context)
                .asBitmap()
                .load(item.photoUrl)
                .transform(CenterCrop(), RoundedCorners(context.resources.getDimensionPixelSize(R.dimen.rounded_image)))
                .into(imgAnak)

            itemBody.setOnClickListener { onClick(item) }
        }

    }

}
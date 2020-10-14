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
import java.text.SimpleDateFormat

class DaftarAnakAdapter(private val context: Context, val onClick: (Anak)->Unit): RecyclerView.Adapter<DaftarAnakAdapter.ViewHolder>() {

    private val listAnak = mutableListOf<Anak>()
    
    fun setData(listAnak: List<Anak>) {
        this.listAnak.clear()
        this.listAnak.addAll(listAnak)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_anak, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listAnak[position])
    }

    override fun getItemCount(): Int = listAnak.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val itemBody = view.findViewById<CardView>(R.id.item_body)
        private val imgAnak = view.findViewById<ImageView>(R.id.img_anak)
        private val tvName = view.findViewById<TextView>(R.id.tv_name)
        private val tvTtl = view.findViewById<TextView>(R.id.tv_ttl)
        private val tvAddress = view.findViewById<TextView>(R.id.tv_address)
        private val tvCreatedAt = view.findViewById<TextView>(R.id.tv_created_at)
        private val tvUpdatedAt = view.findViewById<TextView>(R.id.tv_updated_at)

        fun bindItem(item: Anak) {
            tvName.text = item.fullName
            tvTtl.text = "${item.birthPlace}, ${item.dateOfBirth}"
            tvAddress.text = item.address
            tvCreatedAt.text = "Dibuat pada ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(item.createdAt)}"
            tvUpdatedAt.text = "Terakhir diperbarui ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(item.updatedAt)}"
            GlideApp.with(context)
                .asBitmap()
                .load(item.photoUrl)
                .transform(CenterCrop(), RoundedCorners(context.resources.getDimensionPixelSize(R.dimen.rounded_image)))
                .into(imgAnak)

            itemBody.setOnClickListener { onClick(item) }
        }

    }

}
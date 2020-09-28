package id.trydev.kupompong.adapter

import android.content.Context
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import id.trydev.kupompong.R
import id.trydev.kupompong.model.PilihanMateri
import id.trydev.kupompong.utils.GlideApp

class PilihanAdapter(private val context: Context, val onClick: (PilihanMateri, View) -> Unit): RecyclerView.Adapter<PilihanAdapter.ViewHolder>() {

    private val listPilihan = mutableListOf<PilihanMateri>()
    fun setData(listPilihan: List<PilihanMateri>) {
        this.listPilihan.clear()
        this.listPilihan.addAll(listPilihan)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pilihan_jawaban_materi, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listPilihan[position])
    }

    override fun getItemCount(): Int = listPilihan.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val tvCaption = view.findViewById<TextView>(R.id.tv_caption)
        private val btnMore = view.findViewById<ImageButton>(R.id.btn_more)
        private val inputImage = view.findViewById<ImageView>(R.id.iv_input_img)

        fun bindItem(item: PilihanMateri) {
            tvCaption.text = item.caption
            inputImage.setPadding(0)
            GlideApp.with(context)
                .asBitmap()
                .load(item.imgUrl)
                .transform(CenterCrop(), RoundedCorners(context.resources.getDimensionPixelSize(R.dimen.cover_materi)))
                .into(inputImage)

            btnMore.setOnClickListener {
                onClick(item, it)
            }
        }

    }

}
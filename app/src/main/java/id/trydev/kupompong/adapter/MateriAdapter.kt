package id.trydev.kupompong.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import id.trydev.kupompong.R
import id.trydev.kupompong.model.Materi

class MateriAdapter(private val context: Context, val onClick: (Materi)->Unit): RecyclerView.Adapter<MateriAdapter.ViewHolder>() {

    private val listMateri = mutableListOf<Materi>()
    fun setData(listMateri: List<Materi>) {
        this.listMateri.clear()
        this.listMateri.addAll(listMateri)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_materi, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listMateri[position])
    }

    override fun getItemCount(): Int = this.listMateri.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val itemBody = view.findViewById<CardView>(R.id.item_body)
        private val tvTitle = view.findViewById<TextView>(R.id.tv_title)

        fun bindItem(item: Materi) {

            tvTitle.text = item.judul
            itemBody.setOnClickListener {
                onClick(item)
            }

        }

    }


}
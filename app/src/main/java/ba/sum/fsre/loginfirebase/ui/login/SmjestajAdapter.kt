package ba.sum.fsre.loginfirebase.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ba.sum.fsre.loginfirebase.databinding.ItemSmjestajBinding
import ba.sum.fsre.loginfirebase.entity.Smjestaj

class SmjestajAdapter(private val onClick: (Smjestaj) -> Unit) :
    RecyclerView.Adapter<SmjestajAdapter.VH>() {

    private val items = mutableListOf<Smjestaj>()

    fun submit(list: List<Smjestaj>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemSmjestajBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemSmjestajBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvName.text = item.naziv
        holder.b.tvCity.text = "${item.grad} • ${item.adresa}"
        holder.b.tvDesc.text = item.opis
        holder.b.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
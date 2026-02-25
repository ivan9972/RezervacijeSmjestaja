package ba.sum.fsre.loginfirebase.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ba.sum.fsre.loginfirebase.databinding.ItemSobaBinding
import ba.sum.fsre.loginfirebase.entity.Soba

class SobaAdapter(private val onClick: (Soba) -> Unit) :
    RecyclerView.Adapter<SobaAdapter.VH>() {

    private val items = mutableListOf<Soba>()

    fun submit(list: List<Soba>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemSobaBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemSobaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = items[position]
        holder.b.tvTip.text = "${s.tip} • Soba ${s.brojSobe}"
        holder.b.tvInfo.text = "Kapacitet: ${s.kapacitet}"
        holder.b.tvPrice.text = "%.2f € / noć".format(s.cijenaPoNoci)
        holder.b.root.setOnClickListener { onClick(s) }
    }

    override fun getItemCount(): Int = items.size
}
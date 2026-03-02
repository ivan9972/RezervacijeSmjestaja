package ba.sum.fsre.loginfirebase.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ba.sum.fsre.loginfirebase.databinding.ItemUserBinding
import ba.sum.fsre.loginfirebase.entity.Korisnik

class UserAdapter(
    private val onDelete: (Korisnik) -> Unit
) : RecyclerView.Adapter<UserAdapter.VH>() {

    private val items = mutableListOf<Korisnik>()

    fun submit(list: List<Korisnik>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemUserBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val u = items[position]
        holder.b.tvEmail.text = u.email
        holder.b.tvName.text = "${u.ime} ${u.prezime} • ${u.role}"
        holder.b.btnDelete.isEnabled = u.role != "ADMIN"
        holder.b.btnDelete.setOnClickListener { onDelete(u) }
    }

    override fun getItemCount(): Int = items.size
}
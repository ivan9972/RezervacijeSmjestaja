package ba.sum.fsre.loginfirebase.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ba.sum.fsre.loginfirebase.databinding.ItemAdminUserBinding
import ba.sum.fsre.loginfirebase.entity.Korisnik
import java.util.Locale

class AdminUsersAdapter(
    private val onDelete: (userId: Int) -> Unit
) : RecyclerView.Adapter<AdminUsersAdapter.VH>() {

    private val items = mutableListOf<Korisnik>()

    fun submit(list: List<Korisnik>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemAdminUserBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val k = items[position]

        val fullName = "${k.ime} ${k.prezime}".trim()
        val fallback = k.email.substringBefore("@")
            .replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
            }

        holder.b.tvName.text = if (fullName.isNotBlank()) fullName else fallback
        holder.b.tvEmail.text = k.email
        holder.b.tvId.text = "ID: ${k.id}"
        holder.b.btnDelete.setOnClickListener { onDelete(k.id) }
    }

    override fun getItemCount(): Int = items.size
}
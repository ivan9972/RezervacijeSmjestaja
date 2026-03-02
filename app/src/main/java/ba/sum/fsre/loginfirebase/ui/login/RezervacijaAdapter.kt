package ba.sum.fsre.loginfirebase.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ba.sum.fsre.loginfirebase.databinding.ItemRezervacijaBinding
import ba.sum.fsre.loginfirebase.entity.Rezervacija
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RezervacijaAdapter(
    private val isAdmin: Boolean,
    private val onAction: (Rezervacija) -> Unit
) : ListAdapter<Rezervacija, RezervacijaAdapter.VH>(DIFF) {

    class VH(val b: ItemRezervacijaBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemRezervacijaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = getItem(position)

        val df = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        holder.b.tvTitle.text = "Rezervacija #${r.id} • Soba ${r.sobaId}"
        holder.b.tvDates.text = "${df.format(Date(r.datumDolaska))} – ${df.format(Date(r.datumOdlaska))}"
        holder.b.tvPrice.text = String.format(Locale.getDefault(), "%.2f €", r.ukupnaCijena)
        holder.b.tvStatus.text = r.status

        val isCancelled = r.status.equals("CANCELLED", ignoreCase = true)

        if (isAdmin) {
            holder.b.btnAction.text = "Otkaži (obriši)"
            holder.b.btnAction.isEnabled = true
            holder.b.btnAction.alpha = 1f
            holder.b.btnAction.setOnClickListener { onAction(r) }
        } else {

            val canCancel = !isCancelled
            holder.b.btnAction.isEnabled = canCancel
            holder.b.btnAction.alpha = if (canCancel) 1f else 0.4f
            holder.b.btnAction.text = if (isCancelled) "Otkazano" else "Otkaži rezervaciju"
            holder.b.btnAction.setOnClickListener { if (canCancel) onAction(r) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Rezervacija>() {
            override fun areItemsTheSame(oldItem: Rezervacija, newItem: Rezervacija) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Rezervacija, newItem: Rezervacija) = oldItem == newItem
        }
    }
}
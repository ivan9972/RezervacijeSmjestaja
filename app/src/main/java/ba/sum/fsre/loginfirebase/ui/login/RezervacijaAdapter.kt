package ba.sum.fsre.loginfirebase.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ba.sum.fsre.loginfirebase.databinding.ItemRezervacijaBinding
import ba.sum.fsre.loginfirebase.entity.Rezervacija
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RezervacijaAdapter(
    private val onCancel: (Rezervacija) -> Unit,
    private val onDelete: (Rezervacija) -> Unit
) : RecyclerView.Adapter<RezervacijaAdapter.VH>() {

    private val items = mutableListOf<Rezervacija>()
    private val fmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun submit(list: List<Rezervacija>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemRezervacijaBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemRezervacijaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = items[position]

        holder.b.tvTitle.text = "Rezervacija #${r.id} • SobaId: ${r.sobaId}"
        holder.b.tvDates.text = "${fmt.format(Date(r.datumDolaska))} — ${fmt.format(Date(r.datumOdlaska))}"
        holder.b.tvPrice.text = "%.2f €".format(r.ukupnaCijena)
        holder.b.tvStatus.text = r.status

        val isCancelled = r.status.equals("CANCELLED", ignoreCase = true)

        holder.b.btnCancel.isEnabled = !isCancelled
        holder.b.btnCancel.text = if (isCancelled) "Otkazano" else "Otkaži rezervaciju"
        holder.b.btnCancel.setOnClickListener { onCancel(r) }

        holder.b.btnDelete.isEnabled = isCancelled
        holder.b.btnDelete.setOnClickListener { onDelete(r) }
    }

    override fun getItemCount(): Int = items.size
}
package edu.co.icesi.unistyle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import edu.co.icesi.unistyle.R
import edu.co.icesi.unistyle.databinding.ItemEstablishmentBinding
import edu.co.icesi.unistyle.domain.model.Establishment

class EstablishmentAdapter(var establishments: ArrayList<Establishment?> = arrayListOf()): Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_establishment, parent, false)
        return EstablishmentViewHolder(root)
    }

    override fun getItemCount(): Int {
        return establishments.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder){
            is EstablishmentViewHolder ->{
                holder.nameTV.text = establishments[position]?.name
                holder.addressTV.text = establishments[position]?.address
            }
        }
    }

}

class EstablishmentViewHolder(root: View): ViewHolder(root) {
    private val binding = ItemEstablishmentBinding.bind(root)
    val nameTV = binding.nameTV
    val addressTV = binding.addressTV
}
package com.example.agenda.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.agenda.R
import com.example.agenda.models.Contactos
import com.google.firebase.database.FirebaseDatabase

class AdapterContacto(private val contactos: ArrayList<Contactos>, private val context: Context) :
    RecyclerView.Adapter<AdapterContacto.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val apellidos: TextView = itemView.findViewById(R.id.tvApellidos)
        val telefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val fechaCumple: TextView = itemView.findViewById(R.id.tvFechaCumple)
        val imagen: ImageView = itemView.findViewById(R.id.ivImagenContacto)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contactos, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contacto = contactos[position]
        holder.nombre.text = contacto.nombre
        holder.apellidos.text = contacto.apellidos
        holder.telefono.text = contacto.telefono
        holder.fechaCumple.text = "Cumpleaños: ${contacto.cumpleanos}"

        val imagenBytes = Base64.decode(contacto.imagen, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.size)
        holder.imagen.setImageBitmap(bitmap)

        holder.btnEliminar.setOnClickListener {
            eliminar(contacto.id, context)
            contactos.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun eliminar(id: String?, context: Context) {
        val db = FirebaseDatabase.getInstance()
        val contactosRef = db.getReference("Contactos")

        id?.let {
            contactosRef.child(it).removeValue().addOnSuccessListener {
                Toast.makeText(context, "Contacto eliminado exitosamente", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Error al eliminar el contacto", Toast.LENGTH_LONG).show()
            }
        }
    }
}

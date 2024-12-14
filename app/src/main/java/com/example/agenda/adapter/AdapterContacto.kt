package com.example.agenda.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.agenda.models.Contactos
import com.example.agenda.R
import com.google.firebase.database.FirebaseDatabase

class AdapterContacto(private val contactos: ArrayList<Contactos>, private val context: Context) :
    RecyclerView.Adapter<AdapterContacto.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val apellidos: TextView = itemView.findViewById(R.id.tvApellidos)
        val telefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val fechaCumple: TextView = itemView.findViewById(R.id.tvFechaCumple) // Nuevo campo

        // Botón para eliminar
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
        val a_contacto = contactos[position]
        holder.nombre.text = a_contacto.nombre
        holder.apellidos.text = a_contacto.apellidos
        holder.telefono.text = a_contacto.telefono
        holder.fechaCumple.text = "Cumpleaños: ${a_contacto.cumpleanos}" // Mostrar fecha de cumpleaños

        // Función del botón eliminar
        holder.btnEliminar.setOnClickListener {
            // Llamada al método eliminar atribuyéndole el id y el context
            eliminar(a_contacto.id, context)
            // Eliminar la posición actual y actualizar la vista
            contactos.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // Función que elimina un producto de la base de datos en Firebase dado su ID.
    private fun eliminar(id: String?, context: Context) {
        // Obtiene la instancia de la base de datos de Firebase.
        val db = FirebaseDatabase.getInstance()
        // Referencia a la rama "Productos" en la base de datos.
        val productosRef = db.getReference("Contactos")

        // Elimina el nodo correspondiente al ID proporcionado.
        productosRef.child(id!!).removeValue().addOnSuccessListener {
            // Si la eliminación es exitosa, muestra un mensaje al usuario.
            Toast.makeText(context, "Producto eliminado exitosamente", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Error al eliminar el producto", Toast.LENGTH_LONG).show()
        }
    }
}

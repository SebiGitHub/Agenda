package com.example.agenda.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.media3.common.util.Log
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
        val fechaCumple: TextView = itemView.findViewById(R.id.tvFechaCumple)

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
        val aContacto = contactos[position]
        holder.nombre.text = aContacto.nombre
        holder.apellidos.text = aContacto.apellidos
        holder.telefono.text = aContacto.telefono
        holder.fechaCumple.text = "Cumpleaños: ${aContacto.cumpleanos}"

        // Obtener referencia al LinearLayout
        val fondoLayout = holder.itemView.findViewById<LinearLayout>(R.id.imgDefectoFondo)

        // Configurar imagen como fondo del LinearLayout
        try {
            if (!aContacto.imagen.isNullOrEmpty()) {
                val uriImagen = Uri.parse(aContacto.imagen)
                val inputStream = fondoLayout.context.contentResolver.openInputStream(uriImagen)
                val drawable = Drawable.createFromStream(inputStream, null)
                fondoLayout.background = drawable
            }
        } catch (e: Exception) {
            Log.e("Error al cargar fondo", "Error: ${e.message}")
        }


        // Botón eliminar
        holder.btnEliminar.setOnClickListener {
            eliminar(aContacto.id, context)
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

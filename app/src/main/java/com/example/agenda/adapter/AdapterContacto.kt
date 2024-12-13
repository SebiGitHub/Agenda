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

class AdapterContacto (private val productos: ArrayList<Contactos>, private val context: Context) :
    RecyclerView.Adapter<AdapterContacto.ViewHolder>() {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nombre: TextView = itemView.findViewById(R.id.tvNombre)
            val precio: TextView = itemView.findViewById(R.id.tvPrecio)
            val descripcion: TextView = itemView.findViewById(R.id.tvDescription)

            //Agrego el boton para eliminar
            val btnEliminar : Button = itemView.findViewById(R.id.btnEliminar)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_productos, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.nombre
        holder.precio.text = producto.apellidos.toString()
        holder.descripcion.text = producto.telefono

        //Funcion del boton
        holder.btnEliminar.setOnClickListener {
            //LLamada al metodo eliminar atribuyendole el id y el context
            eliminar(producto.id, context)
            //Eliminar la posicion actual y mostrarlo
            productos.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // Función que elimina un producto de la base de datos en Firebase dado su ID.
    private fun eliminar(id: String?, context: Context) {
        // Obtiene la instancia de la base de datos de Firebase.
        val db = FirebaseDatabase.getInstance()
        // Referencia a la rama "Productos" en la base de datos.
        val productos_ref = db.getReference("Productos")

        // Elimina el nodo correspondiente al ID proporcionado.
        productos_ref.child(id!!).removeValue().addOnSuccessListener {
            // Si la eliminación es exitosa, muestra un mensaje al usuario.
            Toast.makeText(context, "Producto eliminado exitosamente", Toast.LENGTH_LONG).show()
        }
    }
}
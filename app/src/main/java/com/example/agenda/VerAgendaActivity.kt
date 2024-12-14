package com.example.agenda

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agenda.adapter.AdapterContacto
import com.example.agenda.models.Contactos
import com.example.agenda.databinding.ActivityVerAgendaBinding
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerAgendaActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityVerAgendaBinding
    //Lista de productos
    private lateinit var contactosList: ArrayList<Contactos>
    //RecyclerView
    private lateinit var contactosRecyclerView: RecyclerView
    //Firebase
    private lateinit var database: DatabaseReference
    //Adapter
    private lateinit var adapterContactos: AdapterContacto


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVerAgendaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactosRecyclerView = binding.rvContactos
        contactosRecyclerView.layoutManager = LinearLayoutManager(this)
        contactosRecyclerView.setHasFixedSize(true)

        // Inicializa el adaptador sin que la lista esté vacía
        contactosList = arrayListOf() // Lista vacía que se llenará con los contactos de Firebase
        adapterContactos = AdapterContacto(contactosList, this)
        contactosRecyclerView.adapter = adapterContactos

        getProductos()


        //Accion para volver al menu
        binding.btnVolver.setOnClickListener {
            //Funcion que vuelva a la actividad anterior
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getProductos() {
        // Referencia a la base de datos de Firebase
        database = FirebaseDatabase.getInstance().getReference("Contactos")

        // Realiza una consulta de los datos
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    contactosList.clear()  // Limpiar la lista de contactos actual
                    for (contactosSnapshot in snapshot.children) {
                        // Obtenemos cada contacto y lo agregamos a la lista
                        val contacto = contactosSnapshot.getValue(Contactos::class.java)
                        if (contacto != null) {
                            contactosList.add(contacto)  // Agregar el contacto
                        }
                    }

                    // Verificamos si la lista no está vacía antes de actualizar el adaptador
                    if (contactosList.isNotEmpty()) {
                        adapterContactos.notifyDataSetChanged()  // Notificar al adaptador
                    } else {
                        // Si no hay contactos, muestra un mensaje en la UI
                        Toast.makeText(this@VerAgendaActivity, "No hay contactos disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Si no hay contactos, muestra un mensaje
                    Toast.makeText(this@VerAgendaActivity, "No hay datos en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Si ocurre un error con la consulta, muestra un mensaje
                Toast.makeText(this@VerAgendaActivity, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show()
            }
        })
    }


}

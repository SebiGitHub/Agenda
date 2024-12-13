package com.example.agenda

import android.os.Bundle
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

        contactosList = arrayListOf<Contactos>()

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
        //Ruta de productos
        database = FirebaseDatabase.getInstance().getReference("Contactos")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    contactosList.clear()
                    for (productosSnapshot in snapshot.children) {
                        val contacto = productosSnapshot.getValue(Contactos::class.java)
                        contactosList.add(contacto!!)
                    }
                    adapterContactos.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}

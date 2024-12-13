package com.example.agenda

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agenda.models.Contactos
import com.example.agenda.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityMainBinding

    //Firebase database
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicio de la base de datos y ruta de almacenamiento
        database = FirebaseDatabase.getInstance().getReference("Contactos")

        //Obetener datos e insertar
        binding.btnGuardar.setOnClickListener {
            //Obetener los datos del formulario
            val nombre = binding.etNombreContacto.text.toString()
            val precio = binding.etApellidosContacto.text.toString()
            val telefono = binding.etTelefonoContacto.text.toString()

            //Generar el id de forma que sea unico
            val id = database.child("Productos").push().key

            if(nombre.isEmpty()) {
                binding.etNombreContacto.error = "Nombre requerido"
                return@setOnClickListener
            }

            if(precio.isEmpty()) {
                binding.etApellidosContacto.error = "Apellidos requerido"
                return@setOnClickListener
            }

            if(telefono.isEmpty()) {
                binding.etTelefonoContacto.error = "Teléfono requerido"
                return@setOnClickListener
            }


            val contactos = Contactos(id, nombre, precio, telefono)
            database.child(id!!).setValue(contactos)
                .addOnSuccessListener {
                    Toast.makeText(this, "Contacto nuevo añadido", Toast.LENGTH_LONG).show()
                    binding.etNombreContacto.setText("")
                    binding.etApellidosContacto.setText("")
                    binding.etTelefonoContacto.setText("")
                }.addOnFailureListener {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }
        }

        binding.btnVer.setOnClickListener {
            val intent = Intent(this, VerAgendaActivity::class.java)
            startActivity(intent)
        }
    }
}
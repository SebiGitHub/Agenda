package com.example.agenda

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agenda.models.Contactos
import com.example.agenda.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    // Firebase database
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicio de la base de datos y ruta de almacenamiento
        database = FirebaseDatabase.getInstance().getReference("Contactos")

        // Configuración del DatePickerDialog
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        binding.etCumpleanioContacto.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val fechaSeleccionada = dateFormat.format(calendar.time)
                binding.etCumpleanioContacto.setText(fechaSeleccionada)
            }, year, month, day)

            datePickerDialog.show()
        }

        // Obtener datos e insertar
        binding.btnGuardar.setOnClickListener {
            // Obtener los datos del formulario
            val nombre = binding.etNombreContacto.text.toString()
            val apellidos = binding.etApellidosContacto.text.toString()
            val telefono = binding.etTelefonoContacto.text.toString()
            val fechaNacimiento = binding.etCumpleanioContacto.text.toString()

            // Generar el id de forma que sea único
            val id = database.child("Contactos").push().key

            // Validaciones
            if (nombre.isEmpty()) {
                binding.etNombreContacto.error = "Nombre requerido"
                return@setOnClickListener
            }

            if (apellidos.isEmpty()) {
                binding.etApellidosContacto.error = "Apellidos requerido"
                return@setOnClickListener
            }

            if (telefono.isEmpty()) {
                binding.etTelefonoContacto.error = "Teléfono requerido"
                return@setOnClickListener
            }

            if (fechaNacimiento.isEmpty()) {
                binding.etCumpleanioContacto.error = "Fecha de cumpleaños requerida"
                return@setOnClickListener
            }

            // Crear objeto Contactos
            val contactos = Contactos(id, nombre, apellidos, telefono, fechaNacimiento)

            // Guardar en Firebase
            database.child(id!!).setValue(contactos)
                .addOnSuccessListener {
                    Toast.makeText(this, "Contacto nuevo añadido", Toast.LENGTH_LONG).show()
                    binding.etNombreContacto.setText("")
                    binding.etApellidosContacto.setText("")
                    binding.etTelefonoContacto.setText("")
                    binding.etCumpleanioContacto.setText("")
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

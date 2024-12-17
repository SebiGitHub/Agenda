package com.example.agenda

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.agenda.databinding.ActivityMainBinding
import com.example.agenda.models.Contactos
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    // Firebase Realtime Database
    private lateinit var database: DatabaseReference

    // Firebase Storage
    private lateinit var storage: FirebaseStorage

    // Uri para almacenar la imagen seleccionada
    private var imagenUri: Uri? = null

    // Launcher para seleccionar la imagen
    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imagenUri = uri
                binding.ivImagen.setImageURI(uri) // Mostrar la imagen seleccionada en el ImageView
            } else {
                Toast.makeText(this, "No seleccionaste ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Database y Storage
        database = FirebaseDatabase.getInstance().getReference("Contactos")
        storage = FirebaseStorage.getInstance()

        // Configurar el DatePicker
        configurarDatePicker()

        // Selección de imagen
        binding.ivImagen.setOnClickListener {
            selectImageLauncher.launch("image/*") // Permite al usuario seleccionar imágenes
        }

        // Guardar contacto en Firebase
        binding.btnGuardar.setOnClickListener {
            guardarContacto()
        }

        // Botón para ver contactos guardados
        binding.btnVer.setOnClickListener {
            val intent = Intent(this, VerAgendaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarDatePicker() {
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
    }

    private fun guardarContacto() {
        val nombre = binding.etNombreContacto.text.toString()
        val apellidos = binding.etApellidosContacto.text.toString()
        val telefono = binding.etTelefonoContacto.text.toString()
        val fechaNacimiento = binding.etCumpleanioContacto.text.toString()

        // Validación de campos vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || fechaNacimiento.isEmpty() || imagenUri == null) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Subir la imagen a Firebase Storage
        imagenUri?.let { uri ->
            val storageRef = storage.reference.child("images/${System.currentTimeMillis()}.jpg")
            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        // Guardar la URL de la imagen en Firebase Database
                        guardarEnRealtimeDatabase(nombre, apellidos, telefono, fechaNacimiento, downloadUrl.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun guardarEnRealtimeDatabase(
        nombre: String,
        apellidos: String,
        telefono: String,
        fechaNacimiento: String,
        imagenUrl: String
    ) {
        val id = database.push().key // Generar una clave única
        val contacto = Contactos(
            id = id,
            nombre = nombre,
            apellidos = apellidos,
            telefono = telefono,
            cumpleanos = fechaNacimiento,
            imagen = imagenUrl // Guardar la URL de la imagen
        )

        id?.let {
            database.child(it).setValue(contacto)
                .addOnSuccessListener {
                    Toast.makeText(this, "Contacto guardado correctamente", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar el contacto", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun limpiarCampos() {
        binding.etNombreContacto.setText("")
        binding.etApellidosContacto.setText("")
        binding.etTelefonoContacto.setText("")
        binding.etCumpleanioContacto.setText("")
        binding.ivImagen.setImageResource(R.drawable.ic_launcher_foreground)
        imagenUri = null
    }
}

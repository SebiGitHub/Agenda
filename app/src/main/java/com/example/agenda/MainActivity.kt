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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    // Firebase database
    private lateinit var database: DatabaseReference

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

        database = FirebaseDatabase.getInstance().getReference("Contactos")

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // DatePicker para la fecha de cumpleaños
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

        // Seleccionar imagen
        binding.ivImagen.setOnClickListener {
            selectImageLauncher.launch("image/*") // Abre la galería para seleccionar imágenes
        }

        // Guardar contacto
        // Guardar contacto
        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombreContacto.text.toString()
            val apellidos = binding.etApellidosContacto.text.toString()
            val telefono = binding.etTelefonoContacto.text.toString()
            val fechaNacimiento = binding.etCumpleanioContacto.text.toString()

            // Verificar que todos los campos estén completos
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
            if (imagenUri == null) {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto contacto
            val id = database.push().key  // Generar ID único

            val contactos = Contactos(
                id = id,
                nombre = nombre,
                apellidos = apellidos,
                telefono = telefono,
                cumpleanos = fechaNacimiento,
                imagen = imagenUri.toString() // Guardar URI como String
            )

            // Guardar en la base de datos de Firebase
            id?.let {
                database.child(it).setValue(contactos)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Contacto añadido con éxito", Toast.LENGTH_LONG).show()
                        // Limpiar campos
                        binding.etNombreContacto.setText("")
                        binding.etApellidosContacto.setText("")
                        binding.etTelefonoContacto.setText("")
                        binding.etCumpleanioContacto.setText("")
                        binding.ivImagen.setImageResource(R.drawable.ic_launcher_foreground)
                        imagenUri = null
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error al guardar el contacto", Toast.LENGTH_LONG).show()
                    }
            }
        }


        binding.btnVer.setOnClickListener {
            val intent = Intent(this, VerAgendaActivity::class.java)
            startActivity(intent)
        }
    }
}

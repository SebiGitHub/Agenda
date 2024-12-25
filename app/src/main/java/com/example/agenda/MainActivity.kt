package com.example.agenda

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.agenda.databinding.ActivityMainBinding
import com.example.agenda.models.Contactos
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private var imagenUri: Uri? = null

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imagenUri = uri
                binding.ivImagen.setImageURI(uri)
            } else {
                Toast.makeText(this, "No seleccionaste ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Contactos")

        configurarDatePicker()

        // Limitar el formato de fecha en el EditText para que sea "dd/MM/yyyy"
        binding.etCumpleanioContacto.filters = arrayOf(
            InputFilter.LengthFilter(10) // Permite hasta 10 caracteres (formato dd/MM/yyyy)
        )

        binding.ivImagen.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnGuardar.setOnClickListener {
            guardarContacto()
        }

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

    private fun convertirImagenABase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun validarTelefono(telefono: String): Boolean {
        // Verificar que el teléfono tenga exactamente 9 dígitos
        return telefono.length == 9 && telefono.all { it.isDigit() }
    }

    private fun validarFecha(fecha: String): Boolean {
        // Validar que la fecha tenga el formato "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            dateFormat.parse(fecha)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun guardarContacto() {
        val nombre = binding.etNombreContacto.text.toString()
        val apellidos = binding.etApellidosContacto.text.toString()
        val telefono = binding.etTelefonoContacto.text.toString()
        val fechaNacimiento = binding.etCumpleanioContacto.text.toString()

        // Validación de campos vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || imagenUri == null) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar teléfono
        if (!validarTelefono(telefono)) {
            Toast.makeText(this, "El teléfono debe tener 9 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar fecha
        if (!validarFecha(fechaNacimiento)) {
            Toast.makeText(this, "La fecha debe tener el formato dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            return
        }

        val imagenBase64 = convertirImagenABase64(imagenUri!!)

        if (imagenBase64 == null) {
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val id = database.push().key
        val contacto = Contactos(
            id = id,
            nombre = nombre,
            apellidos = apellidos,
            telefono = telefono,
            cumpleanos = fechaNacimiento,
            imagen = imagenBase64
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

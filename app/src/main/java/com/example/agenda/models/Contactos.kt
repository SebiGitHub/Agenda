package com.example.agenda.models

import java.util.Date

data class Contactos(
    val id:String? = null,
    val nombre:String? = null,
    val apellidos:String? = null,
    val telefono:String? = null,
    var cumpleanos: String? = null,
    var imagen: String? = null
)

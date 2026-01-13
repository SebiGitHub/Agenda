# App Agenda (Android)

## Qué es
Agenda es una aplicación diseñada para gestionar entradas — ya sean contactos, citas, tareas o eventos — de forma organizada con autenticación de usuarios y almacenamiento en la nube mediante Firebase. Permite crear, consultar, modificar y eliminar elementos; en otras palabras, implementa operaciones CRUD básicas (Create, Read, Update, Delete).

## Para qué sirve
Agenda sirve como herramienta personal o de pequeño equipo para llevar un registro ordenado de información recurrente: contactos, reuniones, pendientes, citas u otros datos útiles. Facilita mantener un histórico accesible y editable, lo que ayuda a no perder información importante y organizar tareas o contactos de forma clara.


## Stack
- Kotlin
- Android Studio
- Firebase (Auth + base de datos, Realtime Database)
- (Opcional) Material Design

## Features
- Registro / inicio de sesión de usuarios
- CRUD de contactos: alta, edición y eliminación
- Guardado en la nube por usuario
- Validaciones básicas y feedback en UI

## Capturas/GIF
<img width="396" height="871" alt="image" src="https://github.com/user-attachments/assets/eebd218a-4ea2-4c5a-8b27-76bcf6b5e3db" />
<img width="800" height="450" alt="image" src="https://github.com/user-attachments/assets/36dc248c-c99a-4c3c-9ac3-9cc1f66b5e46" />
<img width="800" height="391" alt="image" src="https://github.com/user-attachments/assets/87bd6420-c01f-4eca-991f-b71812236b0d" />

## Cómo ejecutar
1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Configura Firebase:
   - Crea un proyecto en Firebase
   - Añade una app Android con tu `applicationId`
   - Descarga `google-services.json` y colócalo en `app/`
4. Ejecuta en emulador o dispositivo

## Qué aprendí
- Integración real con Firebase (auth + persistencia)
- Estructurar una app Android con pantallas y flujo de usuario
- Manejo de estados y validaciones para evitar datos inconsistentes

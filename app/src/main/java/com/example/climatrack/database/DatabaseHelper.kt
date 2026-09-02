package com.example.climatrack.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "climatrack.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla usuarios
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                nombre TEXT NOT NULL,
                rol TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Tabla clientes
        db.execSQL(
            """
            CREATE TABLE clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                telefono TEXT,
                direccion TEXT,
                email TEXT
            )
            """.trimIndent()
        )

        // Tabla equipos
        db.execSQL(
            """
            CREATE TABLE equipos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo TEXT NOT NULL,
                tipo TEXT,
                marca TEXT,
                modelo TEXT,
                serial TEXT,
                capacidad TEXT,
                ubicacion TEXT,
                cliente_id INTEGER,
                estado TEXT,
                FOREIGN KEY(cliente_id) REFERENCES clientes(id)
            )
            """.trimIndent()
        )

        // Tabla ordenes
        db.execSQL(
            """
            CREATE TABLE ordenes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero TEXT NOT NULL,
                fecha TEXT,
                cliente_id INTEGER,
                equipo_id INTEGER,
                tecnico_id INTEGER,
                tipo_servicio TEXT,
                descripcion TEXT,
                estado TEXT,
                FOREIGN KEY(cliente_id) REFERENCES clientes(id),
                FOREIGN KEY(equipo_id) REFERENCES equipos(id),
                FOREIGN KEY(tecnico_id) REFERENCES usuarios(id)
            )
            """.trimIndent()
        )

        // Tabla mantenimientos
        db.execSQL(
            """
            CREATE TABLE mantenimientos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                orden_id INTEGER,
                fecha TEXT,
                diagnostico TEXT,
                trabajo_realizado TEXT,
                observaciones TEXT,
                recomendaciones TEXT,
                FOREIGN KEY(orden_id) REFERENCES ordenes(id)
            )
            """.trimIndent()
        )

        // Tabla repuestos
        db.execSQL(
            """
            CREATE TABLE repuestos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                codigo TEXT,
                unidad TEXT
            )
            """.trimIndent()
        )

        // Tabla detalle_repuestos
        db.execSQL(
            """
            CREATE TABLE detalle_repuestos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                mantenimiento_id INTEGER,
                repuesto_id INTEGER,
                cantidad INTEGER,
                FOREIGN KEY(mantenimiento_id) REFERENCES mantenimientos(id),
                FOREIGN KEY(repuesto_id) REFERENCES repuestos(id)
            )
            """.trimIndent()
        )

        // Tabla evidencias
        db.execSQL(
            """
            CREATE TABLE evidencias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                orden_id INTEGER,
                ruta_foto TEXT,
                fecha TEXT,
                FOREIGN KEY(orden_id) REFERENCES ordenes(id)
            )
            """.trimIndent()
        )

        // Tabla aprobaciones
        db.execSQL(
            """
            CREATE TABLE aprobaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                orden_id INTEGER,
                cliente TEXT,
                aceptado INTEGER,
                fecha TEXT,
                FOREIGN KEY(orden_id) REFERENCES ordenes(id)
            )
            """.trimIndent()
        )

        // Tabla ubicaciones
        db.execSQL(
            """
            CREATE TABLE ubicaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                orden_id INTEGER,
                latitud REAL,
                longitud REAL,
                fecha TEXT,
                FOREIGN KEY(orden_id) REFERENCES ordenes(id)
            )
            """.trimIndent()
        )

        // Usuario de prueba (tecnico01 / 123456)
        val values = ContentValues().apply {
            put("usuario", "tecnico01")
            put("password", "123456")
            put("nombre", "Técnico 01")
            put("rol", "Técnico")
        }
        val tecnicoId = db.insert("usuarios", null, values)

        // Mock data: Clientes
        val clienteId = db.insert("clientes", null, ContentValues().apply {
            put("nombre", "ACME S.A.S.")
            put("telefono", "555-0123")
            put("direccion", "Calle 123 #45-67")
            put("email", "contacto@acme.com")
        })

        // Mock data: Equipos
        val equipoId = db.insert("equipos", null, ContentValues().apply {
            put("codigo", "EQ-00015")
            put("tipo", "Split Pared")
            put("marca", "LG")
            put("modelo", "Dual Inverter 24K")
            put("serial", "LGD123456789")
            put("capacidad", "24000 BTU")
            put("ubicacion", "Oficina Principal")
            put("cliente_id", clienteId)
            put("estado", "OPERATIVO")
        })

        // Mock data: Ordenes
        db.insert("ordenes", null, ContentValues().apply {
            put("numero", "OT-00025")
            put("fecha", "18/08/2026")
            put("cliente_id", clienteId)
            put("equipo_id", equipoId)
            put("tecnico_id", tecnicoId)
            put("tipo_servicio", "PREVENTIVO")
            put("descripcion", "Mantenimiento preventivo general")
            put("estado", "PENDIENTE")
        })

        db.insert("ordenes", null, ContentValues().apply {
            put("numero", "OT-00026")
            put("fecha", "19/08/2026")
            put("cliente_id", clienteId)
            put("equipo_id", equipoId)
            put("tecnico_id", tecnicoId)
            put("tipo_servicio", "CORRECTIVO")
            put("descripcion", "Revisión fuga de gas")
            put("estado", "EN PROCESO")
        })

        // Mock data: Repuestos
        db.insert("repuestos", null, ContentValues().apply {
            put("nombre", "Filtro de aire")
            put("codigo", "RPT-001")
            put("unidad", "Unidad")
        })
        db.insert("repuestos", null, ContentValues().apply {
            put("nombre", "Capacitor 35 uF")
            put("codigo", "RPT-002")
            put("unidad", "Unidad")
        })
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Por ahora, en desarrollo, recreamos las tablas al subir de versión
        db.execSQL("DROP TABLE IF EXISTS ubicaciones")
        db.execSQL("DROP TABLE IF EXISTS aprobaciones")
        db.execSQL("DROP TABLE IF EXISTS evidencias")
        db.execSQL("DROP TABLE IF EXISTS detalle_repuestos")
        db.execSQL("DROP TABLE IF EXISTS repuestos")
        db.execSQL("DROP TABLE IF EXISTS mantenimientos")
        db.execSQL("DROP TABLE IF EXISTS ordenes")
        db.execSQL("DROP TABLE IF EXISTS equipos")
        db.execSQL("DROP TABLE IF EXISTS clientes")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    /**
     * Valida las credenciales del técnico contra la tabla usuarios.
     * Retorna el Usuario si las credenciales son correctas, o null si no.
     */
    fun validarUsuario(usuario: String, password: String): com.example.climatrack.models.Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, usuario, password, nombre, rol FROM usuarios WHERE usuario = ? AND password = ?",
            arrayOf(usuario, password)
        )

        var resultado: com.example.climatrack.models.Usuario? = null
        if (cursor.moveToFirst()) {
            resultado = com.example.climatrack.models.Usuario(
                id = cursor.getInt(0),
                usuario = cursor.getString(1),
                password = cursor.getString(2),
                nombre = cursor.getString(3),
                rol = cursor.getString(4)
            )
        }
        cursor.close()
        return resultado
    }
}
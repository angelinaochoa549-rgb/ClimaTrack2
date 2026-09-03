package com.example.climatrack.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "climatrack.db"
        private const val DATABASE_VERSION = 3
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
                tiempo_empleado TEXT,
                tecnico_nombre TEXT,
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
                observacion TEXT,
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
                ruta_firma TEXT,
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

    // --- MÉTODOS PARA EQUIPOS ---

    fun getEquipos(filtro: String = ""): List<com.example.climatrack.models.Equipo> {
        val lista = mutableListOf<com.example.climatrack.models.Equipo>()
        val db = readableDatabase
        val query = if (filtro.isEmpty()) {
            "SELECT e.id, e.codigo, e.tipo, e.marca, e.modelo, e.serial, c.nombre as cliente, e.estado " +
                    "FROM equipos e LEFT JOIN clientes c ON e.cliente_id = c.id"
        } else {
            "SELECT e.id, e.codigo, e.tipo, e.marca, e.modelo, e.serial, c.nombre as cliente, e.estado " +
                    "FROM equipos e LEFT JOIN clientes c ON e.cliente_id = c.id " +
                    "WHERE e.codigo LIKE ? OR e.serial LIKE ? OR e.modelo LIKE ?"
        }
        val args = if (filtro.isEmpty()) null else arrayOf("%$filtro%", "%$filtro%", "%$filtro%")

        db.rawQuery(query, args).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    lista.add(
                        com.example.climatrack.models.Equipo(
                            id = cursor.getInt(0),
                            codigo = cursor.getString(1),
                            tipo = cursor.getString(2),
                            marca = cursor.getString(3),
                            modelo = cursor.getString(4),
                            serie = cursor.getString(5),
                            cliente = cursor.getString(6) ?: "Sin Cliente",
                            estado = cursor.getString(7)
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return lista
    }

    fun getEquipoPorId(id: Int): com.example.climatrack.models.Equipo? {
        val db = readableDatabase
        val query = "SELECT e.id, e.codigo, e.tipo, e.marca, e.modelo, e.serial, c.nombre as cliente, e.estado " +
                "FROM equipos e LEFT JOIN clientes c ON e.cliente_id = c.id WHERE e.id = ?"
        db.rawQuery(query, arrayOf(id.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                return com.example.climatrack.models.Equipo(
                    id = cursor.getInt(0),
                    codigo = cursor.getString(1),
                    tipo = cursor.getString(2),
                    marca = cursor.getString(3),
                    modelo = cursor.getString(4),
                    serie = cursor.getString(5),
                    cliente = cursor.getString(6) ?: "Sin Cliente",
                    estado = cursor.getString(7)
                )
            }
        }
        return null
    }

    fun insertarEquipo(codigo: String, tipo: String, marca: String, modelo: String, serial: String, capacidad: String, ubicacion: String, clienteId: Int, estado: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("codigo", codigo)
            put("tipo", tipo)
            put("marca", marca)
            put("modelo", modelo)
            put("serial", serial)
            put("capacidad", capacidad)
            put("ubicacion", ubicacion)
            put("cliente_id", clienteId)
            put("estado", estado)
        }
        return db.insert("equipos", null, values)
    }

    // --- MÉTODOS PARA HISTORIAL Y MANTENIMIENTOS ---

    fun getHistorialMantenimientos(tipoFiltro: String = "TODOS"): List<com.example.climatrack.models.Mantenimiento> {
        val lista = mutableListOf<com.example.climatrack.models.Mantenimiento>()
        val db = readableDatabase
        var query = """
            SELECT m.id, o.numero, m.fecha, 'N/A' as hora, u.nombre as tecnico, o.tipo_servicio, m.trabajo_realizado
            FROM mantenimientos m
            JOIN ordenes o ON m.orden_id = o.id
            JOIN usuarios u ON o.tecnico_id = u.id
        """.trimIndent()

        if (tipoFiltro != "TODOS") {
            query += " WHERE o.tipo_servicio = '$tipoFiltro'"
        }

        db.rawQuery(query, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    lista.add(
                        com.example.climatrack.models.Mantenimiento(
                            id = cursor.getString(0),
                            orden = cursor.getString(1),
                            fecha = cursor.getString(2),
                            hora = cursor.getString(3),
                            tecnico = cursor.getString(4),
                            tipo = cursor.getString(5),
                            descripcion = cursor.getString(6)
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return lista
    }

    // --- MÉTODOS PARA REPUESTOS ---

    fun getRepuestosDisponibles(): List<com.example.climatrack.models.Repuesto> {
        val lista = mutableListOf<com.example.climatrack.models.Repuesto>()
        val db = readableDatabase
        db.rawQuery("SELECT id, nombre, codigo, unidad FROM repuestos", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    lista.add(
                        com.example.climatrack.models.Repuesto(
                            id = cursor.getInt(0),
                            nombre = cursor.getString(1),
                            codigo = cursor.getString(2),
                            unidad = cursor.getString(3),
                            cantidad = 0
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return lista
    }

    // --- MÉTODOS PARA CLIENTES ---

    fun getClientes(): List<com.example.climatrack.models.Cliente> {
        val lista = mutableListOf<com.example.climatrack.models.Cliente>()
        val db = readableDatabase
        db.rawQuery("SELECT id, nombre, telefono, direccion, email FROM clientes", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    lista.add(
                        com.example.climatrack.models.Cliente(
                            id = cursor.getInt(0),
                            nombre = cursor.getString(1),
                            telefono = cursor.getString(2),
                            direccion = cursor.getString(3),
                            email = cursor.getString(4)
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return lista
    }

    fun insertarCliente(nombre: String, telefono: String, direccion: String, email: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("telefono", telefono)
            put("direccion", direccion)
            put("email", email)
        }
        return db.insert("clientes", null, values)
    }
}
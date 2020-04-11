package mx.edu.ittepic.ladm_u3_practica1_ismaelcastaneda

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(context: Context?,
                name: String?,
                factory: SQLiteDatabase.CursorFactory?,
                version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        var consulta1 = "CREATE TABLE actividades (id_actividad INTEGER PRIMARY KEY, descripcion VARCHAR(2000), fechaCaptura DATE DEFAULT CURRENT_DATE, fechaEntrega DATE);"
        var consulta2 = "CREATE TABLE evidencias (idEvidencia INTEGER PRIMARY KEY, id_actividad INTEGER DEFAULT 1, foto BLOB, FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad));"
        db?.execSQL(consulta1)
        db?.execSQL(consulta2)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}
package mx.edu.ittepic.ladm_u3_practica1_ismaelcastaneda

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Actividades(d: String, fe: String, f: ByteArray) {

    var desc = d
    var fEntrega = fe
    var fCaptura = ""
    var foto = f
    var id = 0
    var nombreBaseDatos = "practica"
    var puntero : Context ?= null
    var error  = -1
    /* valores de error
    * -----------------
    * 1 = Error en tabla, no se creó o no se conecto a base de datos
    * 2 = Error, no se pudo insertar actividad
    * 3 = Error, no se pudo insertar evidencia
    * 4 = No se pudo realizar consulta o tabla vacía
    * 5 = No se encontró último ID
    * 6 = No borró evidencia
    * 7 = No borró actividad
    * 8 = No encontro el ID especificado
    * */

    fun asignarPuntero(p: Context){
        puntero = p
    }

    fun insertarActividad(): Boolean{
        error = -1
        try{
            /*val fActual = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formatted = fActual.format(formatter)*/
            var base1 = BaseDatos(puntero!!, nombreBaseDatos, null, 1)

            var insertarActividad = base1.writableDatabase
            var datosActividad = ContentValues()
            datosActividad.put("descripcion", desc)
            datosActividad.put("fechaEntrega", fEntrega)
            var resActividad = insertarActividad.insert("actividades", "id_actividad", datosActividad)

            if(resActividad.toInt() == -1){
                error = 2
                return false
            }
        }catch (e : SQLiteException){
            error = 1
            return false
        }
        return true
    }

    fun insertarEvidencia(idActividad: Int): Boolean{
        error = -1
        try{
            var base2 = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var insertarEvidencia = base2.writableDatabase
            var datosEvidencia = ContentValues()
            datosEvidencia.put("id_actividad", idActividad)
            datosEvidencia.put("foto", foto)
            var resEvidencia = insertarEvidencia.insert("evidencias", "idEvidencia", datosEvidencia)

            if(resEvidencia.toInt() == -1){
                error = 3
                return false
            }
        }catch (e : SQLiteException){
            error = 1
            return false
        }
        return true
    }

    /*fun obtenerUltimo(): Int{
        var id = -1
        error = -1

        try{
            var base = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var select = base.readableDatabase
            var columnas = arrayOf("id_actividad")
            var cursor = select.query("actividades", columnas, null, null, null, null, "id_actividad DESC LIMIT 1")
            if(cursor.moveToFirst()){
                id = cursor.getInt(0)
            }else{
                error = 5
            }
        }catch (e: SQLiteException){
            error = 1
        }
        return id
    }*/

    fun mostrarActividades(): ArrayList<Actividades>{
        var data = ArrayList<Actividades>()
        error = -1
        try{
            var baseAct = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var selectAct = baseAct.readableDatabase
            var columnasAct = arrayOf("*")

            var cursorAct = selectAct.query("actividades", columnasAct, null, null, null, null, null)

            /*var baseEvi = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var selectEvi = baseEvi.readableDatabase
            var columnasEvi = arrayOf("*")

            var cursorEvi = selectEvi.query("evidencias", columnasEvi, null, null, null, null, null)*/

            if(cursorAct.moveToFirst()){
                do{
                    var actTemp = Actividades(cursorAct.getString(1),
                        cursorAct.getString(3),
                        ByteArray(0))
                    actTemp.id = cursorAct.getInt(0)
                    actTemp.fCaptura = cursorAct.getString(2)
                    data.add(actTemp)
                }while (cursorAct.moveToNext())
            }
            else{
                error = 4
            }
        }catch (e: SQLiteException){
            error = 1
        }
        return data
    }

    fun mostrarEvidencias(id: String): ArrayList<ByteArray>{
        var data = ArrayList<ByteArray>()
        error = -1
        try{
            var base = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var select = base.readableDatabase
            var columnas = arrayOf("*")
            var selSQL = arrayOf(id)
            var cursor = select.query("evidencias", columnas, "id_actividad = ?", selSQL, null, null, null)

            if(cursor.moveToFirst()){
                do{
                    data.add(cursor.getBlob(2))
                }while (cursor.moveToNext())
            }else{
                error = 4
            }
        }catch (e: SQLiteException){
            error = 1
        }
        return data
    }

    fun eliminar(): Boolean{
        error = -1
        try{
            var idEliminar = arrayOf(id.toString())
            /*var base1 = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var eliminarEv = base1.writableDatabase

            var resEvidencia = eliminarEv.delete("evidencias",
                "id_actividad = ?", idEliminar)

            if(resEvidencia == 0){
                error = 6
                return false
            }*/

            var base2 = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var eliminarAct = base2.writableDatabase
            var resActividad = eliminarAct.delete("actividades",
                "id_actividad = ?", idEliminar)

            if(resActividad == 0){
                error = 7
                return false
            }

        }catch (e : SQLiteException){
            error = 1
            return false
        }

        return true
    }

    fun buscar(id: String): Actividades{
        var actEncontrada = Actividades("", "", ByteArray(0))

        error = -1
        try{
            var idBuscar = arrayOf(id)
            var base = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var select = base.readableDatabase
            var columnas = arrayOf("*")
            var cursor = select.query("actividades", columnas, "id_actividad = ?", idBuscar, null, null, null)

            if(cursor.moveToFirst()){
                actEncontrada.id = id.toInt()
                actEncontrada.desc = cursor.getString(1)
                actEncontrada.fCaptura = cursor.getString(2)
                actEncontrada.fEntrega = cursor.getString(3)
            }else{
                error = 8
            }
        }catch (e: SQLiteException){
            error = 1
        }

        return actEncontrada
    }

    fun dialogo(t: String, s: String){
        AlertDialog.Builder(puntero!!)
            .setTitle(t)
            .setMessage(s)
            .setPositiveButton("Ok"){ d, i -> }
            .show()
    }

}
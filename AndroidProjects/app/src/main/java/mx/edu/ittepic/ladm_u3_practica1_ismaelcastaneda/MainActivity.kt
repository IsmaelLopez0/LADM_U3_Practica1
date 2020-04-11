package mx.edu.ittepic.ladm_u3_practica1_ismaelcastaneda

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listaActividades()

        nuevaAct.setOnClickListener {
            var intent = Intent(this, addActividad::class.java)
            startActivity(intent)
        }
    }

    fun listaActividades(){
        try{
            var conexion = Actividades("","", ByteArray(0))
            conexion.asignarPuntero(this)
            var data = conexion.mostrarActividades()

            if(data.size == 0){
                if(conexion.error == 4){
                    dialogo("Error","No se pudo realizar consulta o tabla vacía")
                    return
                }
            }

            var total = data.size-1
            var vector = Array<String>(data.size, {""})
            listaID = ArrayList<String>()

            (0..total).forEach {
                var actividad = data[it]
                var item = "ID: ${actividad.id}\n ${actividad.desc}"
                vector[it] = item
                listaID.add(actividad.id.toString())
            }

            listaActividades.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vector)
            listaActividades.setOnItemClickListener { parent, view, position, id ->
                var act = Actividades("", "", ByteArray(0))
                act.asignarPuntero(this)
                var actEncontrada = act.buscar(listaID[position])
                if(act.error==8){
                    dialogo("Error","No se encontró ID")
                    return@setOnItemClickListener
                }
                actEncontrada.asignarPuntero(this)
                AlertDialog.Builder(this)
                    .setTitle("¿Qué desea hacer?")
                    .setMessage("Descripción: ${actEncontrada.desc}\nFecha Creación: ${actEncontrada.fCaptura}\nFecha Entrega: ${actEncontrada.fEntrega}\n")
                    .setPositiveButton("Añadir Ev."){ d, i ->
                        var intent = Intent(this, addEvidencia::class.java)
                        intent.putExtra("idActividad", listaID[position])
                        startActivity(intent)
                    }
                    .setNeutralButton("Eliminar"){ d, i ->
                        if( !actEncontrada.eliminar() ){
                            when(actEncontrada.error){
                                1 -> { dialogo("Error", "Error en tabla, no se creó o no se conecto a base de datos") }
                                6 -> { dialogo("Error", "No se borraron evidencias") }
                                7 -> { dialogo("Error", "No borró actividad") }
                            }
                        }else{ listaActividades() }
                    }
                    .setNegativeButton("Cancelar"){ d, i -> }
                    .show()
            }
        }catch (e: SQLiteException){
            dialogo("Error", e.message.toString())
        }
    }

    fun mensaje(s: String){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    fun dialogo(t: String, s: String){
        AlertDialog.Builder(this)
            .setTitle(t)
            .setMessage(s)
            .setPositiveButton("Ok"){ d, i -> }
            .show()
    }

}

package mx.edu.ittepic.ladm_u3_practica1_ismaelcastaneda

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_add_evidencia.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.zip.Inflater

class addEvidencia : AppCompatActivity() {

    private var fotoSel = 0
    var id = ""
    var listaID = ArrayList<ByteArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_evidencia)

        var bundle :Bundle ?=intent.extras
        id = bundle!!.getString("idActividad")!!

        listaEvidencias()

        buscar.setOnClickListener {
            var foto = Intent(Intent.ACTION_PICK)
            foto.type = "image/*"
            startActivityForResult(foto, fotoSel)
        }

        seleccionar.setOnClickListener {
            var bitmap = (img.drawable as BitmapDrawable).bitmap
            var ev = Actividades("", "", Utils.getBytes(bitmap))
            ev.asignarPuntero(this)
            var res = ev.insertarEvidencia(id.toInt())

            if(res){
                mensaje("Se capturó evidencia")
                finish()
            }else{
                when(ev.error) {
                    1 -> { dialogo("Error","Error en tablas, no se crearon o no se conecto a base de datos") }
                    3 -> { dialogo("Error","No se pudo insertar evidencia") }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == fotoSel && resultCode == Activity.RESULT_OK && data != null){
            var foto = data.data
            img.setImageURI(foto)
            seleccionar.isEnabled = true
        }
    }

    fun listaEvidencias(){
        try{
            var conexion = Actividades("","", ByteArray(0))
            conexion.asignarPuntero(this)
            var data = conexion.mostrarEvidencias(id)

            if(data.size == 0){
                if(conexion.error == 4){
                    dialogo("Error","No se pudo realizar consulta o tabla vacía")
                    return
                }
            }

            var total = data.size-1
            //var vector = Array<Bitmap>(data.size, { null })
            var vector = ArrayList<View>()
            listaID = ArrayList<ByteArray>()

            (0..total).forEach {
                //var evidencia = data[it]
                var bitmap = Utils.getImage(data[it])
                var layout = layoutInflater.inflate(R.layout.activity_add_evidencia, null)
                var linear = layout.findViewById<ImageView>(R.id.linearLayout1)
                linear.setImageBitmap(bitmap)
                vector.add(linear)
                listaID.add(data[it])
            }


            listaEvidencias.adapter = ArrayAdapter<View>(this, android.R.layout.simple_list_item_1, vector)
            /*listaActividades.setOnItemClickListener { parent, view, position, id ->
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
            }*/
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

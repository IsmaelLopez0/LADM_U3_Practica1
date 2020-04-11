package mx.edu.ittepic.ladm_u3_practica1_ismaelcastaneda

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_add_actividad.*

class addActividad : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_actividad)

        addActi.setOnClickListener {
            var act = Actividades(editText.text.toString(), editText2.text.toString(), ByteArray(0))
            act.asignarPuntero(this)
            var res = act.insertarActividad()

            if(res){
                //mensaje("Se añadió una nueva actividad")
                finish()
            }else{
                when(act.error) {
                    1 -> { dialogo("Error","Error en tablas, no se crearon o no se conecto a base de datos") }
                    2 -> { dialogo("Error","No se pudo insertar actividad") }
                    3 -> { dialogo("Error","No se pudo insertar evidencia") }
                }
            }
        }

        cancelar.setOnClickListener {
            finish()
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

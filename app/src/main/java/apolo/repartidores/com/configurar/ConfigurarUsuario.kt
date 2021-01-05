package apolo.repartidores.com.configurar

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import apolo.repartidores.com.R
import apolo.repartidores.com.MainActivity
import apolo.repartidores.com.utilidades.FuncionesUtiles
import apolo.repartidores.com.utilidades.SentenciasSQL
import apolo.repartidores.com.utilidades.Sincronizacion
import kotlinx.android.synthetic.main.activity_configurar_usuario.*
import java.lang.Exception

class ConfigurarUsuario : AppCompatActivity() {

    private lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar_usuario)

        inicializarBotones()
    }

    @SuppressLint("ShowToast", "SetTextI18n")
    private fun inicializarBotones(){
        ibtnUsuarioBuscar.setOnClickListener {
            traerUsuario()
        }
        ibtnUsuarioServidor.setOnClickListener {
            borrarUsuario()
        }
        ibtnUsuarioSincronizar.setOnClickListener {
            Toast.makeText(this, "Sincronizar", Toast.LENGTH_SHORT )
            if (usuarioGuardado()){
                try {
                    MainActivity.bd!!.execSQL("UPDATE usuarios SET NOMBRE = '" + etUsuNombre.text.toString().trim() + "'" +
                            ", VERSION = '" + etUsuVersion.text.toString().trim() + "' " +
                            "    WHERE LOGIN = '" + etUsuCodigo.text.toString().trim() + "' "  )
                } catch (e : Exception) {
                    e.message.toString()
                }
            } else {
                FuncionesUtiles.usuario["NOMBRE"] = etUsuNombre.text.toString().trim()
                FuncionesUtiles.usuario["LOGIN"] = etUsuCodigo.text.toString().trim()
                FuncionesUtiles.usuario["VERSION"] = etUsuVersion.text.toString().trim()
                FuncionesUtiles.usuario["TIPO"] = "U"
                FuncionesUtiles.usuario["ACTIVO"] = "S"
                FuncionesUtiles.usuario["COD_EMPRESA"] = "1"
                FuncionesUtiles.usuario["CONF"] = "S"
                FuncionesUtiles.usuario["VALIDA_UBICACION_SUC"] = "S"
                FuncionesUtiles.usuario["ID_CAB_REBOTE"] = "1"
                FuncionesUtiles.usuario["MINUTO_MINIMO"] = "1"
                Sincronizacion.tipoSinc = "T"
                MainActivity.bd!!.execSQL(SentenciasSQL.insertUsuario(FuncionesUtiles.usuario))
                val menu2 = Intent(this, Sincronizacion::class.java)
                startActivity(menu2)
                finish()
            }
            etUsuarioMensaje.text = "Sincronizar"
        }
    }

    @SuppressLint("Recycle")
    private fun traerUsuario(){
        cursor = MainActivity.bd!!.rawQuery("SELECT * FROM usuarios", null)
        if (cursor.count>0){
            cursor.moveToLast()
            etUsuCodigo.setText(cursor.getString(cursor.getColumnIndex("LOGIN")))
            etUsuNombre.setText(cursor.getString(cursor.getColumnIndex("NOMBRE")))
            etUsuVersion.setText(cursor.getString(cursor.getColumnIndex("VERSION")))
        } else {
            Toast.makeText(this,"No ha configurado un usuario.",Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Recycle")
    private fun usuarioGuardado():Boolean{
        return try {
            cursor = MainActivity.bd!!.rawQuery("SELECT * FROM usuarios", null)
            cursor.moveToLast()
            cursor.count > 0 && cursor.getString(cursor.getColumnIndex("LOGIN")) == etUsuCodigo.text.toString().trim()
        } catch (e : Exception) {
            e.message
            false
        }
    }

    private fun borrarUsuario(){
        MainActivity.bd!!.execSQL("delete from usuarios")
    }
}

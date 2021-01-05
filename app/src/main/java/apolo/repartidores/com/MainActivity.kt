package apolo.repartidores.com

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import apolo.repartidores.com.utilidades.*
import kotlinx.android.synthetic.main.activity_configurar_usuario.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        var utilidadesBD: UtilidadesBD? = null
        var bd: SQLiteDatabase? = null
        val conexionWS: ConexionWS = ConexionWS()
        var codPersona : String = ""
        val tablasSincronizacion: TablasSincronizacion = TablasSincronizacion()
        lateinit var funcion : FuncionesUtiles
        const val version : String = "18"
        var nombre : String = ""
        lateinit var etAccion : EditText
        lateinit var dispositivo : FuncionesDispositivo
        lateinit var telMgr : TelephonyManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializarElementos()
    }

    private fun inicializarElementos(){
        funcion = FuncionesUtiles(this)
        utilidadesBD = UtilidadesBD(this, null)
        bd = utilidadesBD!!.writableDatabase
//        crearTablas()
        cargarUsuarioInicial()
        btComenzar.setOnClickListener{comenzar()}
        etAccion()
        dispositivo = FuncionesDispositivo(this)
        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        mostrarImei()
    }

    private fun comenzar(){
        if (etCodigoUsuario.text.isEmpty() || etNombreUsuario.text.isEmpty() || etVersionUsuario.text.isEmpty()){
            funcion.mensaje(this,"","Todos los campos son obligatorios")
            return
        }
        val dialogo = DialogoAutorizacion(this)
        dialogo.dialogoAutorizacion("comenzar",accion)
    }

    private fun etAccion(){
        etAccion = accion
        accion.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().indexOf("comenzar") > -1 ){
                    iniciar()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

//    private fun crearTablas(){
//        for (i in 0 until SentenciasSQL.listaSQLCreateTable().size){
//            funcion.ejecutar(SentenciasSQL.listaSQLCreateTable()[i],this)
//        }
//    }

    private fun cargarUsuarioInicial():Boolean{
        lateinit var cursor : Cursor

        try {
            funcion.ejecutar(SentenciasSQL.createTableUsuarios(),this)
            cursor = funcion.consultar("SELECT * FROM usuarios")
        } catch(e:Exception){
            return false
        }

        if (cursor.moveToFirst()) {
            cursor.moveToLast()
            etNombreUsuario.setText(cursor.getString(cursor.getColumnIndex("NOMBRE")))
            etCodigoUsuario.setText(cursor.getString(cursor.getColumnIndex("LOGIN")))
            FuncionesUtiles.usuario["NOMBRE"] = cursor.getString(cursor.getColumnIndex("NOMBRE"))
            FuncionesUtiles.usuario["LOGIN"] = cursor.getString(cursor.getColumnIndex("LOGIN"))
            FuncionesUtiles.usuario["TIPO"] = cursor.getString(cursor.getColumnIndex("TIPO"))
            FuncionesUtiles.usuario["ACTIVO"] = cursor.getString(cursor.getColumnIndex("ACTIVO"))
            FuncionesUtiles.usuario["COD_EMPRESA"] = cursor.getString(cursor.getColumnIndex("COD_EMPRESA"))
            FuncionesUtiles.usuario["VERSION"] = cursor.getString(cursor.getColumnIndex("VERSION"))
            FuncionesUtiles.usuario["COD_PERSONA"] = " "
            FuncionesUtiles.usuario["CONF"] = "S"
            startActivity(Intent(this,MainActivity2::class.java))
            finish()
            return true
        } else {
            FuncionesUtiles.usuario["CONF"] = "N"
            return false
        }
    }

    fun iniciar(){
        if (usuarioGuardado()){
            try {
                funcion.ejecutar("UPDATE usuarios SET NOMBRE = '" + etUsuNombre.text.toString().trim() + "'" +
                        ", VERSION = '" + etUsuVersion.text.toString().trim() + "' " +
                        "    WHERE LOGIN = '" + etUsuCodigo.text.toString().trim() + "' "  ,this)
            } catch (e : java.lang.Exception) {
            }
        } else {
            FuncionesUtiles.usuario["NOMBRE"] = etNombreUsuario.text.toString().trim()
            FuncionesUtiles.usuario["LOGIN"] = etCodigoUsuario.text.toString().trim()
            FuncionesUtiles.usuario["VERSION"] = etVersionUsuario.text.toString().trim()
            FuncionesUtiles.usuario["TIPO"] = "U"
            FuncionesUtiles.usuario["ACTIVO"] = "S"
            FuncionesUtiles.usuario["COD_EMPRESA"] = "1"
            FuncionesUtiles.usuario["CONF"] = "S"
            Sincronizacion.tipoSinc = "T"
            Sincronizacion.primeraVez = true
            val menu2 = Intent(this, Sincronizacion::class.java)
            startActivity(menu2)
            finish()
        }
    }

    private fun usuarioGuardado():Boolean{
        return try {
            val cursor = funcion.consultar("SELECT * FROM usuarios")
            cursor.moveToLast()
            cursor.count > 0 && cursor.getString(cursor.getColumnIndex("LOGIN")) == etUsuCodigo.text.toString().trim()
        } catch (e : java.lang.Exception) {
            e.message
            false
        }
    }

}
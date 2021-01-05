package apolo.repartidores.com.utilidades

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import apolo.repartidores.com.MainActivity
import apolo.repartidores.com.MainActivity2
import apolo.repartidores.com.R
import kotlinx.android.synthetic.main.activity_sincronizacion.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.DecimalFormat
import java.util.*

@Suppress("DEPRECATION", "ClassName")
class Sincronizacion : AppCompatActivity() {

    lateinit var imeiBD: String

    companion object{
        var tipoSinc: String = "T"
        lateinit var context: Context
        var primeraVez = false
        var nf = DecimalFormat("000")
        var mayorPlanilla = ""
    }

    private var funcion : FuncionesUtiles = FuncionesUtiles(this)
    private var ubicacion : FuncionesUbicacion = FuncionesUbicacion(this)
    private lateinit var lm : LocationManager
//    lateinit var enviarMarcacion : EnviarMarcacion

    @Suppress("ClassName")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sincronizacion)
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ubicacion.obtenerUbicacion(lm)
        context = this
//        EnviarMarcacion.context = context
//        enviarMarcacion = EnviarMarcacion()
        imeiBD = ""
        if (FuncionesUtiles.usuario["CONF"].equals("N")){
            btFinalizar.visibility = View.VISIBLE
            return
        }

        try {
            preparaSincornizacion().execute()
        } catch (e: Exception){
            Log.println(Log.WARN, "Error", e.message!!)
        }
    }

    @Suppress("ClassName")
    @SuppressLint("StaticFieldLeak")
    private inner class preparaSincornizacion: AsyncTask<Void, Void, Void>(){
        lateinit var progressDialog: ProgressDialog
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Consultando disponibilidad")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        @SuppressLint("WrongThread", "SetTextI18n")
        override fun doInBackground(vararg p0: Void?): Void? {
            imeiBD = MainActivity.conexionWS.procesaVersion(FuncionesUtiles.usuario["LOGIN"].toString())
            if (imeiBD.indexOf("Unable to resolve host") > -1 || imeiBD.indexOf("timeout") > -1) {
                progressDialog.dismiss()
                runOnUiThread {
                    Toast.makeText(
                        context,
                        "Verifique su conexion a internet y vuelva a intentarlo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
                return null
            }

//            enviarMarcacion.procesaEnviaMarcaciones()
            if (imeiBD.length>3){
                FuncionesUtiles.usuario["VALIDA_DIS"] = imeiBD.split("-")[1]
                FuncionesUtiles.usuario["ID_CAB_REBOTE"] = imeiBD.split("-")[2]
                FuncionesUtiles.usuario["MINUTO_MINIMO"] = imeiBD.split("-")[5]
                FuncionesUtiles.usuario["VALIDA_UBICACION_SUC"] = imeiBD.split("-")[4]
            }
            insertarUsuario()
            if (imeiBD.length>3 && imeiBD.isNotEmpty()){

                if (!validaVersion(imeiBD.split("-")[6], imeiBD.split("-")[3])){
                    return null
                }
            } else {
                if (imeiBD.isEmpty()){
                    tvImei.text = "Ocurrio un error"
                    return null
                } else {
                    if (!validaVersion(imeiBD.split("-")[6])){
                        return null
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= 26){
                progressDialog.setMessage("Generando Archivos")
            }
            if (tipoSinc == "T"){
//                MainActivity.funcion.ejecutar("update svm_vendedor_pedido set ULTIMA_VEZ = '" + MainActivity.funcion.getFechaActual() + "'",this@Sincronizacion)
                if(!MainActivity.conexionWS.generaArchivos()){
                    runOnUiThread {
                        imeiBD += "\n\nError al generar archivos"
                        tvImei.text = "\n\nError al generar archivos"
                        Toast.makeText(
                            this@Sincronizacion,
                            "Error al generar archivos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                if (Build.VERSION.SDK_INT >= 26){
                    progressDialog.setMessage("Obteniendo Archivos")
                }
                if(!MainActivity.conexionWS.obtenerArchivos()){
                    runOnUiThread {
                        if (tvImei.text.toString().indexOf("Espere")>-1){
                            imeiBD += "\n\nError al obtener archivos"
                            tvImei.text = "\n\nError al obtener archivos"
                            Toast.makeText(
                                this@Sincronizacion,
                                "Error al obtener archivos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            runOnUiThread {
                if (tvImei.text.toString().indexOf("Espere")<0){
                    btFinalizar.visibility = View.VISIBLE
                } else {
                    cargarRegistros()
                }
            }
        }
    }

    fun cargarRegistros(){
        if (tipoSinc == "T"){
            sincronizarTodo()
        }
    }

    private fun borrarTablasTodo(listaTablas: Vector<String>){
        for (i in 0 until listaTablas.size){
            val sql: String = "DROP TABLE IF EXISTS " + listaTablas[i].split(" ")[5]
            try {
                MainActivity.bd!!.execSQL(sql)
            } catch (e: Exception) {
                return
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sincronizarTodo(){
        val th = Thread {
            runOnUiThread {
                tvImei.text = tvImei.text.toString() + "\n\nSincronizando"
            }
            borrarTablasTodo(MainActivity.tablasSincronizacion.listaSQLCreateTables())
            obtenerArchivosTodo(
                MainActivity.tablasSincronizacion.listaSQLCreateTables(),
                MainActivity.tablasSincronizacion.listaCamposSincronizacion()
            )
            insertaDatosNoSincronizados()
        }
        th.start()
    }

    @SuppressLint("SetTextI18n", "SdCardPath")
    private fun obtenerArchivosTodo(
        listaSQLCreateTable: Vector<String>,
        listaCampos: Vector<Vector<String>>
    ):Boolean{
        runOnUiThread {
            pbTabla.progress = 0
            pbProgresoTotal.progress = 0
        }
        for (i in 0 until listaSQLCreateTable.size){
            if (listaSQLCreateTable[i].split(" ")[5] == "rpv_motivo_rep_check_list") {
                MainActivity2.bd!!.execSQL(listaSQLCreateTable[i])
                MainActivity2.bd!!.execSQL("CREATE UNIQUE INDEX  if not exists UCHEKEO " +
                        "ON rpv_motivo_rep_check_list(COD_MOTIVO,NRO_PLANILLA)")
                mayorPlanilla()
            }
            MainActivity.bd!!.beginTransaction()
            try {

                //Leer el archivo desde la direccion asignada
                var archivo     = File(
                    "/data/data/apolo.repartidores.com/" + listaSQLCreateTable[i].split(
                        " "
                    )[5] + ".txt"
                )
                var leeArchivo  = FileReader(archivo)
                var buffer      = BufferedReader(leeArchivo)
                val sql: String = listaSQLCreateTable[i]

                try {
                    if (listaSQLCreateTable[i].split(" ")[5] != "rpv_motivo_rep_check_list"){
                        MainActivity.bd!!.execSQL(sql)
                    }
                } catch (e: Exception) {
                        return false
                }

                //Obtiene cantidad de lineas
                var numeroLineas = 0
                var linea: String? = buffer.readLine()
                while (linea != null){
                    numeroLineas++
                    linea = buffer.readLine()
                }

                archivo     = File(
                    "/data/data/apolo.repartidores.com/" + listaSQLCreateTable[i].split(
                        " "
                    )[5] + ".txt"
                )
                leeArchivo  = FileReader(archivo)
                buffer      = BufferedReader(leeArchivo)

                //Extrae valor de los campo e inserta a la BD
                linea = buffer.readLine()
                var cont = 0
                runOnUiThread {
                    tvImei.text = tvImei.text.toString() + "\n${nf.format(i)} - " + listaSQLCreateTable[i].split(
                        " "
                    )[5]
                }

                while (linea != null){
                    val valores : ArrayList<String> = linea.split("|") as ArrayList<String>
                    val values = ContentValues()
                    for (j in 0 until listaCampos[i].size){
                        if (valores[j] == "null" || valores[j] == "" || valores[j].isEmpty()){
                            values.put(listaCampos[i][j], " ")
                        } else {
                            values.put(listaCampos[i][j], valores[j])
                        }

                    }

                    //inserta valores en tablas especificas
                    if (listaSQLCreateTable[i].split(" ")[5] == "rpv_motivo_rep_check_list") {
                        values.put("FECHA_SINCRO", funcion.getFechaActual())
                        values.put("NRO_PLANILLA", mayorPlanilla)
                    }

                    try {
                        MainActivity.bd!!.insert(listaSQLCreateTable[i].split(" ")[5], null, values)
                    } catch (e: Exception) {
                        return false
                    }
                    linea = buffer.readLine()
                    runOnUiThread {
                        cont++
                        var progreso : Double = (100/numeroLineas.toDouble())*(cont)
                        if (cont == numeroLineas){
                            progreso = 100.0
                        }
                        pbTabla.progress = progreso.toInt()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    tvImei.text = tvImei.text.toString() + "\n\n" + e.message
                }
                return false
            }
            runOnUiThread {
                pbProgresoTotal.progress = (100/listaSQLCreateTable.size)*(i+1)
            }
            MainActivity.bd!!.setTransactionSuccessful()
            MainActivity.bd!!.endTransaction()
        }
        runOnUiThread {
            pbProgresoTotal.progress = 100
            btFinalizar.visibility = View.VISIBLE
        }
        return true
    }

    override fun onBackPressed() {
        return
    }

    fun cerrar(view: View) {
        if (primeraVez){
            startActivity(Intent(this, MainActivity2::class.java))
            primeraVez = false
        }

        finish()
    }

    fun insertarUsuario(){
        try {
            MainActivity.bd!!.execSQL(SentenciasSQL.insertUsuario(FuncionesUtiles.usuario))
        } catch (e: Exception) {
            return
        }
    }

    @SuppressLint("SetTextI18n")
    fun validaVersion(versionUsuario: String, versionSistema: String):Boolean{
        if (!FuncionesUtiles.usuario["VERSION"].equals(versionUsuario)){
            tvImei.text = "Version de usuario no corresponde.$versionUsuario"
            return false
        }
        if (versionSistema != MainActivity.version){
            tvImei.text = "Debe actualizar su version para sincronizar."
            return false
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    fun validaVersion(versionUsuario: String):Boolean{
        if (versionUsuario != FuncionesUtiles.usuario["VERSION"]){
            tvImei.text = "Debe actualizar su version para sincronizar."
            btFinalizar.visibility = View.VISIBLE
            return false
        }
        return true
    }

    private fun insertaDatosNoSincronizados(){
        var listaPlanillas = ""
        var sql = ("SELECT NRO_PLANILLA,COD_REPARTIDOR FROM svm_planilla_reparto WHERE TOT_COMPROBANTE IN "
                + "(SELECT MAX(TOT_COMPROBANTE) FROM svm_planilla_reparto );")
        var cursor:Cursor = funcion.consultar(sql)
        for (i in 0 until cursor.count){
            if (i == 0){
                listaPlanillas = funcion.dato(cursor, "NRO_PLANILLA")
            } else {
                listaPlanillas += ",${funcion.dato(cursor, "NRO_PLANILLA")}"
            }
        }
        sql = ("SELECT COD_MOTIVO "
                + "  FROM rpv_motivo_rep_check_list "
                + " WHERE ESTADO ='P'")
        cursor = funcion.consultar(sql)
        for (i in 0 until cursor.count){
            sql = "update rpv_motivo_rep_check_list set OBSERVACION = '$listaPlanillas' where COD_MOTIVO = '${funcion.dato(
                cursor,
                "COD_MOTIVO"
            )}'"
            if (funcion.dato(cursor, "COD_MOTIVO").trim() != "07"){
                MainActivity2.bd!!.execSQL(sql)
            }
            cursor.moveToNext()
        }
        actualizarEntradaSalida()
        reguladorCheckList()
    }

    private fun actualizarEntradaSalida(){
        ubicacion.obtenerUbicacion(lm)
        val sql = ("update svm_planilla_reparto set "
                + "ENTRADA 			= 'S',"
                + "SALIDA  		   	= 'S',"
                + "LATI	           	= '" + ubicacion.latitud        + "',"
                + "LONGI	       	= '" + ubicacion.longitud       + "',"
                + "FEC_ENTRADA     	= '" + funcion.getFechaActual() + "',"
                + "FEC_SALIDA      	= '" + funcion.getFechaActual() + "',"
                + "HORA_ENTRADA    	= '" + funcion.getHoraActual()  + "',"
                + "HORA_SALIDA     	= '" + funcion.getHoraActual()  + "',"
                + "IND_REPARTO 	   	= 'S', "
                + "SALTAR_LLEGADA  	= 'S', "
                + "ESTADO_SALIDA	= 'E'  "
                + " where "
                + "MARC_SALIDA		= 'S' AND "
                + "IND_ENTREGA	   != 'R' ")
        MainActivity2.bd!!.execSQL(sql)
    }

    private fun reguladorCheckList(){
        val sql = "delete from rpv_motivo_rep_check_list where " +
                "(substr(trim(FECHA_CHEKEO),length(trim(FECHA_CHEKEO))-3,4)||'-'|| " +
                " substr(trim(FECHA_CHEKEO),length(trim(FECHA_CHEKEO))-6,2)||'-'|| " +
                " substr(trim(FECHA_CHEKEO),length(trim(FECHA_CHEKEO))-9,2))<=date('now','-10 day') "
        try {
            MainActivity2.bd!!.execSQL(sql)
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
        }
    }

    private fun mayorPlanilla(){
        val sqlIdMayorPlanilla  = ("SELECT NRO_PLANILLA,COD_REPARTIDOR 	"
                                + "  FROM svm_planilla_reparto 				"
                                + " WHERE TOT_COMPROBANTE IN (			"
                                + "								SELECT MAX(TOT_COMPROBANTE) "
                                + "								  FROM svm_planilla_reparto 	)")
        val cursorPlanilla = funcion.consultar(sqlIdMayorPlanilla)
        mayorPlanilla = funcion.dato(cursorPlanilla,"NRO_PLANILLA")
        mayorPlanilla += ""
    }

}

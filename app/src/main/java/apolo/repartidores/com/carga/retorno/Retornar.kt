package apolo.repartidores.com.carga.retorno

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.telephony.TelephonyManager
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import apolo.repartidores.com.MainActivity2
import apolo.repartidores.com.carga.ListaPlanillas
import apolo.repartidores.com.utilidades.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

@Suppress("NAME_SHADOWING")
class Retornar(
    private val codCliente: String,
    private val codSubcliente: String,
    private val lm: LocationManager?,
    private val telMgr: TelephonyManager?,
    private val latitud: String,
    private val longitud: String,
    private val cliente: HashMap<String, String>
) {

    companion object{
        lateinit var context  : Context
        lateinit var etAccion : EditText
        var resultado = ""
        var noVenta  = ""
        var modificacion = false
        var editable = false
        var nuevo = true
        var id = ""
        var cadena1 = ""
        var distanciaMaxima = 100
    }

    private val ubicacion   = FuncionesUbicacion(context)
    private val dispositivo = FuncionesDispositivo(context)
    private val funcion     = FuncionesUtiles(context)
    private var listaMotivos= ArrayList<HashMap<String, String>>()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun cargarDialogo(){
        if (lm != null) {
            if (!validacion("Abrir")) {
                return
            }
        }
        marcarNoVenta()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun validacion(trigger: String) : Boolean {
        ubicacion.obtenerUbicacion(lm!!)
        if (!ubicacion.validaUbicacionSimulada(lm)) { return false }
        if (!dispositivo.horaAutomatica()) { return false }
        if (!dispositivo.modoAvion()){ return false }
        if (!dispositivo.zonaHoraria()){ return false }
//        if (!dispositivo.fechaCorrecta()){ return false }
        if (!dispositivo.tarjetaSim(telMgr!!)){ return false }
        if (!ubicacion.ubicacionActivada(lm)){
            ubicacion.latitud  = ""
            ubicacion.longitud = ""
            etAccion.setText("abrirUbicacion")
            return false
        }
        if (latitud.trim() == "" || longitud.trim() == "") {
            // ABRIR EL MAPA
            Mapa.modificarCliente = true
            Mapa.codCliente = codCliente
            Mapa.codSubcliente = codSubcliente
            Mapa.codVendedor = FuncionesUtiles.usuario["LOGIN"].toString()
            etAccion.setText("abrirMapa")
            return false
        } else {
            if (ubicacion.latitud.trim() == "" || ubicacion.longitud.trim() == "") {
                funcion.toast(context, "No se encuentra la ubicacion GPS del telefono")
                return false
            }
            val distanciaCliente : Double = ubicacion.calculaDistanciaCoordenadas(
                ubicacion.latitud.toDouble(),
                latitud.toDouble(),
                ubicacion.longitud.toDouble(),
                longitud.toDouble()
            ) * 1000
            if (distanciaCliente > distanciaMaxima) {
                funcion.toast(context, "No se encuentra en el cliente. Se encuentra a " + distanciaCliente.roundToInt() + " m."+"\n"+
                        "Cod: " + codCliente + "Cli: " + cliente["DESC_CLIENTE"])
                return false
            }
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun marcarNoVenta() {
        val cursorNoVenta: Cursor
        try {
            val select = ("Select id, COD_EMPRESA, COD_SUCURSAL, COD_CLIENTE   , COD_SUBCLIENTE  , COD_REPARTIDOR, "
                        + "                 COD_MOTIVO , OBSERVACION , FECHA, LATITUD, LONGITUD, ESTADO, HORA_ALTA "
                        + " from svm_marcacion_visita "
                        + " where COD_CLIENTE    = '" + codCliente + "' "
                        + "   and COD_SUBCLIENTE = '" + codSubcliente + "' "
                        + "   and FECHA 	     = '" + funcion.getFechaActual().substring(0, 10) + "'"
                        + "   and COD_MOTIVO NOT IN ('16')")
            cursorNoVenta = funcion.consultar(select)
            id = ""
            if (cliente["ENTRADA"] == "S" || cliente["SALIDA"] == "S") {
                val estado = funcion.dato(cursorNoVenta, "ESTADO")
                if (estado == "E") {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("Atención")
                    builder.setMessage("Ya se cargo la marcación de visita de este cliente")
                    builder.setCancelable(false)
                    builder.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
                    val alert: AlertDialog = builder.create()
                    alert.show()
                }
            } else {
                val alertMotivos: AlertDialog.Builder = AlertDialog.Builder(context)
                alertMotivos.setTitle("Justificar retorno")
                val fecha: String = funcion.getFechaActual()
                alertMotivos.setMessage("Realizada el $fecha")
                listaMotivos = ArrayList()
                val cursorMotivos: Cursor = funcion.consultar("select * from srm_motivo_no_venta order by DESC_MOTIVO")
                funcion.cargarLista(listaMotivos, cursorMotivos)
                val descMotivo = arrayOfNulls<String?>(cursorMotivos.count)
                val conSpinner = Spinner(context)
                val observacion = EditText(context)
                val tvObservacion = TextView(context)
                for (i in 0 until listaMotivos.size){descMotivo[i] = listaMotivos[i]["DESC_MOTIVO"]}
                tvObservacion.text = "Observaciones:"
                observacion.inputType = InputType.TYPE_CLASS_TEXT
                observacion.width = 60
                observacion.setLines(3)
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(conSpinner)
                layout.addView(tvObservacion)
                layout.addView(observacion)
                alertMotivos.setView(layout)
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    context,
                    R.layout.simple_spinner_item,
                    descMotivo
                )
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                conSpinner.adapter = adapter
                conSpinner.setSelection(0)
                alertMotivos.setPositiveButton("Enviar",
                    DialogInterface.OnClickListener { _, _ ->
                        val horaAlta: String = funcion.getHoraActual().toString()
                        if (!validaTiempo()) {
                            return@OnClickListener
                        }
                        if (retornaNroOrdenVisita() > 1) {
                            funcion.toast(
                                context, "Ya ha superado el límite permitido"
                                        + " de retorno para este cliente"
                            )
                            return@OnClickListener
                        }
                        if (!distanciaPermitidaVisita()) {
                            //entra si no está en la ubicación
                            return@OnClickListener
                        }
                        guardaMarcacionVisita(
                            cliente["COD_EMPRESA"].toString(),
                            "01",
                            ListaPlanillas.nroPlanilla,
                            codCliente,
                            codSubcliente,
                            FuncionesUtiles.usuario["LOGIN"].toString(),
                            listaMotivos[conSpinner.selectedItemPosition]["COD_MOTIVO"].toString(),
                            observacion.text.toString(),
                            fecha.substring(0, 10),
                            ubicacion.latitud,
                            ubicacion.longitud,
                            horaAlta
                        )
                        val cursor1: Cursor
                        val sql: String = ("SELECT "
                                + "COD_EMPRESA,COD_SUCURSAL,NRO_PLANILLA,COD_CLIENTE,COD_SUBCLIENTE,"
                                + "COD_REPARTIDOR,COD_MOTIVO,OBSERVACION,FECHA,LATITUD,"
                                + "LONGITUD,ESTADO,HORA_ALTA,NRO_ORDEN "
                                + "FROM svm_marcacion_visita WHERE "
                                + "ESTADO = 'P' ")
                        cursor1 = funcion.consultar(sql)
                        val nreg = cursor1.count
                        if (nreg > 0) {
                            cursor1.moveToFirst()
                            var codEmpresa: String
                            var codSucursal: String
                            var nroPlanillas: String
                            var codCliente: String
                            var codSubcliente: String
                            var codRepartidor: String
                            var codMotivo: String
                            var observacion: String
                            var fecha: String
                            var latitud: String
                            var longitud: String
                            var nroOrden: String
                            val cabecera = " INTO rp_clientes_no_entrega (" +
                                    " COD_EMPRESA	, " +
                                    " COD_SUCURSAL	, " +
                                    " NRO_PLANILLA	, " +
                                    " COD_CLIENTE	, " +
                                    " COD_SUBCLIENTE, " +
                                    " COD_REPARTIDOR, " +
                                    " COD_MOTIVO	, " +
                                    " OBSERVACION	, " +
                                    " FEC_VISITA	, " +
                                    " LATITUD	    , " +
                                    " LONGITUD	    , " +
                                    " FEC_NO_ENTREGA, " +
                                    " CONS_COMO_VIS	, " +
                                    " NRO_ORDEN	)" +
                                    " values ("

                            cadena1 = "INSERT ALL"
                            try {
                                for (i in 0 until nreg) {
                                    codEmpresa = cursor1.getString(cursor1.getColumnIndex("COD_EMPRESA"))
                                    codSucursal = "01"
                                    nroPlanillas = cursor1.getString(cursor1.getColumnIndex("NRO_PLANILLA"))
                                    codCliente = cursor1.getString(cursor1.getColumnIndex("COD_CLIENTE"))
                                    codSubcliente = cursor1.getString(cursor1.getColumnIndex("COD_SUBCLIENTE"))
                                    codRepartidor = cursor1.getString(cursor1.getColumnIndex("COD_REPARTIDOR"))
                                    codMotivo = cursor1.getString(cursor1.getColumnIndex("COD_MOTIVO"))
                                    observacion = cursor1.getString(cursor1.getColumnIndex("OBSERVACION"))
                                    fecha = cursor1.getString(cursor1.getColumnIndex("FECHA"))
                                    latitud = cursor1.getString(cursor1.getColumnIndex("LATITUD"))
                                    longitud = cursor1.getString(cursor1.getColumnIndex("LONGITUD"))
                                    nroOrden = cursor1.getString(cursor1.getColumnIndex("NRO_ORDEN"))
                                    cursor1.moveToNext()
                                    cadena1 += "$cabecera'$codEmpresa"
                                    cadena1 += "','$codSucursal"
                                    cadena1 += "','$nroPlanillas"
                                    cadena1 += "','$codCliente"
                                    cadena1 += "','$codSubcliente"
                                    cadena1 += "','$codRepartidor"
                                    cadena1 += "','$codMotivo"
                                    cadena1 += "','$observacion"
                                    cadena1 += "',to_date('" + fecha.substring(0, 10) + "','DD/MM/YYYY')"
                                    cadena1 += ",'$latitud"
                                    cadena1 += "','$longitud"
                                    cadena1 += "',to_date('" + fecha.substring(0, 10) + " " + horaAlta + "','dd/MM/yyyy hh24:mi:ss')"
                                    cadena1 += ",'" + "S"
                                    cadena1 += "','$nroOrden' )"
                                    if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"] != null) {
                                        if (listaMotivos[conSpinner.selectedItemPosition]["CIERRA"].equals("S")) {
                                            cerrarSalidaCliente()
                                        }
                                    }
                                }

                                cadena1 += " SELECT * FROM dual "
                                EnviarPositivacion2().execute()
                            } catch (e: java.lang.Exception) {
                                var err = e.message
                                err = "" + err
                            }
                        }
                    })

                alertMotivos.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
                val motivos: AlertDialog = alertMotivos.create()
                motivos.show()
            }
        } catch (e: NumberFormatException) {
        }
    }

    private fun validaTiempo(): Boolean {
        val cursor1: Cursor
        var resultado = false
        val mensaje = "Debe esperar al menos 1 hora para volver a enviar"
        val fechaUltVez: Date
        val ncan: Int
        val sql: String = (" SELECT FECHA ||' '|| HORA_ALTA ULTIMA_VEZ "
                + " FROM svm_marcacion_visita WHERE"
                + " COD_CLIENTE    = '" + codCliente + "' AND"
                + " COD_SUBCLIENTE = '" + codSubcliente + "' AND"
                + " FECHA 	  = '" + funcion.getFechaActual().substring(0, 10) + "'")
        val hourFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        try {
            cursor1 = funcion.consultar(sql)
            ncan = cursor1.count
            if (ncan > 0) {
                val f = cursor1.getString(cursor1.getColumnIndex("ULTIMA_VEZ"))
                fechaUltVez = hourFormat.parse(f)
                val fechaAct = Date()
                val dif: Long = fechaAct.time - fechaUltVez.time
                val tiempo = 1000 * 60 * 60.toLong()
                if (dif < tiempo) {
                    funcion.toast(context, mensaje)
                } else {
                    resultado = true
                }
            } else {
                resultado = true
            }
        } catch (e: java.lang.Exception) {
            val err = e.message.toString()
            funcion.toast(context, err)
        }
        return resultado
    }

    private fun retornaNroOrdenVisita(): Int {
        val cursor1: Cursor
        var nroOrden = 0
        val sql: String = (" SELECT FECHA,COD_CLIENTE,Count(COD_EMPRESA) CANTIDAD "
                + " FROM svm_marcacion_visita WHERE"
                + " COD_CLIENTE    = '" + codCliente + "' AND"
                + " COD_SUBCLIENTE = '" + codSubcliente + "' AND"
                + " FECHA 	  = '" + funcion.getFechaActual().substring(0, 10) + "'"
                + " GROUP BY COD_CLIENTE,COD_SUBCLIENTE,COD_REPARTIDOR,FECHA")
        cursor1 = funcion.consultar(sql)
        cursor1.moveToFirst()
        if (cursor1.count > 0) {
            nroOrden = cursor1.getInt(cursor1.getColumnIndex("CANTIDAD"))
        }
        return nroOrden
    }

    private fun distanciaPermitidaVisita(): Boolean {
        if (lm != null) {
            ubicacion.obtenerUbicacion(lm)
            if (!ubicacion.validaUbicacionSimulada(lm)) {
                return false
            }
        }
        if (cliente["LATITUD"].equals("") || cliente["LONGITUD"].equals("")) {
            //ABRIR EL MAPA
            return false
        } else {
            if (ubicacion.latitud == "" || ubicacion.longitud == "") {
                funcion.toast(context, "No se encuentra la ubicacion GPS del telefono")
                return false
            }
            return ubicacion.distanciaMinima(
                cliente["LATITUD"].toString(),
                cliente["LONGITUD"].toString(), distanciaMaxima
            )
        }
    }

    @SuppressLint("Recycle")
    private fun guardaMarcacionVisita(
        codEmpresa: String,
        codSucursal: String,
        nroPlanilla: String,
        codCliente: String,
        codSubcliente: String,
        codRepartidor: String,
        codMotivo: String,
        observacion: String,
        fecha: String,
        latitud: String,
        longitud: String,
        horaAlta: String
    ) {
        val cursor2: Cursor
        var cantReg2 = 0
        var xIdenti = "0"
        val cv = ContentValues()
        cv.put("COD_EMPRESA", codEmpresa)
        cv.put("COD_SUCURSAL", codSucursal)
        cv.put("NRO_PLANILLA", nroPlanilla)
        cv.put("COD_CLIENTE", codCliente)
        cv.put("COD_SUBCLIENTE", codSubcliente)
        cv.put("COD_REPARTIDOR", codRepartidor)
        cv.put("COD_MOTIVO", codMotivo)
        cv.put("OBSERVACION", observacion)
        cv.put("FECHA", fecha)
        cv.put("LATITUD", latitud)
        cv.put("LONGITUD", longitud)
        cv.put("ESTADO", "P")
        cv.put("HORA_ALTA", horaAlta)
        try {
            MainActivity2.bd!!.beginTransaction()
            MainActivity2.bd!!.insert("svm_marcacion_visita", null, cv)
            MainActivity2.bd!!.setTransactionSuccessful()
            MainActivity2.bd!!.endTransaction()
            val sql2 = "SELECT last_insert_rowid() xid"
            cursor2 = MainActivity2.bd!!.rawQuery(sql2, null)
            cantReg2 = cursor2.count
            cursor2.moveToFirst()
            xIdenti = if (cantReg2 > 0) {
                cursor2.getString(cursor2.getColumnIndex("xid"))
            } else {
                "0"
            }
            id = retornaNroOrdenVisita().toString()
            val sqlUpdate = ("update svm_marcacion_visita set "
                    + " nro_orden 	= '" + retornaNroOrdenVisita() + "'"
                    + " where id   	= '" + xIdenti + "'")
            MainActivity2.bd!!.execSQL(sqlUpdate)
        } catch (e: java.lang.Exception) {
            val aa: String? = e.message
            id = "0"
            return
        }
    }

    private fun cerrarSalidaCliente() {
        val fecEntrada: String = funcion.getFechaActual() + " " + funcion.getHoraActual()

        //INSERTA CABECERA
        val values = ContentValues()
        values.put("COD_EMPRESA", cliente["COD_EMPRESA"])
        values.put("COD_PROMOTOR", FuncionesUtiles.usuario["LOGIN"])
        values.put("COD_CLIENTE", codCliente)
        values.put("COD_SUBCLIENTE", codSubcliente)
        values.put("ESTADO", "P")
        values.put("FECHA", fecEntrada)
        values.put("TIPO", "S")
        values.put("LATITUD", ubicacion.latitud)
        values.put("LONGITUD", ubicacion.longitud)
        MainActivity2.bd!!.insert("svm_marcacion_ubicacion", null, values)
    }

    private class EnviarPositivacion2 :
        AsyncTask<Void?, Void?, Void?>() {
        private var pbarDialog: ProgressDialog? = null
        override fun onPreExecute() {
            pbarDialog = ProgressDialog.show(
                context, "Un momento...",
                "Enviando datos al servidor...", true
            )
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val mensaje: List<String> = MainActivity2.conexionWS.procesaEnviaCheckList(FuncionesUtiles.usuario["LOGIN"].toString(), cadena1).split("\\*")
            resultado = mensaje[0]
//            resultado = "01"
            return null
        }

        override fun onPostExecute(unused: Void?) {
            pbarDialog!!.dismiss()
            val res: String = resultado
            println(res)
            //				if (respuestaWS.indexOf("01*")>=0) {
            if (resultado == "01") {
                resultado = "Marcación de visita enviada con exito!!"
            }
            val builder = AlertDialog.Builder(context)
            builder.setMessage(resultado)
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    var values: ContentValues? = null
                    if (resultado == "Marcación de visita enviada con exito!!") {
                        values = ContentValues()
                        values.put("ESTADO", "E")
                        try {
                            MainActivity2.bd!!.update(
                                "SVM_MARCACION_VISITA", values,
                                "ESTADO = 'P'", null
                            )
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        etAccion.setText("INVALIDATE")
                    }
                }
            val alert = builder.create()
            alert.show()
        }
    }

}
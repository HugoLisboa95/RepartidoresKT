@file:Suppress("DEPRECATION")

package apolo.repartidores.com.carga

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
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import apolo.repartidores.com.MainActivity2
import apolo.repartidores.com.R
import apolo.repartidores.com.R.drawable.ic_checked
import apolo.repartidores.com.utilidades.*
import kotlinx.android.synthetic.main.activity_ckeck_list.*
import kotlinx.android.synthetic.main.barra_titulo.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class CheckList : AppCompatActivity() {

    companion object{
        lateinit var cbPenV : Vector<Boolean>
        lateinit var cbSiV  : Vector<Boolean>
        lateinit var cbNoV  : Vector<Boolean>
        lateinit var lista : ArrayList<HashMap<String, String>>
        lateinit var context: Context
        lateinit var accion : EditText
        private var cadena = ""
        var archivoEnviado = false
        var resultado = ""
        fun pendiente(position: Int){
            lista[position]["CD_PENDIENTE"] = "S"
            lista[position]["CB_SI"]        = "N"
            lista[position]["CB_NO"]        = "N"
            lista[position]["CHEKEO"]       = "N"
            lista[position]["ESTADO"]       = "P"
            lista[position]["HORA_CHEKEO"]  = ""
        }
        fun si(position: Int){
            lista[position]["CD_PENDIENTE"] = "N"
            lista[position]["CB_SI"]        = "S"
            lista[position]["CB_NO"]        = "N"
            lista[position]["CHEKEO"]       = "S"
            lista[position]["ESTADO"]       = "S"
            lista[position]["HORA_CHEKEO"]  = ""
        }
        fun no(position: Int){
            lista[position]["CD_PENDIENTE"] = "N"
            lista[position]["CB_SI"]        = "N"
            lista[position]["CB_NO"]        = "S"
            lista[position]["CHEKEO"]       = "N"
            lista[position]["ESTADO"]       = "N"
            lista[position]["HORA_CHEKEO"]  = ""
        }
        fun procesaEnviaChecklist() {
            var cursor: Cursor
            var nreg = 0
            var mayorValorPl = "0"
            var codRepartidor = "0"
            // Obtiene el mayor valor de la planilla
            val sqlIdMayorPlanilla = ("SELECT NRO_PLANILLA,COD_REPARTIDOR FROM svm_planilla_reparto WHERE TOT_COMPROBANTE IN "
                        + "(SELECT MAX(TOT_COMPROBANTE) FROM svm_planilla_reparto );")
            try {
                cursor = MainActivity2.funcion.consultar(sqlIdMayorPlanilla)
                for (i in 0 until cursor.count) {
                    mayorValorPl = cursor.getString(cursor.getColumnIndex("NRO_PLANILLA"))
                    codRepartidor = cursor.getString(cursor.getColumnIndex("COD_REPARTIDOR"))
                    cursor.moveToNext()
                }
            } catch (e: java.lang.Exception) {
                e.message
            }
            try {
                val sqlChekList: String = ("SELECT id, 			COD_EMPRESA, COD_MOTIVO, 	DESC_MOTIVO,  "
                        + "		  IND_OBLIG, 	DATOS,		 ID_REPARTIDOR, SES_INICIADO, "
                        + "		  CHEKEADO,		ESTADO, 	 OBSERVACION,	VALOR_VIATICO,"
                        + "		  FECHA_CHEKEO,	HORA_CHEKEO "
                        + "  FROM rpv_motivo_rep_check_list "
                        + " WHERE ESTADO ='P'")
                cursor =  MainActivity2.funcion.consultar(sqlChekList)
                cadena = "INSERT ALL"
                val detalle1 = (" INTO RP_REPARTIDORES_CHECK (  "
                        + "		cod_empresa		,nro_planilla	,cod_repartidor	,"
                        + "		cod_motivo 		,estado 		,datos 			, "
                        + "		observacion		,valores		,fec_alta		) "
                        + " VALUES(")
                for (i in 0 until cursor.count) {
                    var fecAlta: String
                    val codEmpresa = "1"
                    val nroPlanilla: String = mayorValorPl
                    val codMotivo: String = cursor.getString(cursor.getColumnIndex("COD_MOTIVO"))
                    val estado: String = cursor.getString(cursor.getColumnIndex("CHEKEADO"))
                    val datos: String = cursor.getString(cursor.getColumnIndex("DATOS"))
                    val observacion: String = MainActivity2.funcion.dato(cursor,"OBSERVACION")
                    val valorViatico: String = cursor.getString(cursor.getColumnIndex("VALOR_VIATICO"))
                    val fechaChekeo: String = cursor.getString(cursor.getColumnIndex("FECHA_CHEKEO"))
                    val horaChekeo: String = cursor.getString(cursor.getColumnIndex("HORA_CHEKEO"))
                    fecAlta = fechaChekeo.trim { it <= ' ' } + " " + horaChekeo.trim { it <= ' ' } //TO_DATE('20/02/2004 14:48:16', 'DD/MM/YYYY HH24:MI:SS')
                    fecAlta = "TO_DATE('$fecAlta', 'DD/MM/YYYY HH24:MI:SS')"
                    cadena += "$detalle1'$codEmpresa',$nroPlanilla,'$codRepartidor','$codMotivo',"
                    cadena += "'$estado','$datos','$observacion','$valorViatico',$fecAlta) "
                    cursor.moveToNext()
                }
                nreg = cursor.count
            } catch (e: java.lang.Exception) {
                e.localizedMessage
            }
            cadena += " SELECT * FROM dual "
            if (nreg == 0) {
                //Toast.makeText(ActCheckListPrueba.this, "No existe ninguna marcacion pendiente de envio", Toast.LENGTH_LONG).show();
            } else {
                val mensaje: List<String> = MainActivity2.conexionWS.procesaEnviaCheckList(
                    FuncionesUtiles.usuario["LOGIN"].toString(), cadena).split("*")
                if (mensaje.size != 1) {
                    if (mensaje[0] == "01") {
                        archivoEnviado = true
                        try {
                            val update = "update rpv_motivo_rep_check_list set ESTADO = 'E' "
                            MainActivity2.bd!!.execSQL(update)
                        } catch (e: java.lang.Exception) {
                            e.message
                        }
                    } else {
                        MainActivity2.funcion.mensaje(
                            context,
                            "Atención",
                            "No se ha podido enviar la información"
                        )
                    }
                }
            }
            return
        }
    }

    private lateinit var funcion : FuncionesUtiles
    private lateinit var adapter : Adapter.AdapterCheckList
    private lateinit var dispositivo : FuncionesDispositivo
    private lateinit var lm : LocationManager
    private lateinit var ubicacion : FuncionesUbicacion
    private var rango = 300
    private var contraClave = ""

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ckeck_list)

        inicializar()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun inicializar(){
        context = this
        accion = etAccion
        dispositivo = FuncionesDispositivo(this)
        ubicacion = FuncionesUbicacion(this)
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ubicacion.obtenerUbicacion(lm)
        funcion = FuncionesUtiles(this, imgTitulo, tvTitulo)
        funcion.cargarTitulo(ic_checked, "Ckeck List")
        cargar()
        mostrar()
        inicializarEtObservacion()
        btConfirmar.setOnClickListener { confirmar() }
        inicializarEtAccion()
    }

    private fun inicializarEtAccion(){
        etAccion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().indexOf("guardarChecklist") > -1) {
                    grabarChekeo(FuncionesUtiles.usuario["LOGIN"].toString(), "S", "S", "N")
                    contraClave = s.toString().split("-")[1].trim()
                    etAccion.setText("")
                }
                if (s.toString().indexOf("finish") > -1) {
                    finish()
                }
            }
        })
    }

    private fun cargar(){
        val sql = " SELECT COD_MOTIVO,DESC_MOTIVO,DATOS,IND_OBLIG,OBSERVACION," +
                " PENDIENTE,VALOR_VIATICO,HORA_CHEKEO,LATITUD,LONGITUD,DATOS,ESTADO," +
                " '' CB_PENDIENTE,'' CB_SI, '' CB_NO, '' CHEKEO " +
                " FROM rpv_motivo_rep_check_list " +
                " WHERE ESTADO='P'"
        lista = ArrayList()
        funcion.cargarLista(lista, funcion.consultar(sql))
        cbPenV = Vector()
        cbSiV = Vector()
        cbNoV = Vector()
        for (i in 0 until lista.size){
            cbPenV.add(true)
            cbSiV.add(false)
            cbNoV.add(false)
        }
    }

    private fun mostrar(){
        funcion.valores = arrayOf("DESC_MOTIVO", "DATOS")
        funcion.vistas  = intArrayOf(R.id.tv1, R.id.tv2)
        adapter = Adapter.AdapterCheckList(
            this,
            lista,
            R.layout.car_che_lista_check_list,
            funcion.vistas,
            funcion.valores
        )
        lvCheckList.adapter = adapter
        lvCheckList.setOnItemClickListener { _, _, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            lvCheckList.invalidateViews()
        }
    }

    private fun inicializarEtObservacion(){
        etObservacion.setOnClickListener {
            funcion.dialogoEntradaTextoLargo(etObservacion, this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun validacion():Boolean{
        if (!dispositivo.horaAutomatica()){
            return false
        }
        if (!dispositivo.modoAvion()){
            return false
        }
        if (!ubicacion.validaUbicacionSimulada(lm)){
            return false
        }
        if (!verificaCampos()){
            return false
        }
        ubicacion.obtenerUbicacion(lm)
        if (!ubicacion.verificarUbicacion()){
            return false
        }
        return true
    }

    private fun verificaCampos():Boolean{
        FuncionesUtiles.usuario
        for (i in 0 until lista.size){
            if (cbPenV[i]){
                funcion.toast(this, "El campo ${lista[i]["DESC_MOTIVO"]} es obligatorio.")
                return false
            }
        }
        for (i in 0 until lista.size){
            if (cbNoV[i] && etObservacion.text.toString().isEmpty() && lista[i]["IND_OBLIG"] == "N"){
                funcion.toast(
                    this,
                    "Justifique en el campo de observación el motivo por el que marcó NO en el campo ${lista[i]["DESC_MOTIVO"]}."
                )
                return false
            } else {
                if (cbNoV[i] && lista[i]["IND_OBLIG"] == "S"){
                    funcion.toast(this, "El campo ${lista[i]["DESC_MOTIVO"]} es obligatorio.")
                    return false
                }
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun confirmar(){
        if (!validacion()){
            return
        }
        ubicacion.obtenerUbicacion(lm)
        if (lista[0]["LATITUD"].toString().trim() == ""  || lista[0]["LONGITUD"].toString().trim() == ""){
            funcion.toast(this, "Coordenadas de la sucursal inaválidas!.")
            return
        } else {
            if (!ubicacion.verificarUbicacion()){
                return
            }
            val distancia = ubicacion.calculaDistanciaCoordenadas(
                ubicacion.latitud.trim().toDouble(),
                lista[0]["LATITUD"].toString().trim().toDouble(),
                ubicacion.longitud.trim().toDouble(),
                lista[0]["LONGITUD"].toString().trim().toDouble()
            )

            if (distancia > rango){
                if(FuncionesUtiles.usuario["VALIDA_UBICACION_SUC"].toString().trim() == "S"){
                    funcion.toast(
                        this,
                        "No se encuentra en la sucursal. Se encuentra a ${distancia.roundToInt()} metros."
                    )
                    val dialogo = DialogoAutorizacion(this)
                    dialogo.dialogoAutorizacion("guardarChecklist", etAccion)
                    return
                } else {
                    grabarChekeo(FuncionesUtiles.usuario["LOGIN"].toString(), "S", "S", "S")
                }
            } else {
                grabarChekeo(FuncionesUtiles.usuario["LOGIN"].toString(), "S", "S", "S")
            }
        }
    }

    fun grabarChekeo(
        varIdRepartidor: String,
        varSesIniciado: String,
        varPendiente: String,
        presencial: String
    ) {
        // Procedimiento que guarda los chekeos en la tabla interna.
        var indicGuardadoCompleto = 0
        val varObservacion: String = etObservacion.text.toString()
        val xHora: String = lista[0]["HORA_CHEKEO"].toString()
        val xNuevaHora: String = funcion.getHoraActual().toString()
        val xNuevaFecha: String = funcion.getFechaActual()
        var key: Int
        for (i in 0 until lista.size) {
            val valChequeo: String = lista[i]["CHEKEO"].toString()
            try {
                val misValues = ContentValues()
                misValues.put("ID_REPARTIDOR", varIdRepartidor)
                misValues.put("SES_INICIADO", varSesIniciado)
                misValues.put("CHEKEADO", valChequeo)
                misValues.put("PENDIENTE", varPendiente)
                misValues.put("VALOR_VIATICO", "0")
                key = i
                when (key) {
                    5 -> {
                        val datosCheckList: String = (presencial + "|"
                                + ubicacion.latitud + "|"
                                + ubicacion.longitud + "|"
                                + contraClave)
                        misValues.put("OBSERVACION", datosCheckList)
                        contraClave = "0"
                    }
                    6 -> {
                    }
                    7 -> misValues.put("VALOR_VIATICO", lista[7]["VALOR_VIATICO"].toString())
                    else -> misValues.put("OBSERVACION", varObservacion)
                }
                if (xHora == "") {
                    misValues.put("FECHA_CHEKEO", xNuevaFecha)
                    misValues.put("HORA_CHEKEO", xNuevaHora)
                }
                MainActivity2.bd!!.update(
                    "rpv_motivo_rep_check_list",
                    misValues,
                    "COD_MOTIVO='" + lista[i]["COD_MOTIVO"].toString() + "'",
                    null
                )
                indicGuardadoCompleto += 1
            } catch (e: Exception) {
                funcion.mensaje(
                    this,
                    "Error en tabla de configuración",
                    "Error al insertar la configuración !  " + e.message
                )
            }
        }
        if (lista.size == indicGuardadoCompleto) {
            // procedimiento para enviar checklist
            Enviar().execute()
        }
    }

    private class Enviar :
        AsyncTask<Void?, Void?, Void?>() {
        var pbarDialog: ProgressDialog? = null
        override fun onPreExecute() {
            try {
                pbarDialog!!.dismiss()
            } catch (e: java.lang.Exception) {
            }
            pbarDialog = ProgressDialog.show(context, "Un momento...", "Comprobando conexion", true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                procesaEnviaChecklist()
                null
            } catch (e: java.lang.Exception) {
                resultado = e.message.toString()
                null
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(unused: Void?) {
            if (archivoEnviado) {
                MainActivity2.funcion.mensaje(
                    context,
                    "Atención",
                    "Se guardó correctamente"
                )
            } else {
                MainActivity2.funcion.mensaje(
                    context,
                    "Error al intentar enviar",
                    "No se ha podido enviar el archivo, intente más tarde."
                )
            }

            //---------------------------------------------//
            object : CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    //startActivity(new Intent(Conf_Planilla.this, ActMapaPrueba.class));
                }

                override fun onFinish() {
                    if (archivoEnviado) {
                        startActivity(context,Intent(context, ListaPlanillas::class.java),null)
                    }
                    accion.setText("finish")
                }
            }.start()
            //---------------------------------------------//
            pbarDialog!!.dismiss()
            if (resultado != "null") {
                return
            }
            Toast.makeText(context, "Verifique su conexion a internet y vuelva a intentarlo", Toast.LENGTH_SHORT).show()
            accion.setText("finish")
            return
        }
    }

}
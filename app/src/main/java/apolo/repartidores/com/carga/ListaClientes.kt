package apolo.repartidores.com.carga

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import apolo.repartidores.com.MainActivity2
import apolo.repartidores.com.R
import apolo.repartidores.com.carga.retorno.Retornar
import apolo.repartidores.com.carga.sd.SolicitudDevolucion
import apolo.repartidores.com.utilidades.*
import kotlinx.android.synthetic.main.activity_lista_clientes.*
import kotlinx.android.synthetic.main.layout_reparto.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ListaClientes: AppCompatActivity() {

    var funcion = FuncionesUtiles()
    private var lista : ArrayList<HashMap<String, String>> = ArrayList()
    private lateinit var adapter : Adapter.AdapterClientes
    private lateinit var adapter2 : Adapter.AdapterGenericoCabecera
    lateinit var spinnerAdapter : ArrayAdapter<String>
    lateinit var ubicacion: FuncionesUbicacion
    lateinit var dispositivo: FuncionesDispositivo
    lateinit var lm: LocationManager
    lateinit var telMgr : TelephonyManager

    companion object{
        var codCliente = ""
        var codSubcliente = ""
        var descCliente = ""
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_clientes)

        inicializar()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun inicializar(){
        funcion = FuncionesUtiles(this)
        ubicacion = FuncionesUbicacion(this)
        dispositivo = FuncionesDispositivo(this)
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        cargar()
        btTabBuscar.setOnClickListener { cargar() }
        inicializarEtAccion()
        addItemSpinner(this, "Código-Descripción", "COD_CLIENTE-DESC_CLIENTE")
        btActualizar.setOnClickListener { actualizarRebote() }
        btMapa.setOnClickListener { abrirMapa() }
        btCobranza.setOnClickListener { cobranza() }
        btSD.setOnClickListener { abrirSD() }
        btRetornar.setOnClickListener { retornar() }
    }

    fun mostrarContenido(view: View) {
        tabReparto.setBackgroundColor(Color.parseColor("#474747"))
        tabRebote.setBackgroundColor(Color.parseColor("#474747"))
        tabRetirarSD.setBackgroundColor(Color.parseColor("#474747"))
        layoutReparto.visibility = View.GONE
        layoutRebote.visibility = View.GONE
        layoutRetirarSD.visibility = View.GONE
        view.setBackgroundColor(Color.parseColor("#116600"))
        if (view.id == tabReparto.id){
            layoutReparto.visibility = View.VISIBLE
        }
        if (view.id == tabRebote.id){
            layoutRebote.visibility = View.VISIBLE
        }
        if (view.id == tabRetirarSD.id){
            layoutRetirarSD.visibility = View.VISIBLE
        }
    }

    private fun cargar(){
        val sql = "SELECT DISTINCT COD_EMPRESA, COD_CLIENTE , COD_SUBCLIENTE      , NOM_CLIENTE  , NOM_SUBCLIENTE, NOM_SUBCLIENTE DESC_CLIENTE," +
                  "       SUM(TOT_COMPROBANTE) TOT_COMPROBANTE, COUNT(*) CANT, ENTRADA," +
                  "       SALIDA, DESC_ZONA, DESC_CIUDAD,SALTAR_LLEGADA,AUTORIZA_SALTAR,NRO_PLANILLA, " +
                  "       DIR_CLIENTE DIRECCION, LATITUD, LONGITUD, IND_CERRADO " +
                  "  FROM svm_planilla_reparto " +
                  " WHERE NRO_PLANILLA = '${ListaPlanillas.nroPlanilla}' " +
                  "   AND TIP_PLANILLA = '${ListaPlanillas.tipPlanilla}' " +
                  "   AND IND_CERRADO  = 'N' " +
                  "   AND COD_REPARTIDOR = '${FuncionesUtiles.usuario["LOGIN"]}' " +
                  "   AND (NOM_CLIENTE    LIKE '%${etTabBuscar.text.toString().toUpperCase(Locale.ROOT)}%' " +
                  "    OR  NOM_SUBCLIENTE LIKE '%${etTabBuscar.text.toString().toUpperCase(Locale.ROOT)}%' " +
                  "    OR  COD_CLIENTE    LIKE '%${etTabBuscar.text.toString().toUpperCase(Locale.ROOT)}%' ) " +
                  " GROUP BY COD_CLIENTE , COD_SUBCLIENTE      , NOM_CLIENTE  , NOM_SUBCLIENTE," +
                  "       ENTRADA, SALIDA, DESC_ZONA, DESC_CIUDAD,SALTAR_LLEGADA,AUTORIZA_SALTAR," +
                  "       NRO_PLANILLA "
        lista = ArrayList()
        funcion.cargarLista(lista, funcion.consultar(sql))
        mostrar()
    }

    private fun mostrar(){
        if (lista.isEmpty()){return}
        funcion.vistas  = intArrayOf(
            R.id.tv1, R.id.tv2, R.id.tv3,
            R.id.tv4, R.id.tv5, R.id.tv6,
            R.id.tv7, R.id.tv8
        )
        funcion.valores = arrayOf(
            "COD_CLIENTE",
            "COD_SUBCLIENTE",
            "NOM_SUBCLIENTE",
            "NOM_CLIENTE",
            "CANT",
            "TOT_COMPROBANTE",
            "DESC_ZONA",
            "DESC_CIUDAD"
        )
        adapter = Adapter.AdapterClientes(
            this,
            lista,
            R.layout.car_lis_cli_lista_lista_clientes,
            funcion.vistas,
            funcion.valores,
            accion
        )
        adapter2 = Adapter.AdapterGenericoCabecera(
            this,
            lista,
            R.layout.car_lis_cli_lista_lista_clientes,
            funcion.vistas,
            funcion.valores
        )
        lvClientes.adapter = adapter
        codCliente = lista[0]["COD_CLIENTE"].toString()
        codSubcliente = lista[0]["COD_SUBCLIENTE"].toString()
        descCliente = lista[0]["DESC_CLIENTE"].toString()
    }

    private fun inicializarEtAccion(){
        accion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim() == "ENTRADA") {
                    lvClientes.invalidateViews()
                }
                if (s.toString().trim() == "SALIDA") {
                    lvClientes.invalidateViews()
                }
                if (s.toString().trim() == "SALTAR_LLEGADA") {
                    lvClientes.invalidateViews()
                }
                if (s.toString().trim() == "NUEVA_UBICACION") {
                    lvClientes.invalidateViews()
                }
                if (s.toString().trim() == "RESTAURAR") {
                    autorizacion("RESTAURAR_AUTORIZADO")
                    lvClientes.invalidateViews()
                }
                if (s.toString().trim().split("-")[0] == "RESTAURAR_AUTORIZADO"){
                    restaurarVisita()
                }
                if (s.toString().trim() == "INVALIDATE") {
                    codCliente = lista[FuncionesUtiles.posicionDetalle]["COD_CLIENTE"].toString()
                    codSubcliente = lista[FuncionesUtiles.posicionDetalle]["COD_SUBCLIENTE"].toString()
                    descCliente = lista[FuncionesUtiles.posicionDetalle]["DESC_CLIENTE"].toString()
                    lvClientes.invalidateViews()
                }
                if (s.toString().trim() == "abrirUbicacion") {
                    abrirUbicacion()
                    return
                }
            }

        })
    }

    private fun addItemSpinner(context: Context, parametro: String, campo: String){
        val valoresSpinner: ArrayList<HashMap<String, String>> = ArrayList()
        val parametros = parametro.split("-").toTypedArray()
        for (i in parametros.indices){
            val valor : HashMap<String, String> = HashMap()
            valor[parametros[i]] = campo.split("-")[i]
            valoresSpinner.add(valor)
        }
        spinnerAdapter = ArrayAdapter<String>(context, R.layout.spinner_adapter, parametros)
        spTabBuscar.adapter = spinnerAdapter
        spTabBuscar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0){
                    etTabBuscar.inputType = 3 //NUMBER
                } else {
                    etTabBuscar.inputType = 1 //TEXTO
                }
            }
        }
    }

    private fun actualizarRebote(){
        if (lista[FuncionesUtiles.posicionDetalle]["ENTRADA"] == "S" &&
            lista[FuncionesUtiles.posicionDetalle]["SALIDA"] == "N"    ){
            return
        } else {
            funcion.toast(this, "Debe estar marcado solamente  la ENTRADA al cliente.")
        }
    }

    private fun abrirMapa(){
        if (lista.size == 0){
            return
        }
        val listaMapa : ArrayList<HashMap<String, String>> = ArrayList()
        listaMapa.add(lista[FuncionesUtiles.posicionDetalle])
        Mapa.lista = listaMapa
        Mapa.modificarCliente = false
        Mapa.codCliente = listaMapa[0]["COD_CLIENTE"].toString()
        Mapa.codSubcliente = listaMapa[0]["COD_SUBCLIENTE"].toString()
        Mapa.codVendedor = ""
        startActivity(Intent(this, Mapa::class.java))
    }

    private fun abrirSD(){
        startActivity(Intent(this, SolicitudDevolucion::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun retornar(){
        if (lista[FuncionesUtiles.posicionDetalle]["SALIDA"].toString().trim() == "S"){
            funcion.toast(this, "Ya posee marcación de salida.")
            return
        }
        if (lista[FuncionesUtiles.posicionDetalle]["ENTRADA"].toString().trim() == "S"){
            funcion.toast(this, "Ya posee marcación de entrada.")
            return
        }
        Retornar.etAccion = accion
        Retornar.context = this
        Retornar.nuevo = true
        val retornar = Retornar(
            codCliente, codSubcliente, lm, telMgr,
            lista[FuncionesUtiles.posicionDetalle]["LATITUD"].toString(),
            lista[FuncionesUtiles.posicionDetalle]["LONGITUD"].toString(),
            lista[FuncionesUtiles.posicionDetalle]
        )
        retornar.cargarDialogo()
    }

    private fun abrirUbicacion(){
        val configurarUbicacion = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(configurarUbicacion)
    }

    private fun cobranza(){
        if (lista[FuncionesUtiles.posicionDetalle]["ENTRADA"] == "N"){
            funcion.toast(this, "Debe marcar la llegada al cliente")
            return
        }
    }

    private fun autorizacion(accion:String){
        val dialogoAutorizacion = DialogoAutorizacion(this)
        dialogoAutorizacion.dialogoAutorizacion(accion,this.accion)
    }

    private fun restaurarVisita(): Boolean {

        //atualiza el estado de todas las tablas con el estado rechazado dejandolos inútiles
        var respuesta = false
        val sqlDelete1: String
        val sqlDelete2: String
        val sqlDelete3: String
        val sqlUpdate1: String
        val miError: String

        // Actualiza el estado de ANULADO REBOTE en svm_planilla_reparto
        // solamente aquellos que fueron anulados por primera vez

        // Busca los rebotes rechazados
        try {
            MainActivity2.bd!!.beginTransaction()
            sqlDelete1 = ("DELETE from svm_rebote_cab where "
                    + "cod_cliente		='" + codCliente + "' and "
                    + "cod_subcliente	='" + codSubcliente + "' ")
            sqlDelete2 = ("DELETE from svm_rebote where "
                    + "cod_cliente		='" + codCliente + "' and "
                    + "cod_subcliente	='" + codSubcliente + "' ")
            sqlDelete3 = ("DELETE from svm_devolucion where "
                    + "cod_cliente		='" + codCliente + "' and "
                    + "cod_subcliente	='" + codSubcliente + "' ")
            MainActivity2.bd!!.execSQL(sqlDelete1)
            MainActivity2.bd!!.execSQL(sqlDelete2)
            MainActivity2.bd!!.execSQL(sqlDelete3)
            // Actualiza el estado de ANULADO REBOTE en svm_planilla_rep
            // solamente aquellos que fueron anulados por primera vez
            sqlUpdate1 = ("Update svm_planilla_reparto set "
                    + " ENTRADA 		= 'N' ,"
                    + " SALIDA			= 'N' ,"
                    + " REP_RECHAZADO 	= 'N' ,"
                    + " REP_GUARDADOCEL = 'N' ,"
                    + " REP_ENVIADO   	= 'N' ,"
                    + " SALTAR_LLEGADA  = 'N' ,"
                    + " ESTADO_SALIDA 	= 'P' ,"
                    + " IND_REPARTO     = 'X' ,"
                    + " AUTORIZACIONES	= '' "
                    + " where COD_CLIENTE		= '" + codCliente                 + "'  "
                    + "   AND COD_SUBCLIENTE	= '" + codSubcliente              + "'     "
            		+ "   AND NRO_PLANILLA		= '" + ListaPlanillas.nroPlanilla + "' "
					+ "   AND TIP_PLANILLA		= '" + ListaPlanillas.tipPlanilla + "' "
                    )
            MainActivity2.bd!!.execSQL(sqlUpdate1)
            MainActivity2.bd!!.setTransactionSuccessful()
            MainActivity2.bd!!.endTransaction()
            eliminarDetSolicitudDevMasivo()
            //   		//--------------------------------------------------------------//
            respuesta = true
        } catch (e: Exception) {
            respuesta = false
            miError = e.localizedMessage
            print(miError)
        }
        return respuesta
    }

    private fun eliminarDetSolicitudDevMasivo() {
        //------------------------------------------------//
        MainActivity2.bd!!.beginTransaction()
        var sql = ("delete from svm_solicitud_dev_det "
                + " where COD_CLIENTE = '$codCliente' "
                + "   and COD_SUBCLIENTE = '$codSubcliente'")
        try {
            MainActivity2.bd!!.execSQL(sql)
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = " $err"
        }
        MainActivity2.bd!!.setTransactionSuccessful()
        MainActivity2.bd!!.endTransaction()

        //------------------------------------------------//
        MainActivity2.bd!!.beginTransaction()
        sql = ("delete from svm_solicitud_dev_cab "
                + " where COD_CLIENTE = '$codCliente' "
                + "   and COD_SUBCLIENTE = '$codSubcliente'")
        try {
            MainActivity2.bd!!.execSQL(sql)
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = " $err"
        }
        MainActivity2.bd!!.setTransactionSuccessful()
        MainActivity2.bd!!.endTransaction()
    }

}
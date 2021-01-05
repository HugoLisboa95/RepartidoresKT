package apolo.repartidores.com.utilidades

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import apolo.repartidores.com.MainActivity
import apolo.repartidores.com.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.dialogo_campos.*
import kotlinx.android.synthetic.main.dialogo_contacto.*
import kotlinx.android.synthetic.main.dialogo_contacto.dBtAceptar
import kotlinx.android.synthetic.main.dialogo_contacto.dBtCancelar
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FuncionesUtiles {

    //CONSTRUCTORES
    constructor()
    constructor(imgTitulo: ImageView?, tvTitulo: TextView?) { //solo titulo
        this.imgTitulo = imgTitulo
        this.tvTitulo = tvTitulo
    }

    constructor(context: Context, imgTitulo: ImageView?, tvTitulo: TextView?) { //solo titulo con funciones
        this.context = context
        this.imgTitulo = imgTitulo
        this.tvTitulo = tvTitulo
    }

    constructor(context: Context){ //funciones
        this.context = context
    }

    //titulo y buscador
    constructor(
        context: Context,
        llTitulo: LinearLayout,
        llBotonVendedor: LinearLayout,
        llBuscar: LinearLayout,
        spBuscar: Spinner,
        etBuscar: EditText,
        btBuscar: Button
    ){
        this.context = context
        llTitulo.visibility = View.GONE
        llBotonVendedor.visibility = View.GONE
        llBuscar.visibility = View.VISIBLE
        this.spBuscar = spBuscar
        this.etBuscar = etBuscar
        this.btBuscar = btBuscar
        etBuscar.inputType = 2
        spBuscar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
                    etBuscar.inputType = 3 //NUMBER
                } else {
                    etBuscar.inputType = 1 //TEXTO
                }
            }
        }
    }

    //titulo y buscador separados
    constructor(
        context: Context,
        imgTitulo: ImageView?,
        tvTitulo: TextView?,
        spBuscar: Spinner,
        etBuscar: EditText,
        btBuscar: Button
    ){
        this.context = context
        this.spBuscar = spBuscar
        this.etBuscar = etBuscar
        this.btBuscar = btBuscar
        this.imgTitulo = imgTitulo
        this.tvTitulo = tvTitulo
        this.spBuscar = spBuscar
        this.etBuscar = etBuscar
        this.btBuscar = btBuscar
        etBuscar!!.inputType = 2
        spBuscar!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
                    etBuscar!!.inputType = 3 //NUMBER
                } else {
                    etBuscar!!.inputType = 1 //TEXTO
                }
            }
        }
    }

    //titulo y lista de vendedores
    constructor(
        context: Context,
        imgTitulo: ImageView?,
        tvTitulo: TextView?,
        imgAnterior: ImageView?,
        imgSiguiente: ImageView?,
        tvVendedor: TextView?,
        contMenu: DrawerLayout,
        barraMenu: NavigationView?,
        llBotonVendedor: LinearLayout
    ) {
        this.imgTitulo = imgTitulo
        this.tvTitulo = tvTitulo
        this.imgAnterior = imgAnterior
        this.imgSiguiente = imgSiguiente
        this.tvVendedor = tvVendedor
        this.contMenu = contMenu
        this.barraMenu = barraMenu
        tvVendedor!!.setOnClickListener{
            mostrarMenu()
        }
        llBotonVendedor.visibility = View.VISIBLE
    }

    //titulo, buscador y barra de vendedores
    constructor(
        context: Context,
        imgTitulo: ImageView?,
        tvTitulo: TextView?,
        imgAnterior: ImageView?,
        imgSiguiente: ImageView?,
        tvVendedor: TextView?,
        contMenu: DrawerLayout,
        barraMenu: NavigationView?,
        llBuscar: LinearLayout?,
        spBuscar: Spinner?,
        etBuscar: EditText?,
        btBuscar: Button?
    ) {
        this.context = context
        this.imgTitulo = imgTitulo
        this.tvTitulo = tvTitulo
        this.imgAnterior = imgAnterior
        this.imgSiguiente = imgSiguiente
        this.tvVendedor = tvVendedor
        this.contMenu = contMenu
        this.barraMenu = barraMenu
        this.llBuscar = llBuscar
        this.spBuscar = spBuscar
        this.etBuscar = etBuscar
        this.btBuscar = btBuscar
        tvVendedor!!.setOnClickListener{
            mostrarMenu()
        }
        llBuscar!!.visibility = View.VISIBLE
        etBuscar!!.inputType = 2
        spBuscar!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
                    etBuscar.inputType = 3 //NUMBER
                } else {
                    etBuscar.inputType = 1 //TEXTO
                }
            }
        }
    }

    //
    constructor(
        context: Context,
        imgTitulo: ImageView?,
        tvTitulo: TextView?,
        imgAnterior: ImageView?,
        imgSiguiente: ImageView?,
        tvVendedor: TextView?,
        contMenu: DrawerLayout,
        barraMenu: NavigationView?,
        llBuscar: LinearLayout?,
        spBuscar: Spinner?,
        etBuscar: EditText?,
        btBuscar: Button?,
        llBotonVendedor: LinearLayout,
        visibilidad: Int
    ) {
        this.context = context
        this.imgTitulo = imgTitulo
        this.tvTitulo = tvTitulo
        this.imgAnterior = imgAnterior
        this.imgSiguiente = imgSiguiente
        this.tvVendedor = tvVendedor
        this.contMenu = contMenu
        this.barraMenu = barraMenu
        this.llBuscar = llBuscar
        this.spBuscar = spBuscar
        this.etBuscar = etBuscar
        this.btBuscar = btBuscar
        tvVendedor!!.setOnClickListener{
            mostrarMenu()
        }
        llBotonVendedor.visibility = visibilidad
        llBuscar!!.visibility = View.VISIBLE
        etBuscar!!.inputType = 2
        spBuscar!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
                    etBuscar.inputType = 3 //NUMBER
                } else {
                    etBuscar.inputType = 1 //TEXTO
                }
            }
        }
    }


    constructor(
        context: Context,
        imgTitulo: ImageView?,
        tvTitulo: TextView?,
        imgAnterior: ImageView?,
        imgSiguiente: ImageView?,
        tvVendedor: TextView?,
        contMenu: DrawerLayout,
        barraMenu: NavigationView?,
        llBuscar: LinearLayout?,
        spBuscar: Spinner?,
        etBuscar: EditText?,
        btBuscar: Button?,
        llBotonVendedor: LinearLayout
    ) {
        this.imgTitulo = imgTitulo
        this.tvTitulo = tvTitulo
        this.imgAnterior = imgAnterior
        this.imgSiguiente = imgSiguiente
        this.tvVendedor = tvVendedor
        this.contMenu = contMenu
        this.barraMenu = barraMenu
        this.llBuscar = llBuscar
        this.spBuscar = spBuscar
        this.etBuscar = etBuscar
        this.btBuscar = btBuscar
        tvVendedor!!.setOnClickListener{
            mostrarMenu()
        }
        llBotonVendedor.visibility = View.GONE
        barraMenu!!.layoutParams.width = 0
        llBuscar!!.visibility = View.VISIBLE
        etBuscar!!.inputType = 2
        spBuscar!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
                    etBuscar.inputType = 3 //NUMBER
                } else {
                    etBuscar.inputType = 1 //TEXTO
                }
            }
        }
    }

    constructor(
        context: Context,
        imgTitulo: ImageView,
        tvTitulo: TextView,
        llBuscar: LinearLayout,
        spBuscar: Spinner,
        etBuscar: EditText,
        btBuscar: Button
    ){
        this.context = context
        this.imgTitulo = imgTitulo
        this.tvTitulo = tvTitulo
        this.llBuscar = llBuscar
        this.spBuscar = spBuscar
        this.etBuscar = etBuscar
        this.btBuscar = btBuscar
        llBuscar.visibility = View.VISIBLE
        etBuscar.inputType = 2
        spBuscar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
                    etBuscar.inputType = 3 //NUMBER
                } else {
                    etBuscar.inputType = 1 //TEXTO
                }
            }
        }
    }


    //Variables
    companion object{
        lateinit var cursor: Cursor
        val usuario : HashMap<String, String> = HashMap()
        var posicionCabecera: Int = 0
        var posicionDetalle : Int = 0
        var posicionDetalle2: Int = 0
        var posicionGenerico: Int = 0
        var listaCabecera: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
        var listaDetalle: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
        var listaDetalle2: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
        var subListaDetalle: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList<ArrayList<HashMap<String, String>>>()
        var subListaDetalle2: ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> =  ArrayList<ArrayList<ArrayList<HashMap<String, String>>>>()
        val formatoNumeroEntero: DecimalFormat = DecimalFormat("###,###,###.##")
        val formatoNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
        var formatoGenerico: NumberFormat = NumberFormat.getInstance()
        var intMarcacion = 0
        var ultimaVenta : Int = -1
    }

    //adaptador
    var llBuscar: LinearLayout? = null
    var spBuscar: Spinner?  = null
    var etBuscar: EditText? = null
    var btBuscar: Button?   = null
    var imgTitulo: ImageView? = null
    var tvTitulo: TextView? = null
    var imgAnterior: ImageView? = null
    var imgSiguiente: ImageView? = null
    var tvVendedor : TextView? = null
    var tvSupervisor : TextView? = null
    var tvGerentes : TextView? = null
    var contMenu: DrawerLayout? = null
    var barraMenu: NavigationView? = null
    var context : Context? = null
    var spinnerAdapter : ArrayAdapter<String>? = null
    var valoresSpinner: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
    var parametros : Array<String> = arrayOf<String>()
    lateinit var vistas : IntArray
    lateinit var vistasCabecera : IntArray
    lateinit var valores: Array<String>
    lateinit var subVistas : IntArray
    lateinit var subValores: Array<String>
    lateinit var subVistas2 : IntArray
    lateinit var subValores2: Array<String>
    lateinit var listaVendedores: ArrayList<HashMap<String, String>>
    lateinit var listaSupervisores: ArrayList<HashMap<String, String>>
    lateinit var listaGerentes: ArrayList<HashMap<String, String>>
    lateinit var adapter: Adapter.AdapterGenericoCabecera
    var posVend : Int = 0
    var posSup : Int = 0
    var posGer : Int = 0

    //FUNCIONES DE BD
    fun dato(cursor: Cursor, index: String): String {
        try {
            return cursor.getString(cursor.getColumnIndex(index))
        } catch (e: java.lang.Exception){
            return ""
        }
    }
    fun datoEntero(cursor: Cursor, index: String): Int{
        return cursor.getInt(cursor.getColumnIndex(index))
    }
    fun datoDecimal(cursor: Cursor, index: String): Double{
        return cursor.getDouble(cursor.getColumnIndex(index))
    }
    fun datoPorcentaje(cursor: Cursor, totalS: String, valorS: String):Double{

        val total: Double =
            cursor.getString(cursor.getColumnIndex(totalS))
                .replace(".", "")
                .replace(",", ".")
                .replace("%", ""
        ).toDouble()
        val valor: Double =
            cursor.getString(cursor.getColumnIndex(valorS)).replace(".", "").replace(",", ".").replace(
            "%",
            ""
        ).toDouble()

        return (valor*100)/total
    }
    @SuppressLint("Recycle")
    fun consultar(sql: String): Cursor{
        return try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
            cursor
        } catch (e: Exception){
            e.message
            cursor
        }
    }
    fun cargarLista(lista: ArrayList<HashMap<String, String>>, cursor: Cursor){
        for (i in 0 until cursor.count){
            val datos : HashMap<String, String> = HashMap()
            for(j in 0 until cursor.columnCount){
                datos[cursor.getColumnName(j)] = dato(cursor, cursor.getColumnName(j))
            }
            lista.add(datos)
            cursor.moveToNext()
        }
    }
    fun cargarValores(valores: ContentValues, cursor: Cursor){
        for(j in 0 until cursor.columnCount){
            valores.put(cursor.getColumnName(j),dato(cursor, cursor.getColumnName(j)))
        }
    }
    fun ejecutar(sql: String, context: Context): Boolean {
        return try {
            MainActivity.bd!!.execSQL(sql)
            true
        } catch (e: Exception) {
            val dialogo:AlertDialog.Builder = AlertDialog.Builder(context)
            dialogo.setMessage(e.message)
            dialogo.setTitle("ERROR")
            dialogo.show()
    //            Toast.makeText(context,"Error al ejecutar " + sql,Toast.LENGTH_LONG).show()
            false
        }
    }
    fun insertar(tabla: String, valores: ContentValues){
        try {
            MainActivity.bd!!.insert(tabla, null, valores)
//            mensaje("Correcto","Insertado correctamente")
        } catch (e: Exception){
            mensaje("Error", e.message.toString())
        }
    }
    fun insertar(tabla: String, campos: String, valores: ContentValues){
        try {
            MainActivity.bd!!.insert(tabla, campos, valores)
//            mensaje("Correcto","Insertado correctamente")
        } catch (e: Exception){
            mensaje("Error", e.message.toString())
        }
    }
    fun actualizar(tabla: String, valores: ContentValues, where: String){
        try {
            MainActivity.bd!!.update(tabla, valores, where, null)
            mensaje("Correcto", "Acualizado correctamente")
        } catch (e: Exception){
            mensaje("Error", e.message.toString())
        }
    }
    fun addItemSpinner(context: Context, parametro: String, campo: String){
        parametros = parametro.split("-").toTypedArray()
        for (i in parametros.indices){
            val valor : HashMap<String, String> = HashMap()
            valor[parametros[i]] = campo.split("-")[i]
            valoresSpinner!!.add(valor)
        }
        spinnerAdapter = ArrayAdapter<String>(context, R.layout.spinner_adapter, parametros)
        spBuscar!!.adapter = spinnerAdapter
    }
    fun buscar(tabla: String) : Cursor{
        var sql: String = ""
        if (spBuscar!!.selectedItemPosition != 0){
            sql = "SELECT * FROM " + tabla +
                    " WHERE " + valoresSpinner!!.get(spBuscar!!.selectedItemPosition).get(spBuscar!!.selectedItem) +
                    "  LIKE '%"  + etBuscar!!.text.toString() + "%' "
            tvVendedor!!.setText("Todos")
            return consultar(sql)
        }
        sql = "SELECT * FROM " + tabla +
              " WHERE " + valoresSpinner!!.get(spBuscar!!.selectedItemPosition).get(spBuscar!!.selectedItem) +
              "  LIKE '%"  + etBuscar!!.text.toString() + "%' "
        tvVendedor!!.setText("Todos")
        return consultar(sql)
    }
    fun buscar(tabla: String, campos: String?, groupBy: String?, orderBy: String?) : Cursor{
        var sql: String = ""
        if (spBuscar!!.selectedItemPosition != 0){
            sql = "SELECT " + campos + " FROM " + tabla +
                    " WHERE " + valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem]!!.split(",")[0].toUpperCase(
                Locale.ROOT) +
                    "  LIKE '%"  + etBuscar!!.text.toString() + "%' "
            if (valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem]!!.split(",").size>1){
                for(i in 1 until valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem]!!.split(
                    ","
                ).size){
                    sql = "$sql OR " + valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem]!!.split(
                        ","
                    )[i].toUpperCase(Locale.ROOT) +
                            " LIKE '%" + etBuscar!!.text.toString() + "%' "
                }
            }
        } else {
            sql = "SELECT " + campos + " FROM " + tabla +
                    " WHERE " + valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem] +
                    "  LIKE '%"  + etBuscar!!.text.toString() + "%' "
        }
        if (!groupBy.equals("")){ sql = "$sql GROUP BY $groupBy" }
        if (!orderBy.equals("")){ sql = "$sql ORDER BY $orderBy" }
        tvVendedor!!.text = "Todos"
        return consultar(sql)
    }
    fun buscar(tabla: String, campos: String?, groupBy: String?, orderBy: String?, where: String) : Cursor{
        var sql = ""
        if (spBuscar!!.selectedItemPosition != 0){
            sql = "SELECT " + campos + " FROM " + tabla +
                    " WHERE " + valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem]!!.split(",")[0].toUpperCase() +
                    "  LIKE '%"  + etBuscar!!.text.toString() + "%' "
            if (valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem]!!.split(",").size>1){
                for(i in 1 until valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem]!!.split(",").size){
                    sql = sql + " OR " + valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem]!!.split(",")[i].toUpperCase() +
                            " LIKE '%" + etBuscar!!.text.toString() + "%' "
                }
            }
        } else {
            sql = "SELECT " + campos + " FROM " + tabla +
                    " WHERE " + valoresSpinner[spBuscar!!.selectedItemPosition][spBuscar!!.selectedItem].toString().split(",")[0] +
                    "  LIKE '%"  + etBuscar!!.text.toString() + "%' "
        }
        sql += where
        if (!groupBy.equals("")){ sql = "$sql GROUP BY $groupBy" }
        if (!orderBy.equals("")){ sql = "$sql ORDER BY $orderBy" }
//        if (tvVendedor!! != null){
//            tvVendedor!!.setText("Todos")
//        }
        return consultar(sql)
    }
    fun ultPedidoVenta(codVendedor: String):Int{
        val sql : String = ("SELECT NUMERO MAXIMO FROM svm_vendedor_pedido  WHERE COD_VENDEDOR = '${codVendedor}'")
        return datoEntero(consultar(sql), "MAXIMO")
    }
    fun cargarDatos(cursor: Cursor):ArrayList<HashMap<String, String>>{
        var lista = ArrayList<HashMap<String, String>>()
        for (i in 0 until cursor.count){
            var dato : HashMap<String, String> = HashMap<String, String>()
            for (j in 0 until cursor.columnCount){
                try {dato.put(cursor.getColumnName(j), dato(cursor, cursor.getColumnName(j)))} catch (e: Exception){}
            }
            lista.add(dato)
            cursor.moveToNext()
        }
        return lista
    }

    //FUNCIONES DE FORMATO DE NUMEROS
    fun entero(entero: String):String{
        if (entero.trim() == ""){
            return "0"
        }
        return formatoNumeroEntero.format(
            entero.replace(".", "").replace("null", "0").trim().toInt()
        )
    }
    fun entero(entero: Int):String{
        return formatoNumeroEntero.format(entero)
    }
    fun aEntero(numero: String):Int{
        return numero.replace(".", "").toInt()
    }
    fun enteroCliente(entero: String):String{
        if (entero.trim() == ""){
            return "0"
        }
        if (entero.trim().indexOf(".")>-1){
            return "0"
        }
        return formatoNumeroEntero.format(
            entero.replace(".", "").replace("null", "0").trim().toInt()
        )
    }
    fun enteroLargo(entero: String):String{
        if (entero.trim() == ""){
            return "0"
        }
        if (entero.trim().indexOf(".")>-1){
            return "0"
        }
        return formatoNumeroEntero.format(entero.replace(".","").replace("null","0").trim().toInt())
    }
    fun decimal(decimal: String):String{
        if (decimal.trim() == "" || decimal.trim() == "null"){
            return "0.0"
        }
        if (decimal.indexOf(",")>-1){
            return formatoNumeroDecimal.format(
                decimal.replace(".", "")
                    .replace(",", ".")
                    .replace("%", "").trim().toDouble()
            ).toString()
        }
        return formatoNumeroDecimal.format(
            decimal.replace(",", ".")
                .replace("%", "")
                .replace("null", "")
                .trim().toDouble()
        ).toString()
    }
    fun decimal(decimal: String, cantidad: Int):String{
        formatoGenerico.minimumFractionDigits = cantidad
        formatoGenerico.maximumFractionDigits = cantidad
        if (decimal.trim().equals("")||decimal.trim().equals("null")){
            return "0.0"
        }
        if (decimal.indexOf(",")>-1){
            return formatoGenerico.format(
                decimal.replace(".", "")
                    .replace(",", ".")
                    .replace("%", "").trim().toDouble()
            ).toString()
        }
        return formatoGenerico.format(
            decimal.replace(",", ".")
                .replace("%", "")
                .replace("null", "")
                .trim().toDouble()
        ).toString()
    }
    fun decimal(decimal: Double):String{
        return formatoNumeroDecimal.format(decimal)
    }
    fun decimalPunto(decimal: String):String{
        return decimal(decimal).replace(".", "").replace(",", ".")
    }
    fun numero(decimales: String, numero: String):String{
        formatoGenerico.minimumFractionDigits = decimales.toInt()
        formatoGenerico.maximumFractionDigits = decimales.toInt()
        return formatoGenerico.format(numero.replace(",", ".").toDouble())
    }
    fun porcentaje(decimal: String):String{
        return decimal(decimal) + "%"
    }
    fun porcentaje(decimal: Double):String{
        return formatoNumeroDecimal.format(decimal) + "%"
    }
    fun inicializaContadores(){
        posicionCabecera = 0
        posicionDetalle = 0
        posicionGenerico = 0
    }
    fun fechaHora(fecha: String): Date {
        val hourFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return hourFormat.parse(fecha)
    }
    fun fecha(fecha: String): Date {
        val hourFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        return hourFormat.parse(fecha)
    }
    fun fechaF(fecha: String): Date {
        val hourFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        return hourFormat.parse(hourFormat.format(fecha))
    }
    fun tiempoTranscurrido(fecha1: String, fecha2: String):Int{
        var diferencia : Long = (fechaHora(fecha2).time/60000) - (fechaHora(fecha1).time / 60000)
        if (diferencia<0){
            diferencia *= (-1)
        }
        return diferencia.toInt()
    }
    fun convertirFechatoSQLFormat(fecha: String): String? {
        var fecha = fecha
        var res = ""
        var dia = ""
        var mes = ""
        var año = ""
        fecha = fecha.replace("/", "")
        fecha = fecha.replace(" ", "")
        for (i in fecha.indices) {
            if (i < 2) {
                dia += fecha[i]
            } else {
                if (i < 4) {
                    mes += fecha[i]
                } else {
                    año += fecha[i]
                }
            }
        }
        res = "$año-$mes-$dia"
        return res
    }

    //FECHAS
    fun getDiaDeLaSemana(str_fecha: String?): String? {
        val formatoDelTexto = SimpleDateFormat("dd/MM/yyyy")
        var fecha: Date? = null
        try {
            fecha = formatoDelTexto.parse(str_fecha)
            val cal: Calendar = GregorianCalendar.getInstance()
            cal.setTime(fecha)
            return getDia(cal.get(Calendar.DAY_OF_WEEK))
        } catch (ex: java.lang.Exception) {
        }
        return "Lunes"
    }
    fun getDia(dia: Int):String{
        when(dia){
            1 -> return "Domingo"
            2 -> return "Lunes"
            3 -> return "Martes"
            4 -> return "Miercoles"
            5 -> return "Jueves"
            6 -> return "Viernes"
            7 -> return "Sabado"
            else -> return "No corresponde"
        }
    }
    fun getDiaDeLaSemana(): String {
        val cal = GregorianCalendar.getInstance()
        return getDia(cal[Calendar.DAY_OF_WEEK])
    }
    fun getMes():Int{
        return getFechaActual().split("/")[1].toInt()
    }
    fun getMes(mes: String):String{
        when (mes){
            "01" -> return "Enero"
            "02" -> return "Febrero"
            "03" -> return "Marzo"
            "04" -> return "Abril"
            "05" -> return "Mayo"
            "06" -> return "Junio"
            "07" -> return "Julio"
            "08" -> return "Agosto"
            "09" -> return "Setiembre"
            "10" -> return "Octubre"
            "11" -> return "Noviembre"
            "12" -> return "Diciembre"
            else -> return "Valor no corresponde"
        }
    }
    fun getMes(mes: Int):String{
        when (mes){
            1 -> return "Enero"
            2 -> return "Febrero"
            3 -> return "Marzo"
            4 -> return "Abril"
            5 -> return "Mayo"
            6 -> return "Junio"
            7 -> return "Julio"
            8 -> return "Agosto"
            9 -> return "Setiembre"
            10 -> return "Octubre"
            11 -> return "Noviembre"
            12 -> return "Diciembre"
            else -> return "Valor no corresponde"
        }
    }
    fun getHoraActual(): String? {
        val calendario = Calendar.getInstance()
        val hora: Int
        val minutos: Int
        val segundos: Int
        hora = calendario[Calendar.HOUR_OF_DAY]
        minutos = calendario[Calendar.MINUTE]
        segundos = calendario[Calendar.SECOND]

        // GUARDAR LA HORA
        var _hora = ""
        _hora += if (hora < 10) {
            "0$hora:"
        } else {
            "$hora:"
        }
        _hora += if (minutos < 10) {
            "0$minutos:"
        } else {
            "$minutos:"
        }
        if (segundos < 10) {
            _hora += "0$segundos"
        } else {
            _hora += segundos
        }
        return _hora
    }
    fun getFechaActual():String{
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        val cal = Calendar.getInstance()
        return dfDate.format(cal.getTime()) + ""
    }
    fun getFechaHoraActual():String{
        return getFechaActual() + " " + getHoraActual()
    }
    fun fechaRuteo(context: Context):Boolean{
        var d: Date? = null
        var d1: Date? = null
        val cal = Calendar.getInstance()
        val sql : String = ("SELECT numero MAXIMO, ind_palm, ultima_sincro, RANGO, TIPO_promotor, MIN_FOTO, MAX_FOTO, IND_FOTO, FEC_CARGA_RUTEO, MAX_DESC "
                + ",version, MAX_DESC_VAR, PER_VENDER, INT_MARCACION  from svm_vendedor_pedido_venta  where COD_promotor ="
                + "'" + usuario.get("LOGIN") + "'")
        val cursor : Cursor = consultar(sql)
        var fecha : String = ""
        if (cursor.count>0){
            fecha = try {
                dato(cursor, "FEC_CARGA_RUTEO").trim()
            } catch (e: java.lang.Exception){
                ""
            }
        }
        if (fecha == ""){
            mensaje(context, "Atención!", "No se ha habilitado este dia para enviar ruteo.")
            return false
        }
        try {
            d = fecha(fecha)
            d1 = fechaF(cal.getTime().toString())
        } catch (e: java.text.ParseException) {
            e.printStackTrace();
        }

        val diffInDays : Int = ((d1!!.time - d!!.time) / (1000 * 60 * 60 * 24)) as Int

        if (diffInDays != 0) {
            mensaje(context, "Atención!", "No se ha habilitado este dia para enviar ruteo.")
            return false
        }
        return true
    }


    //MENU
    fun mostrarMenu(){
        if (contMenu!!.isDrawerOpen(GravityCompat.START)) {
            contMenu!!.closeDrawer(GravityCompat.START)
        } else {
            contMenu!!.openDrawer(GravityCompat.START)
        }
    }
    fun cargarTitulo(icon: Int, titulo: String){
        imgTitulo!!.setImageResource(icon)
        tvTitulo!!.text = titulo
    }
    @SuppressLint("SetTextI18n")
    fun actualizaVendedor(context: Context){
        if (posVend == listaVendedores.size){
            Toast.makeText(context, "Es el ultimo registro", Toast.LENGTH_SHORT).show()
            posVend--
        } else {
            if (posVend == -1){
                Toast.makeText(context, "Es el primer registro", Toast.LENGTH_SHORT).show()
                posVend++
            } else {
                tvVendedor!!.text = listaVendedores[posVend]["codigo"] + "-" +
                        listaVendedores[posVend]["descripcion"]
                inicializaContadores()
            }
        }
    }
    fun actualizaSupervisor(context: Context){
        if (posSup == listaSupervisores.size){
            Toast.makeText(context, "Es el ultimo registro", Toast.LENGTH_SHORT).show()
            posSup--
        } else {
            if (posSup == -1){
                Toast.makeText(context, "Es el primer registro", Toast.LENGTH_SHORT).show()
                posSup++
            } else {
                tvSupervisor!!.setText(
                    listaSupervisores.get(posSup).get("codigo") + "-" +
                            listaSupervisores.get(posSup).get("descripcion")
                )
                inicializaContadores()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun actualizaGerentes(context: Context){
        if (posGer == listaGerentes.size){
            Toast.makeText(context, "Es el ultimo registro", Toast.LENGTH_SHORT).show()
            posGer--
        } else {
            if (posGer == -1){
                Toast.makeText(context, "Es el primer registro", Toast.LENGTH_SHORT).show()
                posGer++
            } else {
                tvGerentes!!.text = listaGerentes[posGer]["codigo"] + "-" + listaGerentes[posGer]["descripcion"]
                inicializaContadores()
            }
        }
    }
    fun listaVendedores(codVendedor: String, descVendedor: String, tabla: String){
        val sql = ("SELECT DISTINCT " + codVendedor + "," + descVendedor + " "
                + " FROM " + tabla
                + " ORDER BY CAST(" + codVendedor + " AS NUMBER)")
        cargarVendedores(sql, codVendedor, descVendedor)
    }
    fun listaSupervisores(codSupervisor: String, descSupervisor: String, tabla: String){
        val sql = ("SELECT DISTINCT " + codSupervisor + "," + descSupervisor + " "
                + " FROM " + tabla
                + " ORDER BY CAST(" + codSupervisor + " AS NUMBER)")
        cargarSupervisores(sql, codSupervisor, descSupervisor)
    }
    fun listaGerentes(codGerente: String, descGerente: String, tabla: String){
        val sql = ("SELECT DISTINCT " + codGerente + "," + descGerente + " "
                + " FROM " + tabla
                + " ORDER BY CAST(" + codGerente + " AS NUMBER)")
        cargarGerentes(sql, codGerente, descGerente)
    }
    fun listaVendedores(codVendedor: String, descVendedor: String, sql: String, orderBy: String){
        cargarVendedores(sql, codVendedor, descVendedor)
    }
    fun listaSupervisores(
        codSupervisor: String,
        descSupervisor: String,
        sql: String,
        orderBy: String
    ){
        cargarSupervisores(sql, codSupervisor, descSupervisor)
    }
    fun listaGerentes(codGerentes: String, descGerentes: String, sql: String, orderBy: String){
        cargarGerentes(sql, codGerentes, descGerentes)
    }
    @SuppressLint("Recycle", "SetTextI18n")
    fun cargarVendedores(sql: String, codVendedor: String, descVendedor: String){
        try {
            cursor = consultar(sql)
            val codigo = dato(cursor,codVendedor)
            val descripcion = dato(cursor,descVendedor)
            tvVendedor!!.text = "$codigo-$descripcion"

        } catch (e: Exception){
            var error = e.message
            return
        }
        barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text = "Vendedores"
        barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvNombreSup).text = "Nom.: " + usuario["NOMBRE"]
        barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoSup).text = "Cod.: " + usuario["LOGIN"]
        barraMenu!!.menu.clear()

        listaVendedores = ArrayList()
        for (i in 0 until cursor.count){
            val codigo = dato(cursor, codVendedor)
            val descripcion = dato(cursor, descVendedor)
            barraMenu!!.menu.add("$codigo-$descripcion").setIcon(R.drawable.ic_usuario)
            addVendedor(codigo, descripcion)
            cursor.moveToNext()
        }
    }
    @SuppressLint("SetTextI18n")
    fun cargarSupervisores(sql: String, codVendedor: String, descVendedor: String){
        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
            val codigo = cursor.getString(cursor.getColumnIndex(codVendedor))
            val descripcion = cursor.getString(cursor.getColumnIndex(descVendedor))
            tvSupervisor!!.text = "$codigo-$descripcion"
        } catch (e: Exception){
            var error = e.message
            return
        }
        barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text = "Supervisores"
        barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvNombreSup).text = "Ger.: " + usuario["NOMBRE"]
        barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoSup).text = "Cod.: " + usuario["LOGIN"]
        barraMenu!!.menu.clear()

        listaSupervisores = ArrayList()
        for (i in 0 until cursor.count){
            var codigo = dato(cursor, codVendedor)
            var descripcion = dato(cursor, descVendedor)
            barraMenu!!.menu.add("$codigo-$descripcion").setIcon(R.drawable.ic_usuario)
            addSupervisor(codigo, descripcion)
            cursor.moveToNext()
        }
    }
    @SuppressLint("SetTextI18n")
    fun cargarGerentes(sql: String, codVendedor: String, descVendedor: String){
        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
            var codigo = cursor.getString(cursor.getColumnIndex(codVendedor))
            var descripcion = cursor.getString(cursor.getColumnIndex(descVendedor))
            tvGerentes!!.text = "$codigo-$descripcion"

        } catch (e: Exception){
            var error = e.message
            return
        }
        barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text = "Gerentes"
        barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvNombreSup).text = "Ger.: " + usuario.get(
            "NOMBRE"
        )
        barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoSup).text = "Cod.: " + usuario["LOGIN"]
        barraMenu!!.menu.clear()

        listaGerentes = ArrayList()
        for (i in 0 until cursor.count){
            val codigo = dato(cursor, codVendedor)
            val descripcion = dato(cursor, descVendedor)
            barraMenu!!.menu.add("$codigo-$descripcion").setIcon(R.drawable.ic_usuario)
            addGerentes(codigo, descripcion)
            cursor.moveToNext()
        }
    }
    private fun addVendedor(codigo: String, descripcion: String){
        val dato : HashMap<String, String> = HashMap()
        dato["codigo"] = codigo
        dato["descripcion"] = descripcion
        listaVendedores.add(dato)
    }
    private fun addSupervisor(codigo: String, descripcion: String){
        val dato : HashMap<String, String> = HashMap()
        dato.put("codigo", codigo)
        dato.put("descripcion", descripcion)
        listaSupervisores.add(dato)
    }
    private fun addGerentes(codigo: String, descripcion: String){
        val dato : HashMap<String, String> = HashMap()
        dato.put("codigo", codigo)
        dato.put("descripcion", descripcion)
        listaGerentes.add(dato)
    }

    //DIALOGOS
    fun mensaje(context: Context, titulo: String, mensaje: String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        dialogo.setPositiveButton("OK", null)
        dialogo.show()
    }
    fun mensaje(titulo: String, mensaje: String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        dialogo.show()
    }
    fun getIntervaloMarcacion(): Int {
        val sql : String =
            ("SELECT numero MAXIMO, ind_palm, ultima_sincro, RANGO, TIPO_promotor, MIN_FOTO, MAX_FOTO, IND_FOTO, FEC_CARGA_RUTEO, MAX_DESC "
                    + ",version, MAX_DESC_VAR, PER_VENDER, INT_MARCACION  from svm_vendedor_pedido  where COD_promotor ="
                    + "'" + usuario.get("LOGIN") + "'")
        return if (consultar(sql).count>0){
            datoEntero(consultar(sql), "INT_MARCACION")
        } else {
            0
        }
    }
    fun getRangoDistancia(): Int {
        val sql = "SELECT RANGO from svm_vendedor_pedido  where COD_VENDEDOR = '${usuario["LOGIN"]}'"
        return if (consultar(sql).count>0){
            datoEntero(consultar(sql), "RANGO")
        } else {
            0
        }
    }
    fun dialogoEntrada(et: EditText, context: Context){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        val entrada = EditText(context)
        dialogo.setTitle(et.hint)
        entrada.inputType = InputType.TYPE_CLASS_TEXT
        dialogo.setView(entrada)
        entrada.text = et.text
        entrada.requestFocus()
        dialogo.setPositiveButton(
            "OK"
        ) { _: DialogInterface, i: Int ->
            et.text = entrada.text
        }
        dialogo.setCancelable(false)
        dialogo.show()
    }
    fun dialogoEntrada(et: EditText, context: Context, etAccion: EditText, accion: String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        val entrada = EditText(context)
        dialogo.setTitle(et.hint)
        entrada.inputType = InputType.TYPE_CLASS_TEXT
        dialogo.setView(entrada)
        entrada.text = et.text
        dialogo.setPositiveButton(
            "OK"
        ) { _: DialogInterface, i: Int ->
            et.text = entrada.text
            etAccion.setText(accion)
        }
        dialogo.setCancelable(false)
        dialogo.show()
    }
    fun dialogoEntradaTextoLargo(et: EditText, context: Context){
        val dialogo = Dialog(context)
        dialogo.setContentView(R.layout.dialogo_texto_largo)
        val titulo  = dialogo.findViewById<TextView>(R.id.tvTitulo)
        val entrada = dialogo.findViewById<EditText>(R.id.etTexto)
        val aceptar = dialogo.findViewById<Button>(R.id.dtlBtAceptar)
        val cancela = dialogo.findViewById<Button>(R.id.dtlBtCancelar)

        titulo.text = et.hint
        entrada.text = et.text
        aceptar.setOnClickListener {
            et.text = entrada.text
            dialogo.dismiss()
        }
        cancela.setOnClickListener {
            dialogo.dismiss()
        }
        dialogo.setCancelable(false)
        dialogo.show()
    }
    fun dialogoEntradaContacto(etNombre: EditText, etTelefono: EditText, context: Context){
        val dialogo  = Dialog(context)
        dialogo.setContentView(R.layout.dialogo_contacto)
        dialogo.etNombre.text = etNombre.text
        dialogo.etTelefono.text = etTelefono.text
        dialogo.dBtAceptar.setOnClickListener {
            etNombre.setText(dialogo.etNombre.text.toString().toUpperCase())
            etTelefono.setText(dialogo.etTelefono.text.toString().toUpperCase())
            dialogo.dismiss()
        }
        dialogo.dBtCancelar.setOnClickListener { dialogo.dismiss() }
        dialogo.setCancelable(false)
        dialogo.show()
    }
    fun dialogoEntradaDosCampos(et1: EditText, et2: EditText, tipo1:Int, tipo2:Int, context: Context){
        val dialogo  = Dialog(context)
        dialogo.setContentView(R.layout.dialogo_campos)
        dialogo.tv1.text = et1.hint
        dialogo.tv2.text = et2.hint
        dialogo.et1.text = et1.text
        dialogo.et2.text = et2.text
        dialogo.tv1.inputType = tipo1
        dialogo.tv2.inputType = tipo2
        dialogo.dBtAceptar.setOnClickListener {
            et1.setText(dialogo.et1.text.toString().toUpperCase())
            et2.setText(dialogo.et2.text.toString().toUpperCase())
            dialogo.dismiss()
        }
        dialogo.dBtCancelar.setOnClickListener { dialogo.dismiss() }
        dialogo.setCancelable(false)
        dialogo.show()
    }
    fun dialogoEntradaCalculo(et1: EditText, et2: EditText, et3:EditText, context: Context){
        val dialogo  = Dialog(context)
        dialogo.setContentView(R.layout.dialogo_campos)
        dialogo.tv1.text = et1.hint
        dialogo.tv2.text = et2.hint
        dialogo.et1.setText(et1.text.toString().replace(".","").replace(",",""))
        dialogo.et2.setText(et2.text.toString().replace(".","").replace(",",""))
        dialogo.et1.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.et2.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.dBtAceptar.setOnClickListener {
            et1.setText(numero("0",dialogo.et1.text.toString().toUpperCase()))
            et2.setText(numero("0",dialogo.et2.text.toString().toUpperCase()))
            et3.setText(numero("0",(dialogo.et1.text.toString().toInt() * dialogo.et2.text.toString().toInt()).toString()))
            dialogo.dismiss()
        }
        dialogo.dBtCancelar.setOnClickListener { dialogo.dismiss() }
        dialogo.setCancelable(false)
        dialogo.show()
    }
    fun dialogoEntradaNumero(et: EditText, context: Context){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        val entrada = EditText(context)
        dialogo.setTitle(et.hint)
        entrada.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.setView(entrada)
        entrada.text = et.text
        if (et.text.toString().trim().equals("0")){
            entrada.setText("")
        }
        dialogo.setPositiveButton(
            "OK"
        ) { _: DialogInterface, i: Int ->
            if (entrada.text.trim() == "" || entrada.text.trim() == "null" || entrada.text.isEmpty()
            ) {
                entrada.setText("0")
            }
            et.setText(entrada.text.toString().toInt().toString())
        }
        dialogo.setCancelable(false)
        dialogo.show()
    }
    fun dialogoEntradaNumeroDecimal(et: EditText, context: Context){
        var dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        var entrada:EditText = EditText(context)
        dialogo.setTitle(et.hint)
        dialogo.setView(entrada)
//        entrada.inputType = InputType.TYPE_CLASS_PHONE
        entrada.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        entrada.keyListener = DigitsKeyListener.getInstance(false, true)
        entrada.text = et.text
        if (et.text.toString().trim().equals("0")){
            entrada.setText("")
        }
        dialogo.setPositiveButton(
            "OK",
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                if (entrada.text.trim().equals("") || entrada.text.trim()
                        .equals("null") || entrada.text.isEmpty()
                ) {
                    entrada.setText("0.0")
                }
                et.setText(decimal(entrada.text.toString().replace(",", ".")))
            })
        dialogo.setCancelable(false)
        dialogo.show()
    }

    //DATOS
    fun codPersona():String{
        var sql : String = "SELECT DISTINCT COD_PERSONA FROM svm_vendedor_pedido"
        var cursor : Cursor = consultar(sql)
        if (cursor.count < 1){
            MainActivity.codPersona = ""
            return ""
        } else {
            MainActivity.codPersona = dato(cursor, "COD_PERSONA")
            return dato(cursor, "COD_PERSONA")
        }
    }
    fun maxDescuento():Double{
        val sql : String = ("SELECT NUMERO MAXIMO, IND_PALM, ULTIMA_SINCRO, RANGO, MIN_FOTOS, "
                +  "       MAX_FOTOS, IND_FOTO, "
                +  "       VERSION, PER_VENDER  "
                +  "  FROM svm_vendedor_pedido WHERE COD_SUPERVISOR = '${FuncionesUtiles.usuario.get(
            "PASS"
        )}'")
        return datoDecimal(consultar(sql), "IND_PALM")
    }
    fun ultimoNroOrden(tabla: String): Int {
        val cursor: Cursor
        val sql: String = ("SELECT  MAX(id),nro_orden FROM '" + tabla + "' "
                + "where est_envio= 'N'")
        cursor = consultar(sql)
        var id: String? = ""
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex("NRO_ORDEN"))
            if (id == null) {
                id = "0"
            }
        } else {
            id = "0"
        }
        return id.toInt()
    }
    fun recalcularNroOrden(tabla: String, context: Context) {
        val cursor: Cursor
        var upd: String
        val sql: String = ("SELECT  id FROM '" + tabla + "' "
                + "where est_envio= 'N' ORDER BY id ASC ")
        cursor = consultar(sql)
        val nreg = cursor.count
        cursor.moveToFirst()
        var cont = 0
        var id = ""
        for (i in 0 until nreg) {
            cont += 1
            id = cursor.getString(cursor.getColumnIndex("id"))
            upd = ("UPDATE '" + tabla + "' SET nro_orden = '" + cont + "" + "' "
                    + "WHERE id=" + id)
            ejecutar(upd, context)
            cursor.moveToNext()
        }
    }

    fun toast(context: Context, mensaje: String){
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
    }

    //DIALOGOS DE ENTRADA DE DATOS
}
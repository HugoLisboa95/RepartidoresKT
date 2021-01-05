package apolo.repartidores.com.utilidades

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import apolo.repartidores.com.R
import apolo.repartidores.com.carga.CheckList
import apolo.repartidores.com.carga.ListaClientes
import apolo.repartidores.com.carga.ListaPlanillas
import kotlinx.android.synthetic.main.ven_lista_sd_detalles.view.*
import java.lang.Exception
import java.text.DecimalFormat

class Adapter {

    companion object{
        val formatNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
        lateinit var etAccion : EditText
    }

    class AdapterGenericoCabecera(private val context: Context,
                                  private val dataSource: ArrayList<HashMap<String, String>>,
                                  private val molde: Int,
                                  private val vistas: IntArray,
                                  private val valores: Array<String>) : BaseAdapter()
    {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).visibility = View.VISIBLE
                    rowView.findViewById<TextView>(vistas[i]).text= dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionCabecera == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        fun getTotalEntero(index: String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource[i][index].toString().replace(".", ""))
            }

            return totalValor
        }

        fun getPorcDecimal(index: String):Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    totalPorcCump += formatNumeroDecimal.format(
                            dataSource[i][index].toString().replace(".", "").replace(",", ".")
                                    .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index], Toast.LENGTH_SHORT).show()
                }
            }

            return totalPorcCump/dataSource.size
        }

        fun getPorcDecimal(index: String,total:Double):Double{

            var valor = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    valor += formatNumeroDecimal.format(
                            dataSource[i][index].toString().replace(".", "").replace(",", ".")
                                    .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index], Toast.LENGTH_SHORT).show()
                }
            }

            return (total*100)/valor
        }

        fun getTotalDecimal(index: String):Double{

            var totalDecimal = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    val subtotal = dataSource[i][index].toString().replace(".","")
                    totalDecimal += subtotal.replace(",", ".").replace("%", "").toDouble()
                }
//                totalDecimal = totalDecimal + dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalDecimal
        }
        //Porcentaje
        fun getPorcentaje(totalS:String, valorS:String, position: Int):Double{

            val total: Double = dataSource[position][totalS].toString().replace(".","").replace(",",".").replace("%","").toDouble()
            val valor: Double = dataSource[position][valorS].toString().replace(".","").replace(",",".").replace("%","").toDouble()

            return (valor*100)/total
        }

    }

    class AdapterCheckList(private val context: Context,
                                  private val dataSource: ArrayList<HashMap<String, String>>,
                                  private val molde: Int,
                                  private val vistas: IntArray,
                                  private val valores: Array<String>) : BaseAdapter()
    {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                } catch (e: Exception){
                    e.printStackTrace()
                }
                try {
                    val cbPendiente = rowView.findViewById<CheckBox>(R.id.cbPendiente)
                    val cbSi = rowView.findViewById<CheckBox>(R.id.cbSi)
                    val cbNo = rowView.findViewById<CheckBox>(R.id.cbNo)
                    rowView.findViewById<CheckBox>(R.id.cbPendiente).setOnClickListener{
                        if (cbPendiente.isChecked){
                            cbSi.isChecked = false
                            cbNo.isChecked = false
                            rowView.findViewById<EditText>(R.id.tv2).isEnabled = false
                            CheckList.pendiente(position)
                        } else {
                            cbSi.isChecked = false
                            cbNo.isChecked = true
                            CheckList.no(position)
                        }
                        CheckList.cbPenV[position]  = cbPendiente.isChecked
                        CheckList.cbSiV[position]   = cbSi.isChecked
                        CheckList.cbNoV[position]   = cbNo.isChecked
                    }
                    rowView.findViewById<CheckBox>(R.id.cbSi).setOnClickListener{
                        if (cbSi.isChecked){
                            cbPendiente.isChecked = false
                            cbNo.isChecked = false
                            if (dataSource[position]["COD_MOTIVO"].toString().trim() == "08"){
                                rowView.findViewById<EditText>(R.id.tv2).isEnabled = position == 7
                            }
                            CheckList.si(position)
                        } else {
                            cbNo.isChecked = false
                            cbPendiente.isChecked = true
                            rowView.findViewById<EditText>(R.id.tv2).isEnabled = false
                            CheckList.pendiente(position)
                        }
                        CheckList.cbPenV[position] = cbPendiente.isChecked
                        CheckList.cbSiV[position] = cbSi.isChecked
                        CheckList.cbNoV[position] = cbNo.isChecked
                    }
                    rowView.findViewById<CheckBox>(R.id.cbNo).setOnClickListener{
                        if (cbNo.isChecked){
                            cbSi.isChecked = false
                            cbPendiente.isChecked = false
                            rowView.findViewById<EditText>(R.id.tv2).isEnabled = false
                            CheckList.no(position)
                        } else {
                            cbSi.isChecked = false
                            cbPendiente.isChecked = true
                            CheckList.pendiente(position)
                        }
                        CheckList.cbPenV[position] = cbPendiente.isChecked
                        CheckList.cbSiV[position]  = cbSi.isChecked
                        CheckList.cbNoV[position]  = cbNo.isChecked
                    }
                } catch (e : Exception){

                }
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionCabecera == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }
            return rowView
        }
    }

    class AdapterClientes(private val context: Context,
                                  private val dataSource: ArrayList<HashMap<String, String>>,
                                  private val molde: Int,
                                  private val vistas: IntArray,
                                  private val valores: Array<String>,
                                  private val accion : EditText) : BaseAdapter()
    {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            val cbEntrada = rowView.findViewById<CheckBox>(R.id.cbEntrada)
            val cbSalida = rowView.findViewById<CheckBox>(R.id.cbSalida)
            val btSaltarLlegada = rowView.findViewById<Button>(R.id.btSaltarLlegada)
            val btNuevaUbicacion = rowView.findViewById<Button>(R.id.btNuevaUbicacion)
            val btRestaurar = rowView.findViewById<Button>(R.id.btRestaurar)

            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).visibility = View.VISIBLE
                    rowView.findViewById<TextView>(vistas[i]).text= dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionDetalle == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            if (dataSource[position]["ENTRADA"] != "N"){
                cbEntrada.isChecked = true
                if (dataSource[position]["SALIDA"] != "N"){
                    cbEntrada.isEnabled = false
                }
            }

            if (dataSource[position]["IND_CERRADO"].equals("S")) {
                cbEntrada.isEnabled = false
                cbSalida.isEnabled = false
            } else {
                if(dataSource[position]["SALTAR_LLEGADA"].equals("N")){
                    cbEntrada.isEnabled = true
                    cbSalida.isEnabled = true
                }else{
                    cbEntrada.isEnabled = false
                    cbSalida.isEnabled = false
                    btSaltarLlegada.isEnabled = false
                    btSaltarLlegada.setBackgroundColor(Color.DKGRAY)
                    btSaltarLlegada.setTextColor(Color.GRAY)
                }
            }

            if (ListaPlanillas.tipPlanilla != "REP"){
                btNuevaUbicacion.isEnabled = false
                btNuevaUbicacion.setBackgroundColor(Color.DKGRAY)
                btNuevaUbicacion.setTextColor(Color.GRAY)
            }

            rowView.setOnClickListener{
                FuncionesUtiles.posicionDetalle = position
                accion.setText("INVALIDATE")
            }

            if (dataSource[position]["SALIDA"] != "N"){
                cbSalida.isChecked = true
                cbSalida.isEnabled = false
            }

            cbEntrada.setOnClickListener {
                FuncionesUtiles.posicionDetalle = position
                accion.setText("ENTRADA")
            }
            cbSalida.setOnClickListener {
                FuncionesUtiles.posicionDetalle = position
                accion.setText("SALIDA")
            }
            btSaltarLlegada.setOnClickListener {
                FuncionesUtiles.posicionDetalle = position
                accion.setText("SALTAR_LLEGADA")
            }
            btNuevaUbicacion.setOnClickListener {
                FuncionesUtiles.posicionDetalle = position
                accion.setText("NUEVA_UBICACION")
            }
            btRestaurar.setOnClickListener {
                FuncionesUtiles.posicionDetalle = position
                ListaClientes.codCliente = dataSource[position]["COD_CLIENTE"].toString()
                ListaClientes.codSubcliente = dataSource[position]["COD_SUBCLIENTE"].toString()
                ListaClientes.descCliente = dataSource[position]["DESC_CLIENTE"].toString()
                accion.setText("RESTAURAR")
            }

            return rowView
        }

        fun getTotalEntero(index: String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource[i][index].toString().replace(".", ""))
            }

            return totalValor
        }

        fun getPorcDecimal(index: String):Double{

            var totalPorcCump: Double = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    totalPorcCump += formatNumeroDecimal.format(
                            dataSource[i][index].toString().replace(".", "").replace(",", ".")
                                    .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index], Toast.LENGTH_SHORT).show()
                }
            }

            return totalPorcCump/dataSource.size
        }

        fun getPorcDecimal(index: String,total:Double):Double{

            var valor = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    valor += formatNumeroDecimal.format(
                            dataSource[i][index].toString().replace(".", "").replace(",", ".")
                                    .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index], Toast.LENGTH_SHORT).show()
                }
            }

            return (total*100)/valor
        }

        fun getTotalDecimal(index: String):Double{

            var totalDecimal = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    val subtotal = dataSource[i][index].toString().replace(".","")
                    totalDecimal += subtotal.replace(",", ".").replace("%", "").toDouble()
                }
//                totalDecimal = totalDecimal + dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalDecimal
        }
        //Porcentaje
        fun getPorcentaje(totalS:String, valorS:String, position: Int):Double{

            val total: Double = dataSource[position][totalS].toString().replace(".","").replace(",",".").replace("%","").toDouble()
            val valor: Double = dataSource[position][valorS].toString().replace(".","").replace(",",".").replace("%","").toDouble()

            return (valor*100)/total
        }

    }


    class AdapterSDDetalle(
            context: Context,
            private val dataSource: ArrayList<HashMap<String, String>>,
            private val molde: Int,
            private val vistas:IntArray,
            private val valores:Array<String>,
            private val accion : String,
            private val etAccion : EditText) : BaseAdapter()
    {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.imgEliminar.setOnClickListener{
                etAccion.setText(accion)
            }
//
            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (apolo.repartidores.com.carga.sd.SolicitudDevolucion.posDetalles == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

    }

    class AdapterSDEnviado(
            context: Context,
            private val dataSource: ArrayList<HashMap<String, String>>,
            private val molde: Int,
            private val vistas:IntArray,
            private val valores:Array<String>) : BaseAdapter()
    {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

//            if (SolicitudDevolucion.posDetalles == position){
//                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
//            }

            return rowView
        }

    }

}
package apolo.repartidores.com.utilidades

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import apolo.repartidores.com.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_mapa.*

class Mapa : AppCompatActivity(), OnMapReadyCallback {

    companion object{
        var modificarCliente : Boolean = false
        var codCliente : String = ""
        var codSubcliente :String = ""
        var codVendedor : String = ""
        var lista : ArrayList<HashMap<String,String>> = ArrayList()
    }

    private lateinit var mMap: GoogleMap
    private var positionCliente : LatLng? = null
    private lateinit var ubicacion : FuncionesUbicacion
    private lateinit var lm: LocationManager
    var funcion  : FuncionesUtiles = FuncionesUtiles(this)
    private var latitud  : String = ""
    private var longitud : String = ""
    var id : String = ""
    var tipo : String  = ""
    var cliente : String = ""
    var resultado : String = ""
    var respuesta : String = ""
    var foto      : String = ""
    var conexion : ConexionWS = ConexionWS()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val googleMap : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        googleMap.getMapAsync(this)

        inicializaElementos()
    }

    private fun inicializaElementos(){
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ubicacion = FuncionesUbicacion(this)
        ubicacion.obtenerUbicacionLatLng(lm)
        inicializaBoton(btnObtenerUbicacion)
        if (!modificarCliente){
            btnObtenerUbicacion.visibility = View.GONE
        }
    }

    private fun validaUbicacion(yo:LatLng, cliente:LatLng):Boolean{
        val rangoDistanciaCliente = 100.0
        val distanciaCliente : Double = ubicacion.calculaDistanciaCoordenadas(yo.latitude,cliente.latitude,yo.longitude,cliente.longitude)
        if (distanciaCliente > rangoDistanciaCliente){
            Toast.makeText(this,
                "No puede marcar a mas de $rangoDistanciaCliente metros de su ubicacion.", Toast.LENGTH_SHORT).show()
            return false
        } else {
            Toast.makeText(this,"Se encuentra a " + funcion.decimal(distanciaCliente.toString()) + " metros de su ubicacion.", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun cargarUbicacion(){
        if (modificarCliente){
            id = ""
            tipo = "G"
            latitud  = positionCliente!!.latitude.toString()
            longitud = positionCliente!!.longitude.toString()
            val sql : String = (" SELECT TELEFONO1, TELEFONO2, DIRECCION,FOTO_FACHADA, CERCA_DE,TIPO "
                            + " FROM  svm_modifica_catastro "
                            + " WHERE COD_CLIENTE    = '" + codCliente      + "'"
                            + " AND   COD_SUBCLIENTE = '" + codSubcliente   + "'"
                            + " AND   ESTADO         = 'P' ")
            var valores : ContentValues = valores(funcion.consultar(sqlCliente()))
            valores.put("TIPO",tipo)
            if (id == ""){
                try {
                    funcion.insertar("svm_modifica_catastro",valores)
                } catch (e : Exception) {
                    e.message
                    return
                }
            } else {
                funcion.actualizar("svm_modifica_catastro",valores, " id = '$id'")
            }
            valores = valores(funcion.consultar(sql))
            cliente = "'1'|'$codCliente'|'$codSubcliente'|'${valores.get("TELEFONO1")}'|'${valores.get("TELEFONO2")}'" +
                    "|'${valores.get("DIRECCION")}'|'${valores.get("CERCA_DE")}'|'$latitud'|'$longitud'|'$tipo'"
            foto = valores.get("FOTO_FACHADA").toString()
            ComprobarConexion().execute()
        }
    }

    private fun sqlCliente() : String {
        return (" SELECT TELEFONO, TELEFONO2, DIRECCION, CERCA_DE "
                + " FROM  svm_cliente_vendedor "
                + " WHERE COD_CLIENTE    = '" + codCliente      + "'"
                + " AND   COD_SUBCLIENTE = '" + codSubcliente   + "'")
    }

    fun valores(cursor: Cursor):ContentValues {
        if (cursor.count>0){
            id = funcion.dato(cursor,"id")
            tipo = (if (funcion.dato(cursor,"TIPO").trim().isNotEmpty()){
                        funcion.dato(cursor,"TIPO")
                    } else {
                        tipo
                    }).toString()
            if (tipo == "D"){
                tipo = "A"
            }
        }

        val valores = ContentValues()
        for (i in 0 until cursor.columnCount){
            valores.put(cursor.getColumnName(i),funcion.dato(cursor,cursor.getColumnName(i)))
        }
        valores.put("COD_EMPRESA","1")
        valores.put("COD_CLIENTE", codCliente)
        valores.put("COD_SUBCLIENTE", codSubcliente)
        valores.put("LATITUD",latitud)
        valores.put("LONGITUD",longitud)
        valores.put("FECHA",funcion.getFechaActual())
        valores.put("ESTADO","P")
        valores.put("TIPO",tipo)
        if (funcion.dato(cursor,"TELEFONO").trim().isNotEmpty()){
            valores.put("TELEFONO1",funcion.dato(cursor,"TELEFONO"))
            valores.remove("TELEFONO")
        }
        return valores
    }

    private fun inicializaBoton(boton:Button){
        boton.setOnClickListener{
            if (validaUbicacion(ubicacion.obtenerUbicacionLatLng(lm),positionCliente!!)){
                cargarUbicacion()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ComprobarConexion : AsyncTask<Void, Void, Void>() {
        lateinit var progressDialog: ProgressDialog
        override fun onPreExecute() {
            super.onPreExecute()
            try {
                progressDialog.dismiss()
            } catch (e:Exception){

            }
            progressDialog = ProgressDialog.show(this@Mapa,"Un momento...","Comprobando conexion", true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                null
            } catch (e:Exception){
                resultado = e.message.toString()
                null
            }
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (resultado != "null"){
                try {
                    Enviar().execute()
                    return
                } catch (e:Exception) {
                    Enviar().execute()
                    return
                }
                funcion.mensaje("Error","Verifique su conexion a internet y vuelva a intentar.")
                finish()
                return
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class Enviar : AsyncTask<Void, Void, Void>() {
        lateinit var progressDialog: ProgressDialog
        override fun onPreExecute() {
            super.onPreExecute()
            try {
                progressDialog.dismiss()
            } catch (e:Exception){

            }
            progressDialog = ProgressDialog.show(this@Mapa,"Un momento...","Enviando la actualización al servidor", true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
//            respuesta = conexion.procesaActualizaDatosClienteFinal(codVendedor, cliente, foto).toString()
//            respuesta = "01*Correcto"
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (respuesta.indexOf("01*")>-1 || respuesta.indexOf("03*")>-1){
                try {
                    var sql : String = "UPDATE svm_modifica_catastro SET ESTADO = 'E' " +
                                       " WHERE COD_CLIENTE    = '" + codCliente    + "' " +
                                       "   AND COD_SUBCLIENTE = '" + codSubcliente + "' "
                    funcion.ejecutar(sql,this@Mapa)
                    sql = "UPDATE svm_modifica_catastro SET LATITUD  = '" + cliente.split("|")[7].replace("'","") + "'," +
                                                           "LONGITUD = '" + cliente.split("|")[8].replace("'","") + "' " +
                           "WHERE COD_CLIENTE    = '" + codCliente    + "' " +
                           "  AND COD_SUBCLIENTE = '" + codSubcliente + "' "
                    funcion.ejecutar(sql,this@Mapa)
                    sql = "UPDATE svm_cliente_vendedor SET LATITUD  = '" + cliente.split("|")[7].replace("'","") + "'," +
                            "LONGITUD = '" + cliente.split("|")[8].replace("'","") + "' " +
                            "WHERE COD_CLIENTE    = '" + codCliente    + "' " +
                            "  AND COD_SUBCLIENTE = '" + codSubcliente + "' "
                    funcion.ejecutar(sql,this@Mapa)
                    sql = "UPDATE svm_cliente_vendedor SET LATITUD  = '" + cliente.split("|")[7].replace("'","") + "'," +
                            "LONGITUD = '" + cliente.split("|")[8].replace("'","") + "' " +
                            "WHERE COD_CLIENTE    = '" + codCliente    + "' " +
                            "  AND COD_SUBCLIENTE = '" + codSubcliente + "' "
                    funcion.ejecutar(sql,this@Mapa)
                } catch (e:Exception) {
                    //enviar().excecute()
                    funcion.mensaje("Error","Verifique su conexion a internet y vuelva a intentar.")
                    return
                }
//                finish()
//                return
            }

//            ListaClientes.etAccion.setText("recargar")

            if (respuesta.indexOf("07*")>-1){
                respuesta = "07*Verifique su conexión a internet y vuelva a intentarlo."
            }

            funcion.mensaje("",respuesta.substring(3))
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        mMap.isMyLocationEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.isIndoorEnabled = true
        mMap.addMarker(MarkerOptions().position(ubicacion.obtenerUbicacionLatLng(lm)).title("Posicion actual"))
        positionCliente = ubicacion.obtenerUbicacionLatLng(lm)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(positionCliente,mMap.maxZoomLevel - 5))

        if (modificarCliente){
            googleMap.setOnMapClickListener { point ->
                //lstLatLngs.add(point);
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(point).title("Este es el cliente"))
                positionCliente = point
            }
        } else {
            for (i in 0 until lista.size){
                var cliente : String = lista[i]["COD_CLIENTE"] + "-" + lista[i]["DESC_CLIENTE"]
                cliente += "\nDIRECCION:\n" + lista[i]["DIRECCION"]
                if (!(lista[i]["LATITUD"].toString().trim() == "" && lista[i]["LONGITUD"].toString().trim() == "") &&
                    !(lista[i]["LATITUD"].toString().trim() == "0.0" && lista[i]["LONGITUD"].toString().trim() == "0.0") &&
                    !(lista[i]["LATITUD"].toString().trim() == "null" && lista[i]["LONGITUD"].toString().trim() == "null") &&
                    !(lista[i]["LATITUD"].toString().isEmpty() && lista[i]["LONGITUD"].toString().isEmpty())){
                        val posicion = LatLng(lista[i]["LATITUD"]!!.toDouble(), lista[i]["LONGITUD"]!!.toDouble())
                        val marcador = MarkerOptions().position(posicion)
                        marcador.title("CLIENTE:")
                        marcador.snippet(lista[i]["COD_CLIENTE"] + "-" + lista[i]["DESC_CLIENTE"] + "\nDIRECCION:\n" + lista[i]["DIRECCION"])
                        marcador.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        if (lista[i]["SALIDA"] == "S"){
                            marcador.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        }
                        mMap.addMarker(marcador)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion,mMap.maxZoomLevel - 5))
                }
            }
        }

        mMap.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(this@Mapa)
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(this@Mapa)
                title.setTextColor(Color.BLACK)
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(this@Mapa)
                snippet.setTextColor(Color.BLACK)
                snippet.text = marker.snippet
                snippet.setTypeface(null, Typeface.BOLD)
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })
    }

}

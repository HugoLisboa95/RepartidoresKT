package apolo.repartidores.com.utilidades

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class FuncionesUbicacion(var context: Context) : AppCompatActivity() {

    companion object {
        var lati : String = "0.0"
        var long : String = "0.0"
    }

    private var funcion: FuncionesUtiles = FuncionesUtiles()
    private var listener : MyLocationListener = MyLocationListener()
    var latitud  :String = ""
    var longitud :String = ""

    @SuppressLint("MissingPermission")
    fun obtenerUbicacion(lm:LocationManager){
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f,listener)
        latitud  = lati
        longitud = long
        if (latitud == "0.0" || longitud == "0.0" || latitud == "" || longitud == "") {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f,listener)
            latitud  = lati
            longitud = long
        }
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionLatLng(lm:LocationManager):LatLng{
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return LatLng("-25.4961055036".toDouble(),"-54.7291505575".toDouble())
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f,listener)
        latitud  = lati
        longitud = long
        return LatLng(latitud.toDouble(),longitud.toDouble())
    }

    fun verificarUbicacion():Boolean{
        if (latitud == "0.0" || longitud == "0.0" || latitud.trim() == "" || longitud.trim() == "" || latitud.isEmpty() || longitud.isEmpty()) {
            Toast.makeText(context, "No se encuentra la ubicacion GPS del telefono", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun calculaDistanciaCoordenadas(lat1: Double, lat2: Double, lng1: Double, lng2: Double): Double {
        val dist: Double
        val earthRadius = 6371000.0 //radio de la tierra en metros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val sindLat = sin(dLat / 2)
        val sindLng = sin(dLng / 2)
        val a = sindLat.pow(2.0) + (sindLng.pow(2.0)
                * cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)))
        val c =
            2 * atan2(sqrt(a), sqrt(1 - a))
        dist = earthRadius * c
        return dist
    }

    fun ubicacionActivada(lm:LocationManager):Boolean {
        return if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //CONECTAR EL GPS
            latitud  = ""
            longitud = ""
    //            Toast.makeText(context,"Debe activar la ubicación del teléfono.",Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    fun validaUbicacionSimulada(lm:LocationManager): Boolean {
        if (Build.VERSION.SDK_INT > 22) {
            var count = 0
            try {
                lm.removeTestProvider(LocationManager.GPS_PROVIDER)
                if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f, listener)
                count++
            } catch (e: java.lang.Exception) {
                var err = e.message
                err += ""
                if (err != null) {
                    if (err.indexOf("not allowed") > -1) {
                        Toast.makeText( context,"Debe habilitar las ubicaciones simuladas para esta aplicacion",Toast.LENGTH_LONG).show()
                        return false
                    }
                }
                if (err != null) {
                    if (err.indexOf("unknow") > -1) {
                        count++
                    }
                }
            }
            try {
                lm.removeTestProvider(LocationManager.NETWORK_PROVIDER)
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0f,listener)
                count++
            } catch (e: java.lang.Exception) {
                var err = e.message
                err += ""
                if (err != null) {
                    if (err.indexOf("not allowed") > -1) {
                        Toast.makeText(context,"Debe habilitar las ubicaciones simuladas para esta aplicacion",Toast.LENGTH_LONG).show()
                        return false
                    }
                }
                if (err != null) {
                    if (err.indexOf("unknow") > -1) {
                        count++
                    } else {
                        Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
            if (count == 2) {
                try {
                    Thread.sleep(2500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                return true
            }
            return false
        } else {
            if (Settings.Secure.getString(context.contentResolver,Settings.Secure.ALLOW_MOCK_LOCATION) != "0") {
                Toast.makeText(context,"Debe deshabilitar las ubicaciones simuladas",Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }

    fun obtenerUbicacion(latitud:String,longitud:String,tabla:String,where:String):String{
        val sql : String = "SELECT DISTINCT " + latitud + "," + longitud + " " +
                           "  FROM " + tabla + " " + where
        return funcion.dato(funcion.consultar(sql),latitud) + "|" + funcion.dato(funcion.consultar(sql),longitud)
    }

    fun distanciaMinima(lat:String,long:String,distanciaMaxima:Int):Boolean{
        var distanciaReal : Double = calculaDistanciaCoordenadas(lat.toDouble(),latitud.toDouble(),long.toDouble(),longitud.toDouble())
        if (distanciaReal>distanciaMaxima){
            if (distanciaReal > 1000000.0){
                distanciaReal /= 1000.0
            }
            Toast.makeText(context, "No se encuentra en el cliente. Se encuentra a " + distanciaReal.roundToInt() + " m." , Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    class MyLocationListener : LocationListener {
        override fun onLocationChanged(loc: Location) {
            try {
                if (loc != null) {
                    lati = loc.latitude.toString()
                    long = loc.longitude.toString()
                }
            } catch (e: Exception) {
                return
            }
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle
        ) {
        }
    }
}
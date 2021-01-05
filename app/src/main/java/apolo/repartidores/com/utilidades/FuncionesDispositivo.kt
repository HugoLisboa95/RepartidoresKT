package apolo.repartidores.com.utilidades

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.Settings.Global
import android.provider.Settings.System
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FuncionesDispositivo(var context: Context) {

    val funcion : FuncionesUtiles = FuncionesUtiles(context)

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun modoAvion():Boolean{
        val valor : Int = Global.getInt(context.contentResolver, Global.AIRPLANE_MODE_ON, 0)
        return if (valor != 0){
            Toast.makeText(context,"Debe desactivar el modo avion", Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    @SuppressLint("DefaultLocale", "SimpleDateFormat")
    fun zonaHoraria(): Boolean {
        val americaAsuncion: TimeZone = TimeZone.getTimeZone("America/Asuncion")
        val nowAmericaAsuncion: Calendar = Calendar.getInstance(americaAsuncion)
        val df: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val phoneDateTime: String = df.format(Calendar.getInstance().time)
        val zoneTime = (java.lang.String.format("%02d", nowAmericaAsuncion.get(Calendar.HOUR_OF_DAY)) + ":" + java.lang.String.format("%02d", nowAmericaAsuncion.get(Calendar.MINUTE)))
        val zoneDate = (java.lang.String.format("%02d", nowAmericaAsuncion
                .get(Calendar.DAY_OF_MONTH)) + "/"
                + java.lang.String.format("%02d", nowAmericaAsuncion
                .get(Calendar.MONTH) + 1)
                + "/"
                + java.lang.String.format("%02d", nowAmericaAsuncion.get(Calendar.YEAR)))
        val zoneDateTime = "$zoneDate $zoneTime"
        return if (phoneDateTime != zoneDateTime) {
            Toast.makeText(context, "La zona horaria no coincide con America/Asuncion", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun fechaCorrecta(): Boolean {
        val sql = "SELECT distinct IFNULL(FECHA,'01/01/2020') AS ULTIMA_SINCRO FROM svm_vendedor_pedido order by id desc"
        val cursor:Cursor = funcion.consultar(sql)
        var fecUltimaSincro = ""
        if (cursor.count > 0) {
            fecUltimaSincro = funcion.dato(cursor,"ULTIMA_SINCRO")
        }

        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        var d: Date? = null
        var d1: Date? = null
        val cal = Calendar.getInstance()
        try {
            d = dfDate.parse(fecUltimaSincro)
            d1 = dfDate.parse(dfDate.format(cal.time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val diffInDays = ((d1!!.time - d!!.time) / (1000 * 60 * 60 * 24)).toInt()
        return if (diffInDays != 0) {
            Toast.makeText(context, "La fecha actual del sistema no coincide con la fecha de sincronizacion", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFechaActual():String{
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        val cal = Calendar.getInstance()
        return dfDate.format(cal.time) + ""
    }

    fun tarjetaSim(telMgr:TelephonyManager): Boolean {
        var state = true
        when (telMgr.simState) {
            TelephonyManager.SIM_STATE_ABSENT -> {
                Toast.makeText(context, "Insertar SIM para realizar la operacion", Toast.LENGTH_SHORT).show()
                state = false
            }
            TelephonyManager.SIM_STATE_UNKNOWN -> {
                Toast.makeText(context, "Insertar SIM para realizar la operacion", Toast.LENGTH_SHORT).show()
                state = false
            }
        }
        return state
    }

    fun horaAutomatica(): Boolean {
        if (System.getInt(context.contentResolver, System.AUTO_TIME, 0) != 1
            || System.getInt(context.contentResolver, System.AUTO_TIME_ZONE, 0) != 1
        ) {
            Toast.makeText(context, "Debe configurar su hora de manera automatica", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun validaEstadoSim(telMgr:TelephonyManager):Boolean{
        try {

            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                funcion.mensaje(context,"Error!","Debe otorgar a la aplicacion los permisos para acceder al telefono.")
                return false
            }
            if (telMgr.dataNetworkType==0){
                funcion.mensaje(context,"Error!","Tarjeta sim fuera de servicio.")
                return false
            }
            val suscripcion = SubscriptionManager.from(context).activeSubscriptionInfoList
            for (subscriptionInfo in suscripcion){
                if (subscriptionInfo.iccId.toString().substring(0,4) != "8959"){
                    funcion.mensaje(context,"Tarjeta SIM extranjera", "Para continuar utilice una tarjeta sim de una operadora nacional.")
                    return false
                }
//                if (subscriptionInfo.countryIso.toString().toUpperCase() != "PY"){
//                    funcion.mensaje(context,"Tarjeta SIM extranjera", "Para continuar utilice una tarjeta sim de una operadora nacional.")
//                    return false
//                }
            }
        } catch (e : Exception){
            return false
        }
        return true
    }

    fun verificaRoot():Boolean{
        return try {
            Runtime.getRuntime().exec("su")
            funcion.mensaje(context,"Atención","El teléfono está rooteado.")
            false
        } catch (e : java.lang.Exception) {
            true
        }
    }

}
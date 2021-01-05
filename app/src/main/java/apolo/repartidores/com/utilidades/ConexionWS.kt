package apolo.repartidores.com.utilidades

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Base64
import apolo.repartidores.com.MainActivity
import apolo.repartidores.com.MainActivity2
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.io.*
import java.util.*
import java.util.zip.GZIPInputStream

class ConexionWS {

    companion object{
        lateinit var context : Context
        var resultado : String = ""
        private const val NAMESPACE: String = "http://edsystem/servidor"
        private var METHOD_NAME = ""
        private const val URL = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
        private var SOAP_ACTION = "${NAMESPACE}/${METHOD_NAME}"
    }

    private fun setMethodName(name: String) {
        METHOD_NAME = name
        SOAP_ACTION = "${NAMESPACE}/${METHOD_NAME}"
    }

    fun procesaVersion(codRepartidor: String):String{
        lateinit var resultado: String
        setMethodName("ProcesaValidaRepatidor")

        val request = SoapObject(NAMESPACE, METHOD_NAME)
        request.addProperty("usuario", "edsystem")
        request.addProperty("password", "#edsystem@polo")
        request.addProperty("codRepartidor", codRepartidor)
        request.addProperty("codEmpresa", "1")
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(URL)
        try {
            transporte.call(SOAP_ACTION, envelope)
            val sp:SoapPrimitive? = envelope.response as SoapPrimitive?
            resultado = sp.toString()
        } catch (e: Exception){
            var error : String = e.message.toString()
            error += ""
            resultado = error
        }
        return resultado
    }

    fun generaArchivos()  : Boolean{
        setMethodName("GeneraArchivosRepartidor")
        lateinit var solicitud : SoapObject
        lateinit var resultado : String
        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codRepartidor", FuncionesUtiles.usuario["LOGIN"])
            solicitud.addProperty("codEmpresa", "1")
//            solicitud.addProperty("codPersona", FuncionesUtiles.usuario["COD_PERSONA"])
        } catch (e: Exception){
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 480000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response.toString()
            if (resultado.indexOf("01*") <= -1){
                return false
            }
        } catch (e: Exception){
            e.message.toString()
            return false
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun obtenerArchivos(): Boolean{
        setMethodName("ObtieneArchivosRepartidor")
        lateinit var solicitud : SoapObject
        lateinit var resultado : Vector<*>
        try {
            solicitud = SoapObject(NAMESPACE, METHOD_NAME)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codRepartidor", FuncionesUtiles.usuario["LOGIN"])
//        request.addProperty("codEmpresa", "1")
        } catch (e: Exception){
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(URL, 240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            resultado = envelope.response as Vector<*>
        } catch (e: Exception){
            e.message
            return false
        }
        try {
            val listaTablas : Vector<String> = MainActivity.tablasSincronizacion.listaSQLCreateTables()
            if (resultado.size>4){
                extraerDatos(resultado, listaTablas)
            }
        } catch (e: Exception){
            return false
        }
        return true
    }

    private fun descomprimir(direccionEntrada: String, direccionSalida: String):Boolean{
        try {
            val entrada = GZIPInputStream(FileInputStream(direccionEntrada))
            val salida = FileOutputStream(direccionSalida)
            val buf = ByteArray(1024)
            var len: Int = entrada.read(buf)
            while (len>0){
                salida.write(buf, 0, len)
                len = entrada.read(buf)
            }
            salida.close()
            entrada.close()
            val archivo = File(direccionEntrada)
            archivo.delete()
            return true
        }catch (e: FileNotFoundException){

        }catch (e: IOException){

        }
        return false
    }

    @SuppressLint("SdCardPath")
    fun extraerDatos(resultado: Vector<*>, listaTablas: Vector<String>) : Boolean{
        lateinit var fos : FileOutputStream
        try {
            for (i in 0 until resultado.size){
                fos = FileOutputStream("/data/data/apolo.repartidores.com/" + listaTablas[i].split(" ")[5] + ".gzip")
                fos.write(Base64.decode(resultado[i].toString(), 0))
                fos.close()
                descomprimir(
                    "/data/data/apolo.repartidores.com/" + listaTablas[i].split(" ")[5] + ".gzip",
                    "/data/data/apolo.repartidores.com/" + listaTablas[i].split(" ")[5] + ".txt"
                )
            }
        } catch (e: Exception) {
            e.message
            return false
        }
        return true
    }

    fun obtieneInstalador(): Boolean {
        setMethodName("ProcesaInstaladorRepartidor")
        val request: SoapObject?

        try {
            request = SoapObject(NAMESPACE, METHOD_NAME)
            request.addProperty("usuario", "edsystem")
            request.addProperty("password", "#edsystem@polo")
//        request.addProperty("codEmpresa", "1")
        } catch (e: java.lang.Exception) {
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(URL, 240000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            val sp = envelope.response as SoapPrimitive
            resultado = sp.toString()
            val fos: FileOutputStream?
//            fos = FileOutputStream("/sdcard/apolo_02.apk")
            fos = FileOutputStream(MainActivity2.nombre)
            fos.write(Base64.decode(resultado.toByteArray(), 0))
            fos.close()
        } catch (e: java.lang.Exception) {
            return false
        }
        return true
    }

    fun procesaEnviaCheckList(cod_repartidor: String, cliente: String): String {
        setMethodName("ProcesaCheckList")
        val request: SoapObject?
        val resultado: String?
        try {
            request = SoapObject(NAMESPACE, METHOD_NAME)
            request.addProperty("usuario", "edsystem")
            request.addProperty("password", "#edsystem@polo")
            request.addProperty("codEmpresa", "1")
            request.addProperty("codRepartidor", cod_repartidor)
            request.addProperty("detalle", cliente)
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(URL, 120000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            val sp = envelope.response as SoapPrimitive
            resultado = sp.toString()
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        return resultado
    }

    fun procesaAutorizaSD(cod_repartidor: String, cabecera: String, detalle: String): String {
        setMethodName("ProcesaAutorizaSD")
        val request: SoapObject?
        val resultado: String?
        try {
            request = SoapObject(NAMESPACE, METHOD_NAME)
            request.addProperty("usuario", "edsystem");
            request.addProperty("password", "#edsystem@polo");
            request.addProperty("codEmpresa", "1");
            request.addProperty("codRepartidor", cod_repartidor);
            request.addProperty("cabecera", cabecera);
            request.addProperty("detalle", detalle);
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(URL, 120000)
        try {
            transporte.call(SOAP_ACTION, envelope)
            val sp = envelope.response as SoapPrimitive
            resultado = sp.toString()
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        return resultado
    }

}
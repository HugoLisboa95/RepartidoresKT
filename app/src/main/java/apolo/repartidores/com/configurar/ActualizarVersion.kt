package apolo.repartidores.com.configurar

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.TrafficStats
import android.os.AsyncTask
import android.os.Process
import android.view.Window
import android.widget.ProgressBar
import android.widget.Toast
import apolo.repartidores.com.R
import apolo.repartidores.com.MainActivity2
import apolo.repartidores.com.utilidades.ConexionWS
import apolo.repartidores.com.utilidades.DialogoAutorizacion

class ActualizarVersion {

    companion object{
        lateinit var context : Context
        lateinit var dialogoDescarga : Dialog
        lateinit var progressBar: ProgressBar
        var estado : Boolean = false
        var arx : Double = 0.0
        var uid = Process.myUid()
        var urx = 0f
        lateinit var miDialogo : MyProgressDialog
    }

    fun preparaActualizacion(){
        dialogoDescarga = Dialog(context)
        dialogoDescarga.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogoDescarga.setContentView(R.layout.dialogo_descarga)
        progressBar = dialogoDescarga.findViewById(R.id.pbProgresoTotal)
        dialogoDescarga.show()
        arx = TrafficStats.getUidRxBytes(uid).toDouble()
        DescargarInstalador().execute()
        DialogoDescarga().execute()
    }

    private class DescargarInstalador : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            miDialogo = MyProgressDialog.show(context, null)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                try {
                    estado = MainActivity2.conexionWS.obtieneInstalador()
                    return null
                } catch (e: Exception) {
                    Toast.makeText(context,"ERROR al CANCELAR", Toast.LENGTH_SHORT).show()
                }
                null
            } catch (e: Exception) {
                e.message.toString()
                MainActivity2.funcion.mensaje(context,"Error al conectar","Intente otra vez o llame al Soporte Informatico de la empresa!")
                null
            }
        }

        override fun onPostExecute(unused: Void?) {
            miDialogo.dismiss()
            var mensaje: String
            if (estado) {
                mensaje = "El sistema será actualizado por una nueva versión!!"
                val dialogoAccion = DialogoAutorizacion(context)
                dialogoAccion.dialogoAccion("ACTUALIZAR",MainActivity2.etAccion,mensaje,"Atención!","OK")
            } else {
                mensaje = "Fallo al bajar la actualización!!\n"
                mensaje += ConexionWS.resultado
                MainActivity2.funcion.mensaje(context,"Error!",mensaje)
            }
        }
    }

    private class DialogoDescarga : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {}
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val th = Thread(Runnable {
                    progressBar.progress = 0
                    var c = 0
                    var pr: Int
                    val tot = 5 * 1024 * 1024.toDouble()
                    while (c == 0) {
                        try {
                            if (!miDialogo.isShowing) {
                                c = 1
                                dialogoDescarga.dismiss()
                            }
                        } catch (e: Exception) {
                        }

                        urx = TrafficStats.getUidRxBytes(uid).toFloat()

                        pr = ((urx - arx) * 100 / tot).toInt()
                        progressBar.progress = pr
                        Thread.sleep(500)
                    }
                })
                th.start()
                return null
            } catch (e: Exception) {
                var err : String = e.message.toString()
                err += ""
                MainActivity2.funcion.mensaje(context,"Error", err)
                return null
            }
        }

        override fun onPostExecute(unused: Void?) {
        }
    }

    class MyProgressDialog(context: Context) : Dialog(context, R.style.NewDialog) {
        companion object {
            @JvmOverloads
            fun show(
                context: Context?,
                title: CharSequence?,
                cancelable: Boolean = false,
                cancelListener: DialogInterface.OnCancelListener? =
                    null
            ): MyProgressDialog {
                val dialog = MyProgressDialog(context!!)
                dialog.setTitle(title)
                dialog.setCancelable(cancelable)
                dialog.setOnCancelListener(cancelListener)
                /* The next line will add the ProgressBar to the dialog. */dialog.addContentView(
                    ProgressBar(context), ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
                    )
                )
                dialog.show()
                return dialog
            }
        }
    }


}
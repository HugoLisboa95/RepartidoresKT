package apolo.repartidores.com.utilidades

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import apolo.repartidores.com.R
import kotlinx.android.synthetic.main.menu_cliente.*
import kotlinx.android.synthetic.main.menu_cliente_texto.*
import java.lang.Exception

class DialogoAutorizacion(private var context: Context) {

    private var claves : Clave = Clave()
    private var funcion : FuncionesUtiles = FuncionesUtiles(context)

    @SuppressLint("SetTextI18n")
    fun dialogoAutorizacion(accion:String, cargaAccion : EditText){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
//        var claveTemp : String = claves.generaClave()
        val claveTemp = "54127854"
        dialogo.setTitle("Solicitar autorizacion")

        dialogo.setMessage(claveTemp)
        val contraClave = EditText(context)
        contraClave.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.setView(contraClave)
        dialogo.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            if (contraClave.text.isEmpty()||contraClave.text.length != 8){
                funcion.mensaje("Error","Clave incorrecta")
            } else {
                if (contraClave.text.toString().trim() == claves.contraClave(claveTemp).trim()){
                    cargaAccion.setText(accion+"-"+contraClave.text)
                    funcion.mensaje(context,"Correcto","La clave fue aceptada")
                } else {
                    funcion.mensaje(context,"Error","La clave no es valida")
                    cargaAccion.setText("no$accion")
                }
            }
        }
        dialogo.setCancelable(false)
        dialogo.show()
    }

    fun dialogoAutorizacion(accion:String,noAccion:String, cargaAccion : EditText,mensaje:String, titulo:String,codigo:String,mTrue:String,mFalse:String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        val contraClave = EditText(context)
        contraClave.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.setView(contraClave)
        dialogo.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            if (contraClave.text.toString().trim() == codigo){
                cargaAccion.setText(accion)
    //                funcion.mensaje(context,"Atencion!",mTrue)
            } else {
                cargaAccion.setText(noAccion)
                funcion.mensaje(context,"Error!",mFalse)
            }
        }
        dialogo.setCancelable(false)
        dialogo.show()
    }

    fun dialogoAccion(accion:String, cargaAccion : EditText, mensaje:String,titulo:String,boton:String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        dialogo.setPositiveButton(boton) { _: DialogInterface, _: Int ->
            cargaAccion.setText(accion)
        }
        dialogo.setCancelable(true)
        dialogo.show()
    }

    fun dialogoAccionOpcion(accionAceptar:String,accionCancelar:String, cargaAccion : EditText, mensaje:String,titulo:String,botonAceptar:String,botonCancelar:String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo.setTitle(titulo)
        dialogo.setMessage(mensaje)
        dialogo.setPositiveButton(botonAceptar) { _: DialogInterface, _: Int ->
            cargaAccion.setText(accionAceptar)
        }
        dialogo.setNegativeButton(botonCancelar) { _: DialogInterface, _: Int ->
            cargaAccion.setText(accionCancelar)
        }
        dialogo.setCancelable(true)
        dialogo.show()
    }

    @SuppressLint("SetTextI18n")
    fun dialogoMapa(accion:String, cargaAccion : EditText){
        val dialogo = Dialog(context)
        try {
            dialogo.setContentView(R.layout.menu_cliente)
        } catch (e: Exception){
            dialogo.setContentView(R.layout.menu_cliente_texto)
            dialogo.btCliente.setOnClickListener{
                cargaAccion.setText("cliente")
                dialogo.dismiss()
            }
            dialogo.btRuteo.setOnClickListener{
                cargaAccion.setText("ruteo")
                dialogo.dismiss()
            }
            dialogo.setCancelable(true)
            dialogo.show()
            dialogo.show()
            return
        }
        dialogo.ibtnCliente.setOnClickListener{
            cargaAccion.setText("cliente")
            dialogo.dismiss()
        }
        dialogo.ibtnRuteo.setOnClickListener{
            cargaAccion.setText("ruteo")
            dialogo.dismiss()
        }
        dialogo.imgCliente.setOnClickListener{
            cargaAccion.setText("cliente")
            dialogo.dismiss()
        }
        dialogo.imgRuteo.setOnClickListener{
            cargaAccion.setText("ruteo")
            dialogo.dismiss()
        }
        dialogo.setCancelable(true)
        dialogo.show()
    }

    @SuppressLint("SetTextI18n")
    fun dialogoAutorizacion(accion:String, noAccion: String, cargaAccion : EditText, mensaje:String){
        val dialogo : AlertDialog.Builder = AlertDialog.Builder(context)
//        var claveTemp : String = "54127854"
        val claveTemp : String = claves.generaClave()
        dialogo.setTitle("Solicitar autorizacion")

        dialogo.setMessage("$mensaje \n$claveTemp")
        val contraClave = EditText(context)
        contraClave.inputType = InputType.TYPE_CLASS_NUMBER
        dialogo.setView(contraClave)
        dialogo.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            if (contraClave.text.isEmpty()||contraClave.text.length != 8){
                cargaAccion.setText("$noAccion*${contraClave.text}*")
                funcion.mensaje("Error","Clave incorrecta")
            } else {
                if (contraClave.text.toString().trim() == claves.contraClave(claveTemp).trim()){
                    cargaAccion.setText("$accion*${contraClave.text}*")
                    funcion.mensaje("Correcto","La clave fue aceptada")
                } else {
                    cargaAccion.setText("$noAccion*${contraClave.text}*")
                    funcion.mensaje("Error","La clave no es valida")
                }
            }
        }
        dialogo.setCancelable(false)
        dialogo.show()
    }


}
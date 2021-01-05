package apolo.repartidores.com.utilidades

import android.content.Context
import android.database.Cursor
import android.widget.ArrayAdapter
import android.widget.Spinner
import apolo.repartidores.com.R
import java.lang.Exception

class FuncionesSpinner(var context: Context, private var spinner: Spinner) {

    var valores : ArrayList<HashMap<String,String>> = ArrayList()
    private var funcion : FuncionesUtiles = FuncionesUtiles(context)
    private var options : Array<String> = arrayOf()

    fun sql(campos:String,tabla:String,where:String,whereOpcional:String,group:String,order:String):String{
        var sql : String = "SELECT " + campos +
                "  FROM " + tabla +
                " WHERE " + where
        if (whereOpcional.trim() != ""){ sql += whereOpcional }
        if (group.trim() != ""){ sql += " GROUP BY $group"
        }
        if (order.trim() != ""){ sql += " ORDER BY $order"
        }

        return sql
    }

    fun generaSpinner(campos:String,tabla:String,where:String,whereOpcional:String,group:String,order:String,campo:String,pref:String){
        cargarSpinner(cargarDatos(funcion.consultar(sql(campos,tabla,where,whereOpcional,group,order)),campo,pref))
    }

    private fun cargarDatos(cursor:Cursor, campo:String, pref:String):Array<String>?{
        valores = ArrayList()
        var opcion : String = pref
        for (i in 0 until cursor.count){
            val valor : HashMap<String,String> = HashMap()
            for (j in 0 until cursor.columnCount){
                try {
                    valor[cursor.getColumnName(j).toString()] = funcion.dato(cursor,cursor.getColumnName(j))
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }
            opcion += if (i<cursor.count-1){funcion.dato(cursor,campo)+"|"+pref}else{funcion.dato(cursor,campo)}
            valores.add(valor)
            cursor.moveToNext()
        }
        if (valores.size == 0){
            return null
        }
        options = opcion.split("|").toTypedArray()
        return opcion.split("|").toTypedArray()
    }

    fun cargarSpinner(opciones:Array<String>?){
        if (opciones.isNullOrEmpty()){return}
        val spinnerAdapter : ArrayAdapter<String>? = ArrayAdapter(context,R.layout.spinner_adapter,opciones)
        spinner.adapter = spinnerAdapter
    }

    fun getDato(key:String):String{
        return valores[spinner.selectedItemPosition][key].toString()
    }

    fun getIndex(key:String,valor:String):Int{
        for (i in 0 until valores.size) {
            if (valores[i][key].toString().trim() == valor.trim()){
                return i
            }
        }
        return 0
    }

}
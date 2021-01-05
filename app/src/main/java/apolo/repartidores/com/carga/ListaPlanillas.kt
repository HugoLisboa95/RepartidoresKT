package apolo.repartidores.com.carga

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import apolo.repartidores.com.MainActivity2
import apolo.repartidores.com.R
import apolo.repartidores.com.utilidades.Adapter
import apolo.repartidores.com.utilidades.FuncionesUtiles
import apolo.repartidores.com.utilidades.Mapa
import kotlinx.android.synthetic.main.activity_lista_planillas.*
import kotlinx.android.synthetic.main.barra_titulo.*

class ListaPlanillas: AppCompatActivity() {

    companion object{
        var nroPlanilla = ""
        var tipPlanilla = ""
    }

    var funcion = FuncionesUtiles(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_planillas)

        inicializar()
    }

    private fun inicializar(){
        funcion = FuncionesUtiles(this, imgTitulo, tvTitulo)
        funcion.cargarTitulo(R.drawable.ic_planilla, "Planillas")
        actualizar()
        cargar()
        mostrar()
        btVerPlanilla.setOnClickListener { abrirPlanilla() }
        btCancelar.setOnClickListener { finish() }
        btVerMapa.setOnClickListener { verMapa() }
    }

    private fun actualizar(){
        try {
            val update = "update svm_planilla_reparto set ESTADO = 'P' where ESTADO = 'X'"
            MainActivity2.bd!!.execSQL(update)
        } catch (e: Exception) {
            e.message
        }
    }

    private fun cargar(){
        val sql = ("select a.NRO_PLANILLA, a.FEC_PLANILLA, SUM(a.TOT_COMPROBANTE) TOT_COMPROBANTE, a.TIPO, "
                + " a.DECIMALES   , a.COD_CHOFER  , a.DESC_CHOFER  , a.TIP_PLANILLA,     a.IND_CERRADO "
                + " , (SELECT count(*) as CANT_VISITADO FROM svm_planilla_reparto b WHERE b.NRO_COMPROBANTE IN (" +
                " SELECT c.NRO_COMPROBANTE FROM svm_planilla_reparto c " +
                " WHERE c.ENTRADA='S' AND c.SALIDA='S' AND c.IND_CERRADO='N' AND c.NRO_PLANILLA=a.NRO_PLANILLA " +
                " GROUP BY c.COD_CLIENTE,c.COD_SUBCLIENTE)) || '/' || " +
                " (SELECT count(*) as CANT_TOTAL FROM svm_planilla_reparto b WHERE b.NRO_COMPROBANTE IN (  " +
                " SELECT c.NRO_COMPROBANTE FROM svm_planilla_reparto c " +
                " WHERE c.IND_CERRADO='N' AND c.NRO_PLANILLA= a.NRO_PLANILLA" +
                " GROUP BY c.COD_CLIENTE,c.COD_SUBCLIENTE) " +
                ") CANTIDAD "
                + " from svm_planilla_reparto a where "
                + " a.ind_cerrado = 'N' "
                + " GROUP BY a.NRO_PLANILLA, a.FEC_PLANILLA, a.DECIMALES, a.COD_CHOFER, a.DESC_CHOFER, a.TIPO, a.TIP_PLANILLA "
                + " Order By  date(substr(a.FEC_PLANILLA,7) || '-' ||"
                + "substr(a.FEC_PLANILLA,4,2) || '-' ||"
                + "substr(a.FEC_PLANILLA,1,2)) ")
        val cursor = funcion.consultar(sql)
        FuncionesUtiles.listaCabecera = ArrayList()
        funcion.inicializaContadores()
        funcion.cargarLista(FuncionesUtiles.listaCabecera,cursor)
    }

    private fun mostrar(){
        funcion.valores = arrayOf("NRO_PLANILLA","FEC_PLANILLA","TIPO","CANTIDAD")
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4)
        funcion.adapter = Adapter.AdapterGenericoCabecera(this,FuncionesUtiles.listaCabecera,R.layout.item_lista,funcion.vistas,funcion.valores)
        lvListaPlanillas.adapter = funcion.adapter
        lvListaPlanillas.setOnItemClickListener { _, _, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            nroPlanilla = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["NRO_PLANILLA"].toString().trim()
            tipPlanilla = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["TIP_PLANILLA"].toString().trim()
            lvListaPlanillas.invalidateViews()
        }
        if (FuncionesUtiles.listaCabecera.size>0){
            nroPlanilla = FuncionesUtiles.listaCabecera[0]["NRO_PLANILLA"].toString().trim()
            tipPlanilla = FuncionesUtiles.listaCabecera[0]["TIP_PLANILLA"].toString().trim()
        }
    }

    private fun verMapa(){
        Mapa.modificarCliente = false
        Mapa.lista = ArrayList()
        val sql = "SELECT DISTINCT COD_CLIENTE || '-' || COD_SUBCLIENTE AS COD_CLIENTE, NOM_SUBCLIENTE DESC_CLIENTE, " +
                "         DIR_CLIENTE DIRECCION, LATITUD, LONGITUD, ENTRADA, SALIDA " +
                "    FROM svm_planilla_reparto " +
                "   where NRO_PLANILLA = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["NRO_PLANILLA"] + "'" +
                "     AND IND_CERRADO = 'N' "
        funcion.cargarLista(Mapa.lista,funcion.consultar(sql))
        startActivity(Intent(this,Mapa::class.java))
    }

    private fun abrirPlanilla(){
        startActivity(Intent(this,ListaClientes::class.java))
    }
}
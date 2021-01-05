package apolo.repartidores.com.utilidades

import java.util.*

class SentenciasSQL {
    companion object{

        fun createTableUsuarios(): String{
            return "CREATE TABLE IF NOT EXISTS usuarios" +
                     " (id INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE TEXT       , LOGIN TEXT    	, TIPO TEXT     , " +
                     " ACTIVO TEXT                          , COD_EMPRESA TEXT  , VALIDA_DIS TEXT , VERSION TEXT  , " +
                     " VALIDA_UBICACION_SUC TEXT			, MINUTO_MINIMO TEXT, ID_CAB_REBOTE TEXT);"
        }

        fun insertUsuario(usuario: HashMap<String, String>):String{
            return "INSERT INTO usuarios (NOMBRE, LOGIN, TIPO, ACTIVO, COD_EMPRESA, VERSION, VALIDA_UBICACION_SUC, MINUTO_MINIMO, ID_CAB_REBOTE) VALUES " +
                    "('" + usuario["NOMBRE"]                + "'," +
                     "'" + usuario["LOGIN"]                 + "'," +
                     "'" + usuario["TIPO"]                  + "'," +
                     "'" + usuario["ACTIVO"]                + "'," +
                     "'1'," +
                     "'" + usuario["VERSION"]               + "'," +
                     "'" + usuario["VALIDA_UBICACION_SUC"]  + "'," +
                     "'" + usuario["MINUTO_MINIMO"]         + "'," +
                     "'" + usuario["ID_CAB_REBOTE"]         + "' " +
//                     "'" +  usuario.get("MAX_FOTOS")        + "' " +
                     ")"
        }


//        values.put("IND_REPARTO", "X");
//        values.put("ESTADO", "P");
//        values.put("ENTRADA", "N");
//        values.put("SALIDA", "N");
//        values.put("IND_CERRADO", "N");
//        values.put("REP_GUARDADOCEL", "N");
//        values.put("REP_ENVIADO", "N");
//        values.put("REP_ANULADO", "C");
//        values.put("SALTAR_LLEGADA", "N");
//        values.put("CAN_ANULADO", "0");
//        values.put("REP_RECHAZADO", "N");
//        values.put("ESTADO_SALIDA", "P");

        //Sincronizacion
        fun createTableSvmPlanillaReparto(): String {
            return  ("CREATE TABLE IF NOT EXISTS svm_planilla_reparto"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT             , TIP_PLANILLA TEXT                 , NRO_PLANILLA TEXT                 ,"
                    + " FEC_PLANILLA TEXT  					 , SER_COMPROBANTE TEXT         , TIP_COMPROBANTE TEXT              , NRO_COMPROBANTE TEXT              ,"
                    + " COD_VEHICULO TEXT  					 , DESC_VEHICULO TEXT           , COD_CHOFER TEXT                   , DESC_CHOFER TEXT                  ,"
                    + " COD_REPARTIDOR TEXT    				 , COD_CLIENTE TEXT	            , COD_SUBCLIENTE TEXT               , NOM_CLIENTE TEXT                  ,"
                    + " NOM_SUBCLIENTE TEXT					 , TOT_COMPROBANTE TEXT         , COD_MONEDA TEXT 	                , SIGLAS TEXT                       ,"
                    + " DECIMALES TEXT     					 , IND_REPARTO TEXT DEFAULT 'X'	, ESTADO TEXT DEFAULT 'P'           , IND_CERRADO TEXT                  ,"
                    + " OBSERVACION_IND TEXT				 , ENTRADA TEXT DEFAULT 'N'     , SALIDA TEXT DEFAULT 'N'           , LATI TEXT  		                ,"
                    + " LONGI TEXT	 	   					 , FEC_ENTRADA TEXT	            , FEC_SALIDA TEXT                   , TIPO TEXT                         ,"
                    + " HORA_ENTRADA TEXT   				 , HORA_SALIDA TEXT	            , DIR_CLIENTE TEXT	                , LONGITUD TEXT	                    ,"
                    + " LATITUD TEXT						 , DESC_ZONA TEXT	            , DESC_CIUDAD TEXT	                , REP_GUARDADOCEL TEXT DEFAULT 'N'  ,"
                    + " REP_ENVIADO TEXT DEFAULT 'N'		 , REP_ANULADO TEXT DEFAULT 'C'	, SALTAR_LLEGADA TEXT DEFAULT 'N'   , AUTORIZA_SALTAR TEXT              ,"
                    + " CAN_ANULADO INTEGER	DEFAULT 0		 , FEC_NUMERO INTEGER           , REP_RECHAZADO TEXT DEFAULT 'N'    , FEC_CIERRE TEXT	                ,"
                    + " IND_REENVIAR TEXT					 , HACER_SD TEXT	            , COD_VENDEDOR TEXT 	            , MARC_SALIDA TEXT	                ,"
                    + " IND_ENTREGA TEXT					 , ESTADO_SALIDA TEXT DEFAULT 'P' , AUTORIZACIONES TEXT             , IND_CERRADO_CEL TEXT DEFAULT 'N'  ,"
                    + " COD_CONDICION_VENTA TEXT "
                    + ")")
        }

        fun createTableSvmVtSolDevCab(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_vt_sol_dev_cab"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , TIP_COMPROBANTE TEXT,"
                    + " SER_COMPROBANTE TEXT                 , NRO_COMPROBANTE TEXT, COD_REPARTIDOR TEXT ,"
                    + " COD_CLIENTE TEXT                     , NOM_CLIENTE TEXT    , TIP_PLANILLA TEXT   ,"
                    + " NRO_PLANILLA TEXT                    , SIGLAS TEXT         , DECIMALES TEXT      ,"
                    + " TOT_COMPROBANTE TEXT)")
        }

        fun createTableSvmVtSolDevDet(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_vt_sol_dev_det"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT      , TIP_COMPROBANTE TEXT	  ,"
                    + " SER_COMPROBANTE TEXT                 , COD_REPARTIDOR TEXT   , NRO_COMPROBANTE TEXT   ,"
                    + " COD_ARTICULO TEXT                    , DESC_ARTICULO TEXT    , CANTIDAD TEXT       	  ,"
                    + " COD_UNIDAD_MEDIDA TEXT               , DESC_UNID TEXT        , MONTO_TOTAL_CONIVA TEXT,"
                    + " COD_MOTIVO TEXT       				 , DESC_MOTIVO TEXT )")
        }

        fun createTableRpvMotivoRepCheckList(): String {
            //estado(e=enviado,p=pendiente)
            val us = FuncionesUtiles.usuario["LOGIN"]
            return ("CREATE TABLE IF NOT EXISTS rpv_motivo_rep_check_list"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, "
                    + " COD_EMPRESA TEXT 			    , COD_MOTIVO TEXT		            , NRO_PLANILLA TEXT	        ,"
                    + " DESC_MOTIVO TEXT			    , IND_OBLIG TEXT 		            , DATOS TEXT		        ,"
                    + " ID_REPARTIDOR TEXT DEFAULT '$us', SES_INICIADO TEXT		            , CHEKEADO TEXT		        ,"
                    + " ESTADO TEXT DEFAULT 'P'	        , OBSERVACION TEXT		            , PENDIENTE TEXT DEFAULT 'S',"
                    + " VALOR_VIATICO TEXT			    , PLANILLA_CERRADO TEXT DEFAULT 'N' , FECHA_SINCRO TEXT	        ,"
                    + " FECHA_CHEKEO TEXT			    , HORA_CHEKEO TEXT		            , LATITUD TEXT		        ,"
                    + " LONGITUD TEXT ); "
                    + "CREATE UNIQUE INDEX  if not exists UCHEKEO ON SVM_CHEKEO_DINAMICO(COD_MOTIVO,NRO_PLANILLA);")
        }

        fun createTableSvmPlanillaRepArt(): String {
            //estado(S=enviado,N=pendiente)
            return ("CREATE TABLE IF NOT EXISTS svm_planilla_rep_art"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT			, TIP_PLANILLA TEXT			, "
                    + " NRO_PLANILLA TEXT						, SER_COMP TEXT				, TIP_COMP TEXT				, "
                    + " NRO_COMP TEXT							, DECIMALES TEXT			, COD_ARTICULO TEXT			, "
                    + " DESC_ARTICULO TEXT						, NRO_ORDEN TEXT			, COD_UNIDAD_MEDIDA TEXT 			, "
                    + " DESC_UNI_MED TEXT						, MULT TEXT					, CANTIDAD TEXT				, "
                    + " PRECIO_UNITARIO_C_IVA TEXT				, PRECIO_UB TEXT			, CANT_DEV TEXT				, "
                    + " MONT_DEV TEXT							, IND_BON_CAU TEXT			, IND_BON TEXT TEXT 		, "
                    + " PRECIO_LISTA							, PORC_DESC_VAR TEXT		, PORC_DESC_FIN TEXT		, "
                    + " DESCUENTO_VAR TEXT						, DESCUENTO_FIN TEXT		, PRECIO_DIFERENCIA TEXT	, "
                    + " TIP_COMPROBANTE_REF TEXT				, SER_COMPROBANTE_REF TEXT	, NRO_COMPROBANTE_REF TEXT	, "
                    + " TIPO_PROMOCION TEXT						, NRO_PROMOCION TEXT		, IND_INPASA TEXT			  "
                    + " ) ")
        }

        fun createTableSvmVtMotivoRebote(): String {
            //estado(S=enviado,N=pendiente)
            return ("CREATE TABLE IF NOT EXISTS svm_vt_motivo_rebote"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT		, COD_PENAL TEXT	, "
                    + " DESCRIPCION TEXT) ")
        }

        fun createTableRvmStArticulos(): String {
            //estado(S=enviado,N=pendiente)
            return ("CREATE TABLE IF NOT EXISTS rvm_st_articulos"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT		, COD_ARTICULO TEXT	, "
                    + " DESC_ARTICULO TEXT						, COD_UNIDAD_REL TEXT	, REFERENCIA TEXT 	, "
                    + " IND_BASICO TEXT							, COD_BARRA TEXT        , MULT TEXT) ")
        }

        fun createTableSvmVtMotivosSodDev(): String {
            //estado(S=enviado,N=pendiente)
            return ("CREATE TABLE IF NOT EXISTS svm_vt_motivos_sod_dev"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT		, COD_MOTIVO TEXT	, "
                    + " DESC_MOTIVO TEXT) ")
        }

        fun createTableSvmMotivoNoVenta(): String {
            return ("CREATE TABLE IF NOT EXISTS srm_motivo_no_venta"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT	, COD_MOTIVO TEXT,"
                    + "	 DESC_MOTIVO TEXT 					 , CIERRE_MARC TEXT)")
        }

        fun createTableRvmFacturaBonificada(): String {
            return ("CREATE TABLE IF NOT EXISTS rvm_factura_bonificada "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT,  "
                    + " COD_EMPRESA TEXT		 , SER_COMPROBANTE TEXT 	, TIP_COMPROBANTE TEXT		, NRO_COMPROBANTE TEXT,	"
                    + " SER_COMPROBANTE_REF TEXT , TIP_COMPROBANTE_REF TEXT	, NRO_COMPROBANTE_REF TEXT	, IND_BON TEXT		  , "
                    + " IND_BON_CAU TEXT 		 , DECIMALES TEXT)")
        }

        //NO SINCRONIZADOS

        fun listaSQLCreateTable(): Vector<String> {
            val lista : Vector<String> = Vector<String>()
            lista.add(0, createTableSvmSolicitudDevCab())
            lista.add(1, createTableSvmSolicitudDevDet())
            lista.add(2, createTableSvmMarcacionVisita())
            lista.add(3, createTableSvmReboteCab())
            lista.add(4, createTableSvmRebote())
            lista.add(5, createTableSvmDevolucion())
//            lista.add(6, createTableSvmPedidosDet())
//            lista.add(7, createTableSvmCatastroCliente())
//            lista.add(8, createTableStvCategoriaPalm())
//            lista.add(9, createTableSpmRetornoComentario())
//            lista.add(10, createTableSvmDiasTomaFotoCliente())
//            lista.add(11, )
//            lista.add(12, createTableSvm_fotos_cab())
//            lista.add(13, createTableSvm_fotos_det())
//            lista.add(14, "PRAGMA automatic_index = true")
//            lista.add(15, createTableSvm_imagenes_det())
//            lista.add(16, createTableSvm_fotos_cab())
//            lista.add(17, createTableSvm_fotos_det())
//            lista.add(18, "")
//            lista.add(19, "")
//            lista.add(20, "")
//            lista.add(21, "")
//            lista.add(22, "")
//            lista.add(23, "")
//            lista.add(24, "")
//            lista.add(25, "")
//            lista.add(26, "")
//            lista.add(27, "")
//            lista.add(28, "")
//            lista.add(29, "")
//            lista.add(30, "")

            return lista
        }
        /*fun createTableSvmPlanillaActiva(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_planilla_activa "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT,  "
                    + " COD_EMPRESA TEXT	, TIP_PLANILLA TEXT 	, NRO_PLANILLA TEXT		, COD_CLIENTE TEXT,	"
                    + " COD_SUBCLIENTE TEXT , TIP_COMPROBANTE TEXT	, SER_COMPROBANTE TEXT	, NRO_COMPROBANTE TEXT, "
                    + " IND_CERRADO_CEL TEXT );")
        }*/
        fun createTableSvmPlanillaActivaSelect(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_planilla_activa AS "
                    + " SELECT id, COD_EMPRESA,TIP_PLANILLA,NRO_PLANILLA,"
                    + "        COD_CLIENTE,COD_SUBCLIENTE,TIP_COMPROBANTE,SER_COMPROBANTE,NRO_COMPROBANTE,IND_CERRADO_CEL"
                    + "   FROM svm_planilla_reparto ")
        }
        fun dropTableSvmPlanillaActiva():String{
            return "DROP TABLE IF EXISTS svm_planilla_activa"
        }

        fun createTableSvmSolicitudDevCab(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_solicitud_dev_cab"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    	, COD_REPARTIDOR TEXT 	,"
                    + " NRO_COMPROBANTE TEXT				 , COD_CLIENTE TEXT    	, COD_SUBCLIENTE TEXT 	,"
                    + " NOM_CLIENTE TEXT                     , TOT_COMPROBANTE TEXT	, NRO_PLANILLA TEXT   	,"
                    + " SIGLAS TEXT         				 , OBSERVACIONES TEXT	, EST_ENVIO TEXT 		,"
                    + " NRO_REGISTRO_REF TEXT 				 , FECHA TEXT )")
        }
        fun createTableSvmSolicitudDevDet(): String {
            //estado(S=enviado,N=pendiente)
            return ("CREATE TABLE IF NOT EXISTS svm_solicitud_dev_det"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT	 	, TIP_PLANILLA TEXT	, "
                    + " NRO_PLANILLA TEXT						, SER_COMP TEXT			, TIP_COMP TEXT		, "
                    + " NRO_COMP TEXT							, COD_REPARTIDOR TEXT	, COD_CLIENTE		, "
                    + " COD_SUBCLIENTE							, NRO_REGISTRO_REF TEXT	, NRO_ORDEN TEXT	, "
                    + " COD_ARTICULO TEXT						, DESC_ARTICULO TEXT	, CANTIDAD TEXT		, "
                    + " PAGO TEXT								, MONTO TEXT			, TOTAL TEXT		, "
                    + " COD_UNIDAD_REL TEXT						, REFERENCIA TEXT 		, IND_BASICO TEXT	, "
                    + " COD_PENALIDAD TEXT						, GRABADO_CAB TEXT		, EST_ENVIO TEXT 	, "
                    + " FECHA TEXT	)")
        }

        fun createTableSvmMarcacionVisita(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_marcacion_visita "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , COD_SUCURSAL TEXT, "
                    + " COD_CLIENTE TEXT					, COD_SUBCLIENTE TEXT, COD_REPARTIDOR TEXT, "
                    + " NRO_PLANILLA TEXT					, COD_MOTIVO TEXT  	 , OBSERVACION TEXT   , FECHA TEXT	    , "
                    + " LATITUD TEXT	  					, LONGITUD TEXT		 , ESTADO TEXT	    ,"
                    + " HORA_ALTA TEXT						, NRO_ORDEN TEXT)")
        }

        fun createTableSvmReboteCab(): String {
            //estado(S=enviado,N=pendiente)
            return ("CREATE TABLE IF NOT EXISTS svm_rebote_cab"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT	, NRO_REGISTRO TEXT    , "
                    + " COD_SUCURSAL TEXT						, NRO_PLANILLA TEXT	,"
                    + " TIP_COMPROBANTE TEXT					, SER_COMPROBANTE	, NRO_COMPROBANTE TEXT , "
                    + " COD_VENDEDOR TEXT						, VENDEDOR_PERSONA TEXT	, COD_REPARTIDOR TEXT,"
                    + " FEC_COMPROBANTE TEXT					, NRO_REGISTRO_REF TEXT , FEC_ALTA TEXT		, COD_CLIENTE TEXT	,"
                    + " COD_SUBCLIENTE TEXT						, COD_MONEDA TEXT	, TIP_REBOTE TEXT	,"
                    + " TOT_COMPROBANTE TEXT					, EST_ENVIO TEXT 	, EST_RESPUESTA TEXT,"
                    + " AUTORIZA_SIST TEXT 						, AUTORIZA_MANU TEXT, EST_ANULADO TEXT,"
                    + " HORA_ALTA TEXT							, EST_RECHAZADO TEXT, COD_PENALIDAD TEXT,"
                    + " EST_ENVIANDO TEXT DEFAULT 'N'	) ")
        }

        fun createTableSvmRebote(): String {
            //estado(S=enviado,N=pendiente)
            return ("CREATE TABLE IF NOT EXISTS svm_rebote"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, "
                    + " COD_EMPRESA TEXT		, COD_SUCURSAL			,NRO_PLANILLA TEXT	,"
                    + " TIP_COMPROBANTE TEXT	, SER_COMPROBANTE		, NRO_COMPROBANTE TEXT , "
                    + " COD_VENDEDOR TEXT		, VENDEDOR_PERSONA TEXT	, COD_REPARTIDOR TEXT,"
                    + " FEC_COMPROBANTE TEXT	, FEC_ALTA TEXT			, COD_CLIENTE TEXT	,"
                    + " COD_SUBCLIENTE TEXT		, COD_MONEDA TEXT		, TIP_REBOTE TEXT	,"
                    + " TOT_COMPROBANTE TEXT	, EST_ENVIO TEXT 		, EST_RESPUESTA TEXT,"
                    + " AUTORIZA_SIST TEXT 		, AUTORIZA_MANU TEXT	, EST_ANULADO TEXT,"
                    + " HORA_ALTA TEXT			, GRABADO_CAB TEXT  	, NRO_REGISTRO_REF TEXT,"
                    + " EST_RECHAZADO TEXT		,"
                    + " COD_ARTICULO TEXT		, COD_UNIDAD_MEDIDA TEXT,COD_CANTIDAD TEXT	,"
                    + " COD_PENALIDAD TEXT		, NRO_ORDEN TEXT		, NRO_REGISTRO_ANU TEXT,"
                    + " NRO_REGISTRO_DEL TEXT)")
        }

        fun createTableSvmDevolucion(): String {
            //estado(S=enviado,N=pendiente)
            return ("CREATE TABLE IF NOT EXISTS svm_devolucion "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT	, COD_EMPRESA TEXT	 	, TIP_PLANILLA 		 TEXT, "
                    + " NRO_PLANILLA TEXT						, SER_COMP TEXT			, TIP_COMP 		 	 TEXT, "
                    + " NRO_COMP TEXT							, COD_CLIENTE TEXT		, COD_SUBCLIENTE 	 TEXT, "
                    + " COD_ARTICULO TEXT						, DESC_ARTICULO TEXT	, "
                    + " CANTIDAD TEXT							, COD_UNIDAD_REL TEXT	, REFERENCIA         TEXT, "
                    + " IND_BASICO TEXT							, NRO_ORDEN TEXT		, COD_PENALIDAD      TEXT, "
                    + " DES_PENALIDAD TEXT						, EST_ENVIO TEXT        , MONTO_TOTAL_CONIVA TEXT) ")
        }

        /*fun insertIntoSvmPlanillaActiva():String{
            return "INSERT INTO svm_planilla_activa (COD_EMPRESA,TIP_PLANILLA,NRO_PLANILLA," +
                    "COD_CLIENTE,COD_SUBCLIENTE,TIP_COMPROBANTE,SER_COMPROBANTE,NRO_COMPROBANTE,IND_CERRADO_CEL)" +
                    " VALUES " +
                    "(SELECT COD_EMPRESA,TIP_PLANILLA,NRO_PLANILLA," +
                    "        COD_CLIENTE,COD_SUBCLIENTE,TIP_COMPROBANTE,SER_COMPROBANTE,NRO_COMPROBANTE,IND_CERRADO_CEL" +
                    "   FROM svm_planilla_reparto )"
        }*/
    }
}
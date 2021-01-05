package apolo.repartidores.com.utilidades

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.tan
import kotlin.random.Random

class Clave {

    fun generaClave():String {
        val clave = Random.nextInt(10000000, 99999999)
        return  clave.toString()
    }

    fun contraClave(clave: String) : String {

        val iP1 = Integer.parseInt(clave.substring(1, 8))
        val iP2 = Integer.parseInt(clave.substring(4, 8))
        val iP3 = Integer.parseInt(clave.substring(0, 8))
        val iP4 = Integer.parseInt(clave.substring(2, 8))
        val iP5 = Integer.parseInt(clave.substring(0, 5))
        val iP6 = Integer.parseInt(clave.substring(3, 8))

        var sContraClave : String = smvRoundAbs(cos(iP3.toDouble()), 100).toString()

        sContraClave += smvRoundAbs(cos(iP1.toDouble()), 100)
        sContraClave += smvRoundAbs(cos(iP4.toDouble()), 10)
        sContraClave += smvRoundAbs(tan(iP3.toDouble()), 10)
        sContraClave += smvRoundAbs(cos(iP6.toDouble()), 100)
        sContraClave += smvRoundAbs(cos(iP2.toDouble()), 10)
        sContraClave += smvRoundAbs(cos(iP5.toDouble()), 10)
        sContraClave = sContraClave.substring(0, 8)

        return sContraClave

    }

    private fun smvRoundAbs(subClave: Double, multiplo: Int) : Int {
        return abs((subClave * multiplo).roundToInt())
    }

    fun smvContraclaveCheckList(
        sChave: String, cod_cliente: String,
        cod_subcliente: String, nro_planilla: String
    ): String {

        //		sChave = "54357440";

        val iP1 = Integer.parseInt(sChave.substring(1, 8))
        val iP2 = Integer.parseInt(sChave.substring(4, 8))
        val iP3 = Integer.parseInt(sChave.substring(0, 8))
        val iP4 = Integer.parseInt(sChave.substring(2, 8))
        val iP5 = Integer.parseInt(sChave.substring(0, 5))
        val iP6 = Integer.parseInt(sChave.substring(3, 8))

        val val1: Int = try {
            Integer.parseInt(cod_cliente)
        } catch (e: Exception) {
            0
        }

        val val2: Int = try {
            Integer.parseInt(cod_subcliente)
        } catch (e: Exception) {
            0
        }

        val val3: Int = try {
            Integer.parseInt(nro_planilla)
        } catch (e: Exception) {
            0
        }

        var sum = 0
        val vCliente = val1 + val2 + val3
        val largo = vCliente.toString().length

        for (i in 0 until largo) {
            var `val` = vCliente.toString()
            `val` = `val`[i].toString()
            try {
                sum += Integer.parseInt(`val`)
            } catch (e: Exception) {

            }

        }

        sum = Integer
            .parseInt(smvRoundAbs(cos(sum.toDouble()), 100).toString())
        val resCliente = retornaValor(sum.toString(), 2)

        // vcliente := substr(abs ( round ( cos(substr(vvalor,1,2 ) ) * 100 , 0
        // ) ), 1, 2 ) ;

        var sContraChave: String
        val dosNumeros = sChave.substring(0, 2)
        sContraChave = if (dosNumeros != resCliente) {
            smvRoundAbs(
                cos(iP3.toDouble()),
                Integer.parseInt(resCliente)
            ).toString()
        } else {
            smvRoundAbs(cos(iP3.toDouble()), 100).toString()
        }

        sContraChave += smvRoundAbs(cos(iP6.toDouble()), 100).toString()//p1
        sContraChave += smvRoundAbs(cos(iP2.toDouble()), 10).toString() //p4
        sContraChave += smvRoundAbs(tan(iP5.toDouble()), 10).toString() //p3
        sContraChave += smvRoundAbs(cos(iP1.toDouble()), 100).toString()//p6
        sContraChave += smvRoundAbs(cos(iP4.toDouble()), 10).toString() //p2
        sContraChave += smvRoundAbs(cos(iP3.toDouble()), 10).toString() //p5
        sContraChave = sContraChave.substring(0, 8)

        return sContraChave
    }

    private fun retornaValor(valor: String, cantidad: Int): String {
        var result = ""

        val vvalor = cantidad - valor.length

        if (vvalor == 0) {
            return valor
        }

        for (i in 0 until vvalor) {
            result += "0"
        }

        result += valor

        if (result.length > cantidad) {
            result = result.substring(0, cantidad)
        }

        return result
    }

}
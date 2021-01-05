package apolo.repartidores.com.utilidades

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UtilidadesBD( context: Context,
                    factory: SQLiteDatabase.CursorFactory?) :
      SQLiteOpenHelper( context,"repartidor",factory,1){
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(SentenciasSQL.createTableUsuarios())
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
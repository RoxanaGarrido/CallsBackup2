package es.recitecnic.rgarrido.callsbackup2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.CancellationSignal
import android.provider.CallLog
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.annotation.RequiresApi
import es.recitecnic.rgarrido.callsbackup2.databinding.ActivityMainBinding
import java.lang.String.format
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Consumer

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                Array(1) { Manifest.permission.READ_CALL_LOG },
                101
            )
        } else
            displayLog()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            displayLog()
    }

    @SuppressLint("MissingPermission", "SimpleDateFormat")
    private fun displayLog() {

        var data = arrayOf<String>(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.DATE
        )

        var rs: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI, data, null, null,
            "${CallLog.Calls.LAST_MODIFIED} DESC"
        )

        while (rs?.moveToNext()!!) {
            var type = ""
            when (rs.getString(2).toInt()) {
                1 -> type = "Entrante"
                2 -> type = "Saliente"
                3 -> type = "Llamada perdida"
                4 -> type = "Correo de voz"
                5 -> type = "Llamada rechazada"
                6 -> type = "Número bloqueado"
            }

            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

            var info = "\n\nTeléfono: "+rs.getString(1)
            info+= "\nTipo: "+type
            info+="\nDuración: "+formatSegons(rs.getString(3).toLong())
            info+="\nFecha: "+simpleDateFormat.format(rs.getString(4).toLong())
            binding.editTextTextMultiLine.append(info)

        }
    }

    private fun formatSegons(TotalSegundos:Long):String{
        // define once somewhere in order to reuse it
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

       // JVM representation of a millisecond epoch absolute instant
        val instant = Instant.ofEpochMilli(TotalSegundos)

       // Adding the timezone information to be able to format it (change accordingly)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val ret = formatter.format(date)
        return ret
        /*
        var restant:Long
        var restant1:Long
        var restant2:Long
        var horas:Long
        var minutos:Long
        var segundos:Long
        var format:String=""
        restant = TotalSegundos%(3600*24);
        horas = restant / 3600;
        restant1 = TotalSegundos % 3600;

        minutos = restant1 /60;
        restant2 = TotalSegundos % 60;

        segundos = restant2;

        format = "$horas:$minutos:$segundos"
        return format
        */

    }
 }

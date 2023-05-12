package id.agen.edc

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** EdcPlugin */
class EdcPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private lateinit var eventChannel: EventChannel
    private lateinit var applicationContext: Context
    var printBroadcastReceiver: PrintBroadcastReceiver? = null
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "edc")
        eventChannel= EventChannel(flutterPluginBinding.binaryMessenger,"edc.eventChannel")
        channel.setMethodCallHandler(this)
        applicationContext = flutterPluginBinding.applicationContext
        eventChannel.setStreamHandler( object : EventChannel.StreamHandler {
        private var eventSink: EventChannel.EventSink? = null
        override fun onListen(p0: Any?, sink: EventChannel.EventSink) {
          if (printBroadcastReceiver == null) {
            printBroadcastReceiver = PrintBroadcastReceiver(sink)
          }
         applicationContext.registerReceiver(
            printBroadcastReceiver,
            IntentFilter("com.example.paymentservice.RESPONSE_BROADCAST")
          )
        }
        override fun onCancel(p0: Any?) {
          eventSink = null
        }
      })
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "callPrint" -> {
                 val args=call.argument<String>("data_print")
             callPrint(args.toString())
            }
            "callPaymentApp" -> {
                val variable = call.argument<String>("nominal")
               callPaymentApp(variable?:"000")
            }
            "callHost" -> {
                val arg = call.arguments as HashMap<*, *> 
                val path = arg["path"]
                val data = arg["data"] 
                val refNumber = arg["refNumber"]
                val trxType = arg["trxType"]
                callHost(
                    path.toString(),
                    data.toString(),
                    refNumber.toString(),
                    trxType.toString()
                )
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun callPrint(path: String) {
       try {
           val intent = Intent("com.example.paymentservice.START_PAYMENT_SERVICE")
           intent.setPackage("com.example.paymentservice")
           intent.putExtra("PRINT_DATA", path)
           applicationContext.startService(intent)
       }catch (e:Exception){
           Toast.makeText(applicationContext, e.localizedMessage,Toast.LENGTH_LONG).show()
       }
    }

    private fun callPaymentApp(nominal: String) {
        val deepLinkUri =
            Uri.parse("gstpayment://navigate?typeScreen=DEBIT_CARD&balance=" + nominal)
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    private fun callHost(path: String, data: String, refNumber: String, trxType: String) {
        val intent = Intent("com.example.paymentservice.START_PAYMENT_SERVICE")
        intent.setPackage("com.example.paymentservice")
        intent.putExtra("HOST_DATA_PARAM", path)
        intent.putExtra("HOST_DATA_BODY", data)
        intent.putExtra("HOST_DATA_REF_NUMBER", refNumber)
        intent.putExtra("HOST_DATA_TRX_TYPE", trxType)
        applicationContext.startService(intent)
    }
}

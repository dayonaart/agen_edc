package id.agen.edc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.EventChannel
import org.json.JSONObject

class PrintBroadcastReceiver(private val sink: EventChannel.EventSink) : BroadcastReceiver() {
    private val TAG = this::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        // val response = intent.getStringExtra("PRINT_RESPONSE")

        // Log.d(TAG, "sink: $sink")
        // Log.d(TAG, "Response: $response")
        // sink?.success(response)

        // Toast.makeText(context, response, Toast.LENGTH_SHORT).show()

        val responsePrint = intent.getStringExtra(CommonUtility.PRINT_RESPONSE)
        val responseInquiry = intent.getStringExtra(CommonUtility.POST_RESPONSE)
        val responseDebit = intent.getStringExtra(CommonUtility.DEBIT_RESPONSE)

        if (responsePrint != null) {
            sink.success(responsePrint)
        }
        if (responseInquiry != null && responseInquiry != "Sending request to server") {
            val resultJson = JSONObject(responseInquiry)
            val body = resultJson["Body"]
            sink.success(body)
        }

        if (responseDebit != null) {
            sink.success(responseDebit)
        }
    }
}
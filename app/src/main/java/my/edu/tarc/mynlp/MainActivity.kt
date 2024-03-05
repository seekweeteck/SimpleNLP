package my.edu.tarc.mynlp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.provider.Telephony.Sms.Conversations
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage

class MainActivity : AppCompatActivity() {
    val arrayMessage = ArrayList<String>()
    lateinit var arrayAdapterMessage : ArrayAdapter<*>
    val smartReply = SmartReply.getClient()
    val conversation = ArrayList<TextMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSend: Button = findViewById(R.id.buttonSend)
        val editTextMessage: EditText = findViewById(R.id.editTextMessage)
        val listView: ListView = findViewById(R.id.listViewMessage)


        arrayAdapterMessage = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayMessage)
        //listView.adapter = arrayAdapterMessage
        
        buttonSend.setOnClickListener {
            val message: String = editTextMessage.text.toString()
            arrayMessage.add("Question:" + message)

            conversation.add(TextMessage.createForLocalUser(message, System.currentTimeMillis()))

            smartReply.suggestReplies(conversation)
                .addOnSuccessListener {result ->
                    if(result.status == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE){
                        Toast.makeText(this, "Language is not supported", Toast.LENGTH_SHORT).show()
                        Log.d("Error", "Language is not supported")
                    }else if(result.status == SmartReplySuggestionResult.STATUS_SUCCESS){
                        for(suggestion in result.suggestions){
                            arrayMessage.add("Reply:" + suggestion.text)
                            conversation.add(TextMessage.createForRemoteUser(suggestion.text, System.currentTimeMillis(), "MLKit"))
                            Log.d("Reply", suggestion.text)
                        }
                        listView.adapter = arrayAdapterMessage
                    }
                }
                .addOnFailureListener { result ->
                    Toast.makeText(this, "Task failed", Toast.LENGTH_SHORT).show()
                    Log.d("Error", result.toString())
                }

        }
    }
}
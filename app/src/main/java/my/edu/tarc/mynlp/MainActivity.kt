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
        val buttoReply: Button = findViewById(R.id.buttonReply)
        val editTextMessage: EditText = findViewById(R.id.editTextMessage)
        val editTextReply: EditText = findViewById(R.id.editTextReply)
        val listView: ListView = findViewById(R.id.listViewMessage)

        arrayAdapterMessage = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayMessage)

        buttonSend.setOnClickListener {
            if(editTextMessage.text.isEmpty()){
                editTextMessage.setError("Please enter text")
                return@setOnClickListener
            }
            val message: String = editTextMessage.text.toString()
            arrayMessage.add("Send : $message")
            conversation.add(TextMessage.createForLocalUser(message, System.currentTimeMillis()))
            editTextMessage.text.clear()
        }

        buttoReply.setOnClickListener {
            val reply = editTextMessage.text.toString()
            arrayMessage.add("Reply : $reply")
            conversation.add(TextMessage.createForRemoteUser(reply, System.currentTimeMillis(), "MLKit"))
            editTextReply.text.clear()

            smartReply.suggestReplies(conversation)
                .addOnSuccessListener {result ->
                    if(result.status == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE){
                        Toast.makeText(this, "Language is not supported", Toast.LENGTH_SHORT).show()
                    }else if(result.status == SmartReplySuggestionResult.STATUS_SUCCESS){
                        arrayMessage.clear()
                        for(suggestion in result.suggestions){
                            arrayMessage.add(suggestion.text)
                        }
                        listView.adapter = arrayAdapterMessage
                    }
                }
                .addOnFailureListener { result ->
                    Toast.makeText(this, "Task failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
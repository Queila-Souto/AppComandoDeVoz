package com.example.treinamento_comando_de_voz

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.treinamento_comando_de_voz.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    val TAGLOG = "LOG_TTS"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var textToSpeech: TextToSpeech? = null
    var mensagem = "texto padrão"
    var telefone = "123456"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        findViewById<View>(R.id.microfone).setOnClickListener {
            capturarFala()
        }
        binding.toolbar.setTitle("Aplicativo para comando de voz")
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        initTTS()

    }


    private fun initTTS() {
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                var result: Int = textToSpeech!!.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.i(TAGLOG, "idioma não suportado")
                } else {
                    sintetizarCaptura("Olá, bem vindo")
                }
            } else {
                Log.i(TAGLOG, "falha ao inicializar sintetizador de voz")

            }

        })
    }

    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        super.onDestroy()
    }

    private fun capturarFala() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale naturalmente...")

        try {
            startActivityForResult(intent, 10)
            Log.i(TAGLOG, "voz reconhecida")
        } catch (e: Exception) {
            Toast.makeText(this, "Reconhecimento de voz não suportado", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == RESULT_OK && null != data) {
                var result: ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                var capturaFala: String = result!!.get(0)
                Log.i(TAGLOG, "Sintetizar " + capturaFala)

                processarMachineLearnig(capturaFala)
            }
        }


    }

    private fun processarMachineLearnig(capturaFala: String) {
        Log.i(TAGLOG, "processar MachineLearning " + capturaFala)
        if (capturaFala.uppercase().contains("SAIR")) {
            try {
                Thread.sleep(1000)
                finish()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        if (capturaFala.uppercase().contains("SMS")) {
            try {
                Thread.sleep(1000)
                enviarSMS(telefone, mensagem)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        if (capturaFala.uppercase().contains("GOVERNO BRASIL")) {
            try {
                Thread.sleep(1000)
                openURLGoverno()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

    }

    private fun openURLGoverno() {
        sintetizarCaptura("Encaminhando" )
        Thread.sleep(2000)
        val URL_GOVERNO = "http://www.brasil.gov.br"
        var intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(URL_GOVERNO))
        startActivity(intent)
    }

    private fun enviarSMS(telefone: String, mensagem: String) {
    val uri : Uri = Uri.parse("tel:"+ telefone)
    val intent: Intent = Intent (Intent.ACTION_VIEW, uri)
        intent.putExtra("address", telefone)
        intent.putExtra("sms_body", mensagem)
        intent.setData(Uri.parse("sms:"));
        startActivity(intent)
        Thread.sleep(2000)
        sintetizarCaptura("ENVIANDO SMS")
    }

    private fun sintetizarCaptura(capturaFala: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech!!.setSpeechRate(00.52f)
            textToSpeech!!.speak(capturaFala, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            textToSpeech!!.speak(capturaFala, TextToSpeech.QUEUE_FLUSH, null)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)



        return true


    }

    override fun onSupportNavigateUp(): Boolean {

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)

    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu != null) {
            sintetizarCaptura("Menu selecionado")
        } else {
        }
        return super.onMenuOpened(featureId, menu!!)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        return if (id == R.id.action_sair) {

                sintetizarCaptura("Saindo")
            try{
                Thread.sleep(1000)

                finish()
            } catch( e : Exception ) {
                Toast.makeText(this, "Erro: "+e , Toast.LENGTH_LONG).show()

            }
                true
                    } else super.onOptionsItemSelected(item)
    }



}
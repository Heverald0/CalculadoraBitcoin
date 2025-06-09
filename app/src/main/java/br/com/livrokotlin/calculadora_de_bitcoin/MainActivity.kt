package br.com.livrokotlin.calculadora_de_bitcoin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val API_URL = "https://www.mercadobitcoin.net/api/BTC/ticker/"
    private var cotacaoBitcoin: Double = 0.0

    private lateinit var btnCalcular: Button
    private lateinit var txtValor: EditText
    private lateinit var txtCotacao: TextView
    private lateinit var txtQtdBitcoins: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializando as views
        btnCalcular = findViewById(R.id.btn_calcular)
        txtValor = findViewById(R.id.txt_valor)
        txtCotacao = findViewById(R.id.txt_cotacao)
        txtQtdBitcoins = findViewById(R.id.txt_qtd_bitcoins)

        buscarCotacao()

        btnCalcular.setOnClickListener {
            calcular()
        }
    }

         fun buscarCotacao() {
             GlobalScope.launch(Dispatchers.Main) {
                 try {
                     val resposta = withContext(Dispatchers.IO) {
                         URL(API_URL).readText()
                     }

                     val json = Gson().fromJson(resposta, TickerResponse::class.java)
                     cotacaoBitcoin = json.ticker.last

                     val f = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                     val cotacaoFormatada = f.format(cotacaoBitcoin)

                     txtCotacao.text = cotacaoFormatada
                 } catch (e: Exception) {
                     e.printStackTrace()
                     txtCotacao.text = "Erro ao buscar cotação"
                 }
             }
         }
    
        fun calcular() {
            if (txtValor.text.isEmpty()) {
                txtValor.error = "Preencha um valor"
                return
            }

            try {
                val valorDigitado = txtValor.text.toString()
                    .replace(",", ".")
                    .toDouble()

                val resultado = if (cotacaoBitcoin > 0) valorDigitado / cotacaoBitcoin else 0.0
                txtQtdBitcoins.text = "%.8f".format(resultado)
            } catch (e: NumberFormatException) {
                txtValor.error = "Valor inválido"
            }
        }
}

data class TickerResponse(val ticker: Ticker)
data class Ticker(val last: Double)

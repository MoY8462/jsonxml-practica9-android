package dgtic.unam.jsonxml

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import dgtic.unam.jsonxml.databinding.ActivityMainBinding
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var volleyAPI: VolleyAPI
    private lateinit var url: String
    private val ipPuerto = "192.168.1.80:8080"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate((layoutInflater))
        setContentView(binding.root)
        volleyAPI = VolleyAPI(this)
        binding.search.setOnClickListener{
            binding.outText.text = ""
            url= "https://www.google.es/search?q="+URLEncoder.encode(
                binding.searchText.text.toString(), "UTF-8"
            )
            buscar()
        }
    }

    private fun buscar(){
        var stringRequest=object: StringRequest(
            Request.Method.GET,url,Response.Listener<String>{response->
                binding.outText.text=response
            },Response.ErrorListener { binding.outText.text = "No se encuentra la informacion, revice si conexion" }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>()
                headers["User-Agent"]="Mozilla/5.0 (Windows NT 6.1)"
                return headers
            }
        }
        volleyAPI.add(stringRequest)
    }

}
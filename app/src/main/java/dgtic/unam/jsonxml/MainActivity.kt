package dgtic.unam.jsonxml

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.error
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import dgtic.unam.jsonxml.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.Charset

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

        binding.restxml.setOnClickListener {
            studenXML()
        }

        binding.restjson.setOnClickListener{
            studenJSON()
        }
        binding.restjsonid.setOnClickListener{
            studiantID()
        }
        binding.restjsonadd.setOnClickListener{
            studentAdd()
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

    private fun studenXML(){
        val urlXML = "http://"+ipPuerto+"/estudiantesXML"
        val stringRequest=object: StringRequest(
            Request.Method.GET,urlXML,Response.Listener<String>{response->
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

    private fun studenJSON(){
        val urlJSON = "http://"+ipPuerto+"/estudiantesJSON"
        var cadena = ""
        val jsonRequest = object: JsonArrayRequest(
            urlJSON,
            Response.Listener<JSONArray>{ response ->
                (0 until response.length()).forEach{
                    val estudiante = response.getJSONObject(it)
                    val materia = estudiante.getJSONArray("materias")
                    cadena+=estudiante.get("cuenta").toString()+">"
                    (0 until materia.length()).forEach{
                        val datos = materia.getJSONObject(it)
                        cadena += datos.get("nombre").toString()+"**"+datos.get("creditos").toString()+"---"
                    }
                    cadena+=">\n"
                }
                binding.outText.text = cadena
            },
            Response.ErrorListener {
                binding.outText.text = "No se encuentra la información, revise la conexión"
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>()
                headers["User-Agent"]="Mozilla/5.0 (Windows NT 6.1)"
                return headers
            }
        }
        volleyAPI.add(jsonRequest)
    }

    private fun studiantID() {
        val urlJSON = "http://" + ipPuerto + "/id/" + binding.searchText.text.toString()
        var cadena = ""
        val jsonRequest = object : JsonObjectRequest(
            Method.GET,
            urlJSON,
            null,
            Response.Listener<JSONObject> { response ->
                binding.outText.text = response.get("cuenta")
                    .toString() + "----" + response.get("nombre").toString() + "\n"
            },
            Response.ErrorListener {
                binding.outText.text = "No se encuentra la información, revice la conexión"
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Windows NT 6.1)"
                return headers
            }
        }
        volleyAPI.add(jsonRequest)
    }

    private fun studentAdd(){
        val urlJSON = "http://"+ipPuerto+"/agregarestudiante"
        var cadena = ""
        val jsonRequest = object: JsonArrayRequest(
            urlJSON,
            Response.Listener<JSONArray>{ response ->
                (0 until response.length()).forEach{
                    val estudiante = response.getJSONObject(it)
                    val materia = estudiante.getJSONArray("materias")
                    cadena+=estudiante.get("cuenta").toString()+">"
                    (0 until materia.length()).forEach{
                        val datos = materia.getJSONObject(it)
                        cadena += datos.get("nombre").toString()+"**"+datos.get("creditos").toString()+"---"
                    }
                    cadena+=">\n"
                }
                binding.outText.text = cadena
            },
            Response.ErrorListener {
                binding.outText.text = "No se encuentra la información, revise la conexión"
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>()
                headers["User-Agent"]="Mozilla/5.0 (Windows NT 6.1)"
                return headers
            }

            override fun getBody(): ByteArray {
                var estudiante = JSONObject()
                estudiante.put("cuenta", "A000")
                estudiante.put("nombre","Android")
                estudiante.put("edad","200")
                var materias = JSONArray()
                val itemMaterias = JSONObject()
                itemMaterias.put("id","1")
                itemMaterias.put("nombre", "Nueva Materia")
                itemMaterias.put("creditos", "100")
                materias.put(itemMaterias)
                estudiante.put("materias",materias)
                return estudiante.toString().toByteArray(charset = Charsets.UTF_8)
            }

            override fun getMethod(): Int {
                return Method.POST
            }
        }
        volleyAPI.add(jsonRequest)
    }
    private fun studentsDelete() {
        val urlJSON = "http://"+ipPuerto+"/borrarestudiante/"+binding.searchText.text.toString()
        var cadena = ""
        val jsonRequest = object : JsonArrayRequest(
            urlJSON,
            Response.Listener<JSONArray> { response ->
                (0 until response.length()).forEach {
                    val estudiante = response.getJSONObject(it)
                    val materia=estudiante.getJSONArray("materias")
                    cadena += estudiante.get("cuenta").toString() + "<-"
                    (0 until materia.length()).forEach {
                        val datos=materia.getJSONObject(it)
                        cadena += datos.get("nombre").
                        toString() + "----" + datos.get("creditos").toString() + "--"
                    }
                    cadena+="> \n"
                }
                binding.outText.text = cadena
            },
            Response.ErrorListener {
                binding.outText.text = getString(R.string.error)
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Windows NT 6.1)"
                return headers
            }
            override fun getMethod(): Int {
                return return Method.DELETE
            }
        }
        volleyAPI.add(jsonRequest)
    }


}
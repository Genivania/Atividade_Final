package br.senai.sp.jandira.atividade_final

package br.senai.sp.jandira.atividade_final

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.JsonObject
import android.content.Intent
import kotlinx.coroutines.launch



class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }



class Imagem : AppCompatActivity() {

    //ATRIBUTOS DE MANIPULAÇÃO DE ENDEREÇOS DAS IMAGENS
    private var image: Uri? = null

    /* CONFIGURAÇÕES DO FIREBASE */
    //DECLARAÇÃO DO STORAGE
    private lateinit var storageRef: StorageReference

    //DECLARAÇÃO DO FIRESTORE DATABASE
    private lateinit var firebaseFirestore: FirebaseFirestore

    /* OBJETOS DE VIEW DA TELA */
    //IMAGEVIEW
    private var btnImg: ImageView? = null

    //BUTTON
    private var btnUpload: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        initVars()

        //TESTE DE RECEBIMENTO DO JSON
        val bodyJSON = intent.getStringExtra("bodyJSON")
        Log.e("TESTE-JSON", bodyJSON.toString())

        //RECUPERA OS ELEMENTOS DE VIEW DE IMAGENS
        btnImg = findViewById<ImageView>(R.id.img)


        //RECUPERA O ELEMENTO DE VIEW DE BUTTON
        btnUpload = findViewById<Button>(R.id.btnSalvar)

        //TRATAMENTO DO EVENTO DE CLICK DO BOTÃO DE IMAGEM GRANDE
        btnImg?.setOnClickListener {
//            Toast.makeText(this,
//                "BOTÃO DA IMAGEM GRANDE",
//                 Toast.LENGTH_LONG).show()
            resultLauncher.launch("image/*")

        }

        //TRATAMENTO DO EVENTO DE CLICK DO BOTÃO DE CADASTRO
        btnUpload?.setOnClickListener {
//            Toast.makeText(this,
//                "BOTÃO DE CADASTRO",
//                Toast.LENGTH_LONG).show()
            uploadImage()
        }
    }

    //LANÇADOR PARA RECUPERAR IMAGEM DA GALERIA PARA O UPLOAD
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        image = it
        btnImg?.setImageURI(image)
        Log.e("IMG-GRD", image.toString())
    }


    //INICIALIZAÇÃO DAS VARIÁVEIS DO FIREBASE
    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("images")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }


    //FUNÇÃO DE UPLOAD
    private fun uploadImage() {

        image?.let {
            val riversRef =
                storageRef.child("${it.lastPathSegment}-${System.currentTimeMillis()}.jpg")
            val uploadTask = riversRef.putFile(it)
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    riversRef.downloadUrl.addOnSuccessListener { uri ->
                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()
                        firebaseFirestore.collection("images").add(map)
                            .addOnCompleteListener { firestoreTask ->
                                if (firestoreTask.isSuccessful) {
                                    Toast.makeText(this, "UPLOAD IMAGEM OK!", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        firestoreTask.exception?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                btnImg?.setImageResource(R.drawable.upload)
                            }
                    }
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    btnImg?.setImageResource(R.drawable.upload)
                }
            }
        }

    }

    }


    class Cadastro : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main2)

            //DECLARANDO E RECUPERANDO OS OBJETOS DE VIEW
            val txtLogin = findViewById<EditText>(R.id.txtLogin)
            val txtSenha = findViewById<EditText>(R.id.txtSenha)
            val btnSalvar = findViewById<Button>(R.id.btnSalvar)

            //TRATAMENTO DA AÇÃO E CLIQUE
            btnSalvar.setOnClickListener {

                //ENTRADA DOS DADOS DE LIVRO
                val login = txtLogin.text.toString()
                val senha = txtSenha.text.toString()


                //MOTAGEM DO CORPO JSON DOS DADOS
                val body = JsonObject().apply {
                    addProperty("Login", login)
                    addProperty("Senha", senha)
                }

                //TESTE DO CORPO DE DADOS JSON
                Log.e("BODY-JSON", body.toString())

                //NAVEGAÇÃO PARA A TELA
                val intent = Intent(
                    this,
                    Cadastro::class.java).apply {
                    putExtra("bodyJSON", body.toString())
                }

                startActivity(intent)

            }


        }
    }


    class cadastro_usuario : AppCompatActivity() {

        private lateinit var apiService: ApiService

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main2)

            apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

            val txtLogin = findViewById<EditText>(R.id.txtLogin)

            findViewById<Button>(R.id.btnSalvar).setOnClickListener {
                val nomeUsuario = txtLogin.text
                //Log.e("TESTE", "${nomeCategoria}")
                createUsuario(nomeUsuario.toString())

            }

        }

    private fun createUsuario(nomeUsuario: String){
        lifecycleScope.launch {
            val body = JsonObject().apply {
                addProperty("nomeUsuario", nomeUsuario)
            }

            val result =apiService.createUsuario(body)

            if(result.isSuccessful){
                val msg =  result.body()?.get("mensagemStatus")
                Log.e("CREATING-CATEGORY", "CREATE CATEGORY SUCCESS: ${result.body()?.get("mensagemStatus")}")
            }else{
                Log.e("CREATING-CATEGORY", "ERROR: ${result.message()}")
            }
        }
    }

    private fun getUsuarioByID(){
        lifecycleScope.launch {
            val result = apiService.getUsuarioByID("1")
            if(result.isSuccessful){
                Log.e("GETTING-CATEGORY", "getUserByID: ${result.body()?.data}")
            }else{
                Log.e("GETTING-CATEGORY", "getUserByID: ${result.message()}")
            }
        }
    }
}
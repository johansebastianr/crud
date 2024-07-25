package com.example.crud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crud.api.RetrofitClient
import com.example.crud.api.Usuario
import com.example.crud.api.UsuarioAdapter
import com.example.crud.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), UsuarioAdapter.OnItemClicked {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UsuarioAdapter

    private var listaUsuarios = arrayListOf<Usuario>()

    private var usuario = Usuario(-1, "", "")

    private var isEditando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()

        obtenerUsuarios()

        binding.btnAddUpdate.setOnClickListener {
            if (validarCampos()) {
                if (isEditando) {
                    actualizarUsuario()
                } else {
                    agregarUsuario()
                }
            } else {
                Toast.makeText(this, "Se deben llenar los campos", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = UsuarioAdapter(this, listaUsuarios)
        adapter.setOnClick(this)
        binding.rvUsuarios.adapter = adapter
    }

    private fun validarCampos(): Boolean {
        return !(binding.etNombre.text.isNullOrEmpty() || binding.etEmail.text.isNullOrEmpty())
    }

    private fun obtenerUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.webService.obtenerUsuarios()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    listaUsuarios = response.body()?.listaUsuarios ?: arrayListOf()
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@MainActivity, "ERROR CONSULTAR TODOS", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun agregarUsuario() {
        usuario = Usuario(-1, binding.etNombre.text.toString(), binding.etEmail.text.toString())

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.webService.agregarUsuario(usuario)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarObjeto()
                } else {
                    Toast.makeText(this@MainActivity, "ERROR ADD", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun actualizarUsuario() {
        usuario.nombre = binding.etNombre.text.toString()
        usuario.email = binding.etEmail.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.webService.actualizarUsuario(usuario.idUsuario, usuario)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarObjeto()
                    binding.btnAddUpdate.setText("Agregar Usuario")
                    binding.btnAddUpdate.backgroundTintList = resources.getColorStateList(R.color.green)
                    isEditando = false
                }
            }
        }
    }

    private fun limpiarCampos() {
        binding.etNombre.setText("")
        binding.etEmail.setText("")
    }

    private fun limpiarObjeto() {
        usuario = Usuario(-1, "", "")
    }

    override fun editarUsuario(usuario: Usuario) {
        binding.etNombre.setText(usuario.nombre)
        binding.etEmail.setText(usuario.email)
        binding.btnAddUpdate.setText("Actualizar Usuario")
        binding.btnAddUpdate.backgroundTintList = resources.getColorStateList(R.color.purple_500)
        this.usuario = usuario
        isEditando = true
    }

    override fun borrarUsuario(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.webService.borrarUsuario(idUsuario)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                }
            }
        }
    }
}

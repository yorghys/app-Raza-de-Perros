package com.example.retrofitexampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
Ejercicio con Retrofit2 y Gson para parceo
Coroutines
RecyclerView
SearchView
Coil
Api usada https://dog.ceo/dog-api/documentation/
 */

//implementamos la interface del SearchView
class MainActivity : AppCompatActivity(),
    androidx.appcompat.widget.SearchView.OnQueryTextListener {

    lateinit var imagesPuppies: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Le decimos que somos los escuchadores
        searchBreed.setOnQueryTextListener(this)
    }

    //Creamos la funcion que nos devuelve un objeto Retrofit
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Corremos la coRutina para el llamado en Backgroud
    private fun searchByName(query: String) = GlobalScope.launch {
        val call = getRetrofit().create(ApiService::class.java).getCharacterByName("$query/images")
            .execute()

        try {
            // TODO: aca se quiere castear un null a DogsResponse y crashea
            val puppies = call.body() as DogsResponse
            launch(Dispatchers.Main) {
                if (puppies.status == "success") {
                    initCharacter(puppies.images)
                } else {
                    showErrorDialog()
                }
                hideKeyboard()
            }
        }
        catch (e: Throwable) {
            launch(Dispatchers.Main) {
                showEmptyDialog()
            }
            hideKeyboard()
        }
    }

    private fun initCharacter(images: List<String>) {
        if (images.isNotEmpty()) {
            imagesPuppies = images
        }
        rvDogs.layoutManager = GridLayoutManager(this, 2)
        rvDogs.adapter = RecyclerDogAdapter(imagesPuppies)
    }

    //es el que usamos porque queremos que se busque cuando se escribe
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchByName(query.toLowerCase())
        }
        return true
    }

    //No lo usamos ya que mira cuando cambia el texto cada vez
    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun showErrorDialog() {
        Toast.makeText(this,"Ha ocurrido un error, intentelo mas tarde", Toast.LENGTH_LONG).show()
    }

    private fun showEmptyDialog() {
        Toast.makeText(this,"No hay perros de esa raza", Toast.LENGTH_LONG).show()
    }

    private fun hideKeyboard(){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(viewRoot.windowToken, 0)
    }
}

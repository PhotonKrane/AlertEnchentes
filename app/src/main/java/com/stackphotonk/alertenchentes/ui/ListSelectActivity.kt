package com.stackphotonk.alertenchentes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.stackphotonk.alertenchentes.data.DBHelper
import com.stackphotonk.alertenchentes.databinding.ActivityListSelectBinding

class ListSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListSelectBinding
    private val db = DBHelper()
    private lateinit var adapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db.getAllLocals { ids ->
            setList(ids)
        }
    }

    private fun setList(list: ArrayList<String>) {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)

        binding.listView.adapter = adapter

        binding.listView.setOnItemClickListener { _, _, pos, _ ->
            val i = Intent(this, MainActivity::class.java).apply {
                putExtra("local", list[pos])
            }

            startActivity(i)
        }
    }
}
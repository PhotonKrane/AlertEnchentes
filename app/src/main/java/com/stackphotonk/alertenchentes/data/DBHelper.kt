package com.stackphotonk.alertenchentes.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DBHelper() {
    private val rootRef = FirebaseDatabase.getInstance()

    fun getLevel(local:String,callback: (Int) -> Unit) {
        val ref = rootRef.getReference("$local/waterlevel")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val level = snapshot.getValue(Int::class.java)!!
                callback(level)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("errorDatabase", "O erro: $error")
            }
        })
    }

    fun getAllLocals(callback: (ArrayList<String>) -> Unit) {
        val ref = rootRef.getReference()
        val childList = ArrayList<String>()

        ref.get().addOnSuccessListener { snap ->
            if(snap != null) {
                childList.clear()
                for(child in snap.children) {
                    val id = child.key.toString()
                    childList.add(id)
                }
                if (childList.isEmpty()) callback(arrayListOf("Nenhuma área encontrada"))
                else callback(childList)
            } else {
                callback(arrayListOf("Nenhuma área encontrada"))
            }
        }.addOnFailureListener { e->
            Log.e("errorDatabase", "O erro: $e")
        }
    }
}
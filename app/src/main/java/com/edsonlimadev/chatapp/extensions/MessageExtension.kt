package com.edsonlimadev.whatappclone.extensions

import android.app.Activity
import android.widget.Toast

fun Activity.message(text: String){
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
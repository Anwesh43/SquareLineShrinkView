package com.anwesh.uiprojects.linkedsquarelineshrinkview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.squarelineshrinkview.SquareLineShrinkView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SquareLineShrinkView.create(this)
    }
}

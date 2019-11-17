package com.anwesh.uiprojects.squarelineshrinkview

/**
 * Created by anweshmishra on 17/11/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val nodes : Int = 5
val squares : Int = 4
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#0D47A1")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 30
val scGap : Float = 0.02f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(Math.PI * this).toFloat()
fun Float.cosify() : Float = 1f - Math.cos(Math.PI / 2 + (Math.PI / 2) * this).toFloat()

fun Canvas.drawSquareLineShrink(i : Int, scale : Float, size : Float, paint : Paint) {
    val sc : Float = scale.divideScale(i, squares)
    val sf : Float = sc.sinify()
    val sfc : Float = sc.divideScale(0, 2).cosify()
    save()
    rotate(90f * i)
    drawLine(0f, 0f, size * sf, size * sf, paint)
    save()
    translate((size / 2) * sfc, (size / 2) * sfc)
    drawRect(RectF(-size / 4, -size / 4, size / 4, size / 4), paint)
    restore()
    restore()
}

fun Canvas.drawSquaresLineShrink(scale : Float, size : Float, paint : Paint) {
    for (j in 0..squares) {
        drawSquareLineShrink(j, scale, size, paint)
    }
}

fun Canvas.drawSLSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawSquaresLineShrink(scale, size, paint)
    restore()
}

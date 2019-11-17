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

class SquareLineShrinkView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SLSNode(var i : Int, val state : State = State()) {

        private var next : SLSNode? = null
        private var prev : SLSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SLSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSLSNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SLSNode {
            var curr : SLSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class SquareLineShrink(var i : Int) {

        private val root : SLSNode = SLSNode(0)
        private var curr : SLSNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SquareLineShrinkView) {

        private val animator : Animator = Animator(view)
        private val sls : SquareLineShrink = SquareLineShrink(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            sls.draw(canvas, paint)
            animator.animate {
                sls.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            sls.startUpdating {
                animator.start()
            }
        }
    }
}
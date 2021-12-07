package view

import processing.core.PApplet
import processing.core.PConstants
import java.awt.Color

abstract class Button internal constructor(val container : PApplet, val x: Int, val y: Int, val w: Int, val h: Int, val lbl: String) {
    var buttonOn = false

    abstract fun action()

    fun display() {
        container.stroke(0)
        container.strokeWeight(2f)
        if (buttonOn) {
            container.fill(ACC)
        } else {
            if (isMouseInside) {
                container.fill(HOVC)
            } else {
                container.fill(BTNC)
            }
        }
        container.rect(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat())
        container.fill(0)
        container.textAlign(PConstants.CENTER, PConstants.CENTER)
        container.text(lbl, (x + w / 2).toFloat(), (y + h / 2).toFloat())
    }

    val isMouseInside: Boolean
        get() = (container.mouseX > x) and (container.mouseX < x + w) and (container.mouseY > y) and (container.mouseY < y + h)

    fun hasClicked(): Boolean {
        val changeState = isMouseInside
        if (changeState) buttonOn = !buttonOn
        return changeState
    }

    companion object {
        val BTNC = Color(252,244,245).rgb //"FCF4F5".toInt(16)
        val HOVC = Color(187,214,182).rgb //"BBD6B6".toInt(16)
        val ACC = Color(154,213,142).rgb //"9AD58E".toInt(16)
    }
}
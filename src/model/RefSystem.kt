package model

import processing.core.PApplet
import processing.core.PGraphics

import processing.core.PVector
import java.awt.Color
import kotlin.math.PI


/*
Class that represents an Reference System
given that a - b is positive
*/
class RefSystem(a: PVector?, b: PVector?, position: PVector?){
  var e1: PVector
  var e2: PVector
  var position: PVector = PVector()
  var color : Color = Color.ORANGE
  var size = 50

//  fun render(rend_color: Color, groesse: Int) {
//    val Color_Pre_Render: Color = getStroke()
//    setStroke(rend_color)
//    line(position.x, position.y, position.x + e1.x * groesse, position.y + e1.y * groesse)
//    line(position.x, position.y, position.x + e2.x * groesse, position.y + e2.y * groesse)
//    setStroke(Color_Pre_Render)
//  }

  fun render(renderContext: PGraphics) {
    val colorPreRender: Int = renderContext.strokeColor
    renderContext.stroke(color.rgb)
    renderContext.line(position.x, position.y, position.x + e1.x * size, position.y + e1.y * size)
    renderContext.line(position.x, position.y, position.x + e2.x * size, position.y + e2.y * size)
    renderContext.stroke(colorPreRender)
  }

  init {
    this.position.set(position)
    e1 = PVector()
    e1.set(b)
    e1.sub(a)
    e1.normalize()
    e2 = PVector()
    e2.set(e1)
    e2.rotate((PI / 2).toFloat())
  }
}
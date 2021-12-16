package model.objectRenderer

import model.Lens
import processing.core.PGraphics

interface ObjectRenderer<in T : Any> {

    fun <S : T> render(element : S)
}

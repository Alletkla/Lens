package model.rayCreator

import java.awt.Color

abstract class ARayCreator() : RayCreator {
    override var color: Color = Color.BLACK
    override val speed: Float = 2f
}
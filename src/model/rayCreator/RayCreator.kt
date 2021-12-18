package model.rayCreator

import model.light.Ray
import processing.core.PVector
import java.awt.Color

interface RayCreator {
    var color : Color
    val speed : Float

    fun create(point : PVector) : List<Ray>

    fun initialize(point : PVector)
}
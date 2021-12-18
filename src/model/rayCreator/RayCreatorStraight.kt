package model.rayCreator

import model.light.Ray
import processing.core.PVector

class RayCreatorStraight : ARayCreator() {
    private val direction = PVector(1f,0f)
    override fun create(point: PVector): List<Ray> {
        //copy is needed, because it will be edited later for hitting the lens correctly
        return listOf(Ray(point, direction.copy(), speed).also { it.color = color })
    }

    override fun initialize(point: PVector) {
        return
    }
}
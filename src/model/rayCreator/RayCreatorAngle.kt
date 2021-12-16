package model.rayCreator

import model.Ray
import processing.core.PVector

class RayCreatorAngle : ARayCreator() {
    private var initVector : PVector? = null

    override fun create(point: PVector) : List<Ray> {
        if (initVector == null) throw IllegalStateException("There must be an initial start Vector for Angle Ray creation")

        var direction = point.sub(initVector)
        if (direction.mag() == 0f){
            direction = PVector(1f, 0f,)
        }

        return listOf(Ray(initVector!!, direction.normalize(), speed).also { it.color = color  })
            .also { initVector = null }
    }

    override fun initialize(point: PVector) {
        initVector = point
    }
}
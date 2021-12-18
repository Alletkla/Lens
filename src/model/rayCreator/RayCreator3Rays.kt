package model.rayCreator

import model.Lens
import model.light.Ray
import processing.core.PVector

class RayCreator3Rays(val lens: Lens) : ARayCreator() {

    override fun create(point: PVector): List<Ray> {
        val rayList = mutableListOf<Ray>()

        rayList.add(Ray(point.copy(), PVector(1f,0f),speed).also { it.color = color })
        rayList.add(Ray(point.copy(), lens.midPoint.copy().sub(point).normalize(),speed).also { it.color = color })

        if (point.dist(lens.focalLengthPoint1) < point.dist(lens.focalLengthPoint2)){
            rayList.add(Ray(point.copy(), lens.focalLengthPoint1.copy().sub(point).normalize(),speed).also { it.color = color })
        }else {
            rayList.add(Ray(point.copy(), lens.focalLengthPoint2.copy().sub(point).normalize(),speed).also { it.color = color })
        }

        return rayList
    }

    override fun initialize(point: PVector) {

    }
}
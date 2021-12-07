package model

import processing.core.PVector
import kotlin.math.roundToInt


object ColissionManager {
    fun rayCollidesLens(ray: Ray, lens: Lens): Boolean {
        if (!ray.inLens && ray.Lens_plane_intersect == null) {
            return false
        }
        if (!ray.inLens && ray.behindLensPlane()) {
            return false
        }
        ray.BezMid = PVector()
        val radius: Float = if (ray.inLens) {
            ray.BezMid!!.set(lens.MidPointCircle1)
            lens.r1
        } else {
            ray.BezMid!!.set(lens.MidPointCircle2)
            lens.r2
        }
        ray.distance = ray.end.copy().add(ray.direction).dist(ray.BezMid)

        val distMinR2: Int = ((ray.end.dist(ray.BezMid) - radius) * 2).roundToInt()
        val toTest: Boolean = if (ray.inLens) {
            ray.distance > radius
        } else {
            ray.distance < radius
        }
        if (toTest && distMinR2 != 0) {
            val EndToMid = PVector()
            EndToMid.set(ray.end).sub(ray.BezMid)
            val DotEndToMid_direction = EndToMid.dot(ray.direction).toDouble()
            val lambda = -DotEndToMid_direction - Math.sqrt(
                Math.pow(DotEndToMid_direction, 2.0) - Math.pow(
                    EndToMid.mag().toDouble(), 2.0
                ) + Math.pow(radius.toDouble(), 2.0)
            )
            ray.direction.mult(lambda.toFloat())
            return true
        } else if (distMinR2 == 0) {
            return true
        }
        return false
    }
}
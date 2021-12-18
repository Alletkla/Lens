package model.manager

import model.Circle
import model.Lens
import model.light.Ray
import model.Side
import processing.core.PVector
import kotlin.math.pow


object CollisionManager {
    /**
     * Problem to solve: "Bullet-trough-Paper-Problem": The tip of the ray moves to fast that it will never exactly hit the radius of the circle
     */
    fun getOrientationToLensPlain(ray: Ray, lens: Lens): Side {
        //Cause the Midpoints are on the opposite site of the displayed lens curve its on the left Side, if the Circle for the right curve is nearer
        if (ray.end.dist(lens.circleLeft.midPoint) > ray.end.dist(lens.circleRight.midPoint)) {
            return Side.LEFT_SIDE_OF_LENS_PLAIN
        }
        return Side.RIGHT_SIDE_IF_LENS_PLAIN
    }

    /**
     * correctly checks if the ray will Intersect with the referenceCircle depending on the position (in or outside)
     * cause the "Intersect" check is kept easy and only checks for "isInsideRadius"
     */
    fun rayWillIntersectLens(ray: Ray, lens : Lens): Boolean {
        return if (ray.inLens){
            !continuousCollisionDetection(ray.end, ray.getMovementVector(), ray.referenceCircle)
        }else{
            continuousCollisionDetection(ray.end, ray.getMovementVector(), ray.referenceCircle)
        }
    }


    /**
     * Checks if the line of current point and point + step intersects the given circle
     * @see <a href="https://gamedev.stackexchange.com/questions/22765/how-do-i-check-collision-when-firing-bullet"</a>
     */
    fun continuousCollisionDetection(point: PVector, step: PVector, circle: Circle): Boolean {
        // if already in circle its already collided
        if (point.dist(circle.midPoint) < circle.r) {
            return true
        }
        // check if it will collide in next step.
        if (point.copy().add(step).dist(circle.midPoint) < circle.r) {
            return true
        }
        return false
    }

    /**
     * This method checks whether the given Ray collides with the given Lens and scales the Ray in such a way that
     * it seems that it hit the lens perfectly
     */
    fun rayCollidesLens(ray: Ray, lens: Lens): Boolean {
        if (!ray.inLens && !ray.willIntersectLensPlane(lens)){
            return false
        }

        ray.referenceCircle = when (getOrientationToLensPlain(ray, lens)) {
            Side.LEFT_SIDE_OF_LENS_PLAIN -> lens.circleLeft
            Side.RIGHT_SIDE_IF_LENS_PLAIN ->lens.circleRight
        }

        if (!rayWillIntersectLens(ray, lens)) {
            return false
        }

        //if the ray will intersect, scale it in the next frame in such a way, that it will look like it hit perfectly
        val distToRadiusOfRefCircle = ray.end.dist(ray.referenceCircle.midPoint) - ray.referenceCircle.r
        val end2Mid = PVector()
        end2Mid.set(ray.referenceCircle.midPoint).sub(ray.end)
        //scale the vector from end to Mid down to the distance to the radius of the Reference Circle
        val end2Radius = PVector()
        end2Radius.set(end2Mid).mult(distToRadiusOfRefCircle / end2Mid.mag())
        //project the distance onto the direction vector of the ray. This is good enough for this purposes, although
        //it may lose precision if the steps of the ray are larger and the distance and direction vector has a large
        //angle to each other
        val distProjectionOnDirection = PVector()
        distProjectionOnDirection.set(ray.direction).mult(
            end2Radius.dot(ray.direction) / ray.direction.mag()
                .pow(2)
        )
        //Direction scaled to the Projection of Distance on the direction vector
        ray.direction = ray.direction.mult(distProjectionOnDirection.mag()).div(ray.speed)
        ray.end.add(ray.direction.copy().mult(ray.speed))
        return true
    }
}
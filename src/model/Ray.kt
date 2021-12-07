package model

import processing.core.PApplet
import processing.core.PApplet.degrees
import processing.core.PApplet.radians
import processing.core.PVector
import java.lang.Math.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin


class Ray(xstart: Float, ystart: Float, temp_speed: Float){
    var start: PVector
    var end: PVector
    var direction: PVector
    var Lens_plane_intersect: PVector? = null
    var BezMid: PVector? = null
    var speed: Float
    var refraction_angle = 0f
    var incidence_angle = 0f
    var distance = 0f
    var inLens = false
    var refracted_ray: Ray? = null

    //calc Direction and save it into Object
    fun calcDirection(xCord: Float, yCord: Float) {
        //If the second Point is exactly the start point, choose Default direction
        if (start.x == xCord && start.y == yCord) {
            direction.set(1F,0F)
            return
        }
        direction.set(xCord,yCord)
        direction.sub(start)
        direction.normalize()
    }

    //berechnet den neuen Endpunkt, wenn nicht kollidiert
    //TODO:: must check on Rendering if it can move
    fun move() {
        if (refracted_ray != null) {
            return
        }
        direction.mult(speed)
        end.add(direction)
        direction.normalize()
    }

    fun intersectsLensPlane(lens: Lens): Boolean {
        val mRay = (direction.y / direction.x).toDouble() //Anstieg des Strahls
        val mLensPlane: Float = lens.LensSystem!!.e2.y / lens.LensSystem!!.e2.x //Anstieg der Linsenebene

        Lens_plane_intersect = PVector()
        //Berechnung des Schnittpunkts im Linsensystem und daher Addition der x-Koordinate des Linsensystems notwendig
        Lens_plane_intersect!!.x =
            (((lens.LensSystem!!.position.y - start.y) / (mRay - mLensPlane) + lens.LensSystem!!.position.x).toFloat())
        Lens_plane_intersect!!.y = (mRay * Lens_plane_intersect!!.x + start.y).toFloat()


        val directionToXPoint = PVector()
        directionToXPoint.set(Lens_plane_intersect!!.x, Lens_plane_intersect!!.y)
        directionToXPoint.sub(end).normalize()
        return directionToXPoint.dot(direction) != -directionToXPoint.mag() * direction.mag()
    }

    fun behindLensPlane(): Boolean {
        val directionToXPoint = PVector()
        directionToXPoint.set(Lens_plane_intersect)
        directionToXPoint.sub(end).normalize()
        return directionToXPoint.dot(direction) == -directionToXPoint.mag() * direction.mag()
    }

    fun refract(lens: Lens) {
        //TODO: Alle Winkel in Radians;
        if (refraction_angle == 0f) {
            var temp1 = PVector()
            var temp2 = PVector()
            temp1.set(lens.lot)
            val dot = temp1.rotate(PI.toFloat()).dot(direction).toDouble()
            //dot = Math.abs(lens.lot.dot(direction));
            val lotmag: Float = lens.lot!!.mag()
            val directionmag = direction.mag().toDouble()
            incidence_angle = degrees(kotlin.math.acos((dot / (lotmag * directionmag)).toFloat()).toFloat())
            if (temp1.y / temp1.x < direction.y / direction.x) {
                incidence_angle = -incidence_angle
            }
            refraction_angle = if (!inLens) {
                degrees(kotlin.math.asin(kotlin.math.sin(radians(incidence_angle)) * lens.nOutside / lens.nLens).toFloat())
            } else {
                degrees(kotlin.math.asin(sin(radians(incidence_angle)) * lens.nLens / lens.nOutside).toFloat())
            }

            refracted_ray = Ray(end.x, end.y, speed)
            refracted_ray!!.end.set(refracted_ray!!.start)
            val refSysRefraction: RefSystem
            if (inLens) {
                refSysRefraction = RefSystem(end, BezMid, end)
            } else {
                refSysRefraction = RefSystem(BezMid, end, end)
            }
            //refSysRefraction.render(Color(255, 0, 0), 50)
            val newRayLength = 10f
            val yDirection: Float = sin(radians(refraction_angle)) * newRayLength
            val xDirection: Float = cos(radians(refraction_angle)) * newRayLength
            temp1 = PVector()
            temp2 = PVector()
            temp1.set(refSysRefraction.position)
            temp2.set(temp1)
            temp2.add(refSysRefraction.e1.copy().mult(xDirection))
            temp2.add(refSysRefraction.e2.copy().mult(yDirection))

            //circle(temp2.x, temp2.y, 5)
            refracted_ray!!.direction.set(temp2.sub(temp1))
            refracted_ray!!.direction.normalize()
            refracted_ray!!.inLens = !inLens
        }
    }

//    fun collide(lens: Lens): Boolean {
//        if (!inLens && Lens_plane_intersect == null) {
//            return false
//        }
//        if (!inLens && behindLensPlane()) {
//            return false
//        }
//
//        //TODO: irgendwie in den Konstruktor bringen.
//        BezMid = PVector()
//        val radius: Float
//        radius = if (inLens) {
//            BezMid!!.set(lens.MidPointCircle1)
//            lens.r1
//        } else {
//            BezMid!!.set(lens.MidPointCircle2)
//            lens.r2
//        }
//        distance = end.copy().add(direction).dist(BezMid)
//        val dist_min_r2: Int = ((end.dist(BezMid) - radius) * 2).roundToInt()
//        val totest: Boolean
//        totest = if (inLens) {
//            distance > radius
//        } else {
//            distance < radius
//        }
//        if (totest && dist_min_r2 != 0) {
//            val EndToMid = PVector()
//            EndToMid.set(end).sub(BezMid)
//            val dotEndToMidDirection = EndToMid.dot(direction).toDouble()
//            val lambda = -dotEndToMidDirection - kotlin.math.sqrt(
//                dotEndToMidDirection.pow(2.0) - EndToMid.mag().toDouble().pow(2.0) + radius.toDouble().pow(2.0)
//            )
//            direction.mult(lambda.toFloat())
//            return true
//        } else if (dist_min_r2 == 0) {
//            return true
//        }
//        return false
//    }

    //renders the ray to the Screen
    fun render(renderContext : PApplet) {
        renderContext.strokeWeight(1F)
        renderContext.stroke(0)
        renderContext.line(start.x, start.y, end.x, end.y)
    }

    //Konstruktor
    init {
        start = PVector()
        start.x = xstart
        start.y = ystart
        end = PVector(start.x, start.y)
        speed = temp_speed
        direction = PVector()
    }
}
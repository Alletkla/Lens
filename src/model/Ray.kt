package model

import processing.core.PApplet
import processing.core.PApplet.degrees
import processing.core.PApplet.radians
import processing.core.PVector
import java.lang.Math.*
import kotlin.math.cos
import kotlin.math.sin


class Ray(xstart: Float, ystart: Float, temp_speed: Float){
    var start: PVector
    var end: PVector
    var direction: PVector
    var Lens_plane_intersect: PVector? = null
    lateinit var referenceCircle: Circle
    var speed: Float
    var inLens = false
    var refracted_ray: Ray? = null

    enum class Orientation{
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

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

    fun getOrientation() : Orientation{
        if (direction.dot(PVector(1F,0F)) > 0){
            return Orientation.LEFT_TO_RIGHT
        }
        return Orientation.RIGHT_TO_LEFT
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
        return when (this.getOrientation()) {
            Orientation.LEFT_TO_RIGHT -> this.start.x < lens.lensSystem.position.x
            Orientation.RIGHT_TO_LEFT -> this.start.x > lens.lensSystem.position.y
        }
    }

    fun willIntersectLensPlane(lens : Lens) : Boolean {
        return when (this.getOrientation()) {
            Orientation.LEFT_TO_RIGHT -> this.end.x < lens.lensSystem.position.x
            Orientation.RIGHT_TO_LEFT -> this.end.x > lens.lensSystem.position.y
        }
    }

    fun getMovementVector() : PVector{
        return PVector().set(this.direction).mult(this.speed)
    }

    fun refract(lens: Lens, renderContext: PApplet) {
        if (this.refracted_ray == null) {

            val lot = if (inLens){
                PVector().set(this.end).sub(this.referenceCircle.midPoint)  //Einfallslot
            }else {
                PVector().set(this.referenceCircle.midPoint).sub(this.end)  //Einfallslot
            }


            //renderContext.line(this.end.x, this.end.y, this.end.x-lot.x, this.end.y-lot.y)

            //Einfallswinkel \alpha .... \vec{a} \dot \vec{b} = a * b * cos \alpha
            //in Radians
            val angleOfIncidence = kotlin.math.acos(lot.dot(direction)/(direction.mag()*lot.mag()))

                //n_2/n_1 = sin(a)/sin(b)
            val angleOfRefraction = if (inLens){
                kotlin.math.asin(sin(angleOfIncidence) * lens.nLens / lens.nOutside)
            }else{
                kotlin.math.asin(sin(angleOfIncidence) * lens.nOutside / lens.nLens)
            }

            //copy lot and rotate by angle of refraction

            //neglect the difference in speed of light.
            refracted_ray = Ray(end.x, end.y, speed)

            //Drehmatrix ist cos()  -sin()
            //               sin()   cos()
            //und mathematischer Drehsinn ist gegen den Uhrzeiger → Winkel zum Drehen = -Brechwinkel
            //Achtung!: Um zwei Matrizen miteinander multiplizieren zu können, muss die Spaltenzahl der ersten Matrix mit der Zeilenzahl der zweiten Matrix übereinstimmen
            //Ergo: Matrix * Vektor

            val rotationDirection = if (lot.y > 0) RotDirection.CLOCKWISE else RotDirection.COUNTERCLOCKWISE

            refracted_ray!!.direction = PVector(lot.x*cos(angleOfRefraction*rotationDirection.corrector)-lot.y*sin(angleOfRefraction*rotationDirection.corrector),
                                                lot.x*sin(angleOfRefraction*rotationDirection.corrector)+lot.y*cos(angleOfRefraction*rotationDirection.corrector)
                                                )
            refracted_ray!!.direction.normalize()

            refracted_ray!!.inLens = !inLens
        }
    }

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
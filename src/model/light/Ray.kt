package model.light

import controller.Controller
import model.Circle
import model.Lens
import model.RotDirection
import model.manager.CollisionManager
import processing.core.PVector
import view.RenderContextProvider
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin


class Ray(val start: PVector, var direction: PVector, val speed: Float) : Light{
    var end: PVector = start.copy()
    lateinit var referenceCircle: Circle //TODO probably think about deleting this here and compute it everytime it is used (actually only 2 times)
    var inLens = false
    var refractedRay: Ray? = null
    var process = true
    var color = Color.BLACK

    enum class Orientation {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    //calc Direction and save it into Object
    fun calcDirection(xCord: Float, yCord: Float) {
        //If the second Point is exactly the start point, choose Default direction
        if (start.x == xCord && start.y == yCord) {
            direction.set(1F, 0F)
            return
        }
        direction.set(xCord, yCord)
        direction.sub(start)
        direction.normalize()
    }

    private fun getOrientation(): Orientation {
        if (direction.dot(PVector(1F, 0F)) > 0) {
            return Orientation.LEFT_TO_RIGHT
        }
        return Orientation.RIGHT_TO_LEFT
    }

    //berechnet den neuen Endpunkt, wenn nicht, kollidiert
    override fun move() {
        if (refractedRay == null) {

            end.add(direction.copy().mult(speed))
            if ( end.y < 0 || end.y > RenderContextProvider.renderContext.height
                || end.x < 0 || end.x > RenderContextProvider.renderContext.width){
                process = false
            }

            if (CollisionManager.rayCollidesLens(this, Controller.lens)) {
                this.refract(Controller.lens)
            }
        }else {
            process = refractedRay?.process == true
        }
        refractedRay?.move()
    }

    fun intersectsLensPlane(lens: Lens): Boolean {
        return when (this.getOrientation()) {
            Orientation.LEFT_TO_RIGHT -> this.start.x < lens.lensSystem.position.x
            Orientation.RIGHT_TO_LEFT -> this.start.x > lens.lensSystem.position.y
        }
    }

    fun willIntersectLensPlane(lens: Lens): Boolean {
        return when (this.getOrientation()) {
            Orientation.LEFT_TO_RIGHT -> this.end.x < lens.lensSystem.position.x
            Orientation.RIGHT_TO_LEFT -> this.end.x > lens.lensSystem.position.y
        }
    }

    fun getMovementVector(): PVector {
        return PVector().set(this.direction).mult(this.speed)
    }

    fun refract(lens: Lens) {
        if (this.refractedRay == null) {

            val lot = if (inLens) {
                PVector().set(this.end).sub(this.referenceCircle.midPoint)  //Einfallslot
            } else {
                PVector().set(this.referenceCircle.midPoint).sub(this.end)  //Einfallslot
            }

            //Lot render
            //renderContext.line(end.x, end.y, end.x-lot.x, end.y - lot.y)


            //renderContext.line(this.end.x, this.end.y, this.end.x-lot.x, this.end.y-lot.y)

            //Einfallswinkel \alpha .... \vec{a} \dot \vec{b} = a * b * cos \alpha
            //in Radians
            val angleOfIncidence = kotlin.math.acos(lot.dot(direction) / (direction.mag() * lot.mag()))

            //n_2/n_1 = sin(a)/sin(b)
            val angleOfRefraction = if (inLens) {
                kotlin.math.asin(sin(angleOfIncidence) * lens.nLens / lens.nOutside)
            } else {
                kotlin.math.asin(sin(angleOfIncidence) * lens.nOutside / lens.nLens)
            }

//            val refSystem = RefSystem(end, referenceCircle.midPoint, end)
//
//            refSystem.render(renderContext.g)

            //Drehmatrix ist cos()  -sin()
            //               sin()   cos()
            //und mathematischer Drehsinn ist gegen den Uhrzeiger → Winkel zum Drehen = -Brechwinkel
            //Achtung!: Um zwei Matrizen miteinander multiplizieren zu können, muss die Spaltenzahl der ersten Matrix mit der Zeilenzahl der zweiten Matrix übereinstimmen
            //Ergo: Matrix * Vektor
            val rotationDirection =
                if (lot.y / lot.x > direction.y / direction.x) RotDirection.CLOCKWISE else RotDirection.COUNTERCLOCKWISE

            //rotate lot by angle of refraction
            val newDirection = PVector(
                lot.x * cos(angleOfRefraction * rotationDirection.corrector) - lot.y * sin(angleOfRefraction * rotationDirection.corrector),
                lot.x * sin(angleOfRefraction * rotationDirection.corrector) + lot.y * cos(angleOfRefraction * rotationDirection.corrector)
            )

            newDirection.normalize()

            //neglect the difference in speed of light.
            refractedRay = Ray(end, newDirection, speed)
            refractedRay!!.color = color
            refractedRay!!.inLens = !inLens
        }
    }

}
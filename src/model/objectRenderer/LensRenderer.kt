package model.objectRenderer

import model.Lens
import processing.core.PApplet
import processing.core.PGraphics
import view.RenderContextProvider

object LensRenderer : ObjectRenderer<Lens> {

    var renderContext : PGraphics = RenderContextProvider.renderContext

    override fun <T : Lens> render(element : T) {
        renderContext.beginDraw()
        renderGlas(element)
        renderFocalLength(element)
        renderLensPlane(element)
        renderContext.endDraw()
    }

    fun renderAccessories(lens: Lens){
        //KreisMittelpunkt der Bogen nach rechts Schlägt
        renderContext.fill(0)
        renderContext.circle(lens.circleRight.midPoint.x, lens.circleRight.midPoint.y, 5f)

        //KreisMittelpunkt der Bogen nach links schlägt
        renderContext.fill(0)
        renderContext.circle(lens.circleLeft.midPoint.x, lens.circleLeft.midPoint.y, 5f)
    }

    fun renderFocalLength(lens: Lens) {
        renderContext.fill(0)
        renderContext.beginDraw()
        renderContext.circle(lens.focalLengthPoint1.x, lens.focalLengthPoint1.y, 5f)
        renderContext.circle(lens.focalLengthPoint2.x,lens.focalLengthPoint2.y, 5f)
        renderLabel("Brennweite", lens.focalLengthPoint1.x, lens.focalLengthPoint1.y, renderContext)
        renderLabel("Brennweite", lens.focalLengthPoint2.x, lens.focalLengthPoint2.y, renderContext)
    }

    private fun renderGlas(lens : Lens): Boolean {
        //Linsenglas
        renderContext.noFill()
        renderContext.arc(
            lens.midPoint.x - lens.circleRight.r + lens.d / 2,
            lens.midPoint.y,
            lens.circleRight.r * 2,
            lens.circleRight.r * 2,
            PApplet.radians(-lens.angleToXPoint),
            PApplet.radians(lens.angleToXPoint)
        )
        renderContext.arc(
            lens.midPoint.x + lens.circleLeft.r - lens.d / 2,
            lens.midPoint.y,
            lens.circleLeft.r * 2,
            lens.circleLeft.r * 2,
            PApplet.radians(180 - lens.angleToXPoint),
            PApplet.radians(180 + lens.angleToXPoint)
        )

        renderContext.fill(255)
        //Kreise an Schnittpunkten der Linsenkreise
        renderContext.circle(lens.circleRight.midPoint.x + lens.xPoint1.x, lens.circleRight.midPoint.y + lens.xPoint2.y, 8f)
        renderContext.circle(lens.circleRight.midPoint.x + lens.xPoint2.x, lens.circleRight.midPoint.y + lens.xPoint2.y, 8f)
        return true
    }

    private fun renderLabel(label: String?, posX: Float, posY: Float, renderContext: PGraphics) {
        val preTextAlign: Int = renderContext.textAlign
        renderContext.textAlign(PApplet.CENTER, PApplet.TOP)
        renderContext.text(label, posX, posY + 5)
        renderContext.textAlign(preTextAlign)
    }

    private fun renderLensPlane(lens : Lens){
        renderContext.line(0F, (renderContext.height / 2).toFloat(), renderContext.width.toFloat(), (renderContext.height / 2).toFloat())
    }
}

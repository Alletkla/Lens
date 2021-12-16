package model.objectRenderer

import model.Ray
import processing.core.PGraphics
import view.RenderContextProvider
import java.awt.Color

object RayRenderer : ObjectRenderer<Ray> {

    var renderContext: PGraphics = RenderContextProvider.renderContext

    override fun <T : Ray> render(element: T) {
        renderContext.beginDraw()

        renderContext.strokeWeight(1F)
        renderContext.stroke(0)
        renderContext.stroke(element.color.rgb)
        renderContext.line(element.start.x, element.start.y, element.end.x, element.end.y)

        element.refractedRay?.also { render(it) }
        renderContext.stroke(Color.BLACK.rgb)
        renderContext.endDraw()
    }

}
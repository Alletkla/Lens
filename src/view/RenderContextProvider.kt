package view

import processing.core.PGraphics
import java.awt.image.renderable.RenderContext

/**
 * holds an instance of Viewport to globally access it. Needs to be initialized on startup
 */
object RenderContextProvider {
    lateinit var renderContext: PGraphics
}
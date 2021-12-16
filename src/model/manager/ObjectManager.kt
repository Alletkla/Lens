package model.manager

import model.Lens
import model.Ray
import model.rayCreator.RayCreator
import model.rayCreator.RayCreatorAngle
import processing.core.PVector
import view.Button
import java.awt.Color

object ObjectManager {
    private val buttonMap = mutableMapOf<String, Button>()
    private val lensList = mutableListOf<Lens>()
    private val rayList = mutableListOf<Ray>()
    private var rayCreator : RayCreator = RayCreatorAngle()

    fun addButton(key : String, button: Button){
        buttonMap[key] = button
    }

    fun addLens(lens: Lens){
        lensList.add(lens)
    }

    fun addRay(ray: Ray){
        rayList.add(ray)
    }

    fun getButtonByID(key : String) : Button? {
        return buttonMap[key]
    }

    fun getButtonList() : List<Button>{
        return buttonMap.values.toList()
    }

    fun getLensList(): List<Lens>{
        return lensList.toList()
    }

    fun getRayList(): List<Ray> {
        return rayList.toList()
    }

    fun rayInit(pVector: PVector){
        rayCreator.initialize(pVector)
    }

    fun rayCreate(pVector: PVector){
        rayList.addAll(rayCreator.create(pVector))
    }

    fun rayCreate(pVector: PVector, color: Color){
        rayCreator.color = color
        rayList.addAll(rayCreator.create(pVector))
        rayCreator.color = Color.BLACK
    }

    fun removeButton(key : String, button: Button) : Boolean{
        return buttonMap.remove(key, button)
    }

    fun removeLens(lens: Lens) : Boolean{
        return lensList.remove(lens)
    }

    fun removeRay(ray: Ray) : Boolean{
        return this.rayList.remove(ray)
    }

    fun setMode(mode : RayCreator){
        this.rayCreator = mode
    }
}
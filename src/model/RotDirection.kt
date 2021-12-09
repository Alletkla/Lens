package model

/**
 * enumeration for mathematical direction of rotation.
 * ClockWise means -1, cause Angles have to be multiplied with -1 to use in mathematical formulas
 */
enum class RotDirection(val corrector : Int) {
    CLOCKWISE(-1), COUNTERCLOCKWISE(1)
}
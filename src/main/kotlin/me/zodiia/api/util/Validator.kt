package me.zodiia.api.util

object Validator {
    private fun errorMsg(type: String, message: String, actual: String? = null, expected: String? = null) =
        "Failed to validate $type: $message${if (actual != null && expected != null) "(actual: $actual, expected: $expected)" else ""}."

    fun requireNonNull(vararg objects: Any?) {
        objects.forEach {
            if (it == null) {
                throw NullPointerException(errorMsg("Object", "Object cannot be null"))
            }
        }
    }

    fun requireStringLength(str: String?, min: Int, max: Int = Int.MAX_VALUE) {
        requireNonNull(str); str as String
        if (str.length < min || str.length > max) {
            throw IllegalStateException(errorMsg("String", "String does not match the required length range",
                    "$str.length", "$min${if (max != Int.MAX_VALUE) " - $max" else "any"}"))
        }
    }

    fun requireStringIsInt(str: String?) {
        requireNonNull(str); str as String
        str.toIntOrNull() ?: throw NumberFormatException(errorMsg("Number", "String is not a valid integer"))
    }

    fun requireStringIsDouble(str: String?) {
        requireNonNull(str); str as String
        str.toIntOrNull() ?: throw NumberFormatException(errorMsg("Number", "String is not a valid floating-point number"))
    }

    fun requireListContainsString(str: String?, values: Collection<String>, ignoreCase: Boolean = false) {
        requireNonNull(str); str as String
        values.forEach {
            if (str.equals(it, ignoreCase)) {
                return
            }
        }
        throw IllegalStateException(errorMsg("String", "String does not match any of the possible values"))
    }

    fun requireValid(vararg valids: Validated) {
        valids.forEach {
            if (!it.valid) {
                throw IllegalStateException(errorMsg("Object", "Object has been invalidated"))
            }
        }
    }

    fun requireListSizeAtLeast(col: Collection<*>, min: Int) {
        if (col.size < min) {
            throw IllegalStateException(errorMsg("List", "List does not match the required length", "${col.size}", ">= $min"))
        }
    }

    fun requireListSizeAtMost(col: Collection<*>, max: Int) {
        if (col.size > max) {
            throw IllegalStateException(errorMsg("List", "List does not match the required length", "${col.size}", "<= $max"))
        }
    }

    fun requireListSizeBetween(col: Collection<*>, min: Int, max: Int) {
        if (col.size < min || col.size > max) {
            throw IllegalStateException(errorMsg("List", "List does not match the required length", "${col.size}", "$min - $max"))
        }
    }
}

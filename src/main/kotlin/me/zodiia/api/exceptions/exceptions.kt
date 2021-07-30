package me.zodiia.api.exceptions

class MissingLanguageException(language: String): ApiException("Tried to retrieve a non-existent language: $language")

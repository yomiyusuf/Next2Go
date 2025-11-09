package com.yomi.next2go.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoryTest {

    @Test
    fun categoryId_greyhound_hasCorrectId() {
        assertEquals("9daef0d7-bf3c-4f50-921d-8e818c60fe61", CategoryId.GREYHOUND.id)
    }

    @Test
    fun categoryId_harness_hasCorrectId() {
        assertEquals("161d9be2-e909-4326-8c2c-35ed71fb460b", CategoryId.HARNESS.id)
    }

    @Test
    fun categoryId_horse_hasCorrectId() {
        assertEquals("4a2788f8-e825-4d36-9894-efd4baf1cfae", CategoryId.HORSE.id)
    }

    @Test
    fun fromId_validGreyhoundId_returnsGreyhound() {
        val result = CategoryId.fromId("9daef0d7-bf3c-4f50-921d-8e818c60fe61")
        assertEquals(CategoryId.GREYHOUND, result)
    }

    @Test
    fun fromId_validHarnessId_returnsHarness() {
        val result = CategoryId.fromId("161d9be2-e909-4326-8c2c-35ed71fb460b")
        assertEquals(CategoryId.HARNESS, result)
    }

    @Test
    fun fromId_validHorseId_returnsHorse() {
        val result = CategoryId.fromId("4a2788f8-e825-4d36-9894-efd4baf1cfae")
        assertEquals(CategoryId.HORSE, result)
    }

    @Test
    fun fromId_invalidId_returnsNull() {
        val result = CategoryId.fromId("invalid-id")
        assertNull(result)
    }

    @Test
    fun fromId_emptyId_returnsNull() {
        val result = CategoryId.fromId("")
        assertNull(result)
    }
}
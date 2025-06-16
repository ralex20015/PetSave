package com.realworld.android.petsave.common.domain.model.animal

import org.junit.Assert.assertEquals
import org.junit.Test


class PhotoTests {

    private val mediumPhoto = "mediumPhoto"
    private val fullPhoto = "fullPhoto"
    private val invalidPhoto = ""

    @Test
    fun photo_getSmallestAvailablePhoto_hasMediumPhoto() {
        //Given
        val photo = Media.Photo(mediumPhoto, fullPhoto)
        val expectedValue = mediumPhoto
        //When
        val smallestPhoto = photo.getSmallestAvailablePhoto()
        //Then
        assertEquals(smallestPhoto, expectedValue)
    }

    @Test
    fun photo_getSmallestAvailablePhoto_noMediumPhoto_hasFullPhoto() {
        //Given
        val photo = Media.Photo(invalidPhoto, fullPhoto)
        val expectedValue = fullPhoto

        //When
        val smallestPhoto = photo.getSmallestAvailablePhoto()

        //Then
        assertEquals(smallestPhoto, expectedValue)
    }

    @Test
    fun photo_getSmallestPhotoAvailablePhoto_noPhotos() {
        //Given
        val photo = Media.Photo(invalidPhoto, invalidPhoto)
        val expectedValue = invalidPhoto

        //When
        val smallestPhoto = photo.getSmallestAvailablePhoto()

        //Then
        assertEquals(smallestPhoto, expectedValue)
    }
}
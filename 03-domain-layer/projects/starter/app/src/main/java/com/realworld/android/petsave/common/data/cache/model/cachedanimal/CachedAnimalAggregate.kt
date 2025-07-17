package com.realworld.android.petsave.common.data.cache.model.cachedanimal

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.realworld.android.petsave.common.domain.model.animal.details.AnimalWithDetails

data class CachedAnimalAggregate(
    @Embedded
    val animal: CachedAnimalWithDetails,
    @Relation(
        parentColumn = "animalId",
        entityColumn = "animalId",
    )
    val photos: List<CachedPhoto>,
    @Relation(
        parentColumn = "animalId",
        entityColumn = "animalId"
    )
    val videos: List<CachedVideo>,
    @Relation(
        parentColumn = "animalId",
        entityColumn = "tag",
        associateBy = Junction(CachedAnimalTagCrossRef::class) //
    )//Use associateBy to create the many-to-many relationship with Room. You set it
    //to a Junction that takes the cross-reference class as a parameter.
    val tags: List<CachedTag>
) {
    companion object {
        fun fromDomain(animalWithDetails: AnimalWithDetails): CachedAnimalAggregate {
            return CachedAnimalAggregate(
                animal = CachedAnimalWithDetails.fromDomain(animalWithDetails),
                photos = animalWithDetails.media.photos.map {
                    CachedPhoto.fromDomain(animalWithDetails.id, it)
                },
                videos = animalWithDetails.media.videos.map {
                    CachedVideo.fromDomain(animalWithDetails.id, it)
                },
                tags = animalWithDetails.tags.map { CachedTag(it) }
            )
        }
    }
}
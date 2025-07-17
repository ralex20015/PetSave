package com.realworld.android.petsave.common.data.cache.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.realworld.android.petsave.common.data.cache.model.cachedanimal.CachedAnimalAggregate
import com.realworld.android.petsave.common.data.cache.model.cachedanimal.CachedAnimalWithDetails
import com.realworld.android.petsave.common.data.cache.model.cachedanimal.CachedPhoto
import com.realworld.android.petsave.common.data.cache.model.cachedanimal.CachedTag
import com.realworld.android.petsave.common.data.cache.model.cachedanimal.CachedVideo
import io.reactivex.Flowable

@Dao
abstract class AnimalsDao {
    @Transaction
    @Query("SELECT * FROM animals")
    abstract fun getAllAnimals(): Flowable<List<CachedAnimalAggregate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAnimalAggregate(
        animal: CachedAnimalWithDetails,
        photos: List<CachedPhoto>,
        videos: List<CachedVideo>,
        tags: List<CachedTag>
    )

    suspend fun insertAnimalsWithDetails(animalAggregates: List<CachedAnimalAggregate>) {
        for (animalAggregate in animalAggregates) {
            insertAnimalAggregate(
                animalAggregate.animal,
                animalAggregate.photos,
                animalAggregate.videos,
                animalAggregate.tags,
            )
        }
    }
}
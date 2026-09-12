package com.lunesu.bcferries

import android.util.Log

open class FerryRepository(db: DbOpenHelper) {
    companion object {
        private const val TAG = "FerryRepository"
    }

    private val ferryDao = FerryDao(db)

    open fun getFerries(from: FerryPier, dow: FerryDay): List<Ferry> {
        return ferryDao.query(from, dow)
    }

    open fun shouldRefresh(): Boolean {
        return false
    }

    open suspend fun refresh() {
        runCatching {
            ferryDao.save(Utils.atLeast(BCFerriesApiFetcher.fetch(), 40), *FerryPier.ENUMS)
        }.onFailure {
            // Keep whatever schedule we already have
            Log.w(TAG, "refresh failed", it)
        }
    }
}

/*
 * Copyright (C) 2017-2018 Trillium Inc. <support@trilliumsecure.com>
 */
package jp.co.trillium.secureskye.vehicle.message.repository

import jp.co.trillium.secureskye.vehicle.message.model.WheelSpeedEvent
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime
import java.util.UUID

/**
 * Repository for managing [WheelSpeedEvent] entities.
 */
interface WheelSpeedEventRepository : WheelSpeedEventRepositoryBase, WheelSpeedEventRepositoryCustom

/**
 * Base repository, containing functions that can be defined by query named methods.
 */
interface WheelSpeedEventRepositoryBase : MongoRepository<WheelSpeedEvent, ObjectId> {

    /**
     * Find all items of one vehicle by its [vehicleId].
     */
    fun findByVehicleId(vehicleId: UUID, page: Pageable): List<WheelSpeedEvent>

    /**
     * Find all items of one vehicle that occurred after a specific [systemTimestamp].
     */
    fun findByVehicleIdAndSystemTimestampAfter(
        vehicleId: UUID,
        systemTimestamp: LocalDateTime,
        page: Pageable
    ): List<WheelSpeedEvent>
}

/**
 * Custom repository, containing functions that need manual implementation.
 */
interface WheelSpeedEventRepositoryCustom : FindLatestLimit<WheelSpeedEvent>

/**
 * Implementation of the custom repository.
 */
class WheelSpeedEventRepositoryCustomImpl(
    private val wheelSpeedEventRepositoryBase: WheelSpeedEventRepositoryBase
) : WheelSpeedEventRepositoryCustom {

    /**
     * Find the latest [limit] items filtered by [vehicleId].
     */
    override fun findLatestLimit(vehicleId: UUID, limit: Int): List<WheelSpeedEvent> =
        wheelSpeedEventRepositoryBase.findByVehicleId(
            vehicleId,
            PageRequest.of(0, limit, DESC, "systemTimestamp.dt", "systemTimestamp.ns")
        )

    /**
     * Find the latest items after [since].
     */
    override fun findLatestSince(vehicleId: UUID, since: LocalDateTime): List<WheelSpeedEvent> =
        wheelSpeedEventRepositoryBase.findByVehicleIdAndSystemTimestampAfter(
            vehicleId,
            since,
            PageRequest.of(0, FindLatestLimit.DEFAULT_LIMIT, DESC, "systemTimestamp.dt", "systemTimestamp.ns")
        )
}

package me.zodiia.api.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.asMono
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.emptySized
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Suppress("UnnecessaryAbstractClass")
abstract class AbstractRepository<I : Comparable<I>, T : Entity<I>>(
    protected val entityClass: EntityClass<I, T>,
    protected val db: Database,
) {
    suspend fun findOne(id: I): Mono<T> {
        return suspendedTransactionAsync(Dispatchers.Default, db) {
            entityClass.findById(id)
        }.asMono(Dispatchers.Default)
    }

    suspend fun find(op: SqlExpressionBuilder.() -> Op<Boolean>): Flux<T> {
        return suspendedTransactionAsync(Dispatchers.Default, db) {
            entityClass.find(op)
        }.asMono(Dispatchers.Default).flatMapMany { Flux.fromIterable(it) }
    }

    suspend fun findLimit(limit: Int = 0, offset: Long = 0L, op: SqlExpressionBuilder.() -> Op<Boolean>): SizedIterable<T> {
        var res: SizedIterable<T> = emptySized()

        transaction(db) {
            res = entityClass.find(op).limit(limit, offset)
        }
        return res
    }

//    protected suspend fun suspendTransaction(op: Transaction.() -> T) {}
}

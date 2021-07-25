package me.zodiia.api.data

import kotlinx.coroutines.reactor.asMono
import me.zodiia.api.threads.SpigotDispatchers
import me.zodiia.api.util.toPairArray
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Suppress("UnnecessaryAbstractClass")
abstract class AbstractRepository<I : Comparable<I>, T : Entity<I>>(
    protected val entityClass: EntityClass<I, T>,
    protected val db: Database,
) {
    suspend fun findOne(id: I): Mono<T> = monoTransaction {
        entityClass.findById(id)
    }

    suspend fun find(
        limit: Int? = null,
        offset: Long? = null,
        orderBy: Map<Expression<*>, SortOrder>? = null,
        forUpdate: Boolean = false,
        op: SqlExpressionBuilder.() -> Op<Boolean>,
    ): Flux<T> = fluxTransaction {
        val query = entityClass.find(op)

        limit?.let { query.limit(it, offset ?: 0L) }
        orderBy?.let { query.orderBy(*it.toPairArray()) }
        if (forUpdate) {
            query.forUpdate()
        }
        query
    }

    suspend fun count(
        limit: Int? = null,
        offset: Long? = null,
        orderBy: Map<Expression<*>, SortOrder>? = null,
        op: SqlExpressionBuilder.() -> Op<Boolean>,
    ): Mono<Long> = monoTransaction {
        val query = entityClass.find(op)
        
        limit?.let { query.limit(it, offset ?: 0L) }
        orderBy?.let { query.orderBy(*it.toPairArray()) }
        query.count()
    }

    suspend fun <R : Any> monoTransaction(op: suspend Transaction.() -> R?) =
        suspendedTransactionAsync(SpigotDispatchers.Async, db, statement = op)
            .asMono(SpigotDispatchers.Async)

    suspend fun <R : Any> fluxTransaction(op: suspend Transaction.() -> SizedIterable<R>) =
        suspendedTransactionAsync(SpigotDispatchers.Async, db, statement = op)
            .asMono(SpigotDispatchers.Async)
            .flatMapMany { Flux.fromIterable(it) }

    suspend fun <R : Any> asyncTransaction(op: suspend Transaction.() -> Unit) =
        suspendedTransactionAsync(SpigotDispatchers.Async, db, statement = op)

    fun <R : Any> syncTransaction(op: Transaction.() -> Unit) =
        transaction(db, statement = op)
}

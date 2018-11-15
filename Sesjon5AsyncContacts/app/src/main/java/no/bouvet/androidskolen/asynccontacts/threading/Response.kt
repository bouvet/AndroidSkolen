package no.bouvet.androidskolen.asynccontacts.threading

@FunctionalInterface
interface Response<V> {

    fun respond(result: V)

}
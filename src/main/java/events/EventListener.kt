package events

fun interface EventListener<T> {
    fun invoke(args: T)
}

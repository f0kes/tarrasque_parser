package events

class Event<T> {
    private val listeners: MutableList<EventListener<T>> = ArrayList()
    private val toAdd: MutableList<EventListener<T>> = ArrayList()
    private val toRemove: MutableList<EventListener<T>> = ArrayList()

    fun subscribe(listener: EventListener<T>) {
        toAdd.add(listener)
    }

    fun unsubscribe(listener: EventListener<T>) {
        toRemove.add(listener)
    }

    fun invoke(args: T) {
        updateListeners()
        for (listener in listeners) {
            listener.invoke(args)
        }
    }

    private fun updateListeners() {
        for (listener in toAdd) {
            listeners.add(listener)
        }
        toAdd.clear()
        for (listener in toRemove) {
            listeners.remove(listener)
        }
        toRemove.clear()
    }
}

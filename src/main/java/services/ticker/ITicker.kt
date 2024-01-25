package services.ticker

import events.EventListener

interface ITicker {
    fun subscribeToTick(tickListener: EventListener<Int>)
    fun unsubscribeFromTick(tickListener: EventListener<Int>)
    fun subscribeToSecond(secondListener: EventListener<Int>)
    fun unsubscribeFromSecond(secondListener: EventListener<Int>)
    fun getServerTime(): Int
}
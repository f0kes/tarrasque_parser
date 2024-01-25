package services

object Services {
    private val services_map: MutableMap<Class<*>, Any> = HashMap()


    @JvmStatic
    inline fun <reified T> register(service: T?, overwrite: Boolean = false): T {
        registerInternal(T::class.java, service, overwrite)
        return service!!
    }

    fun <T> registerInternal(type: Class<T>, service: T?, overwrite: Boolean): T {
        requireNotNull(service) { "Service cannot be null" }
        require(!(services_map.containsKey(type) && !overwrite)) { "Service of type $type already registered" }
        services_map[type] = service
        return service
    }

    @JvmStatic
    inline fun <reified T> get(): T {
        return getInternal(T::class.java)
    }

    fun <T> getInternal(type: Class<T>): T {
        require(services_map.containsKey(type)) { "Service of type $type not registered" }
        return type.cast(services_map[type])
    }

    @JvmStatic
    inline fun <reified T> remove() {
        removeInternal(T::class.java)
    }

    fun <T> removeInternal(type: Class<T>) {
        require(services_map.containsKey(type)) { "Service of type $type not registered" }
        services_map.remove(type)
    }

    fun clear() {
        services_map.clear()
    }
}
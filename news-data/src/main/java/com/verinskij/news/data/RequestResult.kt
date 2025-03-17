package com.verinskij.news.data

sealed class RequestResult<out E: Any>(open val data: E?) {

    class InProgress<E: Any>(data: E? = null) : RequestResult<E>(data)
    class Success<E: Any>(override val data: E) : RequestResult<E>(data)
    class Error<E: Any>(data: E? = null, val error: Throwable? = null) : RequestResult<E>(data)
}

internal fun <T: Any> Result<T>.toRequestResult(): RequestResult<T> {
    return when {
        isSuccess -> RequestResult.Success(getOrThrow())
        isFailure -> RequestResult.Error()
        else -> error("Impossible branch")
    }
}

fun <I : Any, O : Any> RequestResult<I>.map(mapper: (I) -> O): RequestResult<O> {
    return when (this) {
        is RequestResult.Success -> {
            val outData = mapper(data)
            RequestResult.Success(outData)
        }

        is RequestResult.Error -> RequestResult.Error()
        is RequestResult.InProgress -> {
            val outData = data?.let(mapper)
            RequestResult.InProgress(outData)
        }
    }
}
package com.droibit.autoggler.data.provider.rx

import com.jakewharton.rxrelay.BehaviorRelay
import rx.Observable

class RxBus {

    object Nothing

    private val subject: BehaviorRelay<Any> = BehaviorRelay.create()

    fun asObservable(): Observable<Any> {
        return subject.filter { it != Nothing }
                .doOnSubscribe { subject.call(Nothing) }
    }

    fun call(value: Any) = subject.call(value)
}
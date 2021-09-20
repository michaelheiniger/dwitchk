package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class HostCommunicationEventProcessorKey(val value: KClass<out ServerEvent.CommunicationEvent>)

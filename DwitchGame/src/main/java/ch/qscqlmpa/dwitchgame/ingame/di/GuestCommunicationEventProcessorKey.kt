package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class GuestCommunicationEventProcessorKey(val value: KClass<out ClientEvent.CommunicationEvent>)

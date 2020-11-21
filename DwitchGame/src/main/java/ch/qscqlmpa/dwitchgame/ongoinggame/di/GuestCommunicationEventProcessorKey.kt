package ch.qscqlmpa.dwitchgame.ongoinggame.di

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class GuestCommunicationEventProcessorKey(val value: KClass<out ClientCommunicationEvent>)

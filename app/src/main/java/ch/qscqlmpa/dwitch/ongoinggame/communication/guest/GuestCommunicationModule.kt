package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Module
import dagger.Provides

@Module
internal class GuestCommunicationModule {

    @Module
    companion object {

        @OngoingGameScope
        @Provides
        @JvmStatic
        fun provideGuestCommunicator(
            commClient: CommClient,
            messageDispatcher: MessageDispatcher,
            guestCommunicationEventDispatcher: GuestCommunicationEventDispatcher,
            communicationEventRepository: GuestCommunicationEventRepository,
            schedulerFactory: SchedulerFactory
        ): GuestCommunicator {
            return GuestCommunicatorImpl(
                commClient,
                messageDispatcher,
                guestCommunicationEventDispatcher,
                communicationEventRepository,
                schedulerFactory
            )
        }
    }
}

package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
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
            communicationStateRepository: GuestCommunicationStateRepository,
            schedulerFactory: SchedulerFactory
        ): GuestCommunicator {
            return GuestCommunicatorImpl(
                commClient,
                messageDispatcher,
                guestCommunicationEventDispatcher,
                communicationStateRepository,
                schedulerFactory
            )
        }
    }
}

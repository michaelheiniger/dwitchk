package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules


import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicatorImpl
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
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

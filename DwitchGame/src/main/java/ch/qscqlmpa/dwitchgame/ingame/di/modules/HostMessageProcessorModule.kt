package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors.*
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchgame.ingame.di.MessageProcessorKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class HostMessageProcessorModule {

    @InGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.JoinGameMessage::class)
    internal abstract fun bindPlayerJoinMessageProcessor(messageProcessor: JoinGameMessageProcessor): MessageProcessor

    @InGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.RejoinGameMessage::class)
    internal abstract fun bindRejoinGameMessageProcessor(messageProcessor: RejoinGameMessageProcessor): MessageProcessor

    @InGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.LeaveGameMessage::class)
    internal abstract fun bindLeaveGameMessageProcessor(messageProcessor: LeaveGameMessageProcessor): MessageProcessor

    @InGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.PlayerReadyMessage::class)
    internal abstract fun bindPlayerReadyMessageProcessor(messageProcessor: PlayerReadyMessageProcessor): MessageProcessor

    @InGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.WaitingRoomStateUpdateMessage::class)
    internal abstract fun bindWaitingRoomStateUpdateMessageProcessor(messageProcessor: WaitingRoomStateUpdateMessageProcessor): MessageProcessor

    @InGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.GameStateUpdatedMessage::class)
    internal abstract fun bindGameStateUpdatedMessageProcessor(messageProcessor: GameStateUpdatedMessageProcessor): MessageProcessor

    @InGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.CardsForExchangeMessage::class)
    internal abstract fun bindCardsForExchangeMessageProcessor(messageProcessor: CardsForExchangeMessageProcessor): MessageProcessor
}

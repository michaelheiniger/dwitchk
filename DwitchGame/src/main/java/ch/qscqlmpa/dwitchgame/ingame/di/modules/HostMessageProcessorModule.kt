package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors.*
import ch.qscqlmpa.dwitchgame.ingame.di.MessageProcessorKey
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class HostMessageProcessorModule {

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.JoinGameMessage::class)
    internal abstract fun bindPlayerJoinMessageProcessor(messageProcessor: JoinGameMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.RejoinGameMessage::class)
    internal abstract fun bindRejoinGameMessageProcessor(messageProcessor: RejoinGameMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.LeaveGameMessage::class)
    internal abstract fun bindLeaveGameMessageProcessor(messageProcessor: LeaveGameMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.PlayerReadyMessage::class)
    internal abstract fun bindPlayerReadyMessageProcessor(messageProcessor: PlayerReadyMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.WaitingRoomStateUpdateMessage::class)
    internal abstract fun bindWaitingRoomStateUpdateMessageProcessor(messageProcessor: WaitingRoomStateUpdateMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.GameStateUpdatedMessage::class)
    internal abstract fun bindGameStateUpdatedMessageProcessor(messageProcessor: GameStateUpdatedMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.CardsForExchangeMessage::class)
    internal abstract fun bindCardsForExchangeMessageProcessor(messageProcessor: CardsForExchangeMessageProcessor): MessageProcessor
}

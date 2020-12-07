package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchgame.ongoinggame.di.MessageProcessorKey
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.*
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.GameOverMessageProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.JoinGameAckMessageProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.JoinGameMessageProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.RejoinGameAckMessageProcessor
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MessageProcessorModule {

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.JoinGameMessage::class)
    internal abstract fun bindPlayerJoinMessageProcessor(messageProcessor: JoinGameMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.JoinGameAckMessage::class)
    internal abstract fun bindJoinGameAckMessageProcessor(messageProcessor: JoinGameAckMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.RejoinGameMessage::class)
    internal abstract fun bindRejoinGameMessageProcessor(messageProcessor: RejoinGameMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.RejoinGameAckMessage::class)
    internal abstract fun bindRejoinGameAckMessageProcessor(messageProcessor: RejoinGameAckMessageProcessor): MessageProcessor

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
    @MessageProcessorKey(Message.CancelGameMessage::class)
    internal abstract fun bindCancelGameMessageProcessor(messageProcessor: CancelGameMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.LaunchGameMessage::class)
    internal abstract fun bindLaunchGameMessageProcessor(messageProcessor: LaunchGameMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.GameStateUpdatedMessage::class)
    internal abstract fun bindGameStateUpdatedMessageProcessor(messageProcessor: GameStateUpdatedMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.GameOverMessage::class)
    internal abstract fun bindGameOverMessageProcessor(messageProcessor: GameOverMessageProcessor): MessageProcessor
}
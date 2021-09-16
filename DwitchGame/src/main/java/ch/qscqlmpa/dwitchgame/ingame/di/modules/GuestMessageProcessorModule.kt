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
abstract class GuestMessageProcessorModule {

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.JoinGameAckMessage::class)
    internal abstract fun bindJoinGameAckMessageProcessor(messageProcessor: JoinGameAckMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.RejoinGameAckMessage::class)
    internal abstract fun bindRejoinGameAckMessageProcessor(messageProcessor: RejoinGameAckMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.WaitingRoomStateUpdateMessage::class)
    internal abstract fun bindWaitingRoomStateUpdateMessageProcessor(messageProcessor: WaitingRoomStateUpdateMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.KickPlayerMessage::class)
    internal abstract fun bindKickPlayerMessageProcessor(messageProcessor: KickPlayerMessageProcessor): MessageProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @MessageProcessorKey(Message.CancelGameMessage::class)
    internal abstract fun bindCancelGameMessageProcessor(messageProcessor: GameCanceledMessageProcessor): MessageProcessor

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

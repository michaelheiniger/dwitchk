package ch.qscqlmpa.dwitch.utils

//class PlayerDashboardRobot(private val dashboard: PlayerDashboard) {
//
//    fun assertLocalPlayerId(id: PlayerInGameId): PlayerDashboardRobot {
//        assertThat(dashboard.localPlayer.id).isEqualTo(id)
//        return this
//    }
//
//    fun assertCanPlay(canPlay: Boolean): PlayerDashboardRobot {
//        assertThat(dashboard.canPlay).isEqualTo(canPlay)
//        return this
//    }
//
//    fun assertCanPickACard(canPickACard: Boolean): PlayerDashboardRobot {
//        assertThat(dashboard.canPickACard).isEqualTo(canPickACard)
//        return this
//    }
//
//    fun assertCanPass(canPass: Boolean): PlayerDashboardRobot {
//        assertThat(dashboard.canPass).isEqualTo(canPass)
//        return this
//    }
//
//    fun assertCardsInHandInAnyOrder(vararg cards: Card): PlayerDashboardRobot {
//        assertThat(dashboard.cardsInHand).containsExactlyInAnyOrder(*cards)
//        return this
//    }
//
//    fun assertMinimumCardValueAllowed(value: CardName): PlayerDashboardRobot {
//        assertThat(dashboard.minimumCardValueAllowed).isEqualTo(value)
//        return this
//    }
//
//    fun assertPlayerState(playerId: PlayerInGameId, state: PlayerStatus): PlayerDashboardRobot {
//        assertThat(dashboard.players.getValue(playerId).state).isEqualTo(state)
//        return this
//    }
//
//    fun assertPlayerRank(playerId: PlayerInGameId, rank: Rank): PlayerDashboardRobot {
//        assertThat(dashboard.players.getValue(playerId).rank).isEqualTo(rank)
//        return this
//    }
//
//    fun assertCanStartNewRound(canStartNewRound: Boolean): PlayerDashboardRobot {
//        assertThat(dashboard.canStartNewRound).isEqualTo(canStartNewRound)
//        return this
//    }
//
//    fun assertCanEndGame(canEndGame: Boolean): PlayerDashboardRobot {
//        assertThat(dashboard.canEndGame).isEqualTo(canEndGame)
//        return this
//    }
//
//    fun assertGameEvent(gameEvent: GameEvent?): PlayerDashboardRobot {
//        assertThat(dashboard.gameEvent).isEqualTo(gameEvent)
//        return this
//    }
//
//    fun assertGamePhase(gamePhase: GamePhase): PlayerDashboardRobot {
//        assertThat(dashboard.gamePhase).isEqualTo(gamePhase)
//        return this
//    }
//
//    fun assertJoker(card: CardName): PlayerDashboardRobot {
//        assertThat(dashboard.joker).isEqualTo(card)
//        return this
//    }
//
//    fun assertCardsOnTable(cards: List<Card>): PlayerDashboardRobot {
//        assertThat(dashboard.cardsOnTable).isEqualTo(cards)
//        return this
//    }
//
//    fun assertTableEmpty(): PlayerDashboardRobot {
//        assertThat(dashboard.cardsOnTable).isEmpty()
//        return this
//    }
//}
package ch.qscqlmpa.dwitchengine.utils

import ch.qscqlmpa.dwitchengine.utils.ListUtil.shiftRightByN
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ListUtilTest {

    @Nested
    @DisplayName("shiftRightByN")
    inner class Process {

        @Test
        fun `Size does not change`() {
            val listToShift = listOf(1, 2, 3, 4, 5)

            val shiftedList = listToShift.shiftRightByN(1)

            assertThat(shiftedList.size).isEqualTo(listToShift.size)
        }

        @Test
        fun `All elements are right-shifted by 0`() {
            val listToShift = listOf(1, 2, 3, 4, 5)

            val shiftedList = listToShift.shiftRightByN(0)

            assertThat(shiftedList[0]).isEqualTo(1)
            assertThat(shiftedList[1]).isEqualTo(2)
            assertThat(shiftedList[2]).isEqualTo(3)
            assertThat(shiftedList[3]).isEqualTo(4)
            assertThat(shiftedList[4]).isEqualTo(5)
        }

        @Test
        fun `All elements are right-shifted by 1`() {
            val listToShift = listOf(1, 2, 3, 4, 5)

            val shiftedList = listToShift.shiftRightByN(1)

            assertThat(shiftedList[0]).isEqualTo(5)
            assertThat(shiftedList[1]).isEqualTo(1)
            assertThat(shiftedList[2]).isEqualTo(2)
            assertThat(shiftedList[3]).isEqualTo(3)
            assertThat(shiftedList[4]).isEqualTo(4)
        }

        @Test
        fun `All elements are right-shifted by -1`() {
            val listToShift = listOf(1, 2, 3, 4, 5)

            val shiftedList = listToShift.shiftRightByN(-1)

            assertThat(shiftedList[0]).isEqualTo(2)
            assertThat(shiftedList[1]).isEqualTo(3)
            assertThat(shiftedList[2]).isEqualTo(4)
            assertThat(shiftedList[3]).isEqualTo(5)
            assertThat(shiftedList[4]).isEqualTo(1)
        }

        @Test
        fun `All elements are right-shifted by 2`() {
            val listToShift = listOf(1, 2, 3, 4, 5)

            val shiftedList = listToShift.shiftRightByN(2)

            assertThat(shiftedList[0]).isEqualTo(4)
            assertThat(shiftedList[1]).isEqualTo(5)
            assertThat(shiftedList[2]).isEqualTo(1)
            assertThat(shiftedList[3]).isEqualTo(2)
            assertThat(shiftedList[4]).isEqualTo(3)
        }

        @Test
        fun `All elements are right-shifted by -2`() {
            val listToShift = listOf(1, 2, 3, 4, 5)

            val shiftedList = listToShift.shiftRightByN(-2)

            assertThat(shiftedList[0]).isEqualTo(3)
            assertThat(shiftedList[1]).isEqualTo(4)
            assertThat(shiftedList[2]).isEqualTo(5)
            assertThat(shiftedList[3]).isEqualTo(1)
            assertThat(shiftedList[4]).isEqualTo(2)
        }

        @Test
        fun `All elements are right-shifted by the size of the list + 1`() {
            val listToShift = listOf(1, 2, 3, 4, 5)

            val shiftedList = listToShift.shiftRightByN(listToShift.size + 1)

            assertThat(shiftedList[0]).isEqualTo(5)
            assertThat(shiftedList[1]).isEqualTo(1)
            assertThat(shiftedList[2]).isEqualTo(2)
            assertThat(shiftedList[3]).isEqualTo(3)
            assertThat(shiftedList[4]).isEqualTo(4)
        }
    }
}

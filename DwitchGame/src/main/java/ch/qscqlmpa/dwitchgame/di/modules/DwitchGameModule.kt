package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.di.GameScope
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class DwitchGameModule(private val idlingResource: DwitchIdlingResource) {

    @GameScope
    @Provides
    fun providesIdlingResource(): DwitchIdlingResource {
        return idlingResource
    }
}

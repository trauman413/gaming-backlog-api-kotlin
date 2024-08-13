package com.gamingbacklog.api.gamingbacklogapi.models.results

import com.gamingbacklog.api.gamingbacklogapi.models.enums.MultiLibraryStatus

data class MultiLibraryResult(
  val libraries: List<LibraryResult>,
  val libraryStatus: MultiLibraryStatus
)
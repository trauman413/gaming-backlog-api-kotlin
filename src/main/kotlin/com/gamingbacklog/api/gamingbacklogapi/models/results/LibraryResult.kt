package com.gamingbacklog.api.gamingbacklogapi.models.results

import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.enums.LibraryStatus

data class LibraryResult(
  val library: Library?,
  val libraryStatus: LibraryStatus
)
